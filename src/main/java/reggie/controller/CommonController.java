package reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reggie.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;


/**
 * File既是文件也是文件夹
 * 可以通过new File（文件名）的形式来生成文件（应该是空的）
 * file.transferTo（）是把临时文件覆盖（大概吧）指定文件
 */


/**
 * 文件的上传和下载
 */

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @PostMapping("upload")
    public R<String> upload(MultipartFile file){
        log.info("上传：",file.toString());

        //获取原始的文件名
        String originalFilenameFileName = file.getOriginalFilename();
        //获取原始文件的后缀，例如".jpg"
        String suffix = originalFilenameFileName.substring(originalFilenameFileName.lastIndexOf("."));
        //原始文件名有可能会重复，所以通过UUID生成随机的新的文件名，并拼接上文件后缀
        String newFileName = UUID.randomUUID().toString() + suffix;

        //文件夹路径
        String path = System.getProperty("user.dir")+"\\img\\";

        //创建一个目录对象
        File dir = new File(path);
        //若目录不存在则创建之
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            //将上传的文件保存至指定文件夹
            file.transferTo(new File(path + newFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //最后要把新文件的文件名返回
        //由于上传来的文件肯定是放在指定文件夹的，所以下载的时候只需要服务器去指定文件夹搜索就好了
        //所以文件的路径不需要返回，只需要返回文件名就行了
        return R.success(newFileName);
    }

    @GetMapping("/download")
    public void download(String name,HttpServletResponse httpServletResponse){

        try {
            //文件夹路径
            String path = System.getProperty("user.dir")+"\\img\\";

            FileInputStream inputStream = new FileInputStream(path + name);
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1){
                servletOutputStream.write(bytes);
                servletOutputStream.flush();
            }

            //关闭输入流和输出流
            inputStream.close();
            servletOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
