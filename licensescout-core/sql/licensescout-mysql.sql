-- phpMyAdmin SQL Dump
-- version 4.8.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 03. Aug 2018 um 14:30
-- Server-Version: 10.1.32-MariaDB
-- PHP-Version: 5.6.36

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO,ANSI_QUOTES";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `licensescout`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `Builds`
--

CREATE TABLE "Builds" (
  `Id` int(11) NOT NULL,
  `Buildname` varchar(255) NOT NULL,
  `Version` varchar(255) NOT NULL,
  `URL_Build` varchar(500) NOT NULL,
  `URL_Licensereport_CSV` varchar(500) NOT NULL,
  `URL_Licensereport_HTML` varchar(500) NOT NULL,
  `URL_Licensereport_TXT` varchar(500) NOT NULL,
  `Datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `DetectedLicenses`
--

CREATE TABLE "DetectedLicenses" (
  `Id` int(11) NOT NULL,
  `FK_LibraryData_Id` int(11) NOT NULL,
  `License_Name` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `LibraryData`
--

CREATE TABLE "LibraryData" (
  `Id` int(11) NOT NULL,
  `FK_Build_Id` int(11) NOT NULL,
  `Selected_License` varchar(500) NOT NULL,
  `Filename` varchar(500) NOT NULL,
  `Path` varchar(500) NOT NULL,
  `Provider` varchar(500) NOT NULL,
  `Version` varchar(255) NOT NULL,
  `Type` varchar(100) NOT NULL,
  `Message_Digest` varchar(255) NOT NULL,
  `Detection_Status` varchar(255) NOT NULL,
  `Legal_Status` varchar(255) NOT NULL,
  `Documentation_Link` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `Builds`
--
ALTER TABLE "Builds"
  ADD PRIMARY KEY (`Id`);

--
-- Indizes für die Tabelle `DetectedLicenses`
--
ALTER TABLE "DetectedLicenses"
  ADD PRIMARY KEY (`Id`);

--
-- Indizes für die Tabelle `LibraryData`
--
ALTER TABLE "LibraryData"
  ADD PRIMARY KEY (`Id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `Builds`
--
ALTER TABLE `Builds`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT für Tabelle `DetectedLicenses`
--
ALTER TABLE `DetectedLicenses`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4292;

--
-- AUTO_INCREMENT für Tabelle `LibraryData`
--
ALTER TABLE `LibraryData`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2952;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
