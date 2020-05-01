# noinspection SqlNoDataSourceInspectionForFile

CREATE SCHEMA osm_mosques CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
GRANT ALL PRIVILEGES ON osm_mosques.* TO 'osm_mosques'@'localhost' IDENTIFIED BY 'osm_mosques';
GRANT ALL PRIVILEGES ON osm_mosques.* TO 'osm_mosques'@'%' IDENTIFIED BY 'osm_mosques';
