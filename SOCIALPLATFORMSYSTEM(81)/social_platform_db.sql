-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 07:46 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `social_platform_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
CREATE TABLE IF NOT EXISTS `comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `content` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `post_id`, `user_id`, `content`, `created_at`) VALUES
(1, 1, 2, 'Nice post Federance!', '2025-10-15 18:18:52'),
(2, 2, 1, 'That?s great, John!', '2025-10-15 18:18:52'),
(3, 3, 1, 'Amen Claire ?', '2025-10-15 18:18:52'),
(4, 2, 2, 'you are good', '2025-10-25 06:48:40'),
(5, 5, 1, 'good', '2025-11-03 19:20:38'),
(6, 4, 1, 'Amen', '2025-11-03 20:05:09'),
(7, 1, 3, 'good', '2025-12-16 09:03:03');

-- --------------------------------------------------------

--
-- Table structure for table `follows`
--

DROP TABLE IF EXISTS `follows`;
CREATE TABLE IF NOT EXISTS `follows` (
  `id` int NOT NULL AUTO_INCREMENT,
  `follower_id` int DEFAULT NULL,
  `followee_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `follower_id` (`follower_id`),
  KEY `followee_id` (`followee_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `follows`
--

INSERT INTO `follows` (`id`, `follower_id`, `followee_id`, `created_at`) VALUES
(1, 1, 2, '2025-10-15 18:20:05'),
(2, 1, 3, '2025-10-15 18:20:05'),
(3, 2, 3, '2025-10-15 18:20:05'),
(4, 1, 4, '2025-10-21 08:01:35'),
(5, 2, 4, '2025-10-21 08:02:45'),
(6, 4, 1, '2025-10-21 08:03:25'),
(7, 3, 4, '2025-10-25 06:47:45'),
(8, 3, 1, '2025-11-03 19:12:59'),
(9, 3, 2, '2025-11-03 19:13:09'),
(10, 2, 1, '2025-11-03 19:14:38'),
(11, 4, 2, '2025-11-03 20:16:05'),
(12, 3, 5, '2025-12-16 09:03:17');

-- --------------------------------------------------------

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
CREATE TABLE IF NOT EXISTS `likes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `likes`
--

INSERT INTO `likes` (`id`, `post_id`, `user_id`, `created_at`) VALUES
(1, 1, 2, '2025-10-15 18:19:34'),
(2, 2, 1, '2025-10-15 18:19:34'),
(3, 3, 1, '2025-10-15 18:19:34'),
(4, 4, 1, '2025-10-21 08:20:47'),
(5, 5, 1, '2025-10-21 08:31:42'),
(6, 5, 1, '2025-10-21 08:31:57'),
(7, 2, 2, '2025-10-25 06:48:27'),
(8, 5, 1, '2025-11-03 19:20:32'),
(9, 7, 1, '2025-11-03 19:39:46'),
(10, 4, 1, '2025-11-03 20:01:11'),
(11, 4, 1, '2025-11-03 20:01:17'),
(12, 4, 3, '2025-12-16 09:02:54');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `content` text NOT NULL,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`id`, `sender_id`, `receiver_id`, `content`, `timestamp`) VALUES
(1, 1, 2, 'hiiii', '2025-12-15 13:21:16'),
(2, 2, 4, 'hiii', '2025-12-15 13:21:53'),
(3, 1, 2, 'how are you', '2025-12-15 14:58:01');

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
CREATE TABLE IF NOT EXISTS `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `content` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`id`, `user_id`, `content`, `created_at`, `image_path`) VALUES
(1, 1, 'Hello everyone! Welcome to my first post ?', '2025-10-15 18:18:38', NULL),
(2, 2, 'Enjoying today?s community work with friends!', '2025-10-15 18:18:38', NULL),
(3, 3, 'Feeling blessed after reading the Book of John.', '2025-10-15 18:18:38', NULL),
(10, 3, NULL, '2025-12-16 09:04:17', 'C:\\Users\\tuyif\\Documents\\Rbpicturez(161) (1).jpg'),
(5, 1, 'okayyyyyyyyyyyyy', '2025-10-21 08:31:36', NULL),
(6, 1, 'Monday mood', '2025-11-03 19:20:28', NULL),
(8, 1, 'hey today is Monday', '2025-11-03 19:55:09', NULL),
(9, 1, NULL, '2025-11-03 19:57:19', 'C:\\Users\\tuyif\\Documents\\Rbpicturez(132) (1).jpg');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(50) NOT NULL,
  `bio` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`, `bio`, `created_at`) VALUES
(1, 'federance', 'federance@gmail.com', 'fed123', 'Student & community leader', '2025-10-15 18:18:20'),
(2, 'john', 'john@gmail.com', 'john123', 'Community worker', '2025-10-15 18:18:20'),
(3, 'claire', 'claire@gmail.com', 'claire123', 'Bible reader and encourager', '2025-10-15 18:18:20'),
(4, 'kaliza', 'kaliza@gmail.com', 'kaliza123', 'Business information technology student', '2025-10-21 07:33:20'),
(5, 'Joy', 'joy@gmail.com', 'joy123', NULL, '2025-11-03 20:22:26');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
