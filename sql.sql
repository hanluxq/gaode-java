
CREATE TABLE `museum` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `lng` double(12,8) NOT NULL,
  `lat` double(12,8) NOT NULL,
  `level` varchar(255),
  `levelChar` varchar(255),
  `epoch1` varchar(255),
  `epoch2` varchar(255),
  `class` varchar(255),
  `address` varchar(255),
  `date` varchar(255),
  `description` varchar(255),
  `images` longblob,
  PRIMARY KEY (`lng`,`lat`),
  UNIQUE KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


use gaode;



select * from museum;

drop table museum;

delete from museum;

