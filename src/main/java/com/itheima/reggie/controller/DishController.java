package com.itheima.reggie.controller;

/**
 * 关于菜品  和  菜品口味  的操作
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.CategroyService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品 管理页面
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategroyService categroyService;


    /**
     *   菜品管理页面  新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * 因为该 菜品管理页面  中需要将 菜品图片加载  以及
     * 菜品分类也展示不出
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //  构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        //  DishDto 该类 有categoryName 字段  可以为前端  菜品分类字段赋值
        Page<DishDto> dishDtoPage = new Page<>();

        // 构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //  添加过滤条件   name 模糊查询
        queryWrapper.like(name != null, Dish::getName, name);

        //  添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //  执行查询
        dishService.page(pageInfo, queryWrapper);


        //   为了 给菜品管理页面中的  菜品分类字段  进行赋值


        //  对象 拷贝    BeanUtils 工具类
        //  第一个参数为 源   第三个参数为 忽略拷贝的属性  records 是封装好所有数据的集合
        //  忽略 records 属性  是因为 我们想要 List<Dish>这种类型的 records
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");


        //  将 records  集合属性拿出  进行处理
        List<Dish> records = pageInfo.getRecords();

        //  item 代表Dish  也就是遍历出的每一个菜品对象   map 把每个元素拿出
        //  .collect(Collectors.toList())  将该集合 转成 ist<DishDto> 集合
        List<DishDto> list = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();  //  拿到分类id  为了取出 分类名称

            //  根据categoryId 该id  拿到 分类对象  即可取出分类  名称
            Category categroy = categroyService.getById(categoryId);



            /*  进行赋值
                       注意  因为dishDto 是new出来的  属性都为空  所以也要为其他属性赋值
            * */
            DishDto dishDto = new DishDto();
            //item 代表Dish  也就是遍历出的每一个菜品对象  进行拷贝
            BeanUtils.copyProperties(item, dishDto);


            /*
            *   由于数据问题 categroyName 无法categroyId查出 所有无法赋值
            *      所以  当categroy!=null时  直接 为categroyName 进行赋值操作
            * */
            if(categroy!=null){
                //  取出分类名称
                String categroyName = categroy.getName();
                dishDto.setCategoryName(categroyName);
            }
            return dishDto;
        }).collect(Collectors.toList());  //  将 所有对象 收集

        //  处理成  该List<DishDto>类型
//        List<DishDto> list = null;

        //  进行赋值
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**    菜品管理中 进行修改菜品时 页面 数据的初始化
     *  将数据赋值于 前端
     * @param id   根据 id 查询菜品信息和对应的口味信息
     * @return
     */
    //  DishDto  修改页面中  还包含 口味 List<DishFlavor> flavors
    @GetMapping("/{id}")   //  因为 id 是在请求  url路径的
    public R<DishDto> get(@PathVariable Long id){  // @PathVariable 接收id

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    /**
     *   菜品管理页面  修改菜品  保存按钮
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }


    /**
     *   套餐管理页面中的 新建菜品页面的 添加菜品
     *     根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    //  Dish dish  传这个参数 通用性更好  因为他包含了 categoryId属性  并且还有更多其他属性
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        //  条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //  添加条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
       //  在添加一个条件  查询状态为 1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        // 添加排序条件   sort字段相同，则根据 UpdateTime 字段进行排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        //  将该 处理好的数据  传入 list集合中
        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }


    /**  菜品管理  批量起售，批量停售，停售
     *
     *   @PathVariable   参数绑定占位符
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status,String[] ids){
        for (String id:ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }


    /*
    * 菜品管理   删除
    *
    * */
    @DeleteMapping
    public R<String> delete(String[] ids){
        for (String id:ids) {
            dishService.removeById(id);
        }

        return  R.success("删除成功");
    }
}
