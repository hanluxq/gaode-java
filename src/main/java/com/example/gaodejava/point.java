package com.example.gaodejava;

import com.example.gaodejava.mapper.museumMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;

public class point {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLng_GCJ02() {
        return lng_GCJ02;
    }

    public void setLng_GCJ02(Double lng_GCJ02) {
        this.lng_GCJ02 = lng_GCJ02;
    }

    public Double getLat_GCJ02() {
        return lat_GCJ02;
    }

    public void setLat_GCJ02(Double lat_GCJ02) {
        this.lat_GCJ02 = lat_GCJ02;
    }

    public Double getBD09_lng() {
        return BD09_lng;
    }

    public void setBD09_lng(Double BD09_lng) {
        this.BD09_lng = BD09_lng;
    }

    public Double getBD09_lat() {
        return BD09_lat;
    }

    public void setBD09_lat(Double BD09_lat) {
        this.BD09_lat = BD09_lat;
    }

    public Double getLng_WGS84() {
        return lng_WGS84;
    }

    public void setLng_WGS84(Double lng_WGS84) {
        this.lng_WGS84 = lng_WGS84;
    }

    public Double getLat_WGS84() {
        return lat_WGS84;
    }

    public void setLat_WGS84(Double lat_WGS84) {
        this.lat_WGS84 = lat_WGS84;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelChar() {
        return levelChar;
    }

    public void setLevelChar(String levelChar) {
        this.levelChar = levelChar;
    }

    public String getEpoch1() {
        return epoch1;
    }

    public void setEpoch1(String epoch1) {
        this.epoch1 = epoch1;
    }

    public String getEpoch2() {
        return epoch2;
    }

    public void setEpoch2(String epoch2) {
        this.epoch2 = epoch2;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImages() {
        return images;
    }

    public void setImages(Blob images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "point{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lng_GCJ02=" + lng_GCJ02 +
                ", lat_GCJ02=" + lat_GCJ02 +
                ", BD09_lng=" + BD09_lng +
                ", BD09_lat=" + BD09_lat +
                ", lng_WGS84=" + lng_WGS84 +
                ", lat_WGS84=" + lat_WGS84 +
                ", level='" + level + '\'' +
                ", levelChar='" + levelChar + '\'' +
                ", epoch1='" + epoch1 + '\'' +
                ", epoch2='" + epoch2 + '\'' +
                ", clazz='" + clazz + '\'' +
                ", address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", images=" + images +
                '}';
    }

    private Integer id;
    private String name;
    private Double lng_GCJ02;
    private Double lat_GCJ02;
    private Double BD09_lng;
    private Double BD09_lat;
    private Double lng_WGS84;
    private Double lat_WGS84;
    private String level;
    private String levelChar;
    private String epoch1;
    private String epoch2;
    private String clazz;
    private String address;
    private String date;
    private String description;
    private Blob images;

    public List<point> selectAll() throws IOException {
        // 1. 创建 SqlSessionFactory 对象
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 2. 创建 SqlSession 对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        museumMapper mapper = sqlSession.getMapper(museumMapper.class);

        // 3. 获取标记数据
        List<point> points = mapper.selectAll();

        // 4. 关闭 SqlSession 对象
        sqlSession.close();

        //5. 返回标记数据
        return points;
    }

    public static void main(String[] args) throws IOException {
        //1.加载配置，获取factory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        //2.获取session对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3.执行sql语句
//        List<point> points = sqlSession.selectList("pointMapper.selectAll");
        //代理开发
        //3.1 获取usermapper接口的代理对象
        museumMapper museumMapper = sqlSession.getMapper(museumMapper.class);
        List<point> points = museumMapper.selectAll();
        System.out.println(points);

        //4.释放资源
        sqlSession.close();

    }
}
