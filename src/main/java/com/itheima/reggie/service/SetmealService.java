package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * 套餐
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     *    套餐管理 中 添加套餐中 的  保存按钮
     *    将 套餐的基本信息 以及 关联的 菜品信息 进行保存
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);


    /**
     *   套餐管理中的  批量删除  删除 2个按钮
     * 注意删除 套餐时 还有 删除那些菜品的关联数据
     */
    public void removeWithDish(List<Long> ids);
}
