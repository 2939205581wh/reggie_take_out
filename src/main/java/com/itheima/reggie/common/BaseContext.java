package com.itheima.reggie.common;


/**     以线程为作用域
 *
 *  基于 ThreadLocal 封装工具类，用户保存和获取当前登入用户 id
 */
public class BaseContext {
//    ThreadLocal<Long>  因为要获取 id  long型
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     *   将id 存入 该线程之中
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     *   获取 该线程 存入的 id
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
