package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    //   用来操作口味表
    @Autowired
    private DishFlavorService dishFlavorService;

    //  新增菜品  同时保存对应口味数据
    @Override
    @Transactional   //  加上事务   因为 涉及到了2个表
    public void saveWithFlavor(DishDto dishDto) {

        //   保存菜品的基本信息到菜品表dish
        this.save(dishDto);


        //  保存菜品口味数据到口味表dish_flavor   并不会得到菜品id
//        dishFlavorService.saveBatch(dishDto.getFlavors());

        //   获得菜品id
        Long dishDtoId = dishDto.getId();

        //   菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);   //  将得到了菜品id  赋值进去
            return item;
        }).collect(Collectors.toList());   //  重新得到一个  list集合

        //   保存菜品口味数据到口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 菜品管理页面  修改菜品 根据id 来查询对应的菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //  查询菜品基本信息  从dish表查询
        Dish dish = this.getById(id);


        //  查询 当前菜品对应的口味信息， 从 dish_flavor表查询

//        添加条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
//        添加条件   等值查询
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
//       将该 处理好的数据  传入 list集合中
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //     因为返回值为  DishDto对象
        DishDto dishDto = new DishDto();
//        将菜品中的普通属性 全部拷贝来了了
        BeanUtils.copyProperties(dish, dishDto);

//       最后将  口味数据flavors 传入dishDto对象
        dishDto.setFlavors(flavors);

        return dishDto;
    }


    /**
     * 菜品管理页面  修改页面中的保存按钮   更新 菜品信息 和 对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional   // 操作多张表 要加上 事务
    public void updateWithFlavor(DishDto dishDto) {

        //  更新dish表的基本信息
//       要的是Dish    因为DishDto 为 Dish 子类
        this.updateById(dishDto);   //  将普通属性进行赋值

        //  清理当前菜品对应口味数据 ----  dish_flavor表的 delect 操作
//        添加  构造条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
//        添加 条件
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        //  根据 dishId 删除数据
        dishFlavorService.remove(queryWrapper);


        //  添加当前提交过来的口味数据 ----  dish_flavor表的 insert 操作

//        拿到口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

//        因为  DishFlavor类中 只封装上了 name和value 字段  而dishId 并没有封装上
//          和 上面新增菜品 所遇到的问题一样

        /*
        *   遍历 flavors  将每一项拿出来 setDishId
        * */
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());   //  将得到了菜品id  赋值进去
            return item;
        }).collect(Collectors.toList());   //  重新得到一个  list集合

        dishFlavorService.saveBatch(flavors);

    }
}
