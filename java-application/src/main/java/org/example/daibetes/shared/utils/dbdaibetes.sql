-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 11, 2026 at 02:33 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbdaibetes`
--

-- --------------------------------------------------------

--
-- Table structure for table `tblconsultationrequest`
--

CREATE TABLE `tblconsultationrequest` (
  `request_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL,
  `p_id` int(11) NOT NULL,
  `d_id` int(11) NOT NULL,
  `is_accepted` tinyint(1) NOT NULL DEFAULT 0,
  `requested_on` datetime NOT NULL DEFAULT current_timestamp(),
  `responded_on` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tblcriticality`
--

CREATE TABLE `tblcriticality` (
  `criticality_id` int(11) NOT NULL,
  `criticality_lvl` varchar(255) NOT NULL,
  `reasoning` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tbldoctor`
--

CREATE TABLE `tbldoctor` (
  `d_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `doctor_idcard_id` int(11) DEFAULT NULL,
  `license_number` varchar(50) NOT NULL,
  `hospital` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbldoctor`
--

INSERT INTO `tbldoctor` (`d_id`, `user_id`, `doctor_idcard_id`, `license_number`, `hospital`) VALUES
(1, 1, 1, 'LIC-2026-001', 'Cebu City Medical Center'),
(2, 2, 2, 'LIC-2026-002', 'Chong Hua Hospital');

-- --------------------------------------------------------

--
-- Table structure for table `tblfindings`
--

CREATE TABLE `tblfindings` (
  `evaluation_id` int(11) NOT NULL,
  `final_dr_grade` varchar(255) NOT NULL,
  `macular_edema` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tblimage`
--

CREATE TABLE `tblimage` (
  `img_id` int(11) NOT NULL,
  `image_name` varchar(255) NOT NULL,
  `image_type_id` int(1) DEFAULT NULL,
  `uploaded_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `image_data` longblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tblimage`
--

INSERT INTO `tblimage` (`img_id`, `image_name`, `image_type_id`, `uploaded_on`, `image_data`) VALUES
(1, 'doctor1_id.png', 2, '2026-05-06 04:11:47', ''),
(2, 'doctor2_id.png', 2, '2026-05-06 04:11:47', ''),
(3, 'patient1_raw_scan.png', 1, '2026-05-06 04:11:47', ''),
(4, 'patient2_raw_scan.png', 1, '2026-05-06 04:11:47', ''),
(5, 'patient3_raw_scan.png', 1, '2026-05-06 04:11:47', ''),
(6, 'patient4_raw_scan.png', 1, '2026-05-06 04:11:47', ''),
(7, 'patient5_raw_scan.png', 1, '2026-05-06 04:11:47', ''),
(8, 'patient1_filtered.png', 3, '2026-05-06 04:11:47', ''),
(9, 'patient2_filtered.png', 3, '2026-05-06 04:11:47', ''),
(10, 'patient3_filtered.png', 3, '2026-05-06 04:11:47', ''),
(11, 'patient4_filtered.png', 3, '2026-05-06 04:11:47', ''),
(12, 'patient5_filtered.png', 3, '2026-05-06 04:11:47', ''),
(13, 'patient1_detection.png', 4, '2026-05-06 04:11:47', ''),
(14, 'patient2_detection.png', 4, '2026-05-06 04:11:47', ''),
(15, 'patient3_detection.png', 4, '2026-05-06 04:11:47', ''),
(16, 'patient4_detection.png', 4, '2026-05-06 04:11:47', ''),
(17, 'patient5_detection.png', 4, '2026-05-06 04:11:47', '');

-- --------------------------------------------------------

--
-- Table structure for table `tblpathological`
--

CREATE TABLE `tblpathological` (
  `findings_id` int(11) NOT NULL,
  `1` varchar(255) NOT NULL,
  `2` varchar(255) NOT NULL,
  `3` varchar(255) NOT NULL,
  `4` varchar(255) NOT NULL,
  `5` varchar(255) NOT NULL,
  `6` varchar(255) NOT NULL,
  `7` varchar(255) NOT NULL,
  `8` varchar(255) NOT NULL,
  `9` varchar(255) NOT NULL,
  `10` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tblpatient`
--

CREATE TABLE `tblpatient` (
  `p_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `age` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tblpatient`
--

INSERT INTO `tblpatient` (`p_id`, `user_id`, `age`) VALUES
(1, 3, 24),
(2, 4, 25),
(3, 5, 23),
(4, 6, 26),
(5, 7, 27);

-- --------------------------------------------------------

--
-- Table structure for table `tblrecommendations`
--

CREATE TABLE `tblrecommendations` (
  `recommendation_id` int(11) NOT NULL,
  `isAnnual` tinyint(1) NOT NULL,
  `isSixMonth` tinyint(1) NOT NULL,
  `isRefer` tinyint(1) NOT NULL,
  `isUrgent` tinyint(1) NOT NULL,
  `isLaser` tinyint(1) NOT NULL,
  `isVEGF` tinyint(1) NOT NULL,
  `final_notes` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tblreport`
--

CREATE TABLE `tblreport` (
  `report_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL COMMENT 'raw image, patient and doctor name',
  `criticality_id` int(11) NOT NULL COMMENT 'criticality lvl',
  `findings_id` int(11) NOT NULL COMMENT 'pathological findings',
  `recommendations_id` int(11) NOT NULL COMMENT 'management and recommendations',
  `evaluation_id` int(11) NOT NULL COMMENT 'evaluation'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='diagnostic report generator';

-- --------------------------------------------------------

--
-- Table structure for table `tbltests`
--

CREATE TABLE `tbltests` (
  `test_id` int(11) NOT NULL,
  `p_id` int(11) NOT NULL,
  `d_id` int(11) NOT NULL,
  `raw_img_id` int(11) NOT NULL,
  `tested_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tbluser`
--

CREATE TABLE `tbluser` (
  `user_id` int(11) NOT NULL,
  `firstname` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `contact_number` varchar(15) NOT NULL,
  `gender` varchar(6) NOT NULL,
  `birthdate` date NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbluser`
--

INSERT INTO `tbluser` (`user_id`, `firstname`, `lastname`, `email`, `password`, `contact_number`, `gender`, `birthdate`, `created_on`) VALUES
(1, 'John', 'Cruz', 'doctor1@gmail.com', 'Password123', '09123456789', 'Male', '1985-04-12', '2026-05-06 04:11:47'),
(2, 'Maria', 'Santos', 'doctor2@gmail.com', 'Password123', '09987654321', 'Female', '1990-08-20', '2026-05-06 04:11:47'),
(3, 'Ana', 'Reyes', 'patient1@gmail.com', 'Password123', '09111111111', 'Female', '2002-01-15', '2026-05-06 04:11:47'),
(4, 'Mark', 'Dela Cruz', 'patient2@gmail.com', 'Password123', '09222222222', 'Male', '2001-06-10', '2026-05-06 04:11:47'),
(5, 'Jessa', 'Lim', 'patient3@gmail.com', 'Password123', '09333333333', 'Female', '2003-09-25', '2026-05-06 04:11:47'),
(6, 'Carlo', 'Garcia', 'patient4@gmail.com', 'Password123', '09444444444', 'Male', '2000-03-18', '2026-05-06 04:11:47'),
(7, 'Mika', 'Tan', 'patient5@gmail.com', 'Password123', '09555555555', 'Female', '1999-12-05', '2026-05-06 04:11:47');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tblconsultationrequest`
--
ALTER TABLE `tblconsultationrequest`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `test_id` (`test_id`),
  ADD KEY `p_id` (`p_id`),
  ADD KEY `d_id` (`d_id`);

--
-- Indexes for table `tblcriticality`
--
ALTER TABLE `tblcriticality`
  ADD PRIMARY KEY (`criticality_id`);

--
-- Indexes for table `tbldoctor`
--
ALTER TABLE `tbldoctor`
  ADD PRIMARY KEY (`d_id`),
  ADD UNIQUE KEY `license_number` (`license_number`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `doctor_idcard_id` (`doctor_idcard_id`);

--
-- Indexes for table `tblfindings`
--
ALTER TABLE `tblfindings`
  ADD PRIMARY KEY (`evaluation_id`);

--
-- Indexes for table `tblimage`
--
ALTER TABLE `tblimage`
  ADD PRIMARY KEY (`img_id`);

--
-- Indexes for table `tblpathological`
--
ALTER TABLE `tblpathological`
  ADD PRIMARY KEY (`findings_id`);

--
-- Indexes for table `tblpatient`
--
ALTER TABLE `tblpatient`
  ADD PRIMARY KEY (`p_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `tblrecommendations`
--
ALTER TABLE `tblrecommendations`
  ADD PRIMARY KEY (`recommendation_id`);

--
-- Indexes for table `tblreport`
--
ALTER TABLE `tblreport`
  ADD PRIMARY KEY (`report_id`),
  ADD KEY `criticality_id` (`criticality_id`),
  ADD KEY `evaluation_id` (`evaluation_id`),
  ADD KEY `findings_id` (`findings_id`),
  ADD KEY `test_id` (`test_id`),
  ADD KEY `recommendations_id` (`recommendations_id`);

--
-- Indexes for table `tbltests`
--
ALTER TABLE `tbltests`
  ADD PRIMARY KEY (`test_id`),
  ADD KEY `d_id` (`d_id`),
  ADD KEY `p_id` (`p_id`),
  ADD KEY `raw_img_id` (`raw_img_id`);

--
-- Indexes for table `tbluser`
--
ALTER TABLE `tbluser`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tblconsultationrequest`
--
ALTER TABLE `tblconsultationrequest`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tblcriticality`
--
ALTER TABLE `tblcriticality`
  MODIFY `criticality_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tbldoctor`
--
ALTER TABLE `tbldoctor`
  MODIFY `d_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `tblfindings`
--
ALTER TABLE `tblfindings`
  MODIFY `evaluation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `tblimage`
--
ALTER TABLE `tblimage`
  MODIFY `img_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `tblpathological`
--
ALTER TABLE `tblpathological`
  MODIFY `findings_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tblpatient`
--
ALTER TABLE `tblpatient`
  MODIFY `p_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `tblrecommendations`
--
ALTER TABLE `tblrecommendations`
  MODIFY `recommendation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `tblreport`
--
ALTER TABLE `tblreport`
  MODIFY `report_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `tbltests`
--
ALTER TABLE `tbltests`
  MODIFY `test_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `tbluser`
--
ALTER TABLE `tbluser`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tblconsultationrequest`
--
ALTER TABLE `tblconsultationrequest`
  ADD CONSTRAINT `tblconsultationrequest_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `tbltests` (`test_id`),
  ADD CONSTRAINT `tblconsultationrequest_ibfk_2` FOREIGN KEY (`p_id`) REFERENCES `tblpatient` (`p_id`),
  ADD CONSTRAINT `tblconsultationrequest_ibfk_3` FOREIGN KEY (`d_id`) REFERENCES `tbldoctor` (`d_id`);

--
-- Constraints for table `tbldoctor`
--
ALTER TABLE `tbldoctor`
  ADD CONSTRAINT `tbldoctor_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbluser` (`user_id`),
  ADD CONSTRAINT `tbldoctor_ibfk_2` FOREIGN KEY (`doctor_idcard_id`) REFERENCES `tblimage` (`img_id`);

--
-- Constraints for table `tblpatient`
--
ALTER TABLE `tblpatient`
  ADD CONSTRAINT `tblpatient_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbluser` (`user_id`);

--
-- Constraints for table `tblreport`
--
ALTER TABLE `tblreport`
  ADD CONSTRAINT `tblreport_ibfk_1` FOREIGN KEY (`criticality_id`) REFERENCES `tblcriticality` (`criticality_id`),
  ADD CONSTRAINT `tblreport_ibfk_2` FOREIGN KEY (`evaluation_id`) REFERENCES `tblfindings` (`evaluation_id`),
  ADD CONSTRAINT `tblreport_ibfk_3` FOREIGN KEY (`findings_id`) REFERENCES `tblpathological` (`findings_id`),
  ADD CONSTRAINT `tblreport_ibfk_4` FOREIGN KEY (`test_id`) REFERENCES `tbltests` (`test_id`),
  ADD CONSTRAINT `tblreport_ibfk_5` FOREIGN KEY (`recommendations_id`) REFERENCES `tblrecommendations` (`recommendation_id`);

--
-- Constraints for table `tbltests`
--
ALTER TABLE `tbltests`
  ADD CONSTRAINT `tbltests_ibfk_1` FOREIGN KEY (`d_id`) REFERENCES `tbldoctor` (`d_id`),
  ADD CONSTRAINT `tbltests_ibfk_2` FOREIGN KEY (`p_id`) REFERENCES `tblpatient` (`p_id`),
  ADD CONSTRAINT `tbltests_ibfk_3` FOREIGN KEY (`raw_img_id`) REFERENCES `tblimage` (`img_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
