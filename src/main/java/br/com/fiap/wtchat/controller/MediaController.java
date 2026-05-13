package br.com.fiap.wtchat.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/media")
public class MediaController {

    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("java.io.tmpdir"), "wtchat-uploads");

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        Files.createDirectories(UPLOAD_DIR);

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : ".bin";
        String filename = UUID.randomUUID() + ext;
        Files.write(UPLOAD_DIR.resolve(filename), file.getBytes());

        String url = buildBaseUrl(request) + "/media/" + filename;
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> serve(@PathVariable String filename) throws IOException {
        Path file = UPLOAD_DIR.resolve(filename).normalize();
        // Prevent path traversal
        if (!file.startsWith(UPLOAD_DIR) || !Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        contentType != null ? contentType : "application/octet-stream")
                .body(Files.readAllBytes(file));
    }

    private String buildBaseUrl(HttpServletRequest request) {
        String proto = request.getHeader("X-Forwarded-Proto");
        if (proto == null) proto = request.getScheme();
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null) {
            int port = request.getServerPort();
            boolean defaultPort = (port == 80 && "http".equals(proto)) || (port == 443 && "https".equals(proto));
            host = request.getServerName() + (defaultPort ? "" : ":" + port);
        }
        return proto + "://" + host;
    }
}
