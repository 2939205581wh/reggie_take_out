package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategroyService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired    // 通过 CategoryId  来查询分类数据 用来查询分类name
    private CategroyService categroyService;


    /**  套餐管理 中 添加套餐中 的  保存按钮
     *
     * @param setmealDto  因为 参数中 包含2个表的字段  所以用 dto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){  // @RequestBody  JSON数据
        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }


    /**  套餐管理页面  数据的初始化
     *   套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);


        //  构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //  添加查询条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //   添加排序条件， 根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //  进行分页查询
        setmealService.page(pageInfo,queryWrapper);

        /*
        *   有问题  套餐管理页面的套餐分类没有数据
        *       因为 只是传入了一个 Setmeal的categoryId属性  而不是 SetmealDish的name属性
        *
        * */
//        return R.success(pageInfo);


        //  SetmealDto  因为 SetmealDto中有 前端 所需要的 categoryName属性
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);


        /*
        *    对象拷贝
        * records该属性不需要拷贝  records是所有基础属性的集合
        * 主要是因为  泛型不一样  records为 Page<Setmeal>这种类型  我们需要 Page<SetmealDto>这种类型
        * */
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        //   先将 records属性 取出
        List<Setmeal> records = pageInfo.getRecords();
        /*
        *   我们需要 Page<SetmealDto>这种类型  的records
        *      使用 stream流即可
        * */
        List<SetmealDto> list=records.stream().map((item) -> {
//            为了 将CategoryName 赋值  赋值后但是其他属性都为空 所以进行拷贝item
            SetmealDto setmealDto = new SetmealDto();
            //  拷贝
            BeanUtils.copyProperties(item,setmealDto);
//            分类id  通过 CategoryId  来查询分类数据
            Long categoryId = item.getCategoryId();
//            根据分类id 来查询分类对象   拿到一个 category 分类对象  从而获取分类名称
            Category category = categroyService.getById(categoryId);
            if(category!=null){
                //   根据category 分类对象  得到所需要的分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //  将 records 属性赋值上
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }


    /**  套餐管理  批量起售，批量停售，停售
     *
     *   @PathVariable   参数绑定占位符
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status,String[] ids){
        for (String id:ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }

    /**
     *   套餐管理中的  批量删除  删除 2个按钮
     * 注意删除 套餐时 还有那些菜品的关联数据
     */
    @DeleteMapping
    public R<String> delete(String[] ids){
        for (String id:ids) {
            setmealService.removeById(id);
        }
        return  R.success("删除成功");
    }
}
