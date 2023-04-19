package com.example.gaodejava;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*") // 添加 CORS 中间件
public class BackendServerApplication {

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

    // 处理 GET 请求
    @GetMapping("/")
    public List<Map<String, Object>> handleGetRequest(@RequestParam("sql") String sql) {
        try {
            Connection conn = connectToDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                // Represent a row in DB. Key: Column name, Value: Column value
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    // Note that the index is 1-based
                    String colName = rsmd.getColumnName(i);
                    Object colVal = rs.getObject(i);
                    row.put(colName, colVal);
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            return null;
        }
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
}

