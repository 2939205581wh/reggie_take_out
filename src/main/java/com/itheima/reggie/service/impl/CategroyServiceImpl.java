package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategroyService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategroyServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategroyService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;
    /**
     * 删除时要检查删除的分类是否关联了菜品或者套餐
     *      根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
//       构造查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper();
//        添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        //  查询当前分类是否关联了 菜品，如果已经关联，抛出一个业务异常
        if(count1 > 0){
//            count1() > 0  表示 已经关联了 菜品
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //  查询当前分类是否关联了 套餐，如果已经关联，抛出一个业务异常
        if(count2 > 0){
//            count2() > 0  表示 已经关联了 套餐
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //  正常删除分类
        super.removeById(id);

    }
}
