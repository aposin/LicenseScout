SET DATABASE SQL SYNTAX MYS TRUE
-- SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO,ANSI_QUOTES";
-- SET AUTOCOMMIT = 0;
START TRANSACTION;
-- SET time_zone = "+00:00";

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
);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `DetectedLicenses`
--

CREATE TABLE "DetectedLicenses" (
  `Id` int(11) NOT NULL,
  `FK_LibraryData_Id` int(11) NOT NULL,
  `License_Name` varchar(500) NOT NULL
);

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
);

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
ALTER TABLE "Builds" ALTER COLUMN "Id" SET NOT NULL;
ALTER TABLE "Builds" ALTER COLUMN "Id" int(11) IDENTITY;

--
-- AUTO_INCREMENT für Tabelle `DetectedLicenses`
--
ALTER TABLE "DetectedLicenses" ALTER COLUMN "Id" SET NOT NULL;
ALTER TABLE "DetectedLicenses" ALTER COLUMN `Id` IDENTITY;

--
-- AUTO_INCREMENT für Tabelle `LibraryData`
--
ALTER TABLE "LibraryData" ALTER COLUMN "Id" SET NOT NULL;
ALTER TABLE "LibraryData" ALTER COLUMN `Id` IDENTITY;

--
-- Finished
--
COMMIT;
