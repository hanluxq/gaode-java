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
        //const createTableQuery = 'CREATE TABLE IF NOT EXISTS points(id INT(11) NOT NULL AUTO_INCREMENT,lng FLOAT NOT NULL,lat FLOAT NOT NULL,label VARCHAR(255) NOT NULL,PRIMARY KEY (id))';
        const createTableQuery = 'points CREATE TABLE `points` (\n' +
            '  `id` int NOT NULL AUTO_INCREMENT,\n' +
            '  `lng` float NOT NULL,\n' +
            '  `lat` float NOT NULL,\n' +
            '  `label` varchar(255) NOT NULL,\n' +
            '  `description` varchar(255) DEFAULT NULL,\n' +
            '  `images` longblob,\n' +
            '  PRIMARY KEY (`id`),\n' +
            '  UNIQUE KEY `label` (`label`)\n' +
            ') ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci';
        connection.query(createTableQuery);
        //创建折线表
        const createPolylineTableQuery = 'CREATE TABLE IF NOT EXISTS polylines(id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,sta_lng FLOAT NOT NULL,sta_lat FLOAT NOT NULL,end_lng FLOAT NOT NULL,end_lat FLOAT NOT NULL),UNIQUE KEY (sta_lng, sta_lat,end_lng,end_lat)';
        connection.query(createPolylineTableQuery);
        console.log('Database and table successfully created.');
    } catch (error) {
        console.error(error);
    } finally {
        // 关闭连接
        connection.end();
    }
}

init();