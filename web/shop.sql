-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 02, 2025 at 03:34 PM
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
  `datetime` datetime DEFAULT NULL,
  `updated` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customerr_tbl`
--

INSERT INTO `customerr_tbl` (`id`, `name`, `datetime`, `updated`) VALUES
(1, 'cus1', '2024-12-05 16:18:35', NULL),
(2, 'cus2', '2024-12-07 22:23:27', NULL);

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
  `amount` double DEFAULT NULL,
  `updatedTo` int(11) DEFAULT NULL,
  `updatedFrom` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment_tbl`
--

INSERT INTO `payment_tbl` (`id`, `billId`, `datetime`, `dateOfPayment`, `cusId`, `supId`, `amount`, `updatedTo`, `updatedFrom`) VALUES
(44, 44, '2024-12-30 17:57:52', '2024-12-30', 1, NULL, 36, 46, NULL),
(46, 44, '2024-12-30 18:51:32', '2024-12-30', 1, NULL, 3, NULL, 44),
(47, 44, '2024-12-31 18:38:41', '2024-12-31', 1, NULL, 4, NULL, NULL);

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
  `haveToPay` double DEFAULT NULL,
  `updatedTo` int(11) DEFAULT NULL,
  `updatedFrom` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_entry_tbl`
--

INSERT INTO `prod_entry_tbl` (`id`, `cusId`, `dateOfPurchase`, `dateTimeOfEntry`, `purOrSell`, `soldUnsold`, `haveToPay`, `updatedTo`, `updatedFrom`) VALUES
(44, 1, '2024-12-30', '2024-12-30 17:57:52', 2, 1, 36, 45, NULL),
(45, 1, '2024-12-30', '2024-12-30 18:13:31', 2, 1, 39, 46, 44),
(46, 1, '2024-12-30', '2024-12-30 18:21:11', 2, 1, 50, NULL, 45);

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
  `unit` varchar(10) DEFAULT NULL,
  `updatedTo` int(11) DEFAULT NULL,
  `updatedFrom` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_list_tbl`
--

INSERT INTO `prod_list_tbl` (`id`, `prodEntryTblId`, `prodId`, `boxQuan`, `prodQuan`, `totalAmount`, `unit`, `updatedTo`, `updatedFrom`) VALUES
(61, 44, 14, 9, 9, 81, '', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `prod_tbl`
--

CREATE TABLE `prod_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `updated` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_tbl`
--

INSERT INTO `prod_tbl` (`id`, `name`, `datetime`, `updated`) VALUES
(4, 'prod', '2024-12-03 18:41:39', NULL),
(5, 'prod1', '2024-12-03 18:42:14', NULL),
(6, 'prod2', '2024-12-05 11:34:47', NULL),
(7, 'prod3', '2024-12-05 11:35:01', NULL),
(8, 'p1', '2024-12-05 17:45:25', NULL),
(9, 'p2', '2024-12-05 17:45:40', NULL),
(10, 'p3', '2024-12-05 17:45:57', NULL),
(11, 'p4', '2024-12-05 17:46:20', NULL),
(12, 'p5', '2024-12-05 17:46:34', NULL),
(13, 'p6', '2024-12-05 17:46:53', NULL),
(14, 'p7', '2024-12-05 17:47:12', NULL),
(15, 'multi\nline', '2024-12-05 17:47:33', NULL),
(16, 'p8', '2024-12-05 17:48:44', NULL),
(17, 'p9', '2024-12-09 14:39:03', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `supplier_tbl`
--

CREATE TABLE `supplier_tbl` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `updated` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `supplier_tbl`
--

INSERT INTO `supplier_tbl` (`id`, `name`, `datetime`, `updated`) VALUES
(10, 'sup', '2024-12-03 18:41:25', NULL),
(11, 'bapi da', '2024-12-05 16:01:26', NULL),
(12, 'jontropat', '2024-12-07 22:25:56', NULL);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `payment_tbl`
--
ALTER TABLE `payment_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `prod_entry_tbl`
--
ALTER TABLE `prod_entry_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `prod_list_tbl`
--
ALTER TABLE `prod_list_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT for table `prod_tbl`
--
ALTER TABLE `prod_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `supplier_tbl`
--
ALTER TABLE `supplier_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
