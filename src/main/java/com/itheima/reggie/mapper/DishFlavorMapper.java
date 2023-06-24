package com.itheima.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品管理页面     新建菜品 数据层
 */

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
