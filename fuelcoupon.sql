-- MySQL dump 10.13  Distrib 8.0.20, for macos10.15 (x86_64)
--
-- Host: localhost    Database: fuelcoupon
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `carton`
--

DROP TABLE IF EXISTS `carton`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carton` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `id_store_house` bigint DEFAULT NULL,
  `id_store_keeper` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `serial_number` varchar(255) NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6pt471ly9g8rm3micdh9ecsnp` (`internal_reference`),
  UNIQUE KEY `UK_6lhdm9qw3mtfkf8n94rqa2dfd` (`serial_number`),
  KEY `FK66g4868lnn65s3k4duuf2qaee` (`status_id`),
  CONSTRAINT `FK66g4868lnn65s3k4duuf2qaee` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carton`
--

LOCK TABLES `carton` WRITE;
/*!40000 ALTER TABLE `carton` DISABLE KEYS */;
/*!40000 ALTER TABLE `carton` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rccm` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `complete_name` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gulfcam_account_number` varchar(255) DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `type_client_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_bfgjs3fem0hmjhvih80158x29` (`email`),
  UNIQUE KEY `UK_938vsg7lti311cp1l9ieoquau` (`internal_reference`),
  UNIQUE KEY `UK_rfgp2wknf07r4xfx2dcddvplw` (`gulfcam_account_number`),
  KEY `FKp0a3n51hy6x2psr7e2acc4xlt` (`type_client_id`),
  CONSTRAINT `FKp0a3n51hy6x2psr7e2acc4xlt` FOREIGN KEY (`type_client_id`) REFERENCES `type_client` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (1,'frij458','PK 13','LABRED ','Arnold KOM je','2022-11-02 17:02:08','arnoldkom55@gmail.com','1234',3836174,'6904738492','2022-11-02 17:14:30',2);
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `id_client` bigint DEFAULT NULL,
  `id_notebook` bigint DEFAULT NULL,
  `id_station` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `serial_number` varchar(255) NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  `id_ticket` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_5cjbsdwi9elqkvt66uk2x2jvm` (`internal_reference`),
  UNIQUE KEY `UK_pe7cr4yk2t5reoqgfti38gtku` (`serial_number`),
  KEY `FK8k21mk3l7ebh9ctkbsoipe3un` (`status_id`),
  CONSTRAINT `FK8k21mk3l7ebh9ctkbsoipe3un` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (1);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `id_store_house` bigint DEFAULT NULL,
  `id_type_voucher` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `quantity_carton` int NOT NULL,
  `quantity_notebook` int NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sqc8ip03ij0f6a66pqnaii32o` (`internal_reference`),
  KEY `FKa5r9sug9g4f4kif98va9ro3l2` (`status_id`),
  CONSTRAINT `FKa5r9sug9g4f4kif98va9ro3l2` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notebook`
--

DROP TABLE IF EXISTS `notebook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notebook` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `id_carton` bigint DEFAULT NULL,
  `id_store_keeper` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `serial_number` varchar(255) NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2mo8a7seciqm0h8cjj7hu6o87` (`internal_reference`),
  UNIQUE KEY `UK_23f45tax2jukr0f891c80v4c9` (`serial_number`),
  KEY `FK1nwhwuuhy0m4g0i9mq9ea0jlq` (`status_id`),
  CONSTRAINT `FK1nwhwuuhy0m4g0i9mq9ea0jlq` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notebook`
--

LOCK TABLES `notebook` WRITE;
/*!40000 ALTER TABLE `notebook` DISABLE KEYS */;
/*!40000 ALTER TABLE `notebook` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `old_password`
--

DROP TABLE IF EXISTS `old_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `old_password` (
  `old_password_id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`old_password_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `old_password`
--

LOCK TABLES `old_password` WRITE;
/*!40000 ALTER TABLE `old_password` DISABLE KEYS */;
INSERT INTO `old_password` VALUES (1,'$2a$10$7O.jGHR4uq8G/JhTZbvaM.0B4w4eksv7tSY6ff1P8iOo87YxW3xzm');
/*!40000 ALTER TABLE `old_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_documents`
--

DROP TABLE IF EXISTS `order_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_documents` (
  `document_id` bigint NOT NULL AUTO_INCREMENT,
  `document_format` varchar(255) DEFAULT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `id_order` bigint NOT NULL,
  `type_id` bigint DEFAULT NULL,
  PRIMARY KEY (`document_id`),
  KEY `FKjjrbuanpij0dadsc5nk8aswws` (`type_id`),
  CONSTRAINT `FKjjrbuanpij0dadsc5nk8aswws` FOREIGN KEY (`type_id`) REFERENCES `type_document` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_documents`
--

LOCK TABLES `order_documents` WRITE;
/*!40000 ALTER TABLE `order_documents` DISABLE KEYS */;
INSERT INTO `order_documents` VALUES (1,'application/pdf','PDF','5989071_pdf.pdf',5989071,1),(2,'application/pdf','pdf','5989071_pdf.pdf',5989071,2);
/*!40000 ALTER TABLE `order_documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_payment_method` bigint DEFAULT NULL,
  `net_aggregate_amount` int DEFAULT NULL,
  `ttc_aggregatea_amount` int DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `client_reference` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `delivery_time` varchar(255) DEFAULT NULL,
  `description` longtext,
  `id_client` bigint DEFAULT NULL,
  `id_fund` bigint DEFAULT NULL,
  `id_manager_coupon` bigint DEFAULT NULL,
  `id_manager_store` bigint DEFAULT NULL,
  `id_store` bigint NOT NULL,
  `id_storekeeper` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `payment_reference` varchar(255) DEFAULT NULL,
  `reason_for_cancellation` longtext,
  `tax` varchar(255) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  `id_manager_order` bigint DEFAULT NULL,
  `link_invoice` varchar(255) DEFAULT NULL,
  `link_delivery` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_efut83q4hpcs7bana0k6ifydq` (`internal_reference`),
  UNIQUE KEY `UK_3juxhhs8ty8vs5f2v8dkqp2k9` (`client_reference`),
  KEY `FKjiqtdoncm83ebe7alrdhcqda7` (`status_id`),
  CONSTRAINT `FKjiqtdoncm83ebe7alrdhcqda7` FOREIGN KEY (`status_id`) REFERENCES `status_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (6,8843085,120000,123600,'Mail',12345693,'2022-11-07 12:50:14','2 jours','RIEN DASN LA DESCRIPTION',3836174,3314771,3314771,NULL,123456,NULL,5989071,'eadna89as7dyas7n','PAS BESOIN DE DESCRIPTION','3%',NULL,4,3314771,'http://localhost:8080/api/v1.0/order/file/5989071/downloadFile?type=invoice&docType=PDF','http://localhost:8080/api/v1.0/order/file/5989071/downloadFile?type=delivery&docType=pdf');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mk80kemq3lk9lymwrq6wu32x6` (`internal_reference`),
  KEY `FKrfrxi6rwsbaua5ur2d3newkm5` (`status_id`),
  CONSTRAINT `FKrfrxi6rwsbaua5ur2d3newkm5` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
INSERT INTO `payment_method` VALUES (1,'2022-11-05 11:27:03','CARTE VISA',8843085,NULL,NULL);
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `id_order` bigint DEFAULT NULL,
  `id_type_voucher` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `quantity_notebook` int NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mpkwqjdbq7k7ldumyaohwslfl` (`internal_reference`),
  KEY `FK8556hocjcb04st51nt8yknfbg` (`status_id`),
  CONSTRAINT `FK8556hocjcb04st51nt8yknfbg` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'2022-11-27 16:57:03',5989071,7353902,6070266,2,NULL,1),(2,'2022-11-27 16:57:23',5989071,1848705,3906703,1,NULL,1),(3,'2022-11-27 16:57:44',5989071,1763247,7514598,14,NULL,1);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_opposition`
--

DROP TABLE IF EXISTS `request_opposition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_opposition` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `description` longtext,
  `id_manager_coupon` bigint DEFAULT NULL,
  `id_service_client` bigint NOT NULL,
  `internal_reference` bigint NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qycxxafg734bhbhjyht9cbi6m` (`internal_reference`),
  KEY `FKapt4ejdmnj9xxpb0ry2ql0yvl` (`status_id`),
  CONSTRAINT `FKapt4ejdmnj9xxpb0ry2ql0yvl` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_opposition`
--

LOCK TABLES `request_opposition` WRITE;
/*!40000 ALTER TABLE `request_opposition` DISABLE KEYS */;
/*!40000 ALTER TABLE `request_opposition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revinfo`
--

DROP TABLE IF EXISTS `revinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revinfo` (
  `rev` int NOT NULL AUTO_INCREMENT,
  `revtstmp` bigint DEFAULT NULL,
  PRIMARY KEY (`rev`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revinfo`
--

LOCK TABLES `revinfo` WRITE;
/*!40000 ALTER TABLE `revinfo` DISABLE KEYS */;
INSERT INTO `revinfo` VALUES (1,1667312788432),(2,1667313625569),(3,1667313718732),(4,1667313834054),(5,1667313976518),(6,1667319233022),(7,1667320188377),(8,1667320470611),(9,1667320678647),(10,1667325549062),(11,1667325660658),(12,1667332008208),(13,1667332081085),(14,1667332081136),(15,1667332081169),(16,1667333174990),(17,1667333215309),(18,1667333215340),(19,1667333215372),(20,1667335094704),(21,1667335460995),(22,1667335985171),(23,1667336018468),(24,1667336080810),(25,1667336080846),(26,1667336080875),(27,1667336080909),(28,1667336080928),(29,1667406760564),(30,1667406792821),(31,1667406792852),(32,1667406792897),(33,1667406792998),(34,1667406793066),(35,1667407067226),(36,1667407102899),(37,1667407102933),(38,1667407102958),(39,1667407103006),(40,1667407103024),(41,1667409226848),(42,1667409258236),(43,1667409258275),(44,1667409258309),(45,1667409258355),(46,1667409258390),(47,1667415404774),(48,1667415429008),(49,1667415429097),(50,1667415429224),(51,1667415429290),(52,1667415429315),(53,1667646475825),(54,1667646546949),(55,1667646580653),(56,1667646580681),(57,1667646580734),(58,1667646580775),(59,1667646580819),(60,1667646580855),(61,1667647110139),(62,1667647155006),(63,1667647155047),(64,1667647155089),(65,1667647155136),(66,1667647155199),(67,1667647518985),(68,1667647579177),(69,1667647609267),(70,1667647609346),(71,1667647609410),(72,1667647609455),(73,1667647609474),(74,1667647609501),(75,1667648063007),(76,1667649491866),(77,1667649606322),(78,1667649606382),(79,1667649606470),(80,1667649606620),(81,1667649606676),(82,1667650956638),(83,1667651003491),(84,1667651003641),(85,1667651003719),(86,1667651003763),(87,1667651003789),(88,1667813613803),(89,1667825140941),(90,1667825192581),(91,1667825219311),(92,1667825219340),(93,1667825219369),(94,1667825219409),(95,1667825219428),(96,1667825219448),(97,1667841647417),(98,1669567609190),(99,1669567660501),(100,1669567660556),(101,1669567660603),(102,1669567660716),(103,1669567660745),(104,1669574297733),(105,1669574325807),(106,1669574325938),(107,1669574326034),(108,1669574326097),(109,1669574326146),(110,1669592487922),(111,1669592524403),(112,1669592524455),(113,1669592524489),(114,1669592524537),(115,1669592524566);
/*!40000 ALTER TABLE `revinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_user`
--

DROP TABLE IF EXISTS `role_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_user`
--

LOCK TABLES `role_user` WRITE;
/*!40000 ALTER TABLE `role_user` DISABLE KEYS */;
INSERT INTO `role_user` VALUES (1,'','ROLE_AGENT'),(2,'','ROLE_PRE_VERIFICATION_USER'),(3,'','ROLE_USER');
/*!40000 ALTER TABLE `role_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_user_aud`
--

DROP TABLE IF EXISTS `role_user_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_user_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `description_mod` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKmekm5p4mgukwph71pr4lso18k` (`rev`),
  CONSTRAINT `FKmekm5p4mgukwph71pr4lso18k` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_user_aud`
--

LOCK TABLES `role_user_aud` WRITE;
/*!40000 ALTER TABLE `role_user_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_user_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `station`
--

DROP TABLE IF EXISTS `station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `station` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` float NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `localization` varchar(255) DEFAULT NULL,
  `pin_code` int NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g0s8dk73ihemv51nvjrylnkpl` (`internal_reference`),
  UNIQUE KEY `UK_qrdhnkimbvdgv1mubuv2iqipb` (`pin_code`),
  KEY `FK6nku4juvjv9vmp9b5lvhyoixk` (`status_id`),
  CONSTRAINT `FK6nku4juvjv9vmp9b5lvhyoixk` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `station`
--

LOCK TABLES `station` WRITE;
/*!40000 ALTER TABLE `station` DISABLE KEYS */;
/*!40000 ALTER TABLE `station` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES (1,NULL,'STORE_ENABLE');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_aud`
--

DROP TABLE IF EXISTS `status_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `description_mod` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKm4t88904viu7aii6h9paj0njh` (`rev`),
  CONSTRAINT `FKm4t88904viu7aii6h9paj0njh` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_aud`
--

LOCK TABLES `status_aud` WRITE;
/*!40000 ALTER TABLE `status_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `status_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_order`
--

DROP TABLE IF EXISTS `status_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_order`
--

LOCK TABLES `status_order` WRITE;
/*!40000 ALTER TABLE `status_order` DISABLE KEYS */;
INSERT INTO `status_order` VALUES (1,NULL,'ORDER_CREATED'),(2,'','MODIFIED'),(3,'','DELETED'),(4,'','CLOSED'),(5,'','CANCELED'),(6,'','ACCEPTED'),(7,'','REFUSED'),(8,'','PAID'),(9,'','IN_PROCESS_OF_DELIVERY'),(11,'','DELIVERED'),(12,'','CREATED');
/*!40000 ALTER TABLE `status_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_order_aud`
--

DROP TABLE IF EXISTS `status_order_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_order_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `description_mod` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKfxuakw6pj5craycx4tr9q4ma6` (`rev`),
  CONSTRAINT `FKfxuakw6pj5craycx4tr9q4ma6` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_order_aud`
--

LOCK TABLES `status_order_aud` WRITE;
/*!40000 ALTER TABLE `status_order_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `status_order_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_user`
--

DROP TABLE IF EXISTS `status_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_user`
--

LOCK TABLES `status_user` WRITE;
/*!40000 ALTER TABLE `status_user` DISABLE KEYS */;
INSERT INTO `status_user` VALUES (1,'','USER_ENABLED'),(2,'','USER_DISABLED');
/*!40000 ALTER TABLE `status_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_user_aud`
--

DROP TABLE IF EXISTS `status_user_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_user_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `description_mod` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKr1k5qf3v8r9tj5e28s1p61y7k` (`rev`),
  CONSTRAINT `FKr1k5qf3v8r9tj5e28s1p61y7k` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_user_aud`
--

LOCK TABLES `status_user_aud` WRITE;
/*!40000 ALTER TABLE `status_user_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `status_user_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_movement`
--

DROP TABLE IF EXISTS `stock_movement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_movement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_store1` bigint DEFAULT NULL,
  `id_store2` bigint DEFAULT NULL,
  `id_store_house1` bigint DEFAULT NULL,
  `id_store_house2` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_50nr5id2746hbrt4twy27eipf` (`internal_reference`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_movement`
--

LOCK TABLES `stock_movement` WRITE;
/*!40000 ALTER TABLE `stock_movement` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_movement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `localization` varchar(255) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hh8dr1su39mp6ok39l3qdhjr6` (`internal_reference`),
  KEY `FKgvk0xc0tpfmtwa4cx61gs2uh6` (`status_id`),
  CONSTRAINT `FKgvk0xc0tpfmtwa4cx61gs2uh6` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (1,NULL,123456,'Douala',NULL,NULL);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_aud`
--

DROP TABLE IF EXISTS `store_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `created_date_mod` bit(1) DEFAULT NULL,
  `internal_reference` bigint DEFAULT NULL,
  `internal_reference_mod` bit(1) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `location_mod` bit(1) DEFAULT NULL,
  `updated_date` date DEFAULT NULL,
  `updated_date_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKt2lr04608p8l6g6cpsvym2jkg` (`rev`),
  CONSTRAINT `FKt2lr04608p8l6g6cpsvym2jkg` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_aud`
--

LOCK TABLES `store_aud` WRITE;
/*!40000 ALTER TABLE `store_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storehouse`
--

DROP TABLE IF EXISTS `storehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` date DEFAULT NULL,
  `id_store` bigint NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `update_at` date DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cgj8qvuhick7qmtq8ekidxgfy` (`internal_reference`),
  KEY `FKf701jn8ctbjl3n7cmy40p9u7u` (`status_id`),
  CONSTRAINT `FKf701jn8ctbjl3n7cmy40p9u7u` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storehouse`
--

LOCK TABLES `storehouse` WRITE;
/*!40000 ALTER TABLE `storehouse` DISABLE KEYS */;
/*!40000 ALTER TABLE `storehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_request_opposition` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `id_coupon` bigint NOT NULL,
  `internal_reference` bigint NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_8foxbj7qju3p46idwsy8arehc` (`internal_reference`),
  KEY `FK7h1wcba93khggbl1ahgwjlssu` (`status_id`),
  CONSTRAINT `FK7h1wcba93khggbl1ahgwjlssu` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_account`
--

DROP TABLE IF EXISTS `type_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_account`
--

LOCK TABLES `type_account` WRITE;
/*!40000 ALTER TABLE `type_account` DISABLE KEYS */;
INSERT INTO `type_account` VALUES (1,'MANAGER_COUPON'),(2,'MANAGER_STORE'),(3,'MANAGER_ORDER'),(4,'TREASURY');
/*!40000 ALTER TABLE `type_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_account_aud`
--

DROP TABLE IF EXISTS `type_account_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_account_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK4ae429tcvi7bhh9w4d53hiwn5` (`rev`),
  CONSTRAINT `FK4ae429tcvi7bhh9w4d53hiwn5` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_account_aud`
--

LOCK TABLES `type_account_aud` WRITE;
/*!40000 ALTER TABLE `type_account_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `type_account_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_client`
--

DROP TABLE IF EXISTS `type_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_client`
--

LOCK TABLES `type_client` WRITE;
/*!40000 ALTER TABLE `type_client` DISABLE KEYS */;
INSERT INTO `type_client` VALUES (1,'ENTREPRISE'),(2,'PARTICULAR');
/*!40000 ALTER TABLE `type_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_client_aud`
--

DROP TABLE IF EXISTS `type_client_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_client_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKd2oenpv8qaahmvyu93jr7j0fq` (`rev`),
  CONSTRAINT `FKd2oenpv8qaahmvyu93jr7j0fq` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_client_aud`
--

LOCK TABLES `type_client_aud` WRITE;
/*!40000 ALTER TABLE `type_client_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `type_client_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_document`
--

DROP TABLE IF EXISTS `type_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_document` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_document`
--

LOCK TABLES `type_document` WRITE;
/*!40000 ALTER TABLE `type_document` DISABLE KEYS */;
INSERT INTO `type_document` VALUES (1,'INVOICE'),(2,'DELIVERY');
/*!40000 ALTER TABLE `type_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_document_aud`
--

DROP TABLE IF EXISTS `type_document_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_document_aud` (
  `id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK6xyqn7nwrpm0d9cm6fdkag338` (`rev`),
  CONSTRAINT `FK6xyqn7nwrpm0d9cm6fdkag338` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_document_aud`
--

LOCK TABLES `type_document_aud` WRITE;
/*!40000 ALTER TABLE `type_document_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `type_document_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_voucher`
--

DROP TABLE IF EXISTS `type_voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_voucher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` float DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6tcw6e9i1ktxa7mmpdbm9jeii` (`internal_reference`),
  KEY `FKe14knvy5v4yerdtrt3opktpao` (`status_id`),
  CONSTRAINT `FKe14knvy5v4yerdtrt3opktpao` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_voucher`
--

LOCK TABLES `type_voucher` WRITE;
/*!40000 ALTER TABLE `type_voucher` DISABLE KEYS */;
INSERT INTO `type_voucher` VALUES (1,3000,'2022-11-27 16:55:27','',1763247,NULL,1),(2,5000,'2022-11-27 16:55:33','',1848705,NULL,1),(3,10000,'2022-11-27 16:55:39','',7353902,NULL,1);
/*!40000 ALTER TABLE `type_voucher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unit`
--

DROP TABLE IF EXISTS `unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `id_store` bigint DEFAULT NULL,
  `id_type_voucher` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `quantity_notebook` int NOT NULL,
  `update_at` datetime DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9a6s9nu9vfb1sr5knee5ylji3` (`internal_reference`),
  KEY `FKdbllvdqqkem5lvlli2aoj8hcb` (`status_id`),
  CONSTRAINT `FKdbllvdqqkem5lvlli2aoj8hcb` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unit`
--

LOCK TABLES `unit` WRITE;
/*!40000 ALTER TABLE `unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `date_last_login` datetime DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `emailverify` bit(1) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `id_store` bigint DEFAULT NULL,
  `internal_reference` bigint NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `is_first_connection` bit(1) NOT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `notification_key` varchar(255) DEFAULT NULL,
  `otp_code` varchar(255) DEFAULT NULL,
  `otp_code_createdat` datetime DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phoneverify` bit(1) NOT NULL,
  `pin_code` int DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `token_auth` varchar(255) DEFAULT NULL,
  `using_2fa` bit(1) DEFAULT NULL,
  `created_by_user_id` bigint DEFAULT NULL,
  `update_by_user_id` bigint DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  `type_account_id` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_j0m1vqh1pcb2cof2hmese1jue` (`internal_reference`),
  UNIQUE KEY `UK_tqbg2yyhe8mnaco06ahg8mkg6` (`pin_code`),
  UNIQUE KEY `UK_2p58gbqhxvue2igoderm0gh2c` (`telephone`),
  KEY `FKpcsj08dbah77s3pprdt49kleh` (`created_by_user_id`),
  KEY `FKm4r9e2kjfeab5rlypfm33j6t0` (`update_by_user_id`),
  KEY `FK6hywckynpmknvf8hnnqldr4mv` (`status_id`),
  KEY `FKoearyurqehgjrq001s69sysmb` (`type_account_id`),
  CONSTRAINT `FK6hywckynpmknvf8hnnqldr4mv` FOREIGN KEY (`status_id`) REFERENCES `status_user` (`id`),
  CONSTRAINT `FKm4r9e2kjfeab5rlypfm33j6t0` FOREIGN KEY (`update_by_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKoearyurqehgjrq001s69sysmb` FOREIGN KEY (`type_account_id`) REFERENCES `type_account` (`id`),
  CONSTRAINT `FKpcsj08dbah77s3pprdt49kleh` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (8,NULL,NULL,'2022-11-07 12:45:41','2022-11-27 23:42:04','arnoldkim4@gmail.com',_binary '\0','teo',123456,3314771,_binary '\0',_binary '','KIM',NULL,NULL,NULL,'$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',12322,'Développeur d\'application','690362811',NULL,_binary '\0',NULL,NULL,1,3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_aud`
--

DROP TABLE IF EXISTS `users_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_aud` (
  `user_id` bigint NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `created_date_mod` bit(1) DEFAULT NULL,
  `date_last_login` datetime DEFAULT NULL,
  `date_last_login_mod` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `email_mod` bit(1) DEFAULT NULL,
  `emailverify` bit(1) DEFAULT NULL,
  `emailverify_mod` bit(1) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `first_name_mod` bit(1) DEFAULT NULL,
  `id_store` bigint DEFAULT NULL,
  `id_store_mod` bit(1) DEFAULT NULL,
  `internal_reference` bigint DEFAULT NULL,
  `internal_reference_mod` bit(1) DEFAULT NULL,
  `is_delete` bit(1) DEFAULT NULL,
  `is_delete_mod` bit(1) DEFAULT NULL,
  `is_first_connection` bit(1) DEFAULT NULL,
  `is_first_connection_mod` bit(1) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `last_name_mod` bit(1) DEFAULT NULL,
  `notification_key` varchar(255) DEFAULT NULL,
  `notification_key_mod` bit(1) DEFAULT NULL,
  `otp_code` varchar(255) DEFAULT NULL,
  `otp_code_mod` bit(1) DEFAULT NULL,
  `otp_code_createdat` datetime DEFAULT NULL,
  `otp_code_createdat_mod` bit(1) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `password_mod` bit(1) DEFAULT NULL,
  `phoneverify` bit(1) DEFAULT NULL,
  `phoneverify_mod` bit(1) DEFAULT NULL,
  `pin_code` int DEFAULT NULL,
  `pin_code_mod` bit(1) DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `position_mod` bit(1) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `telephone_mod` bit(1) DEFAULT NULL,
  `token_auth` varchar(255) DEFAULT NULL,
  `token_auth_mod` bit(1) DEFAULT NULL,
  `using_2fa` bit(1) DEFAULT NULL,
  `using2fa_mod` bit(1) DEFAULT NULL,
  `roles_mod` bit(1) DEFAULT NULL,
  `status_id` bigint DEFAULT NULL,
  `status_mod` bit(1) DEFAULT NULL,
  `type_account_id` bigint DEFAULT NULL,
  `type_account_mod` bit(1) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`rev`),
  KEY `FKc4vk4tui2la36415jpgm9leoq` (`rev`),
  CONSTRAINT `FKc4vk4tui2la36415jpgm9leoq` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_aud`
--

LOCK TABLES `users_aud` WRITE;
/*!40000 ALTER TABLE `users_aud` DISABLE KEYS */;
INSERT INTO `users_aud` VALUES (1,1,0,'2022-11-01 14:26:28',_binary '',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '',_binary '\0',_binary '','Arnold',_binary '',12,_binary '',1663978,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KOM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$jeN1WUAAbBBx0F3wNSbFluFtpN8T0CktcwEVOQHprJCq/PgL8JSJi',_binary '',_binary '\0',_binary '',12348,_binary '','Développeur d\'application',_binary '','690362800',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',1,_binary ''),(2,2,0,'2022-11-01 14:40:25',_binary '',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '',_binary '\0',_binary '','Arnold',_binary '',12,_binary '',2162416,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KOM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$SmHkiuE0eQ8sihL4mqj2duAIEjiGUov94U1g8ezASfXYJe4dWEZt6',_binary '',_binary '\0',_binary '',12348,_binary '','Développeur d\'application',_binary '','690362800',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',1,_binary ''),(3,3,0,'2022-11-01 14:41:59',_binary '',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '',_binary '\0',_binary '','Arnold',_binary '',12,_binary '',7629164,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KOM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '',_binary '\0',_binary '',12348,_binary '','Développeur d\'application',_binary '','690362800',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',1,_binary ''),(3,4,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','6038',_binary '','2022-11-01 14:43:54',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,5,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2055',_binary '','2022-11-01 14:46:16',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,6,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3624',_binary '','2022-11-01 16:13:53',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,7,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','1538',_binary '','2022-11-01 16:29:48',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,8,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3914',_binary '','2022-11-01 16:34:30',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,9,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8211',_binary '','2022-11-01 16:37:59',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,10,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8211',_binary '\0','2022-11-01 16:37:59',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMjU1NDgsImV4cCI6MTY2NzM0MzU0OH0.w_F5zaZmThed5gyJ2gz0EB7ilKqLVhv0T3tgJPmAYcjAj01eMwx9-QUrPDOq3E8gMJOTGUq0zBbWNwoiT_T47w',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,11,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8991',_binary '','2022-11-01 18:01:00',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMjU1NDgsImV4cCI6MTY2NzM0MzU0OH0.w_F5zaZmThed5gyJ2gz0EB7ilKqLVhv0T3tgJPmAYcjAj01eMwx9-QUrPDOq3E8gMJOTGUq0zBbWNwoiT_T47w',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,12,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','1372',_binary '','2022-11-01 19:46:48',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMjU1NDgsImV4cCI6MTY2NzM0MzU0OH0.w_F5zaZmThed5gyJ2gz0EB7ilKqLVhv0T3tgJPmAYcjAj01eMwx9-QUrPDOq3E8gMJOTGUq0zBbWNwoiT_T47w',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,13,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','1372',_binary '\0','2022-11-01 19:46:48',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzIwODEsImV4cCI6MTY2NzM1MDA4MX0.vJ4D9zVEnPN9uq4yZOrMryuHmW4FwcMv6RYK6OgmJ9vw7WDDAhZFnpc9cVpUbXtAgcknRqGg9U3OMB3zaWBm8Q',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,14,1,'2022-11-01 14:41:59',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','1372',_binary '\0','2022-11-01 19:48:01',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzIwODEsImV4cCI6MTY2NzM1MDA4MX0.vJ4D9zVEnPN9uq4yZOrMryuHmW4FwcMv6RYK6OgmJ9vw7WDDAhZFnpc9cVpUbXtAgcknRqGg9U3OMB3zaWBm8Q',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,15,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 19:48:01',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','1372',_binary '\0','2022-11-01 19:48:01',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzIwODEsImV4cCI6MTY2NzM1MDA4MX0.vJ4D9zVEnPN9uq4yZOrMryuHmW4FwcMv6RYK6OgmJ9vw7WDDAhZFnpc9cVpUbXtAgcknRqGg9U3OMB3zaWBm8Q',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,16,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 19:48:01',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8426',_binary '','2022-11-01 20:06:15',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzIwODEsImV4cCI6MTY2NzM1MDA4MX0.vJ4D9zVEnPN9uq4yZOrMryuHmW4FwcMv6RYK6OgmJ9vw7WDDAhZFnpc9cVpUbXtAgcknRqGg9U3OMB3zaWBm8Q',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,17,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 19:48:01',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8426',_binary '\0','2022-11-01 20:06:15',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzMyMTUsImV4cCI6MTY2NzM1MTIxNX0.KDKAesFdiED_k-m2a9fLcL-SQeQnzh8eF9-VAhNBotS6c4CJ_qioS_wq15bbCq4t6f_PQvADzaW9mw4nYI-pvQ',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,18,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 19:48:01',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8426',_binary '\0','2022-11-01 20:06:55',_binary '','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzMyMTUsImV4cCI6MTY2NzM1MTIxNX0.KDKAesFdiED_k-m2a9fLcL-SQeQnzh8eF9-VAhNBotS6c4CJ_qioS_wq15bbCq4t6f_PQvADzaW9mw4nYI-pvQ',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,19,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8426',_binary '\0','2022-11-01 20:06:55',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzMyMTUsImV4cCI6MTY2NzM1MTIxNX0.KDKAesFdiED_k-m2a9fLcL-SQeQnzh8eF9-VAhNBotS6c4CJ_qioS_wq15bbCq4t6f_PQvADzaW9mw4nYI-pvQ',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,20,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8426',_binary '\0','2022-11-01 20:06:55',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzUwOTQsImV4cCI6MTY2NzM0NzA5NH0.J0o8b99bPp_iTElntKxEQPmOqs4rB7O5W2IyXsRv6m7nt2gjkaKEHEEva4qEz34w3qH0ww6AEhGjiWRbtGBmJw',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,21,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8426',_binary '\0','2022-11-01 20:06:55',_binary '\0','$2a$10$rlKnQzbCC7o8RM3OkZkfV.PTb9p0xy0IrX0uxgNu/s5A4Y9bHlitG',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzU0NjAsImV4cCI6MTY2NzM0NzQ2MH0.lkEkGfQP_beKBmU2olJxVTDwrDpS8X8xvyWq5aBCLb0h56Xc1KAG5uy7x98pU_ogFwMkSGEpXlrRkqWmrQ8X9A',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,22,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,23,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','5815',_binary '','2022-11-01 20:53:38',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,24,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','5815',_binary '\0','2022-11-01 20:53:38',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzYwODAsImV4cCI6MTY2NzM1NDA4MH0.eXW9os-lQiWzFvqdLao7g4IJU_H5tbDEqdyeP7gCzv3g4YNCCx68sn8br0fTr6ZIDrY2FdqoNI7b50hPeEsrCA',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,25,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:06:55',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','5815',_binary '\0','2022-11-01 20:54:41',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzYwODAsImV4cCI6MTY2NzM1NDA4MH0.eXW9os-lQiWzFvqdLao7g4IJU_H5tbDEqdyeP7gCzv3g4YNCCx68sn8br0fTr6ZIDrY2FdqoNI7b50hPeEsrCA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,26,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:54:41',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','5815',_binary '\0','2022-11-01 20:54:41',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2NjczMzYwODAsImV4cCI6MTY2NzM1NDA4MH0.eXW9os-lQiWzFvqdLao7g4IJU_H5tbDEqdyeP7gCzv3g4YNCCx68sn8br0fTr6ZIDrY2FdqoNI7b50hPeEsrCA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,27,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:54:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','5815',_binary '\0','2022-11-01 20:54:41',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,28,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:54:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,29,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:54:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8683',_binary '','2022-11-02 16:32:40',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,30,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:54:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8683',_binary '\0','2022-11-02 16:32:40',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDY3OTIsImV4cCI6MTY2NzQyNDc5Mn0.ix0ygb-g3yP2BB76w1T-vDTd4vzQSUYklkWj13xHgHkdzRDWBcCO9WRa6mD2_zEukOhjuUBL9lWk23GFn9dnew',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,31,1,'2022-11-01 14:41:59',_binary '\0','2022-11-01 20:54:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8683',_binary '\0','2022-11-02 16:33:13',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDY3OTIsImV4cCI6MTY2NzQyNDc5Mn0.ix0ygb-g3yP2BB76w1T-vDTd4vzQSUYklkWj13xHgHkdzRDWBcCO9WRa6mD2_zEukOhjuUBL9lWk23GFn9dnew',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,32,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:33:13',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8683',_binary '\0','2022-11-02 16:33:13',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDY3OTIsImV4cCI6MTY2NzQyNDc5Mn0.ix0ygb-g3yP2BB76w1T-vDTd4vzQSUYklkWj13xHgHkdzRDWBcCO9WRa6mD2_zEukOhjuUBL9lWk23GFn9dnew',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,33,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:33:13',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8683',_binary '\0','2022-11-02 16:33:13',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,34,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:33:13',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,35,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:33:13',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','6469',_binary '','2022-11-02 16:37:47',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,36,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:33:13',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','6469',_binary '\0','2022-11-02 16:37:47',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDcxMDIsImV4cCI6MTY2NzQyNTEwMn0.iGdShLgmYIZnhQi3MRC14vmAiNFrXEJrw5lYqD8OErAVwjjBFq7iQepZqfmLgK_esD5tY7SOBLQ3N34p_aERLA',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,37,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:33:13',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','6469',_binary '\0','2022-11-02 16:38:23',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDcxMDIsImV4cCI6MTY2NzQyNTEwMn0.iGdShLgmYIZnhQi3MRC14vmAiNFrXEJrw5lYqD8OErAVwjjBFq7iQepZqfmLgK_esD5tY7SOBLQ3N34p_aERLA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,38,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:38:23',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','6469',_binary '\0','2022-11-02 16:38:23',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDcxMDIsImV4cCI6MTY2NzQyNTEwMn0.iGdShLgmYIZnhQi3MRC14vmAiNFrXEJrw5lYqD8OErAVwjjBFq7iQepZqfmLgK_esD5tY7SOBLQ3N34p_aERLA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,39,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:38:23',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','6469',_binary '\0','2022-11-02 16:38:23',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,40,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:38:23',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,41,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:38:23',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','7780',_binary '','2022-11-02 17:13:47',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,42,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:38:23',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','7780',_binary '\0','2022-11-02 17:13:47',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDkyNTgsImV4cCI6MTY2NzQyNzI1OH0.K57T--4fcmw7f8Gr7u8nvFfPbj_CiYnr4CXNPjl9JvXPyVrzgoUWf4pzgJoW9ScJ2CRDz_hCyhPz9Jv8M0mqaw',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,43,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 16:38:23',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','7780',_binary '\0','2022-11-02 17:14:18',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDkyNTgsImV4cCI6MTY2NzQyNzI1OH0.K57T--4fcmw7f8Gr7u8nvFfPbj_CiYnr4CXNPjl9JvXPyVrzgoUWf4pzgJoW9ScJ2CRDz_hCyhPz9Jv8M0mqaw',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,44,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 17:14:18',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','7780',_binary '\0','2022-11-02 17:14:18',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MDkyNTgsImV4cCI6MTY2NzQyNzI1OH0.K57T--4fcmw7f8Gr7u8nvFfPbj_CiYnr4CXNPjl9JvXPyVrzgoUWf4pzgJoW9ScJ2CRDz_hCyhPz9Jv8M0mqaw',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,45,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 17:14:18',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','7780',_binary '\0','2022-11-02 17:14:18',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,46,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 17:14:18',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,47,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 17:14:18',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3131',_binary '','2022-11-02 18:56:44',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,48,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 17:14:18',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3131',_binary '\0','2022-11-02 18:56:44',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MTU0MjgsImV4cCI6MTY2NzQzMzQyOH0.VljrGY5MrL_YVXNcbsDNKqVca03m8LOJ47X0osyX194YyILg1ExSCkU3S1HCt9rMfiu69k9Eu-w4AGhuYlpcKg',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,49,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 17:14:18',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3131',_binary '\0','2022-11-02 18:57:09',_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MTU0MjgsImV4cCI6MTY2NzQzMzQyOH0.VljrGY5MrL_YVXNcbsDNKqVca03m8LOJ47X0osyX194YyILg1ExSCkU3S1HCt9rMfiu69k9Eu-w4AGhuYlpcKg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,50,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 18:57:09',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3131',_binary '\0','2022-11-02 18:57:09',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc0MTU0MjgsImV4cCI6MTY2NzQzMzQyOH0.VljrGY5MrL_YVXNcbsDNKqVca03m8LOJ47X0osyX194YyILg1ExSCkU3S1HCt9rMfiu69k9Eu-w4AGhuYlpcKg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,51,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 18:57:09',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3131',_binary '\0','2022-11-02 18:57:09',_binary '\0','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(3,52,1,'2022-11-01 14:41:59',_binary '\0','2022-11-02 18:57:09',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',7629164,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$4W5/6jV9PukO4O56Nezkyukv41sNpeI.EUQQH4lErhbSN1Kjdu/7G',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',1,_binary '\0'),(4,53,0,'2022-11-05 11:07:56',_binary '',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '',_binary '\0',_binary '','Arnold',_binary '',12,_binary '',5445758,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KOM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '',_binary '\0',_binary '',12348,_binary '','Développeur d\'application',_binary '','690362800',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '\0',1,_binary '',NULL,_binary '\0'),(4,54,1,'2022-11-05 11:07:56',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','9175',_binary '','2022-11-05 11:09:07',_binary '','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,55,1,'2022-11-05 11:07:56',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','9175',_binary '\0','2022-11-05 11:09:07',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDY1ODAsImV4cCI6MTY2NzY2NDU4MH0.gwadSO9xjdAxi24ymDbvIap-Bcfm1Ifmkh1iQZMZkwCqRD-i49jPFzet65RljllOL2Fbwzn1_te-zV1pY9IoBA',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,56,1,'2022-11-05 11:07:56',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','9175',_binary '\0','2022-11-05 11:09:41',_binary '','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDY1ODAsImV4cCI6MTY2NzY2NDU4MH0.gwadSO9xjdAxi24ymDbvIap-Bcfm1Ifmkh1iQZMZkwCqRD-i49jPFzet65RljllOL2Fbwzn1_te-zV1pY9IoBA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,57,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','9175',_binary '\0','2022-11-05 11:09:41',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDY1ODAsImV4cCI6MTY2NzY2NDU4MH0.gwadSO9xjdAxi24ymDbvIap-Bcfm1Ifmkh1iQZMZkwCqRD-i49jPFzet65RljllOL2Fbwzn1_te-zV1pY9IoBA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,58,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '','KOM',_binary '\0',NULL,_binary '\0','9175',_binary '\0','2022-11-05 11:09:41',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDY1ODAsImV4cCI6MTY2NzY2NDU4MH0.gwadSO9xjdAxi24ymDbvIap-Bcfm1Ifmkh1iQZMZkwCqRD-i49jPFzet65RljllOL2Fbwzn1_te-zV1pY9IoBA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,59,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','9175',_binary '\0','2022-11-05 11:09:41',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,60,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,61,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8243',_binary '','2022-11-05 11:18:30',_binary '','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,62,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8243',_binary '\0','2022-11-05 11:18:30',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDcxNTQsImV4cCI6MTY2NzY2NTE1NH0.WjeHfcikRFteyDatC4OvpodHDJMbRkuTH05ee-p3Sgu9aXPdQR-YBtLk7OKntix4-EUlbbt7EpdzfOU6MiwmCQ',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,63,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:09:41',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8243',_binary '\0','2022-11-05 11:19:15',_binary '','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDcxNTQsImV4cCI6MTY2NzY2NTE1NH0.WjeHfcikRFteyDatC4OvpodHDJMbRkuTH05ee-p3Sgu9aXPdQR-YBtLk7OKntix4-EUlbbt7EpdzfOU6MiwmCQ',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,64,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:19:15',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8243',_binary '\0','2022-11-05 11:19:15',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDcxNTQsImV4cCI6MTY2NzY2NTE1NH0.WjeHfcikRFteyDatC4OvpodHDJMbRkuTH05ee-p3Sgu9aXPdQR-YBtLk7OKntix4-EUlbbt7EpdzfOU6MiwmCQ',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,65,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:19:15',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','8243',_binary '\0','2022-11-05 11:19:15',_binary '\0','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(4,66,1,'2022-11-05 11:07:56',_binary '\0','2022-11-05 11:19:15',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',5445758,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$opoRo5zsfK3xikEUrqk8c./eKseHdvKUCbe4nV6lBIeyAkFJuFUOC',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',NULL,_binary '\0'),(5,67,0,'2022-11-05 11:25:19',_binary '',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '',_binary '\0',_binary '','Arnold',_binary '',12,_binary '',9275976,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KOM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '',_binary '\0',_binary '',12348,_binary '','Développeur d\'application',_binary '','690362800',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',4,_binary ''),(5,68,1,'2022-11-05 11:25:19',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2180',_binary '','2022-11-05 11:26:19',_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,69,1,'2022-11-05 11:25:19',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2180',_binary '\0','2022-11-05 11:26:19',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDc2MDksImV4cCI6MTY2NzY2NTYwOX0.55Hv7Da91cW8pyxtF0UVL50ZvtwfrHeQSCYSD2dCsZzi0lYHbxKjJenGU8NLYSHBs5dha7r7HfPKUvM1F2mnaA',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,70,1,'2022-11-05 11:25:19',_binary '\0',NULL,_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2180',_binary '\0','2022-11-05 11:26:49',_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDc2MDksImV4cCI6MTY2NzY2NTYwOX0.55Hv7Da91cW8pyxtF0UVL50ZvtwfrHeQSCYSD2dCsZzi0lYHbxKjJenGU8NLYSHBs5dha7r7HfPKUvM1F2mnaA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,71,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2180',_binary '\0','2022-11-05 11:26:49',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDc2MDksImV4cCI6MTY2NzY2NTYwOX0.55Hv7Da91cW8pyxtF0UVL50ZvtwfrHeQSCYSD2dCsZzi0lYHbxKjJenGU8NLYSHBs5dha7r7HfPKUvM1F2mnaA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,72,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '','KOM',_binary '\0',NULL,_binary '\0','2180',_binary '\0','2022-11-05 11:26:49',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDc2MDksImV4cCI6MTY2NzY2NTYwOX0.55Hv7Da91cW8pyxtF0UVL50ZvtwfrHeQSCYSD2dCsZzi0lYHbxKjJenGU8NLYSHBs5dha7r7HfPKUvM1F2mnaA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,73,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2180',_binary '\0','2022-11-05 11:26:49',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,74,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,76,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2540',_binary '','2022-11-05 11:58:12',_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,77,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2540',_binary '\0','2022-11-05 11:58:12',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDk2MDYsImV4cCI6MTY2NzY2NzYwNn0.cCfT_68n0_pCE5RSnFEM7FOfGlsFNd9S5PrjWz3Lwmnu8lqQN1dONcFscWwwSOwJ8Itxlbn35RSWdoe-JtEpZQ',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,78,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 11:26:49',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2540',_binary '\0','2022-11-05 12:00:06',_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDk2MDYsImV4cCI6MTY2NzY2NzYwNn0.cCfT_68n0_pCE5RSnFEM7FOfGlsFNd9S5PrjWz3Lwmnu8lqQN1dONcFscWwwSOwJ8Itxlbn35RSWdoe-JtEpZQ',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,79,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:00:06',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2540',_binary '\0','2022-11-05 12:00:06',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NDk2MDYsImV4cCI6MTY2NzY2NzYwNn0.cCfT_68n0_pCE5RSnFEM7FOfGlsFNd9S5PrjWz3Lwmnu8lqQN1dONcFscWwwSOwJ8Itxlbn35RSWdoe-JtEpZQ',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,80,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:00:06',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','2540',_binary '\0','2022-11-05 12:00:06',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,81,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:00:06',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,82,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:00:06',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3484',_binary '','2022-11-05 12:22:36',_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,83,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:00:06',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3484',_binary '\0','2022-11-05 12:22:36',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NTEwMDMsImV4cCI6MTY2NzY2OTAwM30.DkY6a1wZfqkNyxng-7kA4cWELTgiyY4PGpNgGUnoYrzzLMm4F0QaybOsjhhIRWRUWZP5fOuczyI00jwtdMfWvA',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,84,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:00:06',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3484',_binary '\0','2022-11-05 12:23:24',_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NTEwMDMsImV4cCI6MTY2NzY2OTAwM30.DkY6a1wZfqkNyxng-7kA4cWELTgiyY4PGpNgGUnoYrzzLMm4F0QaybOsjhhIRWRUWZP5fOuczyI00jwtdMfWvA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,85,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:23:24',_binary '','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3484',_binary '\0','2022-11-05 12:23:24',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRrb201NUBnbWFpbC5jb20iLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJpYXQiOjE2Njc2NTEwMDMsImV4cCI6MTY2NzY2OTAwM30.DkY6a1wZfqkNyxng-7kA4cWELTgiyY4PGpNgGUnoYrzzLMm4F0QaybOsjhhIRWRUWZP5fOuczyI00jwtdMfWvA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,86,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:23:24',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0','3484',_binary '\0','2022-11-05 12:23:24',_binary '\0','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(5,87,1,'2022-11-05 11:25:19',_binary '\0','2022-11-05 12:23:24',_binary '\0','arnoldkom55@gmail.com',_binary '\0',_binary '\0',_binary '\0','Arnold',_binary '\0',12,_binary '\0',9275976,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KOM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$fPnXA5S9binI88Uugmbhk.ud.3jDJDiq6e6L3ow52IM1Tz5Mg4TSW',_binary '\0',_binary '\0',_binary '\0',12348,_binary '\0','Développeur d\'application',_binary '\0','690362800',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',4,_binary '\0'),(6,75,0,'2022-11-05 11:34:23',_binary '',NULL,_binary '\0','arnoldkim4@gmail.com',_binary '',_binary '\0',_binary '','teo',_binary '',12,_binary '',7658686,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KIM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$c15o3cIOU0Wg31msaXnzjux7Xw4Q402ru./KjLIsLwl1ySqDWbE8W',_binary '',_binary '\0',_binary '',12322,_binary '','Développeur d\'application',_binary '','690362811',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',3,_binary ''),(7,88,0,'2022-11-07 09:33:33',_binary '',NULL,_binary '\0','arnoldkim4@gmail.com',_binary '',_binary '\0',_binary '','teo',_binary '',12,_binary '',6338951,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KIM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$bTY6cm5aP9T2hPEpuGJSreItwO/o5Ub7mYruBbfB/gNXNFag1lzge',_binary '',_binary '\0',_binary '',12322,_binary '','Développeur d\'application',_binary '','690362811',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',3,_binary ''),(8,89,0,'2022-11-07 12:45:41',_binary '',NULL,_binary '\0','arnoldkim4@gmail.com',_binary '',_binary '\0',_binary '','teo',_binary '',123456,_binary '',3314771,_binary '',_binary '\0',_binary '',_binary '\0',_binary '','KIM',_binary '',NULL,_binary '\0',NULL,_binary '\0',NULL,_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '',_binary '\0',_binary '',12322,_binary '','Développeur d\'application',_binary '','690362811',_binary '',NULL,_binary '\0',_binary '\0',_binary '',_binary '',1,_binary '',3,_binary ''),(8,90,1,'2022-11-07 12:45:41',_binary '\0',NULL,_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','3214',_binary '','2022-11-07 12:46:33',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,91,1,'2022-11-07 12:45:41',_binary '\0',NULL,_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','3214',_binary '\0','2022-11-07 12:46:33',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2NzgyNTIxOSwiZXhwIjoxNjY3ODQzMjE5fQ.VWTHUZ_ajdc9WzpCzuv_jMxbE1uSX03AcMgXXjWSPaTbjPhM2qF8D71qd6pyGcL3dHSf3x1SgKWUzsSLuSK4dg',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,92,1,'2022-11-07 12:45:41',_binary '\0',NULL,_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','3214',_binary '\0','2022-11-07 12:46:59',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2NzgyNTIxOSwiZXhwIjoxNjY3ODQzMjE5fQ.VWTHUZ_ajdc9WzpCzuv_jMxbE1uSX03AcMgXXjWSPaTbjPhM2qF8D71qd6pyGcL3dHSf3x1SgKWUzsSLuSK4dg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,93,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '\0',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','3214',_binary '\0','2022-11-07 12:46:59',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2NzgyNTIxOSwiZXhwIjoxNjY3ODQzMjE5fQ.VWTHUZ_ajdc9WzpCzuv_jMxbE1uSX03AcMgXXjWSPaTbjPhM2qF8D71qd6pyGcL3dHSf3x1SgKWUzsSLuSK4dg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,94,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '','KIM',_binary '\0',NULL,_binary '\0','3214',_binary '\0','2022-11-07 12:46:59',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2NzgyNTIxOSwiZXhwIjoxNjY3ODQzMjE5fQ.VWTHUZ_ajdc9WzpCzuv_jMxbE1uSX03AcMgXXjWSPaTbjPhM2qF8D71qd6pyGcL3dHSf3x1SgKWUzsSLuSK4dg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,95,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','3214',_binary '\0','2022-11-07 12:46:59',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,96,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,97,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','4685',_binary '','2022-11-07 17:20:47',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,98,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','6876',_binary '','2022-11-27 16:46:49',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,99,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','6876',_binary '\0','2022-11-27 16:46:49',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU2NzY2MCwiZXhwIjoxNjY5NTg1NjYwfQ.IiQcJwGEHPG77Gr9JGYJEzYQVMLdUfV7nvXva3xMZ2iA1EWkEXfTe8VYiq0uiASerKUgAgd2zZkqiqh8Wm7ltg',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,100,1,'2022-11-07 12:45:41',_binary '\0','2022-11-07 12:46:59',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','6876',_binary '\0','2022-11-27 16:47:41',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU2NzY2MCwiZXhwIjoxNjY5NTg1NjYwfQ.IiQcJwGEHPG77Gr9JGYJEzYQVMLdUfV7nvXva3xMZ2iA1EWkEXfTe8VYiq0uiASerKUgAgd2zZkqiqh8Wm7ltg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,101,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 16:47:41',_binary '','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','6876',_binary '\0','2022-11-27 16:47:41',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU2NzY2MCwiZXhwIjoxNjY5NTg1NjYwfQ.IiQcJwGEHPG77Gr9JGYJEzYQVMLdUfV7nvXva3xMZ2iA1EWkEXfTe8VYiq0uiASerKUgAgd2zZkqiqh8Wm7ltg',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,102,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 16:47:41',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','6876',_binary '\0','2022-11-27 16:47:41',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,103,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 16:47:41',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,104,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 16:47:41',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','9195',_binary '','2022-11-27 18:38:17',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,105,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 16:47:41',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','9195',_binary '\0','2022-11-27 18:38:17',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU3NDMyNSwiZXhwIjoxNjY5NTkyMzI1fQ.cnX1S3RSgk58Qrsw95SOj7-IxG-SCSDCgt8uqhxMcQtPo2tOlTr5Q_m9KkOzP9KKxNL-KLZGqN4N8VUvG4LYGA',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,106,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 16:47:41',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','9195',_binary '\0','2022-11-27 18:38:46',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU3NDMyNSwiZXhwIjoxNjY5NTkyMzI1fQ.cnX1S3RSgk58Qrsw95SOj7-IxG-SCSDCgt8uqhxMcQtPo2tOlTr5Q_m9KkOzP9KKxNL-KLZGqN4N8VUvG4LYGA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,107,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 18:38:46',_binary '','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','9195',_binary '\0','2022-11-27 18:38:46',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU3NDMyNSwiZXhwIjoxNjY5NTkyMzI1fQ.cnX1S3RSgk58Qrsw95SOj7-IxG-SCSDCgt8uqhxMcQtPo2tOlTr5Q_m9KkOzP9KKxNL-KLZGqN4N8VUvG4LYGA',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,108,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 18:38:46',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','9195',_binary '\0','2022-11-27 18:38:46',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,109,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 18:38:46',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,110,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 18:38:46',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','7077',_binary '','2022-11-27 23:41:28',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,111,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 18:38:46',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','7077',_binary '\0','2022-11-27 23:41:28',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU5MjUyNCwiZXhwIjoxNjY5NjEwNTI0fQ.doKtJ509kPc8aapb6hJUlMgmcXFYqoD9tY94Rdy931bq3WpO_UTr3TetyvnITFa9WptMSHQ_HaoaMYSivphnaw',_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,112,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 18:38:46',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','7077',_binary '\0','2022-11-27 23:42:04',_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU5MjUyNCwiZXhwIjoxNjY5NjEwNTI0fQ.doKtJ509kPc8aapb6hJUlMgmcXFYqoD9tY94Rdy931bq3WpO_UTr3TetyvnITFa9WptMSHQ_HaoaMYSivphnaw',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,113,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 23:42:04',_binary '','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','7077',_binary '\0','2022-11-27 23:42:04',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcm5vbGRraW00QGdtYWlsLmNvbSIsImF1dGhlbnRpY2F0ZWQiOnRydWUsImlhdCI6MTY2OTU5MjUyNCwiZXhwIjoxNjY5NjEwNTI0fQ.doKtJ509kPc8aapb6hJUlMgmcXFYqoD9tY94Rdy931bq3WpO_UTr3TetyvnITFa9WptMSHQ_HaoaMYSivphnaw',_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,114,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 23:42:04',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0','7077',_binary '\0','2022-11-27 23:42:04',_binary '\0','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0'),(8,115,1,'2022-11-07 12:45:41',_binary '\0','2022-11-27 23:42:04',_binary '\0','arnoldkim4@gmail.com',_binary '\0',_binary '\0',_binary '\0','teo',_binary '\0',123456,_binary '\0',3314771,_binary '\0',_binary '\0',_binary '\0',_binary '',_binary '\0','KIM',_binary '\0',NULL,_binary '\0',NULL,_binary '',NULL,_binary '','$2a$10$CTr6Hdhkwtv.KST6WMtcBOUaF2rrLGjXOFmiSEWOhLe4VndpMgYX6',_binary '\0',_binary '\0',_binary '\0',12322,_binary '\0','Développeur d\'application',_binary '\0','690362811',_binary '\0',NULL,_binary '\0',_binary '\0',_binary '\0',_binary '\0',1,_binary '\0',3,_binary '\0');
/*!40000 ALTER TABLE `users_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_old_passwords`
--

DROP TABLE IF EXISTS `users_old_passwords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_old_passwords` (
  `users_user_id` bigint NOT NULL,
  `old_passwords_old_password_id` bigint NOT NULL,
  KEY `FKq30ibe1si8uenio0e085hop26` (`old_passwords_old_password_id`),
  KEY `FKklcfn538nciu2ymyslmqfijjj` (`users_user_id`),
  CONSTRAINT `FKklcfn538nciu2ymyslmqfijjj` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKq30ibe1si8uenio0e085hop26` FOREIGN KEY (`old_passwords_old_password_id`) REFERENCES `old_password` (`old_password_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_old_passwords`
--

LOCK TABLES `users_old_passwords` WRITE;
/*!40000 ALTER TABLE `users_old_passwords` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_old_passwords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_roles`
--

DROP TABLE IF EXISTS `users_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_roles` (
  `users_user_id` bigint NOT NULL,
  `roles_id` bigint NOT NULL,
  PRIMARY KEY (`users_user_id`,`roles_id`),
  KEY `FKoxrfvt1v6mvuofck1ohyri56n` (`roles_id`),
  CONSTRAINT `FKmaps3ffbyjaxkt50q1c7s7v5j` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKoxrfvt1v6mvuofck1ohyri56n` FOREIGN KEY (`roles_id`) REFERENCES `role_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_roles`
--

LOCK TABLES `users_roles` WRITE;
/*!40000 ALTER TABLE `users_roles` DISABLE KEYS */;
INSERT INTO `users_roles` VALUES (8,3);
/*!40000 ALTER TABLE `users_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_roles_aud`
--

DROP TABLE IF EXISTS `users_roles_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_roles_aud` (
  `rev` int NOT NULL,
  `users_user_id` bigint NOT NULL,
  `roles_id` bigint NOT NULL,
  `revtype` tinyint DEFAULT NULL,
  PRIMARY KEY (`rev`,`users_user_id`,`roles_id`),
  CONSTRAINT `FKktxqr55ntd0j2i228uj8sq6j9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_roles_aud`
--

LOCK TABLES `users_roles_aud` WRITE;
/*!40000 ALTER TABLE `users_roles_aud` DISABLE KEYS */;
INSERT INTO `users_roles_aud` VALUES (1,1,2,0),(2,2,2,0),(3,3,2,0),(67,5,3,0),(75,6,3,0),(88,7,3,0),(89,8,3,0);
/*!40000 ALTER TABLE `users_roles_aud` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-11-28  0:54:43
