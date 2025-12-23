package com.shop.bookshop.controller;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class ImageUploadController {

    @PostMapping("/book_image")
    public ResultVO uploadBookImage(MultipartFile bookImage, HttpServletRequest request) {

        //获取项目上传文件夹路径
        String path = "src/main/resources/static/images/book_images";
        File pathFile=new File("src/main/resources/static/images/book_images");
        System.out.println(pathFile.getAbsolutePath());
        String targetFileName=bookImage.getOriginalFilename();
        File targetFile = new File(pathFile.getAbsolutePath(), targetFileName);

        //上传
        try {
            bookImage.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(ResultCode.FAILED, "上传图片失败");
        }
        return new ResultVO(ResultCode.SUCCESS,targetFileName);
    }
}
