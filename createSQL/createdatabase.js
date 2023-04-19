const mysql = require('mysql');
const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'hanlu1997',
});

function init(){
    try {
        // 如果不存在则创建数据库
        connection.query('CREATE DATABASE IF NOT EXISTS gaode');

        // 选择要使用的数据库
        connection.query('USE gaode');
        // 如果不存在则创建标点表
        const createTableQuery = 'CREATE TABLE IF NOT EXISTS points(id INT(11) NOT NULL AUTO_INCREMENT,lng FLOAT NOT NULL,lat FLOAT NOT NULL,label VARCHAR(255) NOT NULL,PRIMARY KEY (id))';
        connection.query(createTableQuery);
        console.log('Database and table successfully created.');
        //添加一列description
        connection.query('ALTER TABLE points ADD COLUMN description VARCHAR(255) DEFAUT "暂无描述"');
        //添加一列images
        connection.query('ALTER TABLE points ADD COLUMN images LONGBLOB');
        //创建折线表
        const createPolylineTableQuery = 'CREATE TABLE IF NOT EXISTS polylines(id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,sta_lng FLOAT NOT NULL,sta_lat FLOAT NOT NULL,end_lng FLOAT NOT NULL,end_lat FLOAT NOT NULL),UNIQUE KEY (sta_lng, sta_lat,end_lng,end_lat)';
        connection.query(createPolylineTableQuery);
    } catch (error) {
        console.error(error);
    } finally {
        // 关闭连接
        connection.end();
    }
}

init();