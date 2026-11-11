-- MySQL dump 10.13  Distrib 26.10.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: darkkitchen
-- ------------------------------------------------------
-- Server version	26.10.0

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
-- Table structure for table `fornecedores`
--

DROP TABLE IF EXISTS `fornecedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fornecedores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `cnpj` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fornecedores`
--

LOCK TABLES `fornecedores` WRITE;
/*!40000 ALTER TABLE `fornecedores` DISABLE KEYS */;
INSERT INTO `fornecedores` VALUES (1,_binary '','12345678000190','contato@fornecedor1.com','Distribuidora Central','11988881111'),(2,_binary '','23456789000191','vendas@hortifruti.com','Hortifruti São Paulo','11988882222'),(3,_binary '','34567890000192','comercial@carnes.com','Carnes Premium','11988883333'),(4,_binary '','45678901000193','contato@bebidas.com','Bebidas Express','11988884444'),(5,_binary '\0','56789012000194','atendimento@antigo.com','Fornecedor Inativo','11988885555');
/*!40000 ALTER TABLE `fornecedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingredientes`
--

DROP TABLE IF EXISTS `ingredientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `custo_unitario` decimal(14,4) DEFAULT NULL,
  `estoque_atual` decimal(14,3) DEFAULT NULL,
  `estoque_minimo` decimal(14,3) DEFAULT NULL,
  `nome` varchar(255) NOT NULL,
  `unidade` enum('G','KG','L','ML','UN') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKstc4s65yimop05l9lukuo2fap` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredientes`
--

LOCK TABLES `ingredientes` WRITE;
/*!40000 ALTER TABLE `ingredientes` DISABLE KEYS */;
INSERT INTO `ingredientes` VALUES (1,_binary '',0.0350,5000.000,1000.000,'Pão brioche','UN'),(2,_binary '',0.0450,1000.000,500.000,'Carne bovina','G'),(3,_binary '',0.0250,2000.000,500.000,'Queijo cheddar','G'),(4,_binary '',0.0300,1500.000,300.000,'Bacon','G'),(5,_binary '\0',0.0150,2000.000,400.000,'Alface','G'),(6,_binary '',0.0120,2000.000,400.000,'Tomate','G'),(7,_binary '',0.0200,2800.000,500.000,'Batata congelada','G'),(8,_binary '',0.0080,1000.000,200.000,'Molho especial','ML'),(9,_binary '',0.0040,5000.000,1000.000,'Refrigerante','ML'),(10,_binary '',0.0800,1000.000,200.000,'Chocolate','G'),(11,_binary '',0.0150,1000.000,200.000,'Farinha de trigo','G'),(12,_binary '',0.0250,500.000,100.000,'Ovos','UN');
/*!40000 ALTER TABLE `ingredientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itens_compra`
--

DROP TABLE IF EXISTS `itens_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custo_unitario` decimal(14,4) DEFAULT NULL,
  `quantidade` decimal(14,3) DEFAULT NULL,
  `validade` date DEFAULT NULL,
  `compra_id` bigint DEFAULT NULL,
  `ingrediente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8fyfkpngdtr6l11a7aosadrqe` (`compra_id`),
  KEY `FK51y1hhf7eswsvj26j8fqgpq4x` (`ingrediente_id`),
  CONSTRAINT `FK51y1hhf7eswsvj26j8fqgpq4x` FOREIGN KEY (`ingrediente_id`) REFERENCES `ingredientes` (`id`),
  CONSTRAINT `FK8fyfkpngdtr6l11a7aosadrqe` FOREIGN KEY (`compra_id`) REFERENCES `pedidos_compra` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_compra`
--

LOCK TABLES `itens_compra` WRITE;
/*!40000 ALTER TABLE `itens_compra` DISABLE KEYS */;
INSERT INTO `itens_compra` VALUES (1,0.0450,5000.000,'2027-03-30',1,2),(2,0.0250,3000.000,'2027-04-15',1,3),(3,0.0300,2000.000,'2027-02-28',2,4);
/*!40000 ALTER TABLE `itens_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itens_ficha`
--

DROP TABLE IF EXISTS `itens_ficha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_ficha` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade` decimal(14,4) DEFAULT NULL,
  `ingrediente_id` bigint DEFAULT NULL,
  `prato_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrkdix5lje3mugouv0tbdky9xt` (`ingrediente_id`),
  KEY `FKodnbpi4t42cif9ywb8ms22p4p` (`prato_id`),
  CONSTRAINT `FKodnbpi4t42cif9ywb8ms22p4p` FOREIGN KEY (`prato_id`) REFERENCES `pratos` (`id`),
  CONSTRAINT `FKrkdix5lje3mugouv0tbdky9xt` FOREIGN KEY (`ingrediente_id`) REFERENCES `ingredientes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_ficha`
--

LOCK TABLES `itens_ficha` WRITE;
/*!40000 ALTER TABLE `itens_ficha` DISABLE KEYS */;
INSERT INTO `itens_ficha` VALUES (1,200.0000,7,5),(2,20.0000,5,3),(3,150.0000,2,3),(4,1.0000,1,3),(5,40.0000,4,2),(6,200.0000,2,2),(7,1.0000,1,2),(8,30.0000,3,1),(9,150.0000,2,1),(10,1.0000,1,1);
/*!40000 ALTER TABLE `itens_ficha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itens_pedido`
--

DROP TABLE IF EXISTS `itens_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custo_unitario` decimal(12,2) DEFAULT NULL,
  `observacao` varchar(300) DEFAULT NULL,
  `preco_unitario` decimal(12,2) DEFAULT NULL,
  `quantidade` int DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  `prato_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK42mycompce3b7yt3l6ukdwsxy` (`pedido_id`),
  KEY `FKerd1ik2bg8noedmmsq8wfr5yq` (`prato_id`),
  CONSTRAINT `FK42mycompce3b7yt3l6ukdwsxy` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`),
  CONSTRAINT `FKerd1ik2bg8noedmmsq8wfr5yq` FOREIGN KEY (`prato_id`) REFERENCES `pratos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_pedido`
--

LOCK TABLES `itens_pedido` WRITE;
/*!40000 ALTER TABLE `itens_pedido` DISABLE KEYS */;
INSERT INTO `itens_pedido` VALUES (1,12.00,'Sem cebola',36.90,1,1,2),(2,4.00,NULL,15.90,1,2,5),(3,7.09,NULL,25.90,1,3,3),(4,10.24,NULL,36.90,1,4,2);
/*!40000 ALTER TABLE `itens_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lotes_estoque`
--

DROP TABLE IF EXISTS `lotes_estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes_estoque` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custo_unitario` decimal(14,4) DEFAULT NULL,
  `entrada_em` datetime(6) DEFAULT NULL,
  `quantidade_atual` decimal(14,3) DEFAULT NULL,
  `quantidade_inicial` decimal(14,3) DEFAULT NULL,
  `validade` date DEFAULT NULL,
  `ingrediente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoey1cardgy68l5nkiqtxf9fh5` (`ingrediente_id`),
  CONSTRAINT `FKoey1cardgy68l5nkiqtxf9fh5` FOREIGN KEY (`ingrediente_id`) REFERENCES `ingredientes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lotes_estoque`
--

LOCK TABLES `lotes_estoque` WRITE;
/*!40000 ALTER TABLE `lotes_estoque` DISABLE KEYS */;
INSERT INTO `lotes_estoque` VALUES (1,0.0450,'2026-11-07 12:49:23.000000',0.000,2000.000,'2027-03-30',2),(2,0.0250,'2026-11-07 12:49:26.000000',1500.000,1500.000,'2027-04-15',3),(3,0.0250,'2026-11-07 12:49:29.000000',1500.000,1500.000,'2027-04-15',3),(4,0.0300,'2026-11-07 12:49:32.000000',1000.000,1000.000,'2027-02-28',4),(5,0.0200,'2026-11-07 12:49:35.000000',2800.000,3000.000,'2027-06-30',7);
/*!40000 ALTER TABLE `lotes_estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `atualizado_em` datetime(6) DEFAULT NULL,
  `codigo_pagamento` varchar(30) DEFAULT NULL,
  `criado_em` datetime(6) DEFAULT NULL,
  `endereco_entrega` varchar(300) DEFAULT NULL,
  `forma_pagamento` enum('CARTAO_CREDITO','CARTAO_DEBITO','PIX') DEFAULT NULL,
  `pagamento_aprovado` bit(1) NOT NULL,
  `status` enum('CANCELADO','CONFIRMADO','EM_PREPARO','ENTREGUE','PRONTO','RECEBIDO','SAIU_PARA_ENTREGA') DEFAULT NULL,
  `telefone_contato` varchar(30) DEFAULT NULL,
  `total` decimal(12,2) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `entregador_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6dtctern9votxnaydg7g2uifb` (`cliente_id`),
  KEY `FKjgicxqvajc4u80jvgfky32ha` (`entregador_id`),
  CONSTRAINT `FK6dtctern9votxnaydg7g2uifb` FOREIGN KEY (`cliente_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKjgicxqvajc4u80jvgfky32ha` FOREIGN KEY (`entregador_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (1,'2026-11-11 00:01:33.927465','PIX-TESTE-001','2026-11-07 12:54:12.000000','Rua das Flores, 120','PIX',_binary '','ENTREGUE','11999990001',36.90,NULL,1),(2,'2026-11-11 00:20:43.434355','SIM-253DD71B','2026-11-07 22:29:16.620820','rua da d','PIX',_binary '','ENTREGUE','11985790395',15.90,2,8),(3,'2026-11-07 22:37:46.658457','SIM-A9935FC2','2026-11-07 22:37:46.658457','rua da d','CARTAO_DEBITO',_binary '','RECEBIDO','11985790395',25.90,2,NULL),(4,'2026-11-08 15:24:29.990736','SIM-5E508563','2026-11-08 15:24:29.990736','rua da d','PIX',_binary '','RECEBIDO','1235226464',36.90,3,NULL);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos_compra`
--

DROP TABLE IF EXISTS `pedidos_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `criado_em` datetime(6) DEFAULT NULL,
  `recebido_em` datetime(6) DEFAULT NULL,
  `status` enum('ABERTO','CANCELADO','ENVIADO','RECEBIDO') DEFAULT NULL,
  `fornecedor_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKan3yv2vk5m3gbphqf4f6yxj4j` (`fornecedor_id`),
  CONSTRAINT `FKan3yv2vk5m3gbphqf4f6yxj4j` FOREIGN KEY (`fornecedor_id`) REFERENCES `fornecedores` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos_compra`
--

LOCK TABLES `pedidos_compra` WRITE;
/*!40000 ALTER TABLE `pedidos_compra` DISABLE KEYS */;
INSERT INTO `pedidos_compra` VALUES (1,'2026-11-07 12:49:58.000000','2026-11-07 12:49:58.000000','RECEBIDO',1),(2,'2026-11-07 12:50:03.000000',NULL,'ENVIADO',3);
/*!40000 ALTER TABLE `pedidos_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pratos`
--

DROP TABLE IF EXISTS `pratos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pratos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `categoria` varchar(100) DEFAULT NULL,
  `descricao` varchar(1000) DEFAULT NULL,
  `imagem_url` varchar(500) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `preco` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pratos`
--

LOCK TABLES `pratos` WRITE;
/*!40000 ALTER TABLE `pratos` DISABLE KEYS */;
INSERT INTO `pratos` VALUES (1,_binary '','Lanches','Hambúrguer artesanal com queijo cheddar e molho especial.','/imagens/pratos/dark-burger.jpg','Dark Burger',29.90),(2,_binary '','Lanches','Hambúrguer duplo com bacon crocante e cheddar.','/imagens/pratos/double-bacon.jpg','Double Bacon',36.90),(3,_binary '','Lanches','Hambúrguer artesanal com salada e molho da casa.','/imagens/pratos/classic-burger.jpg','Classic Burger',25.90),(4,_binary '','Combos','Hambúrguer, batata frita e refrigerante.','/imagens/pratos/combo-dark.jpg','Combo Dark',42.90),(5,_binary '','Acompanhamentos','Porção de batatas fritas crocantes.','/imagens/pratos/batata-frita.jpg','Batata Frita',15.90),(6,_binary '\0','Bebidas','Refrigerante lata 350ml.','/imagens/pratos/refrigerante.jpg','Refrigerante',6.00),(7,_binary '','Sobremesas','Brownie de chocolate artesanal.','/imagens/pratos/brownie.jpg','Brownie',12.90),(8,_binary '\0','Lanches','Produto temporariamente indisponível.','/imagens/pratos/burger.jpg','Burger Desativado',22.90);
/*!40000 ALTER TABLE `pratos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `criado_em` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `endereco` varchar(300) DEFAULT NULL,
  `nome` varchar(120) NOT NULL,
  `perfil` varchar(20) NOT NULL,
  `senha_hash` varchar(255) NOT NULL,
  `telefone` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkfsp0s1tflm1cwlj8idhqsad0` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,_binary '','2026-11-07 15:33:02.496868','admin@darkkitchen.com',NULL,'Administrador','ADMIN','$2a$10$IEAbVD8d3PAhQfFpycItt.yvBVcNN9evGtExg4Zjg2ylQEomeDEea',NULL),(2,_binary '\0','2026-11-07 22:19:11.934512','analuizaoli.cp@gmail.com','rua da d','Ana Luiza','CLIENTE','$2a$10$RCod8ZKXGFlEzU27/j600ecDzK51xNBxgQUA9LFBBehV5kwtPDyWG','11985790395'),(3,_binary '','2026-11-08 15:23:20.988909','ana@gmail.com','rua da d','Ana Luiza','CLIENTE','$2a$10$qDsofTDBU2/iwizpE/BrFOOFJ4AvAORfslSGZnYIPNVVdhMNTLs9i','1235226464'),(4,_binary '\0','2026-11-08 12:34:00.000000','cozinheiro@darkkitchen.com','Rua das Flores, 123','Carlos Silva','COZINHEIRO','$2a$10$8c9ts6pN3lXJ8XUjmaTBPeEQHA3k/n/SHs/gmLCKi/tHfgRqTE40q','(11) 99999-9999'),(7,_binary '','2026-11-10 23:58:45.796613','cozinha@darkkitchen.com',NULL,'Rogerio Dionisio da Silva','COZINHEIRO','$2a$10$w5bW3E2TuXMVonexh0RzKO6ms9DNiVuyWTj6M2adQmtEb2u5Lm8N.',NULL),(8,_binary '','2026-11-11 00:00:44.103224','motoboy@darkkitchen.com',NULL,'Ana Luiza de Oliveira','MOTOBOY','$2a$10$1xITCaGloWC0EWLKNHpy9OROI0ZRkrembIWQTiad1rltQIeOzB8aC',NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-10 21:24:53
