package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品   数据层
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
