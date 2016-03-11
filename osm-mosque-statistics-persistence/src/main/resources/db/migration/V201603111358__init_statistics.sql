CREATE TABLE `statistics`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,

  PRIMARY KEY(`id`)
)
DEFAULT CHARSET=utf8;

ALTER TABLE `statistics` add `valid` bit(1) DEFAULT NULL;
