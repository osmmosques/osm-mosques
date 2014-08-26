CREATE TABLE `qa_places`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,

  PRIMARY KEY(`id`)
)
DEFAULT CHARSET=utf8;

ALTER TABLE `qa_places` add `valid` bit(1) DEFAULT NULL;

ALTER TABLE `qa_places` add `ditib_code` varchar(20) DEFAULT NULL;
ALTER TABLE `qa_places` add `cagedata_key` varchar(20) DEFAULT NULL;
ALTER TABLE `qa_places` add `osm_id` varchar(20) DEFAULT NULL;
