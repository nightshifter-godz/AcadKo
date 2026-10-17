-- MySQL dump 10.13  Distrib 8.4.0, for macos13.2 (arm64)
--
-- Host: localhost    Database: AcadKo
-- ------------------------------------------------------
-- Server version	8.4.11

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
-- Table structure for table `block_section`
--

DROP TABLE IF EXISTS `block_section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `block_section` (
  `block_section_Id` int NOT NULL AUTO_INCREMENT,
  `day` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday') NOT NULL,
  `subject_Id` int NOT NULL,
  `faculty_Id` int NOT NULL,
  `mode` enum('FTF','Online') NOT NULL,
  `room` varchar(50) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `block_id` int NOT NULL,
  PRIMARY KEY (`block_section_Id`),
  KEY `subject_Id` (`subject_Id`),
  KEY `faculty_Id` (`faculty_Id`),
  KEY `fk_block_section_block` (`block_id`),
  CONSTRAINT `block_section_ibfk_1` FOREIGN KEY (`subject_Id`) REFERENCES `subjects` (`subject_Id`),
  CONSTRAINT `block_section_ibfk_2` FOREIGN KEY (`faculty_Id`) REFERENCES `faculty` (`faculty_id`),
  CONSTRAINT `fk_block_section_block` FOREIGN KEY (`block_id`) REFERENCES `blocks` (`block_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `block_section`
--

LOCK TABLES `block_section` WRITE;
/*!40000 ALTER TABLE `block_section` DISABLE KEYS */;
INSERT INTO `block_section` VALUES (1,'Tuesday',18,7,'FTF','TBA','16:30:00','19:00:00',1),(2,'Friday',18,7,'FTF','TBA','18:30:00','21:00:00',1),(3,'Thursday',19,1,'FTF','TBA','18:00:00','21:00:00',1),(4,'Friday',19,1,'FTF','TBA','11:00:00','13:00:00',1),(5,'Monday',21,3,'FTF','TBA','16:30:00','19:30:00',1),(6,'Monday',22,5,'FTF','TBA','09:30:00','14:30:00',1),(7,'Tuesday',23,4,'FTF','TBA','12:30:00','15:30:00',1),(8,'Friday',23,4,'FTF','TBA','14:30:00','16:30:00',1),(9,'Thursday',24,6,'FTF','TBA','15:00:00','17:00:00',1);
/*!40000 ALTER TABLE `block_section` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blocks`
--

DROP TABLE IF EXISTS `blocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blocks` (
  `block_id` int NOT NULL AUTO_INCREMENT,
  `block_name` varchar(50) NOT NULL,
  PRIMARY KEY (`block_id`),
  UNIQUE KEY `block_name` (`block_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blocks`
--

LOCK TABLES `blocks` WRITE;
/*!40000 ALTER TABLE `blocks` DISABLE KEYS */;
INSERT INTO `blocks` VALUES (1,'DIT 2-6');
/*!40000 ALTER TABLE `blocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty`
--

DROP TABLE IF EXISTS `faculty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty` (
  `faculty_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `faculty_rank` varchar(100) NOT NULL,
  `department` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `preferred_medium` varchar(50) NOT NULL,
  PRIMARY KEY (`faculty_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty`
--

LOCK TABLES `faculty` WRITE;
/*!40000 ALTER TABLE `faculty` DISABLE KEYS */;
INSERT INTO `faculty` VALUES (1,'Jerome T. De Guzman','Instructor I','DIT','jerome.t.deguzman87@gmail.com','Google Meet'),(2,'Marisol D. Payra','Assistant Professorial Lecturer I','DIT','marisol.payra@deped.gov.ph','Google Meet'),(3,'Edrian G. Blasquino','Instructor I','DIT','egblasquino@iskolarngbayan.pup.edu.ph','Microsoft Teams'),(4,'Regie S. Cruz','Instructor I','DIT','rcruz@mapua.edu.ph','Microsoft Teams'),(5,'Victor James B. Ordonez','Instructor I','DIT','ordonezvictorjames@gmail.com','Google Meet'),(6,'Elijah Pol L. Bautista','Instructor I','DIT','eplbautista@pup.edu.ph','Microsoft Teams'),(7,'Mylo D.P. Atencio','Assistant Professor I','DIT','mdatencio@pup.edu.ph','Messenger');
/*!40000 ALTER TABLE `faculty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` varchar(30) NOT NULL,
  `user_id` int DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `pup_email` varchar(100) DEFAULT NULL,
  `block_id` int DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `pup_email` (`pup_email`),
  KEY `fk_students_block` (`block_id`),
  CONSTRAINT `fk_students_block` FOREIGN KEY (`block_id`) REFERENCES `blocks` (`block_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES ('2025-11828-MN-0',NULL,'VERGARA, GIAN PATRICK SALIMBANGON','gianpatricksvergara@iskolarngbayan.pup.edu.ph',1),('2025-11835-MN-0',NULL,'BUMANGLAG, JAN ROBIEN PAMITTAN','janrobienpbumanglag@iskolarngbayan.pup.edu.ph',1),('2025-11888-MN-0',NULL,'SUAVERDEZ, KARL STEVEN PAGUIRIGAN','karlstevenpsuaverdez@iskolarngbayan.pup.edu.ph',1),('2025-11889-MN-0',NULL,'SUPERAL, NATALIA BASILGA','nataliabsuperal@iskolarngbayan.pup.edu.ph',1),('2025-11917-MN-0',NULL,'DELA CRUZ, JOHN ADRIAN VILLAROMAN','johnadrianvdelacruz@iskolarngbayan.pup.edu.ph',1),('2025-12066-MN-0',NULL,'GAYA, RYAN JOSEPH CASPE','ryanjosephcgaya@iskolarngbayan.pup.edu.ph',1),('2025-12089-MN-0',NULL,'MACASINAG, MARY GRACE BERNABE','marygracebmacasinag@iskolarngbayan.pup.edu.ph',1),('2025-12092-MN-0',NULL,'TALAVERA, ROFEL PEDRONIO','rofelptalavera@iskolarngbayan.pup.edu.ph',1),('2025-12108-MN-0',NULL,'UNDA, ABDUL-RASHID MONASAPRANG','abdul-rashidmunda@iskolarngbayan.pup.edu.ph',1),('2025-12112-MN-0',NULL,'CABAUATAN, JAKE BENEDICT LAGMAN','jakebenedictlcabauatan@iskolarngbayan.pup.edu.ph',1),('2025-12146-MN-0',NULL,'SALVACION, KAELEY STACE CUATICO','kaeleystacecsalvacion@iskolarngbayan.pup.edu.ph',1),('2025-12147-MN-0',NULL,'TANGARO, JERIC MAXIMO','jericmtangaro@iskolarngbayan.pup.edu.ph',1),('2025-12152-MN-0',NULL,'TORREFRANCA, AIRA LEI MANACMUL','airaleimtorrefranca@iskolarngbayan.pup.edu.ph',1),('2025-12163-MN-0',NULL,'BLANCO, APRIL LOVE OMAY','aprilloveoblanco@iskolarngbayan.pup.edu.ph',1),('2025-12223-MN-0',NULL,'ESTRAVO, BEN GONZALES','bengestravo@iskolarngbayan.pup.edu.ph',1),('2025-12245-MN-0',NULL,'DAVID, JAHZEEL SANTIAGO','jahzeelsdavid@iskolarngbayan.pup.edu.ph',1),('2025-12248-MN-0',NULL,'LUMAGABAS, KIAN CEDRICK FRANCISCO','kiancedrickflumagabas@iskolarngbayan.pup.edu.ph',1),('2025-12259-MN-0',NULL,'MONION, CHRISTIAN YESUH GARCIA','christianyesuhgmonion@iskolarngbayan.pup.edu.ph',1),('2025-12285-MN-0',NULL,'GREGORIO, ANGELICA JOLLETTE MAYO','angelicajollettemgregorio@iskolarngbayan.pup.edu.ph',1),('2025-12288-MN-0',NULL,'PEÑAS, SIMPLE AYESHA SARITO','simpleayeshaspenas@iskolarngbayan.pup.edu.ph',1),('2025-12299-MN-0',NULL,'CARLOS, JOHN AIRICK ALISASIS','johnacarlos@iskolarngbayan.pup.edu.ph',1),('2025-12770-MN-0',NULL,'CABORNAY, GHIO TAMAYO','ghiotcabornay@iskolarngbayan.pup.edu.ph',1),('2025-12780-MN-0',NULL,'CONSORTE, JOHN ALBERT PORNETE','johnalbertpconsorte@iskolarngbayan.pup.edu.ph',1),('2025-12786-MN-0',NULL,'ARJONA, KAITHLYN JOIE BEGINO','kaithlynjoiebarjona@iskolarngbayan.pup.edu.ph',1),('2025-12813-MN-0',NULL,'JOSE, ALYSSA RAE CUENTO','alyssaraecjose@iskolarngbayan.pup.edu.ph',1),('2025-12832-MN-0',NULL,'ALVAREZ, FRINCZ ANGELO NAVOTAS','frinczangelonalvarez@iskolarngbayan.pup.edu.ph',1),('2025-12872-MN-0',NULL,'COFUENTES, HANAH JEMIMAH IBAYAN','hanahjemimahicofuentes@iskolarngbayan.pup.edu.ph',1),('2025-12875-MN-0',NULL,'ADUL, HONEY GRACE JAKOSALEM','honeygracejadul@iskolarngbayan.pup.edu.ph',1),('2025-12885-MN-0',NULL,'BENDANA, ENRIQUE HENRY III CUASAY','enriquehenrycbendanaiii@iskolarngbayan.pup.edu.ph',1),('2025-12889-MN-0',NULL,'DELA PAZ, ANNIKA PARAISO','annikapdelapaz@iskolarngbayan.pup.edu.ph',1),('2025-12891-MN-0',NULL,'DE LEON III, BENJAMIN ANDREI BENDANA','benjaminandreibdeleoniii@iskolarngbayan.pup.edu.ph',1),('2025-12905-MN-0',NULL,'ANASTACIO, MARIA MICHAELA FERNANDO','mariamichaelafanastacio@iskolarngbayan.pup.edu.ph',1),('2025-12921-MN-0',NULL,'CERUJANO, DANLEY DEINLA','danleydcerujano@iskolarngbayan.pup.edu.ph',1),('2025-12933-MN-0',NULL,'ALLER, KATHERINE PADER','katherinepaller@iskolarngbayan.pup.edu.ph',1),('2025-12947-MN-0',NULL,'AGUYAOY, RIAN CHEREIGN DANAO','rianchereigndaguyaoy@iskolarngbayan.pup.edu.ph',1),('2025-12950-MN-0',NULL,'BELDENIZA, YUAN ANTON AMATOS','yuanantonabeldeniza@iskolarngbayan.pup.edu.ph',1),('2025-12952-MN-0',NULL,'CANALES, ESTHER EBIDO','estherecanales@iskolarngbayan.pup.edu.ph',1),('2025-12976-MN-0',NULL,'CLERIGO, ANTONIO III CORONEL','antoniocclerigoiii@iskolarngbayan.pup.edu.ph',1),('2025-13810-MN-0',NULL,'MAGLALANG, MARIAANE JANE MIRANDA','mariaanejanemmaglalang@iskolarngbayan.pup.edu.ph',1),('2025-13850-MN-0',NULL,'MEDEL, CYMON JHEP DELAROSA','cymonjhepdmedel@iskolarngbayan.pup.edu.ph',1),('2025-14679-MN-0',NULL,'TAGABAN, CRIS LAURENCE SARZA','crislaurencestagaban@iskolarngbayan.pup.edu.ph',1),('2025-15725-MN-0',NULL,'AMIT, MICAELA JOANNA BATUIGAS','micaelajoannabamit@iskolarngbayan.pup.edu.ph',1),('2025-15964-MN-0',NULL,'UMPAD, AJAY ABES','ajayaumpad@iskolarngbayan.pup.edu.ph',1),('2025-16516-MN-0',NULL,'CRUZ, JAMAICA ELIZA CARYS PEÑAFLOR','jamaicaelizacaryspcruz@iskolarngbayan.pup.edu.ph',1),('2025-17364-MN-0',NULL,'FAUSTINO, JOHN KARL BULACLAC','johnkarlbfaustino@iskolarngbayan.pup.edu.ph',1);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `subject_Id` int NOT NULL AUTO_INCREMENT,
  `subject_code` varchar(20) NOT NULL,
  `subject_name` varchar(100) NOT NULL,
  PRIMARY KEY (`subject_Id`),
  UNIQUE KEY `subject_code` (`subject_code`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (1,'COMP 001','Introduction to Computing'),(2,'COMP 002','Computing Programming 1'),(3,'CWTS 001','Civil Welfare Training Service 1'),(4,'GEED 004','Mathematics in the Modern World'),(5,'GEED 005','Purposive Communication'),(6,'ITEC 101','Keyboarding Documents Processing with Laboratory'),(7,'ITEC 102','Basic Computer Hardware Servicing'),(8,'PATHFIT 1','Physical Activity Towards Health and Fitness 1'),(9,'COMP 003','Computer Programming 2'),(10,'COMP 004','Discrete Structures'),(11,'COMP 024','Technopreneurship'),(12,'CWTS 002','Civil Welfare Training Service 2'),(13,'GEED 001','Understanding the Self'),(14,'GEED 007','Science, Technology and Society/Agham, Teknolohiya, at Lipunan'),(15,'ITEC 103','Hardware/Software Installation and Maintenance'),(16,'ITEC 104','Basic Electronic'),(17,'PATHFIT 2','Physical Activity Towards Health and Fitnes 2'),(18,'COMP 006','Data Structures and Algorithm'),(19,'COMP 007','Operating Systems'),(20,'COMP 008','Data Communications and Networking'),(21,'COMP 023','Social and Professional Issues in Computing'),(22,'INTE 201','Programming 3 (Structured Programming)'),(23,'INTE 202','Integrative Programming and Technologies 1'),(24,'PATHFIT 3','Physical Activity Towards Health and Fitness 3');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_Id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','student','professor','student representative') DEFAULT NULL,
  PRIMARY KEY (`user_Id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-17 10:40:07
