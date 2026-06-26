package com.ems.controller;

import com.ems.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        String subDir = "avatar";
        File dir = new File(uploadPath, subDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, newFilename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + "/uploads/" + subDir + "/" + newFilename;
        return Result.success(url);
    }
}
