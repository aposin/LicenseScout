
SET AUTOCOMMIT FALSE;

BEGIN TRANSACTION; 

--
-- Database: `licensescout`
--

-- --------------------------------------------------------
--
-- Table `Builds`
--

CREATE TABLE "Builds" (
  `Id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
-- Table `DetectedLicenses`
--

CREATE TABLE "DetectedLicenses" (
  `Id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `FK_LibraryData_Id` int(11) NOT NULL,
  `License_Name` varchar(500) NOT NULL
);

-- --------------------------------------------------------
--
-- Table `LibraryData`
--

CREATE TABLE "LibraryData" (
  `Id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `FK_Build_Id` int(11) NOT NULL,
  `Selected_License` varchar(500) NOT NULL,
  `Filename` varchar(500) NOT NULL,
  `Provider` varchar(500) NOT NULL,
  `Version` varchar(255) NOT NULL,
  `Type` varchar(100) NOT NULL,
  `Message_Digest` varchar(255) NOT NULL,
  `Detection_Status` varchar(255) NOT NULL,
  `Legal_Status` varchar(255) NOT NULL,
  `Documentation_Link` varchar(500) NOT NULL
);

--
-- Finished
--
COMMIT;
