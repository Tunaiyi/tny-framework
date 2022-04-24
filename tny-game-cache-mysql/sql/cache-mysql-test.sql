-- MySQL dump 10.13  Distrib 5.6.19, for osx10.7 (i386)
--
-- Host: 127.0.0.1    Database: kv_test
-- ------------------------------------------------------
-- Server version	5.6.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `CPlayer0`
--

DROP TABLE IF EXISTS `CPlayer0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer0`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer1`
--

DROP TABLE IF EXISTS `CPlayer1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer1`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer2`
--

DROP TABLE IF EXISTS `CPlayer2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer2`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer3`
--

DROP TABLE IF EXISTS `CPlayer3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer3`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer4`
--

DROP TABLE IF EXISTS `CPlayer4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer4`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer5`
--

DROP TABLE IF EXISTS `CPlayer5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer5`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer6`
--

DROP TABLE IF EXISTS `CPlayer6`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer6`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer7`
--

DROP TABLE IF EXISTS `CPlayer7`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer7`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer8`
--

DROP TABLE IF EXISTS `CPlayer8`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer8`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CPlayer9`
--

DROP TABLE IF EXISTS `CPlayer9`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CPlayer9`
(
    `key`     varchar(200) NOT NULL,
    `flags`   int(11) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    `saveAt`  bigint(20) NOT NULL,
    PRIMARY KEY (`key`),
    KEY       `index_flags` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UpObject`
--

DROP TABLE IF EXISTS `UpObject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UpObject`
(
    `id`     int(11) NOT NULL,
    `name`   varchar(45) NOT NULL,
    `age`    tinyint(4) NOT NULL,
    `gender` tinyint(4) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP PROCEDURE IF EXISTS `createTablesByName`;
DELIMITER
$$

CREATE PROCEDURE `createTablesByName`(tableName varchar (45))
BEGIN
--    set @delSql = concat('DROP TABLE IF EXISTS `', tableName, '`;');
--    PREPARE delTable from  @delSql;
--    EXECUTE delTable;
    
    set
@crtSql = concat(
        'create table `', tableName, '`',
        ' (`key` varchar(200) NOT NULL,',
        '`flags` int NOT NULL,',
        '`data` mediumblob,',
        '`expire` bigint(20) NOT NULL,',
        '`version` bigint(20) NOT NULL DEFAULT "0",',
        '`saveAt` bigint(20) NOT NULL,',
        'PRIMARY KEY (`key`)',
        ') ENGINE=InnoDB DEFAULT CHARSET=utf8');
PREPARE crtTable from @crtSql;
EXECUTE crtTable;
END;

$$

