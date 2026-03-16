package com.zoctan.api.controller;

import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    // 配置上传目录（建议放在项目外，避免被清理）
    private static final Path UPLOAD_PATH = Paths.get("B:/毕设/代码/psychology-backend/uploads").toAbsolutePath();

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println(">>> FileUploadController 被调用了！");
        if (file.isEmpty()) {
            return ResultGenerator.genFailedResult("文件为空");
        }

        // 1. 验证是否为图片
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResultGenerator.genFailedResult("只能上传图片文件");
        }

        // 2. 限制大小（例如 5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResultGenerator.genFailedResult("图片不能超过5MB");
        }

        // 3. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        // 4. 确保目录存在
        if (!Files.exists(UPLOAD_PATH)) {
            try {
                Files.createDirectories(UPLOAD_PATH);
            } catch (IOException e) {
                e.printStackTrace();
                return ResultGenerator.genFailedResult("上传目录创建失败: " + e.getMessage());
            }
        }

        // 5. 保存文件 —— 关键：使用 InputStream + Files.copy（绕过 Jetty 的 transferTo）
        try (InputStream inputStream = file.getInputStream()) {
            Path targetPath = UPLOAD_PATH.resolve(filename);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> 文件已保存到: " + targetPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.genFailedResult("文件保存失败: " + e.getMessage());
        }

        // 6. 返回可访问的 URL
        String url = "/uploads/" + filename;
        return ResultGenerator.genOkResult(url);
    }
}