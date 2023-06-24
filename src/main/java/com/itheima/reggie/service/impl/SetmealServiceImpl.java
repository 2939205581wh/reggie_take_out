package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 套餐管理 中 添加套餐中 的  保存按钮
     * 将 套餐的基本信息 以及 关联的 菜品信息 进行保存
     *
     * @param setmealDto
     */
    @Override
    @Transactional  //  保证数据的一致性   操作2张表
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息  ---- 操作setmeal表 ，执行 insert操作
        this.save(setmealDto);

        // 保存套餐和菜品的关联信息  -----   操作 setmeal_dish表 ， 执行 insert操作

//        得到 SetmealDishes属性 并放入集合中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//       因为  SetmealDish类中 setmealId属性 是没有被赋值上的
//        所以 应该遍历 setmealDishes对象   使用 stream流进行处理  将setmealId属性进行赋值
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        setmealDishService.saveBatch(setmealDishes);   //  saveBatch  批量保存
    }



    /**
     *   套餐管理中的  批量删除  删除 2个按钮
     * 注意删除 套餐时 还有 删除那些菜品的关联数据
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {  // ids 为套餐的id

        //  1,查询套餐状态，确定是否可以删除
       //  select count(*) from setmeal where id in (1,2,3) and status=1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

//        this为  继承的  ServiceImpl类  框架的方法
        int count = this.count(queryWrapper);

        //  2,如果不能删除，抛出一个业务异常
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //  3,如果可以删除， 先删除 是套餐表的数据 ---- setmeal表
        this.removeByIds(ids);



        //  4,在 删除关系表的数据 ----- setmeal_dish表

        /*
        *  removeByIds()  不能使用该方法  因为该方法参数  必须为 主键值 ids不是主键值
         * */
//        setmealDishService.removeByIds(ids)
        /*
        *    所以 使用另一种方式
        * */
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //  构造条件
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
