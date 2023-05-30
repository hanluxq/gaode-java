CREATE TABLE `museum` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`batch` varchar(255),
	`lng_GCJ02` double(12,8) ,
  `lat_GCJ02` double(12,8) ,
  `BD09_lng` double(12,8) ,
  `BD09_lat` double(12,8) ,
  `lng_WGS84` double(12,8) ,
  `lat_WGS84` double(12,8) ,
  `level` varchar(255),
  `levelChar` varchar(255),
  `epoch1` varchar(255),
  `epoch2` varchar(255),
  `class` varchar(255),
  `address` varchar(255),
  `date` varchar(255),
  `description` varchar(255),
  `images` longblob,
  PRIMARY KEY (`name`),
  UNIQUE KEY (`id`),
  UNIQUE (lng_GCJ02, lat_GCJ02),
  UNIQUE (BD09_lng, BD09_lat),
  UNIQUE (lng_WGS84, lat_WGS84)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

use gaode;
CREATE TABLE `fenjifenlei` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`batch` varchar(255),
	`lng_GCJ02` double(12,8) ,
  `lat_GCJ02` double(12,8) ,
  `BD09_lng` double(12,8) ,
  `BD09_lat` double(12,8) ,
  `lng_WGS84` double(12,8) ,
  `lat_WGS84` double(12,8) ,
  `level` varchar(255),
  `levelChar` varchar(255),
  `epoch1` varchar(255),
  `epoch2` varchar(255),
  `class` varchar(255),
  `address` varchar(255),
  `date` varchar(255),
  `description` varchar(255),
  `images` longblob,
  PRIMARY KEY (`name`),
  UNIQUE KEY (`id`),
  UNIQUE (lng_GCJ02, lat_GCJ02),
  UNIQUE (BD09_lng, BD09_lat),
  UNIQUE (lng_WGS84, lat_WGS84)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
select * from fenjifenlei;



CREATE TABLE `polylines` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `sta_lng` float NOT NULL,
                           `sta_lat` float NOT NULL,
                           `end_lng` float NOT NULL,
                           `end_lat` float NOT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `sta_lng` (`sta_lng`,`sta_lat`,`end_lng`,`end_lat`))
  ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
  
  
use gaode;

mysqldump -u root -p gaode > gaode.sql;



use gaode;
drop table fenjifenlei;
CREATE TABLE `fenjifenlei` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `batch` varchar(255),
  `lng_GCJ02` double(12,8) ,
  `lat_GCJ02` double(12,8) ,
  `BD09_lng` double(12,8) ,
  `BD09_lat` double(12,8) ,
  `lng_WGS84` double(12,8) ,
  `lat_WGS84` double(12,8) ,
  `level` varchar(255),
  `levelChar` varchar(255),
  `comment1` varchar(255),
  `comment2` varchar(255),
  `comment3` varchar(255),
  `comment4` varchar(255),
  `epoch1` varchar(255),
  `epoch2` varchar(255),
  `class` varchar(255),
  `address` varchar(255),
  `date` varchar(255),
  `description` varchar(255),
  `images` longblob,
  PRIMARY KEY (`name`),
  UNIQUE KEY (`id`),
  UNIQUE (lng_GCJ02, lat_GCJ02),
  UNIQUE (BD09_lng, BD09_lat),
  UNIQUE (lng_WGS84, lat_WGS84)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
select * from fenjifenlei;



SELECT * FROM fenjifenlei WHERE comment4 IS NOT NULL AND comment4 != '';