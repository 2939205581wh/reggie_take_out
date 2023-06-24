package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义元数据对象处理器
 */
    @Component
    @Slf4j
    /**
     *  当有插入操作时 执行
     */
    public class MyMetaObjecthandler implements MetaObjectHandler {
        @Override
        public void insertFill(MetaObject metaObject) {
            log.info("公共字段自动填充[insert]...");
            log.info(metaObject.toString());

            metaObject.setValue("createTime",LocalDateTime.now());
            metaObject.setValue("updateTime",LocalDateTime.now());
            metaObject.setValue("createUser",BaseContext.getCurrentId());
            metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }

    /**
     * 当更新操作时  执行
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        /**
         * 查看是否 线程一致
         *
         * LoginCheckFilter
         * MyMetaObjecthandler.updateFill
         * EmployeeController.update
         */
        //   获取 线程id值
        Long id  =  Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        //  设置 更新时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        //   设置  更新用户id  通过 BaseContext 工具类  获取 存入线程的id
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
