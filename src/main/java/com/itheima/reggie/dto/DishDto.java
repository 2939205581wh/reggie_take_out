package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 *   因为  前端需要的数据  不止一个类   所以直接定义 一个dto类 来将 全部数据存入   类似于外键
 *
 *   extends Dish   继承了 Dish  并且还扩展了
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
