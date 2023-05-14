package com.example.gaodejava;

import com.example.gaodejava.mapper.lineMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;

public class line {

    private Integer id;
    private Double sta_lng;
    private Double sta_lat;
    private Double end_lng;
    private Double end_lat;

    public List<line> selectAll() throws IOException {
        // 1. 创建 SqlSessionFactory 对象
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 2. 创建 SqlSession 对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        lineMapper mapper = sqlSession.getMapper(lineMapper.class);

        // 3. 获取标记数据
        List<line> lines = mapper.selectAll();

        // 4. 关闭 SqlSession 对象
        sqlSession.close();

        //5. 返回标记数据
        return lines;
    }

    @Override
    public String toString() {
        return "line{" +
                "id=" + id +
                ", sta_lng=" + sta_lng +
                ", sta_lat=" + sta_lat +
                ", end_lng=" + end_lng +
                ", end_lat=" + end_lat +
                '}';
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
        lineMapper lineMapper = sqlSession.getMapper(lineMapper.class);
        List<line> lines = lineMapper.selectAll();
        System.out.println(lines);

        //4.释放资源
        sqlSession.close();
    }
}
