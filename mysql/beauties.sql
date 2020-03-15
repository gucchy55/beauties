-- MySQL dump 10.13  Distrib 5.7.28, for osx10.14 (x86_64)
--
-- Host: localhost    Database: beauties
-- ------------------------------------------------------
-- Server version	5.7.28

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
-- Table structure for table `cbm_book`
--

DROP TABLE IF EXISTS `cbm_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbm_book` (
  `BOOK_ID` int(10) NOT NULL AUTO_INCREMENT,
  `BOOK_NAME` varchar(50) NOT NULL,
  `BALANCE` int(11) DEFAULT NULL,
  `INCLUDE_FLG` bit(1) NOT NULL DEFAULT b'1',
  `SORT_KEY` int(10) NOT NULL DEFAULT '9999',
  `DEL_FLG` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`BOOK_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbm_category`
--

DROP TABLE IF EXISTS `cbm_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbm_category` (
  `CATEGORY_ID` int(10) NOT NULL AUTO_INCREMENT,
  `CATEGORY_NAME` varchar(50) NOT NULL,
  `DEL_FLG` bit(1) NOT NULL DEFAULT b'0',
  `REXP_DIV` int(10) NOT NULL DEFAULT '1',
  `SORT_KEY` int(10) NOT NULL DEFAULT '0',
  `SPECIAL_FLG` bit(1) DEFAULT b'0',
  `TEMP_FLG` bit(1) DEFAULT b'0',
  PRIMARY KEY (`CATEGORY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbm_item`
--

DROP TABLE IF EXISTS `cbm_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbm_item` (
  `CATEGORY_ID` int(10) NOT NULL,
  `DEL_FLG` bit(1) NOT NULL DEFAULT b'0',
  `ITEM_ID` int(10) NOT NULL AUTO_INCREMENT,
  `ITEM_NAME` varchar(50) NOT NULL,
  `MOVE_FLG` bit(1) NOT NULL DEFAULT b'0',
  `SORT_KEY` int(10) NOT NULL DEFAULT '0',
  `TARGET_VALUE` double(17,0) DEFAULT '0',
  PRIMARY KEY (`ITEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=180 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbr_book`
--

DROP TABLE IF EXISTS `cbr_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbr_book` (
  `BOOK_ID` int(10) NOT NULL,
  `DEL_FLG` bit(1) NOT NULL DEFAULT b'0',
  `ITEM_ID` int(10) NOT NULL,
  PRIMARY KEY (`BOOK_ID`,`ITEM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbt_act`
--

DROP TABLE IF EXISTS `cbt_act`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbt_act` (
  `ACT_DT` datetime NOT NULL,
  `ACT_ID` int(10) NOT NULL AUTO_INCREMENT,
  `BOOK_ID` int(10) NOT NULL,
  `DEL_FLG` bit(1) NOT NULL DEFAULT b'0',
  `EXPENSE` int(11) DEFAULT NULL,
  `FREQUENCY` int(10) DEFAULT NULL,
  `GROUP_ID` int(10) NOT NULL DEFAULT '0',
  `INCOME` int(11) DEFAULT NULL,
  `ITEM_ID` int(10) NOT NULL,
  `NOTE_NAME` mediumtext,
  `time_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ACT_ID`),
  KEY `BOOK_ID` (`BOOK_ID`,`ACT_DT`,`GROUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=82046 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cbt_note`
--

DROP TABLE IF EXISTS `cbt_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cbt_note` (
  `DEL_FLG` bit(1) NOT NULL DEFAULT b'0',
  `ITEM_ID` int(10) NOT NULL,
  `NOTE_ID` int(10) NOT NULL AUTO_INCREMENT,
  `NOTE_NAME` mediumtext NOT NULL,
  PRIMARY KEY (`NOTE_ID`),
  KEY `ITEM_ID` (`ITEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=50188 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `setting`
--

DROP TABLE IF EXISTS `setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `setting` (
  `CHAR_VALUE` varchar(50) DEFAULT NULL,
  `DATE_VALUE` datetime DEFAULT NULL,
  `DEL_FLG` bit(1) NOT NULL,
  `EXPLANATION` varchar(50) DEFAULT NULL,
  `NUM_VALUE` double(15,0) DEFAULT NULL,
  `SID` varchar(50) NOT NULL,
  `SNAME` varchar(50) NOT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-17 15:09:39
