package com.example.gaodejava;

import com.example.gaodejava.point;
import com.example.gaodejava.line;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.*;
import java.util.*;

import oracle.sql.BLOB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*") // 添加 CORS 中间件
public class BackendServerApplication {
    static Logger logger = LoggerFactory.getLogger(BackendServerApplication.class);
    private JdbcTemplate jdbcTemplate;
    // 构造函数
    public BackendServerApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public static void main(String[] args) {
        SpringApplication.run(BackendServerApplication.class, args);
    }

    // 配置数据库连接
    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gaode";
        String user = "root";
        String password = "hanlu1997";
        return DriverManager.getConnection(url, user, password);
    }

    @GetMapping("/selectMarker/selectAll")
    public List<point> handleGetMarker(@RequestParam("table") String table) throws IOException {
        point pointInstance = new point();
        return pointInstance.selectAll(table);
    }

    @GetMapping("/selectMarker/selectByKeyword")
    public List<point> handleGetMarker(@RequestParam("cols") String[] cols) throws IOException {
        point pointInstance = new point();
        return pointInstance.searchByKeyword(cols);
    }

    @GetMapping("/selectLine")
    public List<line> handleGetLine() throws IOException {
        line lineInstance = new line();
        return lineInstance.selectAll();
    }


    // 处理 POST 请求
    @PostMapping("/")
    public String handlePostRequest(@RequestParam("sql") String sql) {
        try {
            Connection conn = connectToDatabase();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return "{\"success\": true}";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // ER_DUP_ENTRY
                return "{\"error\": \"该记录已经存在，无法插入\", \"message\": \"" + e.getMessage() + "\"}";
            } else {
                return "{\"error\": \"未知错误\", \"message\": \"" + e.getMessage() + "\"}";
            }
        }
    }

    @PostMapping("/uploadImages")
    public ResponseEntity<String> uploadImages(@RequestParam("file") MultipartFile file,@RequestParam("id") Long id) throws IOException, SQLException {
        // 从 MultipartFile 中获取字节数组
        byte[] bytes = file.getBytes();
        logger.info("上传图片的大小为：" + bytes.length);
        // 使用 Spring JDBC 将数据插入到 images 表中
        String sql = "UPDATE points SET images = ? WHERE id = ?";
        jdbcTemplate.update(sql, bytes, id);

        // 返回成功的响应
        return ResponseEntity.ok("Image uploaded successfully");
    }
}

