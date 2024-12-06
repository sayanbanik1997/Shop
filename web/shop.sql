-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 06, 2024 at 07:04 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `shop`
--

-- --------------------------------------------------------

--
-- Table structure for table `customerr_tbl`
--

CREATE TABLE `customerr_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customerr_tbl`
--

INSERT INTO `customerr_tbl` (`id`, `name`, `datetime`) VALUES
(1, 'cus1', '2024-12-05 16:18:35');

-- --------------------------------------------------------

--
-- Table structure for table `payment_tbl`
--

CREATE TABLE `payment_tbl` (
  `id` int(11) NOT NULL,
  `billId` int(11) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `dateOfPayment` date DEFAULT NULL,
  `cusId` int(11) DEFAULT NULL,
  `supId` int(11) DEFAULT NULL,
  `amount` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment_tbl`
--

INSERT INTO `payment_tbl` (`id`, `billId`, `datetime`, `dateOfPayment`, `cusId`, `supId`, `amount`) VALUES
(4, 13, '2024-12-05 16:15:13', '2024-12-05', NULL, 10, NULL),
(5, 14, '2024-12-05 16:19:47', '2024-12-05', 1, NULL, NULL),
(6, 15, '2024-12-05 17:48:22', '2024-12-05', NULL, 10, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `prod_entry_tbl`
--

CREATE TABLE `prod_entry_tbl` (
  `id` int(11) NOT NULL,
  `cusId` int(11) DEFAULT NULL,
  `dateOfPurchase` date DEFAULT NULL,
  `dateTimeOfEntry` datetime DEFAULT NULL,
  `purOrSell` int(11) DEFAULT NULL,
  `soldUnsold` int(11) DEFAULT NULL,
  `due` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_entry_tbl`
--

INSERT INTO `prod_entry_tbl` (`id`, `cusId`, `dateOfPurchase`, `dateTimeOfEntry`, `purOrSell`, `soldUnsold`, `due`) VALUES
(13, 10, '2024-12-05', '2024-12-05 16:15:13', 1, 1, 3106),
(14, 1, '2024-12-05', '2024-12-05 16:19:47', 2, 1, 35255),
(15, 10, '2024-12-05', '2024-12-05 17:48:22', 1, 1, 83249);

-- --------------------------------------------------------

--
-- Table structure for table `prod_list_tbl`
--

CREATE TABLE `prod_list_tbl` (
  `id` int(11) NOT NULL,
  `prodEntryTblId` int(11) NOT NULL,
  `prodId` int(11) NOT NULL,
  `boxQuan` double DEFAULT NULL,
  `prodQuan` double DEFAULT NULL,
  `totalAmount` double DEFAULT NULL,
  `unit` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_list_tbl`
--

INSERT INTO `prod_list_tbl` (`id`, `prodEntryTblId`, `prodId`, `boxQuan`, `prodQuan`, `totalAmount`, `unit`) VALUES
(21, 13, 5, 4, 39, 2106, 'p'),
(22, 13, 4, 2, 100, 2000, 'p'),
(23, 14, 7, 5, 665, 37240, 'p'),
(24, 14, 6, 5, 100, 2000, 'p'),
(25, 15, 8, 5, 300, 600, 'p'),
(26, 15, 14, 89, 66, 528, 'h'),
(27, 15, 15, 8, 69, 6072, 'n'),
(28, 15, 13, 49, 64, 3712, 'u'),
(29, 15, 12, 6, 64, 4096, 'i'),
(30, 15, 9, 4, 649, 41536, 'p'),
(31, 15, 4, 6, 200, 4000, 'p'),
(32, 15, 10, 46, 949, 4745, 'l'),
(33, 15, 11, 76, 949, 929071, 'is');

-- --------------------------------------------------------

--
-- Table structure for table `prod_tbl`
--

CREATE TABLE `prod_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_tbl`
--

INSERT INTO `prod_tbl` (`id`, `name`, `datetime`) VALUES
(4, 'prod', '2024-12-03 18:41:39'),
(5, 'prod1', '2024-12-03 18:42:14'),
(6, 'prod2', '2024-12-05 11:34:47'),
(7, 'prod3', '2024-12-05 11:35:01'),
(8, 'p1', '2024-12-05 17:45:25'),
(9, 'p2', '2024-12-05 17:45:40'),
(10, 'p3', '2024-12-05 17:45:57'),
(11, 'p4', '2024-12-05 17:46:20'),
(12, 'p5', '2024-12-05 17:46:34'),
(13, 'p6', '2024-12-05 17:46:53'),
(14, 'p7', '2024-12-05 17:47:12'),
(15, 'multi\nline', '2024-12-05 17:47:33'),
(16, 'p8', '2024-12-05 17:48:44');

-- --------------------------------------------------------

--
-- Table structure for table `supplier_tbl`
--

CREATE TABLE `supplier_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier_tbl`
--

INSERT INTO `supplier_tbl` (`id`, `name`, `datetime`) VALUES
(10, 'sup', '2024-12-03 18:41:25'),
(11, 'bapi da', '2024-12-05 16:01:26');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customerr_tbl`
--
ALTER TABLE `customerr_tbl`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `payment_tbl`
--
ALTER TABLE `payment_tbl`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `prod_entry_tbl`
--
ALTER TABLE `prod_entry_tbl`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `prod_list_tbl`
--
ALTER TABLE `prod_list_tbl`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `prod_tbl`
--
ALTER TABLE `prod_tbl`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `supplier_tbl`
--
ALTER TABLE `supplier_tbl`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `customerr_tbl`
--
ALTER TABLE `customerr_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `payment_tbl`
--
ALTER TABLE `payment_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `prod_entry_tbl`
--
ALTER TABLE `prod_entry_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `prod_list_tbl`
--
ALTER TABLE `prod_list_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `prod_tbl`
--
ALTER TABLE `prod_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `supplier_tbl`
--
ALTER TABLE `supplier_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
