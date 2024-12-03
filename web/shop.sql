-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 03, 2024 at 07:54 PM
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
  `paid` double DEFAULT NULL,
  `due` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prod_entry_tbl`
--

INSERT INTO `prod_entry_tbl` (`id`, `cusId`, `dateOfPurchase`, `dateTimeOfEntry`, `purOrSell`, `soldUnsold`, `paid`, `due`) VALUES
(9, 10, '2024-12-03', '2024-12-03 23:05:59', NULL, NULL, NULL, NULL);

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
(15, 9, 5, 4, 1000, 30000, 'pair'),
(16, 9, 4, 3, 100, 2000, 'pc');

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
(5, 'prod1', '2024-12-03 18:42:14');

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
(10, 'sup', '2024-12-03 18:41:25');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customerr_tbl`
--
ALTER TABLE `customerr_tbl`
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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `prod_entry_tbl`
--
ALTER TABLE `prod_entry_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `prod_list_tbl`
--
ALTER TABLE `prod_list_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `prod_tbl`
--
ALTER TABLE `prod_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `supplier_tbl`
--
ALTER TABLE `supplier_tbl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
