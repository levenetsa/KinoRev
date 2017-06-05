CREATE TABLE `i_reviews` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `film_id` int(11) NOT NULL,
  `content` varchar(20000) DEFAULT NULL,
  `mood` enum('positive','negative','neutral','nothing') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `reviews_film_id_index` (`film_id`),
  CONSTRAINT `reviews___fk___reviews` FOREIGN KEY (`film_id`) REFERENCES `films` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;