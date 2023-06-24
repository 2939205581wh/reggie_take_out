package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;


/**
 *  菜品
 */
public interface DishService extends IService<Dish> {
    //  菜品管理页面  新增按钮  新增菜品  同时插入菜品对应的口味数据，需要操作两张表： dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //  菜品管理页面  修改按钮   修改菜品 根据id 来查询对应的菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //  菜品管理页面  修改页面中的保存按钮   更新 菜品信息 和 对应的口味信息
    public void updateWithFlavor(DishDto dishDto);
}
