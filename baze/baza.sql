CREATE DATABASE  IF NOT EXISTS `projekatis` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `projekatis`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: projekatis
-- ------------------------------------------------------
-- Server version	5.6.40-log

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
-- Table structure for table `dogadjaj`
--

DROP TABLE IF EXISTS `dogadjaj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dogadjaj` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATUM` date DEFAULT NULL,
  `DESTINACIJA` varchar(255) DEFAULT NULL,
  `OPIS` varchar(255) DEFAULT NULL,
  `PODSETI` int(11) DEFAULT NULL,
  `SDATUM` date DEFAULT NULL,
  `SVREME` time DEFAULT NULL,
  `VREME` time DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dogadjaj`
--

LOCK TABLES `dogadjaj` WRITE;
/*!40000 ALTER TABLE `dogadjaj` DISABLE KEYS */;
INSERT INTO `dogadjaj` VALUES (2,'2019-06-05','kragujevac','kosarka',0,'2019-06-05','18:30:00','16:49:00'),(4,'2019-06-04','beograd','fakultet',1,'2019-06-04','09:30:00','09:30:00'),(6,'2019-06-06',NULL,'zubar',1,'2019-06-06','16:00:00','16:00:00'),(19,'2019-06-04',NULL,'parkic',1,'2019-06-04','22:49:00','22:49:00'),(21,'2019-06-05','beograd','ples',0,'2019-06-05','21:00:00','21:00:00'),(22,'2019-06-05',NULL,'klub',1,'2019-06-05','23:00:00','23:00:00'),(23,'2019-06-06','novi sad','izlet',1,'2019-06-06','14:00:00','12:41:00'),(24,'2019-06-06',NULL,'kupovina',1,'2019-06-06','18:00:00','18:00:00'),(25,'2019-06-06','beograd','lab',1,'2019-06-06','17:30:00','17:30:00'),(28,'2019-06-06','beograd','setnja',1,'2019-06-06','20:15:00','20:15:00');
/*!40000 ALTER TABLE `dogadjaj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pesma`
--

DROP TABLE IF EXISTS `pesma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pesma` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `link` varchar(1000) NOT NULL,
  `melodija` int(11) DEFAULT NULL,
  `pretraga` varchar(500) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pesma`
--

LOCK TABLES `pesma` WRITE;
/*!40000 ALTER TABLE `pesma` DISABLE KEYS */;
INSERT INTO `pesma` VALUES (1,'https://www.youtube.com/watch?v=hKY4UpQLpyQ',0,'sankeru'),(3,'https://www.youtube.com/watch?v=Yz9P4Lnulw0',0,'ti si jedina od svih'),(4,'https://www.youtube.com/watch?v=lzk-86GF3ZE',0,'dodirni mi kolena'),(8,'www.youtube.com/watch?v=MsLSDaiX6mY',0,'rane'),(9,'https://www.youtube.com/watch?v=PCrNORbMuR0',1,'uzicko kolo');
/*!40000 ALTER TABLE `pesma` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence`
--

LOCK TABLES `sequence` WRITE;
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` VALUES ('SEQ_GEN',100);
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zvono`
--

DROP TABLE IF EXISTS `zvono`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zvono` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `AKTIVAN` int(11) DEFAULT NULL,
  `DATUM` date DEFAULT NULL,
  `PONAVLJA_SE` int(11) DEFAULT NULL,
  `VREME` time DEFAULT NULL,
  `dog` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ZVONO_dog` (`dog`),
  CONSTRAINT `FK_ZVONO_dog` FOREIGN KEY (`dog`) REFERENCES `dogadjaj` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zvono`
--

LOCK TABLES `zvono` WRITE;
/*!40000 ALTER TABLE `zvono` DISABLE KEYS */;
INSERT INTO `zvono` VALUES (3,1,NULL,1,'13:10:00',NULL),(4,1,NULL,0,'13:15:00',NULL),(6,1,'2019-06-06',0,'16:00:00',6),(13,1,NULL,1,'22:48:00',NULL),(14,0,NULL,0,'22:50:00',NULL),(15,0,'2019-06-04',0,'22:49:00',19),(18,1,'2019-06-04',0,'09:30:00',4),(21,1,NULL,1,'20:25:00',NULL),(22,1,'2019-06-05',0,'23:00:00',22),(23,1,'2019-06-06',0,'12:41:00',23),(24,1,'2019-06-06',0,'18:00:00',24),(25,1,'2019-06-06',0,'17:30:00',25),(28,1,'2019-06-06',0,'20:15:00',28);
/*!40000 ALTER TABLE `zvono` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-06-05 20:52:43
