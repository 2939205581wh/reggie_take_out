package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 *   分类管理   数据库操作
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
