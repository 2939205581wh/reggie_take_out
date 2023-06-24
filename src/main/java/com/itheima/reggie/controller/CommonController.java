package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file 该参数名不能 随便取   要与前端数据 name="~~"  保持一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
//        file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //   获取 原始文件名     不建议  因为可能会被后面 名字一样的图片 覆盖   可以选择随机生成文件名
        String originalFilename = file.getOriginalFilename();  //  ~~.jpg
//      截取  jpg 该后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID重新生成文件名，防止文件名称重复造成文件覆盖、
//        生成一个 随机的 30多位的 字符串
        String fileName = UUID.randomUUID().toString() + suffix;  //  加上suffix  即  .jpg

        /**
         *   判断 文件是否存在  是否需要创建
         */
//        创建一个目录对象
        File dir = new File(basePath);

        if (!dir.exists()) {     //  当目录不存在时
            dir.mkdirs();  // 创建目录
        }
        try {
            //  将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response 输出流需要 response来获得
     */
    @GetMapping("download")
    public void download(String name, HttpServletResponse response) {

        try {
            // 输入流  通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 输入流  通过输入流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            //  设置 响应回去的是  什么类型的文件
            response.setContentType("image/jpeg");

          byte[] bytes =new byte[1024];  // 长度为 1024
            //   将获取的文件 进行读     每次读  都放入 bytes数组中
            int len=0;
            while ((len=fileInputStream.read(bytes))!=-1){  //  len=-1  表示读完了

                //  通过 输出流  向浏览器进行 写操作
                outputStream.write(bytes,0,len);  //  从第1个写  写到len 长度
                //  刷新
                outputStream.flush();

            }

            //  关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
