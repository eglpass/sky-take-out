package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {


    /**
     * 菜品图片的上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("文件上传开始: {}", file.getOriginalFilename());

        // 创建存储目录
        String uploadDir = "D:\\image\\";
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("目录创建失败: {}", uploadDir);
            return Result.error("上传失败，无法创建目录");
        }

        try {
            // 处理文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + suffix;
            File destination = new File(uploadDir + fileName);

            // 保存文件
            file.transferTo(destination);
            log.info("文件上传成功: {}", destination.getAbsolutePath());
            return Result.success(destination.getAbsolutePath());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}