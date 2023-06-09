package com.example.gaodejava;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class importData {
    //导入博物馆信息到mysql数据库
    // getStringValue() 方法，用于获取单元格中的 String 值，若单元格为空，则返回空字符串
    private static String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        String value = cell.toString();
        if (value == null) {
            value = "";
        }

        return value.trim();
    }

    // getDoubleValue() 方法，用于获取单元格中的 Double 值，若单元格为空或值不合法，则返回 null
    private static Double getDoubleValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        String value = cell.toString();
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        importData importData = new importData();
        String table = "fenjifenlei";
        importData.importSheet0(table);
//        importData.importSheet1(table);
//        importData.importSheet2(table);
//        importData.importSheet3(table);
//        importData.importSheet4(table);
//        importData.importSheet5(table);
//        importData.importSheet6(table);
//        importData.importSheet7(table);

    }

    public void importSheet0(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String name = getStringValue(row.getCell(1));
                if (name == "") break;
                String date = getStringValue(row.getCell(2));
                String epoch1 = getStringValue(row.getCell(3));
                String epoch2 = getStringValue(row.getCell(4));
                Double lng_GCJ02 = getDoubleValue(row.getCell(5));
                Double lat_GCJ02 = getDoubleValue(row.getCell(6));
                Double BD09_lng = getDoubleValue(row.getCell(7));
                Double BD09_lat = getDoubleValue(row.getCell(8));
                Double lng_WGS84 = getDoubleValue(row.getCell(9));
                Double lat_WGS84 = getDoubleValue(row.getCell(10));
                String levelChar = getStringValue(row.getCell(11));
                String comment1 = getStringValue(row.getCell(12));
                String comment2 = getStringValue(row.getCell(13));
                String comment3 = getStringValue(row.getCell(14));
                String comment4 = getStringValue(row.getCell(15));
                String comment = (comment1 != null && !comment1.isEmpty() ? (comment1 + ",") : "")
                        + (comment2 != null && !comment2.isEmpty() ? (comment2 + ",") : "")
                        + (comment3 != null && !comment3.isEmpty() ? (comment3 + ",") : "")
                        + (comment4 != null && !comment4.isEmpty() ? comment4 : "");
                if (comment.endsWith(",")) {
                    comment = comment.substring(0, comment.length() - 1);
                }
                // 使用 StringBuilder 添加输出信息
                builder.append(name).append(" ")
                        .append(date).append(" ")
                        .append(epoch1).append(" ")
                        .append(epoch2).append(" ")
                        .append(lng_GCJ02 == null ? "" : Double.toString(lng_GCJ02)).append(" ")
                        .append(lat_GCJ02 == null ? "" : Double.toString(lat_GCJ02)).append(" ")
                        .append(BD09_lng == null ? "" : Double.toString(BD09_lng)).append(" ")
                        .append(BD09_lat == null ? "" : Double.toString(BD09_lat)).append(" ")
                        .append(lng_WGS84 == null ? "" : Double.toString(lng_WGS84)).append(" ")
                        .append(lat_WGS84 == null ? "" : Double.toString(lat_WGS84)).append(" ")
                        .append(levelChar).append(" ")
                        .append(comment).append("\n");

                // 将数据保存到数组中
                Object[] row_data = {name, date, epoch1, epoch2, lng_GCJ02, lat_GCJ02, BD09_lng, BD09_lat, lng_WGS84, lat_WGS84, levelChar, comment, comment1, comment2, comment3, comment4};
                data.add(row_data);
            }

            // 输出信息到控制台
            //System.out.println(builder.toString());

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(name, date, epoch1, epoch2, lng_GCJ02, lat_GCJ02, BD09_lng, BD09_lat, lng_WGS84, lat_WGS84, levelChar, description,comment1, comment2, comment3, comment4) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet1(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(1);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String name = getStringValue(row.getCell(2));
                if (name == "") break;
                Double lng_GCJ02 = getDoubleValue(row.getCell(3));
                Double lat_GCJ02 = getDoubleValue(row.getCell(4));
                Double lng_WGS84 = getDoubleValue(row.getCell(5));
                Double lat_WGS84 = getDoubleValue(row.getCell(6));
                String comment1 = getStringValue(row.getCell(8));
                String comment2 = getStringValue(row.getCell(9));
                String comment3 = getStringValue(row.getCell(10));
                String comment = (comment1 != null && !comment1.isEmpty() ? (comment1 + ",") : "")
                        + (comment2 != null && !comment2.isEmpty() ? (comment2 + ",") : "")
                        + (comment3 != null && !comment3.isEmpty() ? (comment3) : "");
                if (comment.endsWith(",")) {
                    comment = comment.substring(0, comment.length() - 1);
                }
                // 使用 StringBuilder 添加输出信息
                builder.append(name).append(" ")
                        .append(lng_GCJ02 == null ? "" : Double.toString(lng_GCJ02)).append(" ")
                        .append(lat_GCJ02 == null ? "" : Double.toString(lat_GCJ02)).append(" ")
                        .append(lng_WGS84 == null ? "" : Double.toString(lng_WGS84)).append(" ")
                        .append(lat_WGS84 == null ? "" : Double.toString(lat_WGS84)).append(" ")
                        .append(comment).append("\n");

                // 将数据保存到数组中
                Object[] row_data = {name,  lng_GCJ02, lat_GCJ02,  lng_WGS84, lat_WGS84,  comment};
                data.add(row_data);
            }

            // 输出信息到控制台
            //System.out.println(builder.toString());

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(name, lng_GCJ02, lat_GCJ02,  lng_WGS84, lat_WGS84,  description) VALUES (?, ?, ?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet2(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(2);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String level = getStringValue(row.getCell(1));
                String batch = getStringValue(row.getCell(2));
                String name = getStringValue(row.getCell(3));
                if (name == "") break;
                Double lng_GCJ02 = getDoubleValue(row.getCell(3));
                Double lat_GCJ02 = getDoubleValue(row.getCell(4));
                Double lng_WGS84 = getDoubleValue(row.getCell(5));
                Double lat_WGS84 = getDoubleValue(row.getCell(6));
                String comment1 = getStringValue(row.getCell(8));
                String comment2 = getStringValue(row.getCell(9));
                String comment3 = getStringValue(row.getCell(10));
                String comment = (comment1 != null && !comment1.isEmpty() ? (comment1 + ",") : "")
                        + (comment2 != null && !comment2.isEmpty() ? (comment2 + ",") : "")
                        + (comment3 != null && !comment3.isEmpty() ? (comment3) : "");
                if (comment.endsWith(",")) {
                    comment = comment.substring(0, comment.length() - 1);
                }
                // 使用 StringBuilder 添加输出信息
                builder.append(name).append(" ")
                        .append(level).append(" ")
                        .append(batch).append(" ")
                        .append(lng_GCJ02 == null ? "" : Double.toString(lng_GCJ02)).append(" ")
                        .append(lat_GCJ02 == null ? "" : Double.toString(lat_GCJ02)).append(" ")
                        .append(lng_WGS84 == null ? "" : Double.toString(lng_WGS84)).append(" ")
                        .append(lat_WGS84 == null ? "" : Double.toString(lat_WGS84)).append(" ")
                        .append(comment).append("\n");

                // 将数据保存到数组中
                Object[] row_data = {level, batch, name,  lng_GCJ02, lat_GCJ02,  lng_WGS84, lat_WGS84,  comment};
                data.add(row_data);
            }

            // 输出信息到控制台
            //System.out.println(builder.toString());

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(level, batch, name, lng_GCJ02, lat_GCJ02,  lng_WGS84, lat_WGS84,  description) VALUES (?,?,?, ?, ?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet3(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(3);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String name = getStringValue(row.getCell(1));
                if (name == "") break;
                Double BD09_lng = getDoubleValue(row.getCell(2));
                Double BD09_lat = getDoubleValue(row.getCell(3));
                Double lng_WGS84 = getDoubleValue(row.getCell(4));
                Double lat_WGS84 = getDoubleValue(row.getCell(5));
                String clazz = getStringValue(row.getCell(6));
                String address = getStringValue(row.getCell(7));

                // 将数据保存到数组中
                Object[] row_data = {name,BD09_lng,BD09_lat,lng_WGS84, lat_WGS84, clazz, address};
                data.add(row_data);
            }

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(name, BD09_lng, BD09_lat, lng_WGS84, lat_WGS84,  class,  address) VALUES (?,?,?, ?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet4(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(4);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String name = getStringValue(row.getCell(1));
                if (name == "") break;
                Double BD09_lng = getDoubleValue(row.getCell(2));
                Double BD09_lat = getDoubleValue(row.getCell(3));
                Double lng_WGS84 = getDoubleValue(row.getCell(4));
                Double lat_WGS84 = getDoubleValue(row.getCell(5));
                String clazz = getStringValue(row.getCell(6));
                String address = getStringValue(row.getCell(7));

                // 将数据保存到数组中
                Object[] row_data = {name,BD09_lng,BD09_lat,lng_WGS84, lat_WGS84, clazz, address};
                data.add(row_data);
            }

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(name, BD09_lng, BD09_lat, lng_WGS84, lat_WGS84,  class,  address) VALUES (?,?,?, ?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet5(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(5);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String name = getStringValue(row.getCell(2));
                if (name == "") break;
                Double lng_GCJ02 = getDoubleValue(row.getCell(4));
                Double lat_GCJ02 = getDoubleValue(row.getCell(5));
                Double lng_WGS84 = getDoubleValue(row.getCell(6));
                Double lat_WGS84 = getDoubleValue(row.getCell(7));
                String comment1 = getStringValue(row.getCell(8));
                String comment2 = getStringValue(row.getCell(9));
                String comment3 = getStringValue(row.getCell(10));
                String comment4 = getStringValue(row.getCell(11));
                String comment = (comment1 != null && !comment1.isEmpty() ? (comment1 + ",") : "")
                        + (comment2 != null && !comment2.isEmpty() ? (comment2 + ",") : "")
                        + (comment3 != null && !comment3.isEmpty() ? (comment3 + ",") : "")
                        + (comment4 != null && !comment4.isEmpty() ? comment4 : "");
                if (comment.endsWith(",")) {
                    comment = comment.substring(0, comment.length() - 1);
                }

                // 将数据保存到数组中
                Object[] row_data = {name,lng_GCJ02,lat_GCJ02,lng_WGS84, lat_WGS84, comment};
                data.add(row_data);
            }

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(name, lng_GCJ02, lat_GCJ02, lng_WGS84, lat_WGS84, description) VALUES (?,?,?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet6(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(6);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String clazz = getStringValue(row.getCell(1));
                String name = getStringValue(row.getCell(2));
                if (name == "") break;
                Double lng_GCJ02 = getDoubleValue(row.getCell(4));
                Double lat_GCJ02 = getDoubleValue(row.getCell(5));
                Double lng_WGS84 = getDoubleValue(row.getCell(6));
                Double lat_WGS84 = getDoubleValue(row.getCell(7));
                String comment = getStringValue(row.getCell(8));

                // 将数据保存到数组中
                Object[] row_data = {clazz,name,lng_GCJ02,lat_GCJ02,lng_WGS84, lat_WGS84, comment};
                data.add(row_data);
            }

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(class, name, lng_GCJ02, lat_GCJ02, lng_WGS84, lat_WGS84, description) VALUES (?,?,?,?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importSheet7(String table){
        try {
            //读取当前目录下的博物馆信息文件
            FileInputStream file = new FileInputStream(new File("src/main/java/com/example/gaodejava/数据汇总分级分类.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(7);
            // 迭代行
            // 定义数组保存所有数据
            List<Object[]> data = new ArrayList<>();

            // 新建 StringBuilder 对象
            StringBuilder builder = new StringBuilder();

            for (Row row : sheet) {
                // 跳过第一行
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 读取单元格的值
                String clazz = getStringValue(row.getCell(1));
                String name = getStringValue(row.getCell(2));
                if (name == "") break;
                Double lng_GCJ02 = getDoubleValue(row.getCell(4));
                Double lat_GCJ02 = getDoubleValue(row.getCell(5));
                Double lng_WGS84 = getDoubleValue(row.getCell(6));
                Double lat_WGS84 = getDoubleValue(row.getCell(7));
                String comment = getStringValue(row.getCell(8));

                // 将数据保存到数组中
                Object[] row_data = {clazz,name,lng_GCJ02,lat_GCJ02,lng_WGS84, lat_WGS84, comment};
                data.add(row_data);
            }

            // 将数据批量插入到数据库中
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT IGNORE INTO "+ table +"(class, name, lng_GCJ02, lat_GCJ02, lng_WGS84, lat_WGS84, description) VALUES (?,?,?,?, ?, ?, ?)")) {
                conn.setAutoCommit(false);
                for (Object[] row_data : data) {
                    // 为每个参数设置值
                    for (int i = 1; i <= row_data.length; i++) {
                        pstmt.setObject(i, row_data[i - 1]);
                    }
                    // 添加批处理
                    pstmt.addBatch();
                }
                // 批量插入数据
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                // 处理异常
                e.printStackTrace();
            }

            // 关闭工作簿
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gaode";
        String user = "root";
        String password = "hanlu1997";
        return DriverManager.getConnection(url, user, password);
    }


}
