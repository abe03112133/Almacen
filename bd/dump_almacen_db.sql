-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: almacen_db
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `almacen`
--

DROP TABLE IF EXISTS `almacen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `almacen` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) DEFAULT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero` int NOT NULL,
  `prefijo_codigo` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2f7p92n4ncfnatg2lvc5gfsdr` (`numero`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `almacen`
--

LOCK TABLES `almacen` WRITE;
/*!40000 ALTER TABLE `almacen` DISABLE KEYS */;
INSERT INTO `almacen` VALUES (1,1,'ALMACEN 1',1,'30'),(2,1,'ALMACEN 2',2,'32'),(3,1,'ALMACEN 3',3,'33'),(4,1,'ALMACEN 1',4,'31');
/*!40000 ALTER TABLE `almacen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consumo_insumo`
--

DROP TABLE IF EXISTS `consumo_insumo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consumo_insumo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha_hora` datetime(6) NOT NULL,
  `insumo_id` int NOT NULL,
  `maquina_id` int NOT NULL,
  `producccion_alm_id` int DEFAULT NULL,
  `usuario_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmsk665pkj37qisyftebrdmja6` (`insumo_id`),
  KEY `FKiysmukbqufkq23yvod8i605j2` (`maquina_id`),
  KEY `FKa0d2tcrue2kbqpsvu2pywg6db` (`producccion_alm_id`),
  KEY `FK6eteig0eb2b8e26m8fgwcao7r` (`usuario_id`),
  CONSTRAINT `FK6eteig0eb2b8e26m8fgwcao7r` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FKa0d2tcrue2kbqpsvu2pywg6db` FOREIGN KEY (`producccion_alm_id`) REFERENCES `produccion_alm` (`id`),
  CONSTRAINT `FKiysmukbqufkq23yvod8i605j2` FOREIGN KEY (`maquina_id`) REFERENCES `maquina` (`id`),
  CONSTRAINT `FKmsk665pkj37qisyftebrdmja6` FOREIGN KEY (`insumo_id`) REFERENCES `insumo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumo_insumo`
--

LOCK TABLES `consumo_insumo` WRITE;
/*!40000 ALTER TABLE `consumo_insumo` DISABLE KEYS */;
/*!40000 ALTER TABLE `consumo_insumo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `espacio`
--

DROP TABLE IF EXISTS `espacio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `espacio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_qr` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_liberacion` datetime(6) DEFAULT NULL,
  `fecha_ocupacion` datetime(6) DEFAULT NULL,
  `lado` int NOT NULL,
  `nivel` int NOT NULL,
  `posicion` int NOT NULL,
  `bloqueado_por` int DEFAULT NULL,
  `rack_id` int NOT NULL,
  `tarima_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhnkrpgi74jw60ymgq5smau4nq` (`rack_id`,`nivel`,`lado`,`posicion`),
  KEY `FKeog7rysf07mdwrvdy72i41d33` (`bloqueado_por`),
  KEY `FKgrshotxthfchjbjqosp14mkvw` (`tarima_id`),
  CONSTRAINT `FKb1pyic7sfj7crfccojv0ka0o9` FOREIGN KEY (`rack_id`) REFERENCES `rack` (`id`),
  CONSTRAINT `FKeog7rysf07mdwrvdy72i41d33` FOREIGN KEY (`bloqueado_por`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FKgrshotxthfchjbjqosp14mkvw` FOREIGN KEY (`tarima_id`) REFERENCES `tarima` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `espacio`
--

LOCK TABLES `espacio` WRITE;
/*!40000 ALTER TABLE `espacio` DISABLE KEYS */;
/*!40000 ALTER TABLE `espacio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estado_producto`
--

DROP TABLE IF EXISTS `estado_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estado_producto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `surtido` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_pd6jitlqs9x7tuyva1fb93t3b` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estado_producto`
--

LOCK TABLES `estado_producto` WRITE;
/*!40000 ALTER TABLE `estado_producto` DISABLE KEYS */;
INSERT INTO `estado_producto` VALUES (1,'TERMINADO','Producto Terminado',1),(2,'CUARENTENA','Producto en Cuarentena',0),(3,'NO_ESTANDAR','Producto No estándar',0),(4,'MUESTRA','Producto para muestras',0),(5,'ESPECIAL','Especial',0);
/*!40000 ALTER TABLE `estado_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `insumo`
--

DROP TABLE IF EXISTS `insumo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `insumo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2872hxtvcbal82fvjkibkamfw` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `insumo`
--

LOCK TABLES `insumo` WRITE;
/*!40000 ALTER TABLE `insumo` DISABLE KEYS */;
INSERT INTO `insumo` VALUES (1,'TAPE','Cinta adhesiva'),(2,'HOJAS','Hojas para Impresora laser'),(3,'CAJAS','Cajas para Producto'),(4,'TINTA','Tinta de impresora'),(5,'ETIQUETAS','Rollo de etiquetas para etiquetadora'),(6,'FLEJE','Proteccion para Tarimas'),(7,'BOLSAS','Bolsas para Producto');
/*!40000 ALTER TABLE `insumo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maquina`
--

DROP TABLE IF EXISTS `maquina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maquina` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) DEFAULT NULL,
  `codigo` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `insumo_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_rptujpq5p1k3jk28812ys9kqb` (`codigo`),
  UNIQUE KEY `UK_qge2uv298hw5wakxatsjyatoy` (`insumo_id`),
  CONSTRAINT `FKbe0uhnwx83rmumhurdxl6wp49` FOREIGN KEY (`insumo_id`) REFERENCES `insumo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maquina`
--

LOCK TABLES `maquina` WRITE;
/*!40000 ALTER TABLE `maquina` DISABLE KEYS */;
INSERT INTO `maquina` VALUES (1,1,'M-C300','C300',1),(2,1,'M-ENVOLVEDORA','ENVOLVEDORA',2);
/*!40000 ALTER TABLE `maquina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pre_tarima`
--

DROP TABLE IF EXISTS `pre_tarima`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pre_tarima` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cantidad_cajas` int DEFAULT NULL,
  `codigo` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  `hora_creacion` datetime(6) NOT NULL,
  `tipo_estado_producto_id` int DEFAULT NULL,
  `produccion_id` int DEFAULT NULL,
  `producccion_alm_id` int DEFAULT NULL,
  `usuario_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_88r2qduuyc2jlgu22qei0e59j` (`codigo`),
  KEY `FKqwpcfnctth53p8guqwj9ga1xn` (`tipo_estado_producto_id`),
  KEY `FKj8gsec3oqije3s9r4ubumrlmg` (`produccion_id`),
  KEY `FKgjuyux6tgq0i6pindkp8pfdpo` (`producccion_alm_id`),
  KEY `FKkrlfvh41sowrkdivoq42lmgyv` (`usuario_id`),
  CONSTRAINT `FKgjuyux6tgq0i6pindkp8pfdpo` FOREIGN KEY (`producccion_alm_id`) REFERENCES `produccion_alm` (`id`),
  CONSTRAINT `FKj8gsec3oqije3s9r4ubumrlmg` FOREIGN KEY (`produccion_id`) REFERENCES `produccion` (`id`),
  CONSTRAINT `FKkrlfvh41sowrkdivoq42lmgyv` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FKqwpcfnctth53p8guqwj9ga1xn` FOREIGN KEY (`tipo_estado_producto_id`) REFERENCES `estado_producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pre_tarima`
--

LOCK TABLES `pre_tarima` WRITE;
/*!40000 ALTER TABLE `pre_tarima` DISABLE KEYS */;
/*!40000 ALTER TABLE `pre_tarima` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produccion`
--

DROP TABLE IF EXISTS `produccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produccion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) DEFAULT NULL,
  `fecha` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produccion`
--

LOCK TABLES `produccion` WRITE;
/*!40000 ALTER TABLE `produccion` DISABLE KEYS */;
/*!40000 ALTER TABLE `produccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produccion_alm`
--

DROP TABLE IF EXISTS `produccion_alm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produccion_alm` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cajas_finales` int DEFAULT NULL,
  `cajas_iniciales` int DEFAULT NULL,
  `codigo` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estatus` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hora_fin` datetime(6) DEFAULT NULL,
  `hora_inicio` datetime(6) NOT NULL,
  `producto_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_n9ked92pe4m2gqru8kyfwde5k` (`codigo`),
  KEY `FK4fwngtgs4hokknt6pw4afwoei` (`producto_id`),
  CONSTRAINT `FK4fwngtgs4hokknt6pw4afwoei` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produccion_alm`
--

LOCK TABLES `produccion_alm` WRITE;
/*!40000 ALTER TABLE `produccion_alm` DISABLE KEYS */;
/*!40000 ALTER TABLE `produccion_alm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `abreviacion` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT NULL,
  `codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color_display` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `piezas_por_caja` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kxdt4u9c4w6vveo7ylph4pd09` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'PBG',1,'242052',NULL,'PAN BLANCO GRANDE',100),(2,'INT',1,'240123',NULL,'TELERA',84),(3,'VIR',1,'244120',NULL,'VIRGINIA',98),(4,'PBCH',1,'240124',NULL,'PAN BLANCO CHICO',200),(5,'PIN',1,'244232',NULL,'PAN INTEGRAL',100),(6,'MBM',1,'240695',NULL,'MINI BAGUETTE MULTIG',100),(7,'MBA',1,'240696',NULL,'MINI BAGUETTE ARTESA',100),(8,'BLL',1,'244018',NULL,'BOLLITO',288);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rack`
--

DROP TABLE IF EXISTS `rack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rack` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) DEFAULT NULL,
  `es_pasillo` tinyint(1) DEFAULT NULL,
  `es_tunel` tinyint(1) DEFAULT NULL,
  `espacios_por_nivel` int NOT NULL,
  `lados_por_rack` int NOT NULL,
  `niveles_activos` int NOT NULL,
  `numero` int NOT NULL,
  `numero_dos_digitos` varchar(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prefijo_codigo` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `almacen_id` int NOT NULL,
  `tipo_rack_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKisurrpp40rrh58n2k01nbv9rv` (`almacen_id`,`numero`,`prefijo_codigo`),
  KEY `FKsyhxnscpfc750d3wu1cbpuglf` (`tipo_rack_id`),
  CONSTRAINT `FKpkueg6d2ym43624nso5qwg2ii` FOREIGN KEY (`almacen_id`) REFERENCES `almacen` (`id`),
  CONSTRAINT `FKsyhxnscpfc750d3wu1cbpuglf` FOREIGN KEY (`tipo_rack_id`) REFERENCES `tipo_rack` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rack`
--

LOCK TABLES `rack` WRITE;
/*!40000 ALTER TABLE `rack` DISABLE KEYS */;
/*!40000 ALTER TABLE `rack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `tipo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_3a0sdqoex77hppupnidq0jew4` (`tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'Operador que guarda tarimas','ACOMODADOR'),(2,'Operador que cubre ocasiones','DESCANSERO'),(3,'Operador que ejecuta picklist','SURTIDOR'),(4,'Valida y autoriza acciones','SUPERVISOR'),(5,'Cambia estado de tarima','CALIDAD'),(6,'Administrador sistema','ADMIN'),(7,'Acceso a reportes y auditorías','GERENTE');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surtido_diario`
--

DROP TABLE IF EXISTS `surtido_diario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surtido_diario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) DEFAULT NULL,
  `fecha` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surtido_diario`
--

LOCK TABLES `surtido_diario` WRITE;
/*!40000 ALTER TABLE `surtido_diario` DISABLE KEYS */;
/*!40000 ALTER TABLE `surtido_diario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surtido_producto`
--

DROP TABLE IF EXISTS `surtido_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surtido_producto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cantidad` int NOT NULL,
  `destino` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `producto_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgatin5qr4fyjxivli3v9ka51l` (`producto_id`),
  CONSTRAINT `FKgatin5qr4fyjxivli3v9ka51l` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surtido_producto`
--

LOCK TABLES `surtido_producto` WRITE;
/*!40000 ALTER TABLE `surtido_producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `surtido_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tarima`
--

DROP TABLE IF EXISTS `tarima`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarima` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_almacen` datetime(6) NOT NULL,
  `fecha_surtido` datetime(6) DEFAULT NULL,
  `espacio_id` int DEFAULT NULL,
  `codigo__pre_tarima_id` int NOT NULL,
  `usuario_id_almacen` int DEFAULT NULL,
  `usuario_id_surtido` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dt2mk22p8c739s3qbwwlsxnav` (`codigo__pre_tarima_id`),
  KEY `FK9fd888q1sk7usi5a8er8ushy3` (`espacio_id`),
  KEY `FK50oal3xp9u1l04swxqf987hvd` (`usuario_id_almacen`),
  KEY `FKlr2ywse7rob0prsyvw3eng2qu` (`usuario_id_surtido`),
  CONSTRAINT `FK50oal3xp9u1l04swxqf987hvd` FOREIGN KEY (`usuario_id_almacen`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FK9fd888q1sk7usi5a8er8ushy3` FOREIGN KEY (`espacio_id`) REFERENCES `espacio` (`id`),
  CONSTRAINT `FKlr2ywse7rob0prsyvw3eng2qu` FOREIGN KEY (`usuario_id_surtido`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FKqu3tcdsmc89tu3e9ewiixord9` FOREIGN KEY (`codigo__pre_tarima_id`) REFERENCES `pre_tarima` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tarima`
--

LOCK TABLES `tarima` WRITE;
/*!40000 ALTER TABLE `tarima` DISABLE KEYS */;
/*!40000 ALTER TABLE `tarima` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_rack`
--

DROP TABLE IF EXISTS `tipo_rack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_rack` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `espacios_por_nivel` int NOT NULL,
  `lados_por_rack` int NOT NULL,
  `niveles` int NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ik8sw03mvro8a8kivlm7vco5p` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_rack`
--

LOCK TABLES `tipo_rack` WRITE;
/*!40000 ALTER TABLE `tipo_rack` DISABLE KEYS */;
INSERT INTO `tipo_rack` VALUES (1,'DINAMICO','Rack dinámico FIFO - 9 posiciones por nivel - 5 niveles',9,1,5,'Dinámico FIFO'),(2,'TUNEL','Túnel - 3 niveles - 9 posiciones',9,1,3,'Túnel FIFO'),(3,'REVERSIBLE','Reversible FILO - 8 posiciones por nivel',8,1,5,'Reversible FILO'),(4,'FIJO_ALM1','Fijo (Almacen 1) - 2 espacios por lado',2,2,5,'Fijo Almacén 1'),(5,'FIJO_ALM2','Fijo (Almacen 2) - 1 espacio por lado',1,2,5,'Fijo Almacén 2'),(6,'PISO','Pasillo / Espacio en piso - 30 posiciones',30,1,1,'Piso / Pasillo');
/*!40000 ALTER TABLE `tipo_rack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` tinyint(1) DEFAULT NULL,
  `fecha_ingreso` datetime(6) DEFAULT NULL,
  `nombre_completo` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_863n1y3x0jalatoir4325ehal` (`username`),
  KEY `FKshkwj12wg6vkm6iuwhvcfpct8` (`rol_id`),
  CONSTRAINT `FKshkwj12wg6vkm6iuwhvcfpct8` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,NULL,'2025-11-25 00:46:36.000000','Administrador','adm1234','admin',6);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'almacen_db'
--
/*!50003 DROP PROCEDURE IF EXISTS `crear_rack_y_espacios` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`ppa`@`localhost` PROCEDURE `crear_rack_y_espacios`(
  IN p_almacen_num INT,
  IN p_numero INT,
  IN p_prefijo_codigo VARCHAR(10),
  IN p_tipo_rack_codigo VARCHAR(50),
  IN p_es_tunel TINYINT,
  IN p_es_pasillo TINYINT
)
BEGIN
  DECLARE v_tipo_id INT;
  DECLARE v_almacen_id INT;
  DECLARE v_rack_id INT;
  DECLARE v_niveles INT;
  DECLARE v_espacios_por_nivel INT;
  DECLARE v_lados INT;
  DECLARE v_nivel INT;
  DECLARE v_lado INT;
  DECLARE v_pos INT;
  DECLARE v_codigo_qr VARCHAR(140);
  DECLARE v_num2 VARCHAR(4);
  DECLARE v_msg VARCHAR(255);

  SELECT id INTO v_almacen_id FROM almacen WHERE numero = p_almacen_num LIMIT 1;
  IF v_almacen_id IS NULL THEN
    SET v_msg = 'Almacen no encontrado';
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_msg;
  END IF;

  SELECT id, niveles, espacios_por_nivel, lados_por_rack INTO v_tipo_id, v_niveles, v_espacios_por_nivel, v_lados
  FROM tipo_rack WHERE codigo = p_tipo_rack_codigo LIMIT 1;

  IF v_tipo_id IS NULL THEN
    SET v_msg = 'Tipo rack no encontrado';
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_msg;
  END IF;

  IF p_es_pasillo = 1 THEN
    SET v_niveles = 1;
    SET v_espacios_por_nivel = 30;
    SET v_lados = 1;
  END IF;

  SET v_num2 = LPAD(p_numero,2,'0');
  INSERT INTO rack (almacen_id, numero, numero_dos_digitos, prefijo_codigo, tipo_rack_id, niveles_activos, espacios_por_nivel, lados_por_rack, es_tunel, es_pasillo, activo)
  VALUES (v_almacen_id, p_numero, v_num2, p_prefijo_codigo, v_tipo_id, v_niveles, v_espacios_por_nivel, v_lados, p_es_tunel, p_es_pasillo, 1);

  SET v_rack_id = LAST_INSERT_ID();

 IF p_tipo_rack_codigo = 'TUNEL' THEN
    SET v_nivel = 3;          -- primer nivel físico real
    SET v_niveles = 5;        -- túnel siempre termina en nivel 5
ELSE
    SET v_nivel = 1;
END IF;
  WHILE v_nivel <= v_niveles DO
    SET v_lado = 1;
    WHILE v_lado <= v_lados DO
      SET v_pos = 1;
      WHILE v_pos <= v_espacios_por_nivel DO
        IF p_es_pasillo = 1 THEN
          SET v_codigo_qr = CONCAT('PISO-P', p_numero, '-', LPAD(v_pos,2,'0'));
          INSERT INTO espacio (rack_id, nivel, lado, posicion, codigo_qr, estado)
            VALUES (v_rack_id, 0, 1, v_pos, v_codigo_qr, 'DISPONIBLE');
        ELSE
          SET v_codigo_qr = CONCAT('+C1PACON', p_prefijo_codigo, LPAD(p_numero,2,'0'), v_nivel, v_lado);
          INSERT INTO espacio (rack_id, nivel, lado, posicion, codigo_qr, estado)
            VALUES (v_rack_id, v_nivel, v_lado, v_pos, v_codigo_qr, 'DISPONIBLE');
        END IF;
        SET v_pos = v_pos + 1;
      END WHILE;
      SET v_lado = v_lado + 1;
    END WHILE;
    SET v_nivel = v_nivel + 1;
  END WHILE;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-25  0:58:03
