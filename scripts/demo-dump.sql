
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
DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_no` varchar(10) NOT NULL COMMENT '工号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `age` int DEFAULT NULL COMMENT '年龄',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-女，1-男',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `id_card` varchar(255) DEFAULT NULL COMMENT '身份证号（加密）',
  `id_card_hash` varchar(64) DEFAULT NULL COMMENT '身份证号SHA256哈希',
  `bank_card` varchar(255) DEFAULT NULL COMMENT '银行卡号（加密）',
  `bank_card_hash` varchar(64) DEFAULT NULL COMMENT '银行卡号SHA256哈希',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `rank` varchar(20) DEFAULT NULL COMMENT '职级（如 P5/P6/P7）',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-离职，1-在职',
  `sort_order` int DEFAULT '0' COMMENT '自定义排序号',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
  `current_address` varchar(255) DEFAULT NULL COMMENT '现住址',
  `hukou_address` varchar(255) DEFAULT NULL COMMENT '户籍地址',
  `political_status` varchar(20) DEFAULT NULL COMMENT '政治面貌',
  `marital_status` tinyint DEFAULT '0' COMMENT '婚姻状况',
  `nation` varchar(20) DEFAULT NULL COMMENT '民族',
  `native_place` varchar(50) DEFAULT NULL COMMENT '籍贯',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_name` (`name`),
  KEY `idx_department` (`department`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'0001','徐勇',18,1,'13798580599','12455@qq.com',NULL,NULL,NULL,NULL,'研发部','Java 开发','P8',10000.00,'2026-06-24',NULL,1,900,'徐勇','13798580599','江夏区保利清能西海岸南区G38-1406',NULL,NULL,0,NULL,NULL,0,'2026-06-26 18:38:17','2026-06-27 23:17:46'),(2,'0002','测试',25,1,'13800138000','test@qq.com',NULL,NULL,NULL,NULL,'研发部','Java',NULL,10000.00,'2026-06-17',NULL,1,1000,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-26 18:38:17','2026-06-26 18:38:17'),(4,'0004','徐勇',27,1,'13798580600','12541348@qq.com',NULL,NULL,NULL,NULL,'研发部','Java 开发','P6',10000.00,'2026-06-17',NULL,1,8,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-24 23:00:12','2026-06-25 17:18:04'),(6,'0006','谢衣',25,1,'13798580588','1254138@qq.com',NULL,NULL,NULL,NULL,'design','UI 设计师','P7',10000.00,'2026-06-10',NULL,1,7,'徐勇','13798580555','',NULL,NULL,0,NULL,NULL,0,'2026-06-24 23:08:14','2026-06-25 13:26:52'),(7,'0007','云无月',27,0,'13984356688','nxhyong@163.com',NULL,NULL,NULL,NULL,'运营中心','运营主管','P6',12000.00,'2025-02-01',NULL,1,6,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-24 23:22:33','2026-06-25 14:58:53'),(8,'0008','小七',23,1,'15896668888','xuyong@163.com',NULL,NULL,NULL,NULL,'tech','java_dev','P6',12000.00,'2026-06-03',NULL,1,0,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 00:16:03','2026-06-25 23:37:15'),(9,'0009','吴敏',23,1,'13798580768','nhxuyong14@gmail.com',NULL,NULL,NULL,NULL,'product','product_manager','P6',9000.00,'2026-06-17',NULL,1,3,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 07:57:25','2026-06-25 16:18:58'),(10,'0010','阿四',22,1,'13709586588','nhxuyong@gmail.com',NULL,NULL,NULL,NULL,'finance','finance_specialist',NULL,7000.00,'2026-05-03',NULL,0,4,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 08:27:24','2026-06-27 18:13:13'),(11,'0011','小叶',24,0,'18934839655','',NULL,NULL,NULL,NULL,'hr','hr_supervisor','P7',6500.00,'2025-07-16','http://localhost:8080/uploads/avatar/f9af4b3acdaa4186b64eb956a3f78da7.jpg',1,0,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 08:28:21','2026-06-27 19:51:17'),(12,'0012','安安',22,1,'13847385566','anan@company.com','',NULL,NULL,NULL,'product','product_manager','P5',9500.00,'2026-06-02','http://localhost:8080/uploads/avatar/fe9ae875a3d342c6868208c0f46ddcea.jpg',1,0,'张妈妈','13900001111','北京市朝阳区xxx','河南省郑州市xxx','团员',0,'汉','河南郑州',0,'2026-06-25 11:40:23','2026-06-27 19:47:21'),(13,'0013','测试职级',25,1,'13912345678','test@example.com',NULL,NULL,NULL,NULL,'tech','java_dev','P6',15000.00,'2026-06-25',NULL,1,1001,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,1,'2026-06-25 13:24:55','2026-06-25 13:25:20'),(14,'0014','安之',22,1,'15855555555','',NULL,NULL,NULL,NULL,'finance','finance_specialist','P5',5000.00,'2026-06-02',NULL,1,1002,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 16:13:06','2026-06-25 16:13:06'),(15,'0015','缓存测试',30,1,'15374789754',NULL,NULL,NULL,NULL,NULL,'operation','operation_specialist','P6',10000.00,'2026-03-02',NULL,1,1003,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 19:11:20','2026-06-27 20:12:29'),(16,'0016','缓存测试员',30,1,NULL,NULL,NULL,NULL,NULL,NULL,'技术部','工程师',NULL,10000.00,NULL,NULL,1,1004,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,1,'2026-06-25 19:15:45','2026-06-25 19:16:05'),(17,'0017','测试合同',25,1,'13900000092',NULL,NULL,NULL,NULL,NULL,'product','product_manager','P6',15000.00,'2024-12-01','http://localhost:8080/uploads/avatar/e85167273fb244ffb9206783e2ab5900.jpg',1,0,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-06-25 20:04:19','2026-06-27 19:52:35'),(18,'0018','许愿',22,1,'13698580599','','',NULL,'',NULL,'tech','架构师','P8',20000.00,'2026-06-17','',1,1005,'','','','','',0,'汉','湖北武汉',0,'2026-06-27 18:12:55','2026-06-27 18:12:55'),(19,'0019','北洛',22,1,'16798580599','','',NULL,'',NULL,'tech','qa_engineer','P6',8000.00,'2026-06-26','',1,1006,'','','','','',0,'','',0,'2026-06-27 18:27:28','2026-06-27 18:27:28'),(20,'0020','笑一笑',22,1,'16789898989','','',NULL,'',NULL,'tech','frontend_dev','P6',8000.00,'2026-06-26','',1,1007,'','','','','群众',1,'','',0,'2026-06-27 18:47:31','2026-06-27 20:11:31'),(21,'0021','罗罗',28,0,'18956789876','','',NULL,'',NULL,'design','资深设计专家','P8',15000.00,'2026-04-06','',1,1008,'','','','','',0,'','',0,'2026-06-27 20:11:07','2026-06-27 20:11:07');
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `attendance_date` date NOT NULL COMMENT '考勤日期',
  `check_in_time` datetime DEFAULT NULL COMMENT '签到时间',
  `check_out_time` datetime DEFAULT NULL COMMENT '签退时间',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-正常，1-迟到，2-早退，3-缺勤，4-请假，5-出差',
  `work_hours` decimal(5,2) DEFAULT NULL COMMENT '工作时长（小时）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emp_date` (`employee_id`,`attendance_date`),
  KEY `idx_attendance_date` (`attendance_date`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工考勤记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_attendance` DISABLE KEYS */;
INSERT INTO `employee_attendance` VALUES (1,4,'2026-06-26','2026-06-26 13:39:50',NULL,1,NULL,NULL,'2026-06-26 13:39:50','2026-06-26 13:39:50'),(2,7,'2026-06-27','2026-06-27 18:45:46','2026-06-27 18:57:03',1,0.18,NULL,'2026-06-27 18:45:46','2026-06-27 18:45:46'),(3,9,'2026-06-27','2026-06-27 18:46:00',NULL,1,NULL,NULL,'2026-06-27 18:46:00','2026-06-27 18:46:00'),(4,8,'2026-06-27','2026-06-27 19:01:40',NULL,1,NULL,NULL,'2026-06-27 19:01:39','2026-06-27 19:01:39'),(5,6,'2026-06-27','2026-06-27 19:02:55',NULL,1,NULL,NULL,'2026-06-27 19:02:54','2026-06-27 19:02:54'),(6,12,'2026-06-29','2026-06-29 09:54:14',NULL,1,NULL,NULL,'2026-06-29 09:54:14','2026-06-29 09:54:14');
/*!40000 ALTER TABLE `employee_attendance` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_certificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `name` varchar(100) NOT NULL COMMENT '证书名称',
  `level` varchar(20) DEFAULT NULL COMMENT '证书级别：初级/中级/高级',
  `issuer` varchar(100) DEFAULT NULL COMMENT '发证机构',
  `issue_date` date DEFAULT NULL COMMENT '发证日期',
  `expire_date` date DEFAULT NULL COMMENT '到期日期',
  `cert_no` varchar(100) DEFAULT NULL COMMENT '证书编号',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工技能证书表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_certificate` DISABLE KEYS */;
INSERT INTO `employee_certificate` VALUES (3,12,'系统架构师','高级','工信部','2024-12-01','2027-12-01','SA-2024-001',0,'2026-06-25 19:44:11','2026-06-25 19:44:11');
/*!40000 ALTER TABLE `employee_certificate` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_contract` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `contract_no` varchar(50) DEFAULT NULL COMMENT '合同编号',
  `contract_type` varchar(20) NOT NULL COMMENT '合同类型：固定期限/无固定期限/实习/兼职/劳务派遣',
  `start_date` date NOT NULL COMMENT '合同开始日期',
  `end_date` date DEFAULT NULL COMMENT '合同结束日期',
  `probation_months` int DEFAULT '0' COMMENT '试用期月数',
  `probation_start_date` date DEFAULT NULL COMMENT '试用期开始日期',
  `probation_end_date` date DEFAULT NULL COMMENT '试用期结束日期',
  `signed_date` date DEFAULT NULL COMMENT '签订日期',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '合同约定薪资',
  `work_location` varchar(100) DEFAULT NULL COMMENT '工作地点',
  `status` tinyint DEFAULT '1' COMMENT '合同状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '合同附件URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_end_date` (`end_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工合同信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee_contract` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_education`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_education` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `school` varchar(100) NOT NULL COMMENT '学校名称',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `education` varchar(20) DEFAULT NULL COMMENT '学历：高中/大专/本科/硕士/博士',
  `degree` varchar(20) DEFAULT NULL COMMENT '学位：无/学士/硕士/博士',
  `start_date` date DEFAULT NULL COMMENT '入学日期',
  `end_date` date DEFAULT NULL COMMENT '毕业日期',
  `is_full_time` tinyint DEFAULT '1' COMMENT '是否全日制：0-否，1-是',
  `sort_order` int DEFAULT '0' COMMENT '排序（学历由高到低）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工教育经历表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_education` DISABLE KEYS */;
INSERT INTO `employee_education` VALUES (3,12,'清华大学','计算机科学','本科','学士','2018-09-01','2022-06-30',1,0,'2026-06-25 19:44:11','2026-06-25 19:44:11');
/*!40000 ALTER TABLE `employee_education` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_family`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_family` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `name` varchar(50) NOT NULL COMMENT '成员姓名',
  `relation` varchar(20) NOT NULL COMMENT '与本人关系：配偶/父亲/母亲/儿子/女儿/兄弟/姐妹/其他',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-女，1-男',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `company` varchar(100) DEFAULT NULL COMMENT '工作单位',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工家庭成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_family` DISABLE KEYS */;
INSERT INTO `employee_family` VALUES (3,12,'张父','父亲',1,'1970-01-01','某工厂','13900002222',0,'2026-06-25 19:44:11','2026-06-25 19:44:11');
/*!40000 ALTER TABLE `employee_family` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_leave`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_leave` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `leave_type` varchar(20) DEFAULT '主动离职',
  `apply_date` date NOT NULL,
  `last_work_date` date NOT NULL,
  `leave_date` date NOT NULL,
  `handover_to` bigint DEFAULT NULL,
  `handover_note` varchar(1000) DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `exit_interview_note` varchar(1000) DEFAULT NULL,
  `applicant` varchar(50) DEFAULT NULL,
  `approver` varchar(50) DEFAULT NULL,
  `approve_date` date DEFAULT NULL,
  `social_insurance_cutoff` date DEFAULT NULL,
  `housing_fund_cutoff` date DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_leave_date` (`leave_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_leave` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee_leave` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_probation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_probation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `contract_id` bigint DEFAULT NULL COMMENT '关联合同ID',
  `start_date` date NOT NULL COMMENT '试用期开始日期',
  `end_date` date NOT NULL COMMENT '试用期结束日期',
  `result` tinyint DEFAULT NULL COMMENT '考核结果',
  `result_date` date DEFAULT NULL COMMENT '考核结果日期',
  `extension_end_date` date DEFAULT NULL COMMENT '延长后结束日期',
  `evaluator` varchar(50) DEFAULT NULL COMMENT '考核人',
  `evaluation` text COMMENT '考核评语',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_end_date` (`end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工试用期记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_probation` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee_probation` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_work_experience`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_work_experience` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `company` varchar(100) NOT NULL COMMENT '公司名称',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `start_date` date DEFAULT NULL COMMENT '入职日期',
  `end_date` date DEFAULT NULL COMMENT '离职日期',
  `leave_reason` varchar(255) DEFAULT NULL COMMENT '离职原因',
  `sort_order` int DEFAULT '0' COMMENT '排序（按时间倒序）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工工作经历表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_work_experience` DISABLE KEYS */;
INSERT INTO `employee_work_experience` VALUES (3,12,'某科技公司','Java开发','后端组','2022-07-01','2026-05-31','个人发展',0,'2026-06-25 19:44:11','2026-06-25 19:44:11');
/*!40000 ALTER TABLE `employee_work_experience` ENABLE KEYS */;
DROP TABLE IF EXISTS `employee_workflow_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_workflow_change` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `change_type` varchar(20) NOT NULL,
  `effective_date` date NOT NULL,
  `before_value` text,
  `after_value` text,
  `change_summary` varchar(500) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `applicant` varchar(50) DEFAULT NULL,
  `apply_date` date DEFAULT NULL,
  `approver` varchar(50) DEFAULT NULL,
  `approve_date` date DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `employee_workflow_change` DISABLE KEYS */;
INSERT INTO `employee_workflow_change` VALUES (1,17,'ADJUST_SALARY','2024-12-01','{\"name\":\"测试合同\",\"rank\":\"P5\",\"position\":\"java_dev\",\"department\":\"tech\",\"salary\":12000.00,\"status\":1}','{\"name\":\"测试合同\",\"rank\":\"P5\",\"position\":\"java_dev\",\"department\":\"tech\",\"salary\":15000,\"status\":1}','薪资: ¥12,000.00 → ¥15,000.00（+25.00%）',1,'admin','2026-06-25','admin','2026-06-25','年终调薪',NULL,'2026-06-25 21:52:47','2026-06-25 21:52:47'),(2,17,'CONFIRM','2024-12-15','{\"name\":\"测试合同\",\"rank\":\"P5\",\"position\":\"java_dev\",\"department\":\"tech\",\"salary\":15000.00,\"status\":1}','{\"name\":\"测试合同\",\"rank\":\"P5\",\"position\":\"java_dev\",\"department\":\"tech\",\"salary\":15000.00,\"status\":1}','试用期转正：员工由试用期转为正式员工（测试合同）',1,'admin','2026-06-25','admin','2026-06-25','试用期表现优秀',NULL,'2026-06-25 21:52:47','2026-06-25 21:52:47'),(3,17,'TRANSFER','2024-12-20','{\"name\":\"测试合同\",\"rank\":\"P5\",\"position\":\"java_dev\",\"department\":\"tech\",\"salary\":15000.00,\"status\":1}','{\"name\":\"测试合同\",\"rank\":\"P6\",\"position\":\"product_mgr\",\"department\":\"product\",\"salary\":15000.00,\"status\":1}','部门: tech → product、职级: P5 → P6、职位: java_dev → product_mgr',1,'admin','2026-06-25','admin','2026-06-25','业务调整',NULL,'2026-06-25 21:52:47','2026-06-25 21:52:47'),(4,12,'TRANSFER','2026-06-25','{\"name\":\"安安\",\"rank\":\"P5\",\"position\":\"java_dev\",\"department\":\"tech\",\"salary\":9500.00,\"status\":1}','{\"name\":\"安安\",\"rank\":\"P5\",\"position\":\"product_mgr\",\"department\":\"product\",\"salary\":9500.00,\"status\":1}','部门: tech → product、职位: java_dev → product_mgr',1,'管理员','2026-06-25','CEO','2026-06-25','组织架构调整',NULL,'2026-06-25 21:56:36','2026-06-25 21:56:36'),(5,12,'ADJUST_SALARY','2026-06-20','{\"name\":\"安安\",\"rank\":\"P5\",\"position\":\"product_mgr\",\"department\":\"product\",\"salary\":9500.00,\"status\":1}','{\"name\":\"安安\",\"rank\":\"P5\",\"position\":\"product_mgr\",\"department\":\"product\",\"salary\":9500,\"status\":1}','薪资: ¥9,500.00 → ¥9,500.00（+0.00%）',1,'HR','2026-06-25','CEO','2026-06-20','绩效优异',NULL,'2026-06-25 21:56:36','2026-06-25 21:56:36'),(6,12,'CONFIRM','2026-06-10','{\"name\":\"安安\",\"rank\":\"P5\",\"position\":\"product_mgr\",\"department\":\"product\",\"salary\":9500.00,\"status\":1}','{\"name\":\"安安\",\"rank\":\"P5\",\"position\":\"product_mgr\",\"department\":\"product\",\"salary\":9500.00,\"status\":1}','试用期转正：员工由试用期转为正式员工（安安）',1,'HR','2026-06-25','部门负责人','2026-06-10','试用期表现良好',NULL,'2026-06-25 21:56:36','2026-06-25 21:56:36'),(7,6,'TRANSFER','2026-06-27','{\"name\":\"谢衣\",\"rank\":\"P5\",\"position\":\"UI 设计师\",\"department\":\"研发部\",\"salary\":10000.00,\"status\":1}','{\"name\":\"谢衣\",\"rank\":\"P7\",\"position\":\"UI 设计师\",\"department\":\"design\",\"salary\":10000.00,\"status\":1}','部门: 研发部 → design、职级: P5 → P7',1,'管理员','2026-06-27','admin','2026-06-27','哈哈哈','','2026-06-27 18:15:06','2026-06-27 18:15:06');
/*!40000 ALTER TABLE `employee_workflow_change` ENABLE KEYS */;
DROP TABLE IF EXISTS `leave_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_request` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请假记录ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `leave_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请假类型：年假、病假、事假、婚假、产假、陪产假、丧假、其他',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `days` decimal(4,1) NOT NULL COMMENT '请假天数',
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请假事由',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0=待审批，1=已批准，2=已拒绝，3=已撤销',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
  `approve_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批备注',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `leave_request` DISABLE KEYS */;
INSERT INTO `leave_request` VALUES (1,12,'年假','2026-06-30','2026-06-30',1.0,'处理个人事务',1,0,'','2026-06-29 10:25:29','2026-06-29 09:53:54','2026-06-29 10:25:29',0);
/*!40000 ALTER TABLE `leave_request` ENABLE KEYS */;
DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module` varchar(50) DEFAULT NULL COMMENT '操作模块',
  `action` varchar(50) NOT NULL COMMENT '操作类型',
  `content` varchar(500) DEFAULT NULL COMMENT '操作内容',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator` (`operator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,'员工管理','更新','更新员工：小叶',1,'admin',NULL,'2026-06-25 09:50:45'),(2,'员工管理','更新','更新员工：小七',1,'admin',NULL,'2026-06-25 09:52:48'),(3,'员工管理','更新','更新员工：吴敏（年龄、薪资）',1,'admin',NULL,'2026-06-25 10:01:58'),(4,'员工管理','更新','更新员工：吴敏（部门、职位、薪资）',1,'admin',NULL,'2026-06-25 10:02:34'),(5,'员工管理','新增','新增员工：安安',1,'admin',NULL,'2026-06-25 11:40:23'),(6,'员工管理','更新','更新员工：安安（薪资）',1,'admin',NULL,'2026-06-25 11:40:45'),(7,'员工管理','更新','更新员工：安安（职位：测试工程师 → Java 开发、薪资：¥9,000.00 → ¥10,000.00）',1,'admin',NULL,'2026-06-25 11:57:40'),(8,'员工管理','更新','更新员工：阿四（手机号：137****0599 → 137****6588、薪资：¥6,000.00 → ¥7,000.00）',1,'admin',NULL,'2026-06-25 11:58:16'),(9,'员工管理','更新','更新员工：安安（身份证号：yeBy********SVo= → 空）',1,'admin',NULL,'2026-06-25 12:59:47'),(10,'员工管理','导出','导出员工信息',1,'admin',NULL,'2026-06-25 13:00:42'),(11,'员工管理','排序','调整员工排序顺序，数量：8',1,'admin',NULL,'2026-06-25 13:01:08'),(12,'员工管理','排序','调整员工排序顺序，数量：8',1,'admin',NULL,'2026-06-25 13:03:49'),(13,'员工管理','排序','调整员工排序顺序，数量：8',1,'admin',NULL,'2026-06-25 13:03:57'),(14,'员工管理','排序','调整员工排序顺序，数量：8',1,'admin',NULL,'2026-06-25 13:04:06'),(15,'员工管理','排序','调整员工排序顺序，数量：8',1,'admin',NULL,'2026-06-25 13:06:31'),(16,'员工管理','新增','新增员工：测试职级',1,'admin',NULL,'2026-06-25 13:24:55'),(17,'员工管理','删除','删除员工：测试职级',1,'admin',NULL,'2026-06-25 13:25:20'),(18,'员工管理','更新','更新员工：谢衣（职级：空 → P5）',1,'admin',NULL,'2026-06-25 13:26:52'),(19,'员工管理','更新','更新员工：小七（职级：空 → P6）',1,'admin',NULL,'2026-06-25 13:27:13'),(20,'员工管理','更新','更新员工：安安（职级：空 → P5）',1,'admin',NULL,'2026-06-25 13:27:47'),(21,'员工管理','更新','更新员工：云无月（入职日期：2026-06-18 → 2025-02-01、职级：空 → P6）',1,'admin',NULL,'2026-06-25 14:58:53'),(22,'员工管理','新增','新增员工：安之',1,'admin',NULL,'2026-06-25 16:13:06'),(23,'员工管理','更新','更新员工：吴敏（职级：空 → P6）',1,'admin',NULL,'2026-06-25 16:18:44'),(24,'员工管理','更新','更新员工：吴敏（部门：市场部 → product、职位：operation_specialist → product_manager）',1,'admin',NULL,'2026-06-25 16:18:58'),(25,'员工管理','更新','更新员工：安安（薪资：¥10,000.00 → ¥9,000.00）',1,'admin',NULL,'2026-06-25 17:14:55'),(26,'员工管理','更新','更新员工：徐勇（年龄：18 → 27、职级：空 → P6）',1,'admin',NULL,'2026-06-25 17:18:04'),(27,'员工管理','批量调薪','批量调薪（固定金额 500），共 2 人',1,'admin',NULL,'2026-06-25 18:42:55'),(28,'员工管理','新增','新增员工：缓存测试',1,'admin',NULL,'2026-06-25 19:11:20'),(29,'员工管理','新增','新增员工：缓存测试员',1,'admin',NULL,'2026-06-25 19:15:45'),(30,'员工管理','批量删除','批量删除员工，数量：1',1,'admin',NULL,'2026-06-25 19:16:05'),(31,'认证管理','登录失败','登录失败：用户名或密码错误（admin）',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:24:55'),(32,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:24:56'),(33,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:24:59'),(34,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:25:11'),(35,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:25:24'),(36,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:25:36'),(37,'认证管理','登录失败','登录失败：用户名或密码错误（admin）',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:26:46'),(38,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:26:47'),(39,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:26:49'),(40,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:26:51'),(41,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:27:02'),(42,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:39:08'),(43,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:39:14'),(44,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:41:43'),(45,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:43:18'),(46,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:43:23'),(47,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:44:06'),(48,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 19:44:11'),(49,'员工管理','更新','更新员工：安安（邮箱: 空 → an****@company.com、身份证号: 空 → 空、部门: 研发部 → tech、职位: Java 开发 → java_dev、紧急联系人: 空 → 张妈妈、紧急电话: 空 → 139****1111、现住址: 空 → 北京市朝阳区xxx、户籍地址: 空 → 河南省郑州市xxx、政治面貌: 空 → 团员、民族: 空 → 汉、籍贯: 空 → 河南郑州）',NULL,NULL,NULL,'2026-06-25 19:44:11'),(50,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:03:47'),(51,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:03:53'),(52,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:04:19'),(53,'员工管理','新增','新增员工：测试合同',NULL,NULL,NULL,'2026-06-25 20:04:19'),(54,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:04:32'),(55,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:04:48'),(56,'员工管理','更新','更新员工：测试合同（年龄: 25 → 空、性别: 男 → 空、手机号: 139****0092 → 空、入职日期: 2024-12-01 → 空、部门: tech → 空、职位: java_dev → 空、职级: P5 → 空、薪资: ¥12,000.00 → 空）',NULL,NULL,NULL,'2026-06-25 20:04:48'),(57,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:12:53'),(58,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:15:24'),(59,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:15:26'),(60,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:15:55'),(61,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:38:44'),(62,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 20:38:56'),(63,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:45:11'),(64,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:45:18'),(65,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:46:07'),(66,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:46:07'),(67,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:52:17'),(68,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:52:24'),(69,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:52:47'),(70,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:56:07'),(71,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:56:13'),(72,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:56:21'),(73,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:56:26'),(74,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:56:36'),(75,'认证管理','登录失败','登录失败：用户名或密码错误（admin）',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:58:50'),(76,'认证管理','登录失败','登录失败：用户名或密码错误（admin）',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:59:04'),(77,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:59:33'),(78,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 21:59:51'),(79,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:00:11'),(80,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:09:09'),(81,'认证管理','登录失败','登录失败：用户名或密码错误（admin）',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:12:22'),(82,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:12:31'),(83,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:22:57'),(84,'认证管理','登录失败','登录失败：用户名或密码错误（admin）',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:30:47'),(85,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-25 22:30:58'),(86,'员工管理','更新','更新员工：安安（职位: product_mgr → product_manager）',NULL,NULL,NULL,'2026-06-25 22:55:09'),(87,'员工管理','更新','更新员工：测试合同（职位: product_mgr → product_manager）',NULL,NULL,NULL,'2026-06-25 23:28:48'),(88,'员工管理','批量调岗','批量调岗至 [tech · java_dev]，共 1 人',NULL,NULL,NULL,'2026-06-25 23:37:15'),(89,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:52:39'),(90,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:52:50'),(91,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:54:13'),(92,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:56:19'),(93,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:58:50'),(94,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:59:02'),(95,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:59:21'),(96,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 10:59:44'),(97,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 11:01:18'),(98,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 11:02:27'),(99,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 11:03:17'),(100,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 16:22:57'),(101,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 16:23:10'),(102,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 16:29:36'),(103,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 16:29:41'),(104,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 16:29:46'),(105,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 16:30:01'),(106,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:33:08'),(107,'认证管理','登录失败','登录失败：用户名或密码错误（admin），失败次数：0',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:36:20'),(108,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:36:54'),(109,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:36:58'),(110,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:37:21'),(111,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:37:43'),(112,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:38:30'),(113,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:38:41'),(114,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:39:36'),(115,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:40:53'),(116,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:41:53'),(117,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:42:24'),(118,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:45:04'),(119,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:45:39'),(120,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 18:45:52'),(121,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 20:28:40'),(122,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 20:32:49'),(123,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 20:34:39'),(124,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 20:35:48'),(125,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-26 20:36:47'),(126,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 17:47:33'),(127,'员工管理','新增','新增员工：许愿',NULL,NULL,NULL,'2026-06-27 18:12:55'),(128,'员工管理','更新','更新员工：阿四（状态: 在职 → 离职）',NULL,NULL,NULL,'2026-06-27 18:13:13'),(129,'员工管理','新增','新增员工：北洛',NULL,NULL,NULL,'2026-06-27 18:27:28'),(130,'员工管理','新增','新增员工：笑一笑',NULL,NULL,NULL,'2026-06-27 18:47:31'),(131,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 19:42:24'),(132,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 19:45:49'),(133,'员工管理','更新','更新员工：安安',NULL,NULL,NULL,'2026-06-27 19:47:21'),(134,'员工管理','更新','更新员工：小叶',NULL,NULL,NULL,'2026-06-27 19:48:15'),(135,'员工管理','更新','更新员工：小叶（职级: 空 → P7）',NULL,NULL,NULL,'2026-06-27 19:51:17'),(136,'员工管理','更新','更新员工：徐勇（职级: 空 → P8）',NULL,NULL,NULL,'2026-06-27 19:51:29'),(137,'员工管理','更新','更新员工：测试合同',NULL,NULL,NULL,'2026-06-27 19:52:35'),(138,'员工管理','新增','新增员工：罗罗',NULL,NULL,NULL,'2026-06-27 20:11:07'),(139,'员工管理','更新','更新员工：笑一笑（薪资: ¥0.00 → ¥8,000.00）',NULL,NULL,NULL,'2026-06-27 20:11:31'),(140,'员工管理','更新','更新员工：缓存测试（手机号: 空 → 153****9754、入职日期: 空 → 2026-03-02、部门: 技术部 → operation、职位: 工程师 → operation_specialist、职级: 空 → P6）',NULL,NULL,NULL,'2026-06-27 20:12:29'),(141,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 20:27:13'),(142,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 20:27:17'),(143,'员工管理','更新','更新员工：徐勇（紧急联系人: 空 → 、紧急电话: 空 → 空、现住址: 空 → 江夏区保利清能西海岸南区G38-1406）',NULL,NULL,NULL,'2026-06-27 22:39:44'),(144,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 22:55:15'),(145,'认证管理','登录成功','用户 admin 登录成功',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 22:55:17'),(146,'员工管理','更新','更新员工：徐勇（紧急联系人:  → 徐勇、紧急电话: 空 → 137****0599）',NULL,NULL,NULL,'2026-06-27 23:17:46'),(147,'认证管理','退出登录','用户 admin 退出登录',NULL,'admin','0:0:0:0:0:0:0:1','2026-06-27 23:37:51'),(148,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-27 23:37:56'),(149,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-27 23:38:29'),(150,'认证管理','登录成功','用户 hrspecialist 登录成功',NULL,'hrspecialist','0:0:0:0:0:0:0:1','2026-06-27 23:38:35'),(151,'认证管理','退出登录','用户 hrspecialist 退出登录',NULL,'hrspecialist','0:0:0:0:0:0:0:1','2026-06-27 23:39:18'),(152,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-27 23:39:32'),(153,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-28 00:07:42'),(154,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-28 00:08:06'),(155,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-28 00:08:27'),(156,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-28 00:14:36'),(157,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-28 00:15:28'),(158,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-28 00:15:30'),(159,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:12:15'),(160,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:12:17'),(161,'认证管理','登录成功','用户 hradmin 登录成功',NULL,'hradmin','0:0:0:0:0:0:0:1','2026-06-29 09:25:37'),(162,'认证管理','登录成功','用户 hradmin 登录成功',NULL,'hradmin','0:0:0:0:0:0:0:1','2026-06-29 09:25:45'),(163,'认证管理','登录成功','用户 employee 登录成功',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:25:51'),(164,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:26:57'),(165,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:27:01'),(166,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:27:27'),(167,'认证管理','登录成功','用户 hradmin 登录成功',NULL,'hradmin','0:0:0:0:0:0:0:1','2026-06-29 09:27:32'),(168,'认证管理','退出登录','用户 hradmin 退出登录',NULL,'hradmin','0:0:0:0:0:0:0:1','2026-06-29 09:27:50'),(169,'认证管理','登录成功','用户 employee 登录成功',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:27:54'),(170,'认证管理','退出登录','用户 employee 退出登录',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:28:34'),(171,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:28:49'),(172,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:29:49'),(173,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:29:53'),(174,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:43:00'),(175,'认证管理','登录成功','用户 employee 登录成功',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:43:00'),(176,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:43:11'),(177,'认证管理','登录失败','登录失败：用户名或密码错误（employee），失败次数：1',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:45:46'),(178,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:46:11'),(179,'认证管理','登录成功','用户 employee 登录成功',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:46:24'),(180,'认证管理','登录失败','登录失败：用户名或密码错误（安之），失败次数：1',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:46:59'),(181,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:47:45'),(182,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:48:23'),(183,'认证管理','登录失败','登录失败：用户名或密码错误（安之），失败次数：1',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:48:36'),(184,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:48:49'),(185,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:49:31'),(186,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:50:30'),(187,'认证管理','退出登录','用户 安之 退出登录',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 09:54:36'),(188,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:54:45'),(189,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:55:14'),(190,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:55:21'),(191,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:56:10'),(192,'认证管理','登录成功','用户 employee 登录成功',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:56:14'),(193,'认证管理','退出登录','用户 employee 退出登录',NULL,'employee','0:0:0:0:0:0:0:1','2026-06-29 09:56:20'),(194,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 09:56:28'),(195,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 10:01:43'),(196,'认证管理','登录成功','用户 徐勇_0001 登录成功',NULL,'徐勇_0001','0:0:0:0:0:0:0:1','2026-06-29 10:01:43'),(197,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 10:01:50'),(198,'认证管理','登录成功','用户 徐勇_0001 登录成功',NULL,'徐勇_0001','0:0:0:0:0:0:0:1','2026-06-29 10:01:50'),(199,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 10:03:01'),(200,'认证管理','退出登录','用户 安之 退出登录',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 10:03:45'),(201,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 10:03:52'),(202,'认证管理','退出登录','用户 superadmin 退出登录',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 10:04:40'),(203,'认证管理','登录成功','用户 谢衣 登录成功',NULL,'谢衣','0:0:0:0:0:0:0:1','2026-06-29 10:04:43'),(204,'认证管理','登录成功','用户 安之 登录成功',NULL,'安之','0:0:0:0:0:0:0:1','2026-06-29 10:11:28'),(205,'个人中心','更新资料','员工更新个人联系资料：谢衣',NULL,NULL,NULL,'2026-06-29 10:18:28'),(206,'认证管理','退出登录','用户 谢衣 退出登录',NULL,'谢衣','0:0:0:0:0:0:0:1','2026-06-29 10:23:34'),(207,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 10:23:42'),(208,'认证管理','登录成功','用户 superadmin 登录成功',NULL,'superadmin','0:0:0:0:0:0:0:1','2026-06-29 10:38:24');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
DROP TABLE IF EXISTS `salary_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salary_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `year_month` varchar(7) NOT NULL COMMENT '年月（YYYY-MM）',
  `base_salary` decimal(10,2) DEFAULT '0.00' COMMENT '基本工资',
  `performance_salary` decimal(10,2) DEFAULT '0.00' COMMENT '绩效工资',
  `allowance` decimal(10,2) DEFAULT '0.00' COMMENT '岗位津贴',
  `subsidy` decimal(10,2) DEFAULT '0.00' COMMENT '补贴',
  `overtime_pay` decimal(10,2) DEFAULT '0.00' COMMENT '加班费',
  `deduction` decimal(10,2) DEFAULT '0.00' COMMENT '扣款',
  `social_security_personal` decimal(10,2) DEFAULT '0.00' COMMENT '社保个人部分',
  `housing_fund_personal` decimal(10,2) DEFAULT '0.00' COMMENT '公积金个人部分',
  `tax` decimal(10,2) DEFAULT '0.00' COMMENT '个税',
  `actual_salary` decimal(10,2) DEFAULT '0.00' COMMENT '实发工资',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-草稿，1-已确认，2-已发放',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emp_month` (`employee_id`,`year_month`),
  KEY `idx_year_month` (`year_month`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='月度工资记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `salary_record` DISABLE KEYS */;
INSERT INTO `salary_record` VALUES (1,4,'2026-06',10000.00,0.00,0.00,0.00,0.00,0.00,1050.00,1200.00,82.50,7667.50,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(2,6,'2026-06',10000.00,0.00,0.00,0.00,0.00,0.00,1050.00,1200.00,82.50,7667.50,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(3,7,'2026-06',12000.00,0.00,0.00,0.00,0.00,0.00,1260.00,1440.00,220.00,9080.00,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(4,8,'2026-06',12000.00,0.00,0.00,0.00,0.00,0.00,1260.00,1440.00,220.00,9080.00,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(5,9,'2026-06',9000.00,0.00,0.00,0.00,0.00,0.00,945.00,1080.00,59.25,6915.75,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(6,10,'2026-06',7000.00,0.00,0.00,0.00,0.00,0.00,735.00,840.00,12.75,5412.25,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(7,11,'2026-06',6500.00,0.00,0.00,0.00,0.00,0.00,682.50,780.00,1.13,5036.37,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(8,12,'2026-06',9500.00,0.00,0.00,0.00,0.00,0.00,997.50,1140.00,70.88,7291.62,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(9,14,'2026-06',5000.00,0.00,0.00,0.00,0.00,0.00,525.00,600.00,0.00,3875.00,1,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(10,15,'2026-06',10000.00,0.00,0.00,0.00,0.00,0.00,1050.00,1200.00,82.50,7667.50,0,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(11,17,'2026-06',15000.00,0.00,0.00,0.00,0.00,0.00,1575.00,1800.00,198.75,11426.25,1,NULL,'2026-06-26 10:58:50','2026-06-26 10:58:50'),(12,1,'2026-06',10000.00,0.00,0.00,0.00,0.00,0.00,1050.00,1200.00,82.50,7667.50,0,NULL,'2026-06-27 18:50:17','2026-06-27 18:50:17'),(13,2,'2026-06',10000.00,0.00,0.00,0.00,0.00,0.00,1050.00,1200.00,82.50,7667.50,0,NULL,'2026-06-27 18:50:17','2026-06-27 18:50:17'),(14,18,'2026-06',20000.00,0.00,0.00,0.00,0.00,0.00,2100.00,2400.00,840.00,14660.00,0,NULL,'2026-06-27 18:50:17','2026-06-27 18:50:17'),(15,19,'2026-06',8000.00,0.00,0.00,0.00,0.00,0.00,840.00,960.00,36.00,6164.00,0,NULL,'2026-06-27 18:50:17','2026-06-27 18:50:17'),(16,20,'2026-06',0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0,NULL,'2026-06-27 18:50:17','2026-06-27 18:50:17');
/*!40000 ALTER TABLE `salary_record` ENABLE KEYS */;
DROP TABLE IF EXISTS `salary_structure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salary_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `base_salary` decimal(10,2) DEFAULT '0.00' COMMENT '基本工资',
  `performance_salary` decimal(10,2) DEFAULT '0.00' COMMENT '绩效工资',
  `allowance` decimal(10,2) DEFAULT '0.00' COMMENT '岗位津贴',
  `subsidy` decimal(10,2) DEFAULT '0.00' COMMENT '补贴（交通/餐饮/通讯）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工薪资结构表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `salary_structure` DISABLE KEYS */;
/*!40000 ALTER TABLE `salary_structure` ENABLE KEYS */;
DROP TABLE IF EXISTS `social_security_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `social_security_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `year_month` varchar(7) NOT NULL COMMENT '年月（YYYY-MM）',
  `pension_base` decimal(10,2) DEFAULT '0.00' COMMENT '养老保险基数',
  `pension_company_rate` decimal(5,2) DEFAULT '16.00' COMMENT '养老保险公司比例(%)',
  `pension_personal_rate` decimal(5,2) DEFAULT '8.00' COMMENT '养老保险个人比例(%)',
  `medical_base` decimal(10,2) DEFAULT '0.00' COMMENT '医疗保险基数',
  `medical_company_rate` decimal(5,2) DEFAULT '9.50' COMMENT '医疗公司比例(%)',
  `medical_personal_rate` decimal(5,2) DEFAULT '2.00' COMMENT '医疗个人比例(%)',
  `unemployment_base` decimal(10,2) DEFAULT '0.00' COMMENT '失业保险基数',
  `unemployment_company_rate` decimal(5,2) DEFAULT '0.50' COMMENT '失业公司比例(%)',
  `unemployment_personal_rate` decimal(5,2) DEFAULT '0.50' COMMENT '失业个人比例(%)',
  `injury_base` decimal(10,2) DEFAULT '0.00' COMMENT '工伤保险基数',
  `injury_company_rate` decimal(5,2) DEFAULT '0.40' COMMENT '工伤公司比例(%)',
  `maternity_base` decimal(10,2) DEFAULT '0.00' COMMENT '生育保险基数',
  `maternity_company_rate` decimal(5,2) DEFAULT '0.80' COMMENT '生育公司比例(%)',
  `housing_fund_base` decimal(10,2) DEFAULT '0.00' COMMENT '公积金基数',
  `housing_fund_company_rate` decimal(5,2) DEFAULT '12.00' COMMENT '公积金公司比例(%)',
  `housing_fund_personal_rate` decimal(5,2) DEFAULT '12.00' COMMENT '公积金个人比例(%)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_year_month` (`year_month`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社保公积金配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `social_security_config` DISABLE KEYS */;
INSERT INTO `social_security_config` VALUES (1,'2026-06',10000.00,16.00,8.00,10000.00,9.50,2.00,10000.00,0.50,0.50,10000.00,0.40,10000.00,0.80,12000.00,12.00,12.00,'2026-06-26 10:59:21','2026-06-27 18:50:01');
/*!40000 ALTER TABLE `social_security_config` ENABLE KEYS */;
DROP TABLE IF EXISTS `sys_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_code` varchar(50) DEFAULT NULL COMMENT '父级编码',
  `leader_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `leader_name` varchar(50) DEFAULT NULL COMMENT '负责人姓名',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `employee_count` int DEFAULT '0' COMMENT '员工数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sys_department` DISABLE KEYS */;
INSERT INTO `sys_department` VALUES (1,'product','产品部',NULL,NULL,NULL,2,1,NULL,0,'2026-06-26 18:40:41','2026-06-26 18:40:41',0),(2,'hr','人事部',NULL,NULL,NULL,5,1,NULL,0,'2026-06-26 18:40:41','2026-06-26 18:40:41',0),(3,'tech','技术部',NULL,7,NULL,1,1,NULL,0,'2026-06-26 18:40:41','2026-06-26 18:40:41',0),(4,'design','设计部',NULL,NULL,NULL,3,1,NULL,0,'2026-06-26 18:40:41','2026-06-26 18:40:41',0),(5,'finance','财务部',NULL,NULL,NULL,6,1,NULL,0,'2026-06-26 18:40:41','2026-06-26 18:40:41',0),(6,'operation','运营部',NULL,NULL,NULL,4,1,NULL,0,'2026-06-26 18:40:41','2026-06-26 18:40:41',0);
/*!40000 ALTER TABLE `sys_department` ENABLE KEYS */;
DROP TABLE IF EXISTS `sys_dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dictionary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL COMMENT '字典类型：department/position',
  `code` varchar(50) DEFAULT NULL COMMENT '字典编码',
  `name` varchar(50) NOT NULL COMMENT '字典名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `parent_code` varchar(50) DEFAULT NULL COMMENT '父级编码（如岗位所属部门编码）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_name` (`type`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sys_dictionary` DISABLE KEYS */;
INSERT INTO `sys_dictionary` VALUES (15,'department','tech','技术部',1,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(16,'department','product','产品部',2,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(17,'department','design','设计部',3,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(18,'department','operation','运营部',4,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(19,'department','hr','人事部',5,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(20,'department','finance','财务部',6,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(21,'position','java_dev','Java开发',1,1,'tech','2026-06-25 13:15:19','2026-06-25 13:15:19'),(22,'position','frontend_dev','前端开发',2,1,'tech','2026-06-25 13:15:19','2026-06-25 13:15:19'),(23,'position','product_manager','产品经理',3,1,'product','2026-06-25 13:15:19','2026-06-25 13:15:19'),(24,'position','ui_designer','UI设计师',4,1,'design','2026-06-25 13:15:19','2026-06-25 13:15:19'),(25,'position','qa_engineer','测试工程师',5,1,'tech','2026-06-25 13:15:19','2026-06-25 13:15:19'),(26,'position','operation_specialist','运营专员',6,1,'operation','2026-06-25 13:15:19','2026-06-25 13:15:19'),(27,'position','hr_supervisor','人事主管',7,1,'hr','2026-06-25 13:15:19','2026-06-25 13:15:19'),(28,'position','finance_specialist','财务专员',8,1,'finance','2026-06-25 13:15:19','2026-06-25 13:15:19'),(29,'rank','P5','P5',1,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(30,'rank','P6','P6',2,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(31,'rank','P7','P7',3,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(32,'rank','P8','P8',4,1,NULL,'2026-06-25 13:15:19','2026-06-25 13:15:19'),(33,'position','架构师','架构师',0,1,'tech','2026-06-25 18:54:03','2026-06-25 18:54:03'),(34,'position','资深设计专家','资深设计专家',0,1,'design','2026-06-25 18:54:49','2026-06-25 18:54:49');
/*!40000 ALTER TABLE `sys_dictionary` ENABLE KEYS */;
DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码（唯一）',
  `type` tinyint NOT NULL COMMENT '类型：1=菜单，2=按钮，3=API',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID，0表示顶级',
  `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由路径或API路径',
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标名称',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'个人中心','menu:personal',1,0,'/personal','HomeFilled',10,'2026-06-27 23:17:20'),(2,'数据看板','menu:dashboard',1,0,'/dashboard','DataLine',20,'2026-06-27 23:17:20'),(3,'员工管理','menu:employee',1,0,'/employee','UserFilled',30,'2026-06-27 23:17:20'),(4,'组织设置','menu:organization',1,0,'/organization','OfficeBuilding',40,'2026-06-27 23:17:20'),(5,'考勤管理','menu:attendance',1,0,'/attendance','Clock',50,'2026-06-27 23:17:20'),(6,'请假管理','menu:leave',1,0,'/leave','Document',60,'2026-06-27 23:17:20'),(7,'薪资社保','menu:salary',1,0,'/salary','Money',70,'2026-06-27 23:17:20'),(8,'系统设置','menu:system',1,0,'/system','Setting',80,'2026-06-27 23:17:20'),(9,'查看个人中心','personal:view',3,1,'/api/personal/**',NULL,10,'2026-06-27 23:17:20'),(10,'查看员工列表','employee:view',3,3,'/api/employee/page',NULL,10,'2026-06-27 23:17:20'),(11,'新增员工','employee:create',3,3,'/api/employee/create',NULL,20,'2026-06-27 23:17:20'),(12,'编辑员工','employee:edit',3,3,'/api/employee/update',NULL,30,'2026-06-27 23:17:20'),(13,'删除员工','employee:delete',3,3,'/api/employee/delete',NULL,40,'2026-06-27 23:17:20'),(14,'导出员工','employee:export',3,3,'/api/employee/export',NULL,50,'2026-06-27 23:17:20'),(15,'导入员工','employee:import',3,3,'/api/employee/import',NULL,60,'2026-06-27 23:17:20'),(16,'查看员工详情','employee:detail',3,3,'/api/employee/get',NULL,70,'2026-06-27 23:17:20'),(17,'查看组织架构','org:view',3,4,'/api/department/**',NULL,10,'2026-06-27 23:17:20'),(18,'管理组织架构','org:manage',3,4,'/api/department/**',NULL,20,'2026-06-27 23:17:20'),(19,'查看考勤','attendance:view',3,5,'/api/attendance/**',NULL,10,'2026-06-27 23:17:20'),(20,'管理考勤','attendance:manage',3,5,'/api/attendance/**',NULL,20,'2026-06-27 23:17:20'),(21,'打卡','attendance:checkin',3,5,'/api/attendance/create',NULL,30,'2026-06-27 23:17:20'),(22,'提交请假','leave:submit',3,6,'/api/leave/submit',NULL,10,'2026-06-27 23:17:20'),(23,'查看请假','leave:view',3,6,'/api/leave/**',NULL,20,'2026-06-27 23:17:20'),(24,'审批请假','leave:approve',3,6,'/api/leave/approve',NULL,30,'2026-06-27 23:17:20'),(25,'拒绝请假','leave:reject',3,6,'/api/leave/reject',NULL,40,'2026-06-27 23:17:20'),(26,'撤销请假','leave:cancel',3,6,'/api/leave/cancel',NULL,50,'2026-06-27 23:17:20'),(27,'查看薪资结构','salary:view',3,7,'/api/salary/structure',NULL,10,'2026-06-27 23:17:20'),(28,'管理薪资','salary:manage',3,7,'/api/salary/record',NULL,20,'2026-06-27 23:17:20'),(29,'确认工资条','salary:confirm',3,7,'/api/salary/confirm',NULL,30,'2026-06-27 23:17:20'),(30,'发放工资','salary:pay',3,7,'/api/salary/batchPay',NULL,40,'2026-06-27 23:17:20'),(31,'查看系统设置','system:view',3,8,'/api/user/**',NULL,10,'2026-06-27 23:17:20'),(32,'管理系统用户','system:manage',3,8,'/api/user/**',NULL,20,'2026-06-27 23:17:20'),(33,'查看操作日志','system:log',3,8,'/api/operation-log/**',NULL,30,'2026-06-27 23:17:20'),(34,'角色权限管理','system:role',3,8,'/api/role/**',NULL,40,'2026-06-27 23:17:20'),(35,'查看数据看板','dashboard:view',3,8,'/api/dashboard/**',NULL,50,'2026-06-27 23:17:20'),(137,'流程管理','menu:workflow',1,0,'/workflow','Document',50,'2026-06-29 10:48:05'),(156,'查看流程台账','workflow:view',3,137,'/api/workflow/page',NULL,10,'2026-06-29 10:48:05'),(157,'管理流程变更','workflow:manage',3,137,'/api/workflow/**',NULL,20,'2026-06-29 10:48:05');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码（唯一）',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0=禁用，1=启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','SUPER_ADMIN','系统最高权限，所有功能均可访问',1,1,'2026-06-27 23:17:20','2026-06-27 23:17:20'),(2,'HR管理员','HR_ADMIN','负责人事、组织、考勤、请假、薪资和系统用户，不维护角色权限',2,1,'2026-06-27 23:17:20','2026-06-29 09:20:07'),(3,'HR专员','HR_SPECIALIST','负责员工基础资料、考勤和请假流转，不查看薪资和系统设置',3,1,'2026-06-27 23:17:20','2026-06-29 09:20:07'),(4,'部门经理','DEPT_MANAGER','查看本部门员工和考勤，审批本部门请假',4,1,'2026-06-27 23:17:20','2026-06-29 09:20:07'),(5,'普通员工','EMPLOYEE','仅可查看个人中心、本人打卡、提交/撤销请假',5,1,'2026-06-27 23:17:20','2026-06-29 09:20:07');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`permission_id`),
  KEY `idx_role` (`role_id`),
  KEY `idx_perm` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=869 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (666,1,1,'2026-06-29 10:48:05'),(667,1,2,'2026-06-29 10:48:05'),(668,1,3,'2026-06-29 10:48:05'),(669,1,4,'2026-06-29 10:48:05'),(670,1,5,'2026-06-29 10:48:05'),(671,1,6,'2026-06-29 10:48:05'),(672,1,7,'2026-06-29 10:48:05'),(673,1,8,'2026-06-29 10:48:05'),(674,1,137,'2026-06-29 10:48:05'),(675,1,9,'2026-06-29 10:48:05'),(676,1,10,'2026-06-29 10:48:05'),(677,1,11,'2026-06-29 10:48:05'),(678,1,12,'2026-06-29 10:48:05'),(679,1,13,'2026-06-29 10:48:05'),(680,1,14,'2026-06-29 10:48:05'),(681,1,15,'2026-06-29 10:48:05'),(682,1,16,'2026-06-29 10:48:05'),(683,1,17,'2026-06-29 10:48:05'),(684,1,18,'2026-06-29 10:48:05'),(685,1,19,'2026-06-29 10:48:05'),(686,1,20,'2026-06-29 10:48:05'),(687,1,21,'2026-06-29 10:48:05'),(688,1,22,'2026-06-29 10:48:05'),(689,1,23,'2026-06-29 10:48:05'),(690,1,24,'2026-06-29 10:48:05'),(691,1,25,'2026-06-29 10:48:05'),(692,1,26,'2026-06-29 10:48:05'),(693,1,27,'2026-06-29 10:48:05'),(694,1,28,'2026-06-29 10:48:05'),(695,1,29,'2026-06-29 10:48:05'),(696,1,30,'2026-06-29 10:48:05'),(697,1,31,'2026-06-29 10:48:05'),(698,1,32,'2026-06-29 10:48:05'),(699,1,33,'2026-06-29 10:48:05'),(700,1,34,'2026-06-29 10:48:05'),(701,1,35,'2026-06-29 10:48:05'),(702,1,156,'2026-06-29 10:48:05'),(703,1,157,'2026-06-29 10:48:05'),(729,2,21,'2026-06-29 10:48:05'),(730,2,20,'2026-06-29 10:48:05'),(731,2,19,'2026-06-29 10:48:05'),(732,2,35,'2026-06-29 10:48:05'),(733,2,11,'2026-06-29 10:48:05'),(734,2,13,'2026-06-29 10:48:05'),(735,2,16,'2026-06-29 10:48:05'),(736,2,12,'2026-06-29 10:48:05'),(737,2,14,'2026-06-29 10:48:05'),(738,2,15,'2026-06-29 10:48:05'),(739,2,10,'2026-06-29 10:48:05'),(740,2,24,'2026-06-29 10:48:05'),(741,2,26,'2026-06-29 10:48:05'),(742,2,25,'2026-06-29 10:48:05'),(743,2,22,'2026-06-29 10:48:05'),(744,2,23,'2026-06-29 10:48:05'),(745,2,5,'2026-06-29 10:48:05'),(746,2,2,'2026-06-29 10:48:05'),(747,2,3,'2026-06-29 10:48:05'),(748,2,6,'2026-06-29 10:48:05'),(749,2,4,'2026-06-29 10:48:05'),(750,2,1,'2026-06-29 10:48:05'),(751,2,7,'2026-06-29 10:48:05'),(752,2,8,'2026-06-29 10:48:05'),(753,2,137,'2026-06-29 10:48:05'),(754,2,18,'2026-06-29 10:48:05'),(755,2,17,'2026-06-29 10:48:05'),(756,2,9,'2026-06-29 10:48:05'),(757,2,29,'2026-06-29 10:48:05'),(758,2,28,'2026-06-29 10:48:05'),(759,2,30,'2026-06-29 10:48:05'),(760,2,27,'2026-06-29 10:48:05'),(761,2,33,'2026-06-29 10:48:05'),(762,2,32,'2026-06-29 10:48:05'),(763,2,31,'2026-06-29 10:48:05'),(764,2,157,'2026-06-29 10:48:05'),(765,2,156,'2026-06-29 10:48:05'),(792,3,21,'2026-06-29 10:48:05'),(793,3,20,'2026-06-29 10:48:05'),(794,3,19,'2026-06-29 10:48:05'),(795,3,35,'2026-06-29 10:48:05'),(796,3,11,'2026-06-29 10:48:05'),(797,3,16,'2026-06-29 10:48:05'),(798,3,12,'2026-06-29 10:48:05'),(799,3,10,'2026-06-29 10:48:05'),(800,3,24,'2026-06-29 10:48:05'),(801,3,26,'2026-06-29 10:48:05'),(802,3,25,'2026-06-29 10:48:05'),(803,3,22,'2026-06-29 10:48:05'),(804,3,23,'2026-06-29 10:48:05'),(805,3,5,'2026-06-29 10:48:05'),(806,3,2,'2026-06-29 10:48:05'),(807,3,3,'2026-06-29 10:48:05'),(808,3,6,'2026-06-29 10:48:05'),(809,3,1,'2026-06-29 10:48:05'),(810,3,137,'2026-06-29 10:48:05'),(811,3,9,'2026-06-29 10:48:05'),(812,3,157,'2026-06-29 10:48:05'),(813,3,156,'2026-06-29 10:48:05'),(823,4,21,'2026-06-29 10:48:05'),(824,4,19,'2026-06-29 10:48:05'),(825,4,16,'2026-06-29 10:48:05'),(826,4,10,'2026-06-29 10:48:05'),(827,4,24,'2026-06-29 10:48:05'),(828,4,26,'2026-06-29 10:48:05'),(829,4,25,'2026-06-29 10:48:05'),(830,4,22,'2026-06-29 10:48:05'),(831,4,23,'2026-06-29 10:48:05'),(832,4,5,'2026-06-29 10:48:05'),(833,4,3,'2026-06-29 10:48:05'),(834,4,6,'2026-06-29 10:48:05'),(835,4,1,'2026-06-29 10:48:05'),(836,4,137,'2026-06-29 10:48:05'),(837,4,9,'2026-06-29 10:48:05'),(838,4,156,'2026-06-29 10:48:05'),(854,5,21,'2026-06-29 10:48:05'),(855,5,19,'2026-06-29 10:48:05'),(856,5,26,'2026-06-29 10:48:05'),(857,5,22,'2026-06-29 10:48:05'),(858,5,23,'2026-06-29 10:48:05'),(859,5,5,'2026-06-29 10:48:05'),(860,5,6,'2026-06-29 10:48:05'),(861,5,1,'2026-06-29 10:48:05'),(862,5,9,'2026-06-29 10:48:05');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `employee_id` bigint DEFAULT NULL COMMENT '关联员工ID',
  `dept_data_scope` tinyint DEFAULT '0' COMMENT '数据权限：0=仅本人，1=本部门，2=本部门及下级，3=全部',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `role` varchar(20) DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_sys_user_employee_id` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','管理员',NULL,0,NULL,'SUPER_ADMIN',1,'2026-06-25 00:04:57','2026-06-27 23:17:20'),(2,'安安','$2a$10$qpzHS5w7IJxh5iLcmCEuoekYOD8kQQItHGi.0ATuTvonZTTAt21IO','安安',12,0,'员工默认账号','EMPLOYEE',1,'2026-06-25 18:15:34','2026-06-29 10:01:32'),(3,'superadmin','$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K','超级管理员',NULL,3,'测试账号：超级管理员，拥有所有权限','SUPER_ADMIN',1,'2026-06-27 23:22:15','2026-06-27 23:22:15'),(4,'hradmin','$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K','HR管理员',NULL,2,'测试账号：HR管理员，除系统管理外全部权限','HR_ADMIN',1,'2026-06-27 23:22:15','2026-06-27 23:22:15'),(5,'hrspecialist','$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K','HR专员',NULL,2,'测试账号：HR专员，可管理员工和考勤','HR_SPECIALIST',1,'2026-06-27 23:22:15','2026-06-27 23:22:15'),(6,'deptmanager','$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K','部门经理',NULL,1,'测试账号：部门经理，可审批本部门请假','DEPT_MANAGER',1,'2026-06-27 23:22:15','2026-06-27 23:22:15'),(7,'employee','$2b$12$UBfI7TqSieEq5kQJ9uR51eNbykHAZ5UEDAb2nQyoT6rd/RdnXF28K','普通员工',NULL,0,'测试账号：普通员工，仅个人中心/打卡/请假','EMPLOYEE',1,'2026-06-27 23:22:15','2026-06-27 23:22:15'),(19,'云无月','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','云无月',7,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(20,'北洛','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','北洛',19,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(21,'吴敏','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','吴敏',9,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(22,'安之','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','安之',14,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(23,'小七','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','小七',8,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(24,'小叶','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','小叶',11,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(25,'徐勇_0001','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','徐勇',1,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(26,'徐勇_0004','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','徐勇',4,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(27,'测试','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','测试',2,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(28,'测试合同','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','测试合同',17,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(29,'笑一笑','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','笑一笑',20,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(30,'缓存测试','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','缓存测试',15,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(31,'罗罗','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','罗罗',21,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(32,'许愿','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','许愿',18,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(33,'谢衣','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','谢衣',6,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32'),(34,'阿四','$2a$10$HKtNm2e0Y/Twm6/YKNDJ9uaJXcFwTxOF84xUdAtLs92gPwyXXkTIO','阿四',10,0,'员工默认账号，默认密码：123456','EMPLOYEE',1,'2026-06-29 10:01:32','2026-06-29 10:01:32');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

