package com.ems.controller;

import com.ems.common.BusinessException;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:5242880}")
    private long maxFileSize;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp",
            ".pdf", ".doc", ".docx", ".xls", ".xlsx"
    );

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, 
                                 @RequestParam(value = "type", defaultValue = "avatar") String type,
                                 HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String suffix = getFileExtension(originalFilename);
        validateExtension(suffix);

        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        String subDir = getSubDir(type);
        Path dir = getUploadRoot().resolve(subDir);
        try {
            Files.createDirectories(dir);
            Path dest = dir.resolve(newFilename).normalize();
            file.transferTo(dest.toFile());
            validateImageContent(dest.toFile(), suffix);
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }

        String url = buildUrl(request, subDir, newFilename);
        return Result.success(url);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(400, "文件大小超过限制（最大 " + formatFileSize(maxFileSize) + "）");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(400, "不支持的文件类型");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException(400, "文件名格式不正确");
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private void validateExtension(String suffix) {
        if (!ALLOWED_EXTENSIONS.contains(suffix)) {
            throw new BusinessException(400, "不支持的文件扩展名: " + suffix);
        }
    }

    private String getSubDir(String type) {
        return switch (type.toLowerCase()) {
            case "avatar" -> "avatar";
            case "document" -> "document";
            case "contract" -> "contract";
            case "certificate" -> "certificate";
            default -> throw new BusinessException(400, "不支持的文件类型: " + type);
        };
    }

    private Path getUploadRoot() {
        return Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    private void validateImageContent(File file, String suffix) throws IOException {
        if (!IMAGE_EXTENSIONS.contains(suffix)) {
            return;
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        if (bytes.length < 4) {
            file.delete();
            throw new BusinessException(400, "无效的图片文件");
        }

        String magicNumber = String.format("%02x%02x%02x%02x", bytes[0], bytes[1], bytes[2], bytes[3]);
        boolean valid = switch (suffix) {
            case ".jpg", ".jpeg" -> magicNumber.startsWith("ffd8");
            case ".png" -> magicNumber.startsWith("89504e47");
            case ".gif" -> magicNumber.startsWith("47494638");
            case ".webp" -> magicNumber.startsWith("52494646") && bytes.length > 12 
                    && String.format("%02x%02x%02x%02x", bytes[8], bytes[9], bytes[10], bytes[11]).startsWith("57454250");
            case ".bmp" -> magicNumber.startsWith("424d");
            default -> true;
        };

        if (!valid) {
            file.delete();
            throw new BusinessException(400, "图片文件内容无效或被篡改");
        }
    }

    private String buildUrl(HttpServletRequest request, String subDir, String filename) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if (!("http".equals(scheme) && serverPort == 80) 
                && !("https".equals(scheme) && serverPort == 443)) {
            url.append(":").append(serverPort);
        }
        url.append("/uploads/").append(subDir).append("/").append(filename);
        return url.toString();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
