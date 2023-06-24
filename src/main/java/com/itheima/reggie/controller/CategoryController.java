package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategroyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 *   分类管理页面   控制层
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategroyService categroyService;

    /**
     *    新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categroyService.save(category);
        return R.success("新增分类成功");
    }

    /**
     *   分页管理页面   加载页面初始化数据
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //  分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //  构造条件构造器  （为 排序 准备）
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        //  添加排序条件  根据 sort字段进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //  进行 分页查询
        categroyService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     *    因为删除时要检查删除的分类是否关联了菜品或者套餐
     *  所以 创建  Dish菜品  Setmeal套餐  实体类  以及 mapper和 service
     *
     *   根据 id 删除菜品分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delect(Long id){
        log.info("删除分类。id为：{}",id);
//        categroyService.removeById(id);
        categroyService.remove(id);   //  自己定义的remove()方法
        return R.success("分类信息删除成功");
    }

    /**
     *   根据 id 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);

        categroyService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     *     菜品管理页面    新建菜品中的 下拉框 菜品分类 的填充数据
     *
     *    根据条件查询分类数据
     *
     * @param category  因为前端页面只需要一个参数 type: 1   category  可以调用更多的相关数据
      * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //  条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件  eq -->  等值查询
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //  添加排序条件
//     优先使用 Sort 排序   Sort相同则 使用 UpdateTime排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //  将该 处理好的数据  传入 list集合中
        List<Category> list = categroyService.list(queryWrapper);
        //  返回给前端
        return R.success(list);
    }
}
