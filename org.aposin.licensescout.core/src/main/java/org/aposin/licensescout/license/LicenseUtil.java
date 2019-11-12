/**
 * Copyright 2019 Association for the promotion of open-source insurance software and for the establishment of open interface standards in the insurance industry (Verein zur Förderung quelloffener Versicherungssoftware und Etablierung offener Schnittstellenstandards in der Versicherungsbranche)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aposin.licensescout.license;

import static org.aposin.licensescout.license.DetectionStatus.DETECTED;
import static org.aposin.licensescout.license.DetectionStatus.MANUAL_DETECTED;
import static org.aposin.licensescout.license.DetectionStatus.MANUAL_SELECTED;
import static org.aposin.licensescout.license.DetectionStatus.MULTIPLE_DETECTED;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveIdentifierPattern;
import org.aposin.licensescout.archive.PatternType;
import org.aposin.licensescout.exporter.DetectionStatusStatistics;
import org.aposin.licensescout.exporter.GeneralStatistics;
import org.aposin.licensescout.exporter.IDetectionStatusStatistics;
import org.aposin.licensescout.exporter.ILegalStatusStatistics;
import org.aposin.licensescout.exporter.LegalStatusStatistics;
import org.aposin.licensescout.model.Notice;
import org.aposin.licensescout.model.Provider;
import org.aposin.licensescout.util.ILFLog;

/**
 *
 */
public class LicenseUtil {

    private static final int VERSION_LINE_TOLERANCE = 3;
    private static final Pattern VERSION_NUMBER_PATTERN = Pattern
            .compile(".*((VERSION|V).*(\\d\\.\\d)|(VERSION|V) (\\d)).*");

    /**
     * Private constructor.
     */
    private LicenseUtil() {
        // DO NOTHING
    }

    /**
     * Tries to detect a license from a text.
     * 
     * <p>This algorithm can detect multiple licenses in the same file.</p>
     * 
     * Problem with the old version: it detects only one license per file. If a file contains more than one license, only one is detected.
     * This version tries to detect all licenses. However, it currently does not handle well the versions. If the license name and the version are on different lines (see example below),
     * the default version is used. In some cases, this leads to detecting Apache 1.0 as well as Apache 2.0 from the same file, while only apache 2.0 is named in the file.
     * So the logic should be extended to use version in nearby lines.
     * 
     * ---
     *                                 Apache License
     *                           Version 2.0, January 2004
     * ---
     * 
     * @param reader text input
     * @param licenseStoreData 
     * @return the license or an empty collection if none was detected
     * @throws IOException signals that an I/O exception has occurred
     */
    public static Collection<License> detectLicenses(final BufferedReader reader,
                                                     final LicenseStoreData licenseStoreData)
            throws IOException {

        final Set<License> allLicenses = new HashSet<>();
        final List<List<License>> processedCandidateLicenseLists = new ArrayList<>();
        final Set<Entry<String, List<License>>> licenseDetectionStringMap = licenseStoreData
                .getLicenseDetectionStringMap();
        List<License> candidateLicenses = null;

        String line;
        int lineCountAfterCandidatesFound = 0;
        while ((line = reader.readLine()) != null) {

            lineCountAfterCandidatesFound++;
            final String lineString = line.trim().toUpperCase();
            if (lineString.isEmpty()) {
                continue;
            }
            if (candidateLicenses != null && lineCountAfterCandidatesFound > VERSION_LINE_TOLERANCE) {
                /*
                 * We are over the tolerance of the number of lines a version information is allowed after the license name if found. In this case we assume there is no more
                 * valid information on the version, so the default license of the candidate list is selected and added to the all list.
                 */
                addVersionedLicenseFromCandidateList(allLicenses, candidateLicenses, null);
                candidateLicenses = null;
            }

            final List<License> localCandidateLicenses = getCandidateLicenses(licenseDetectionStringMap, lineString);
            final boolean alreadyProcessed = processedCandidateLicenseLists.contains(localCandidateLicenses);
            if (localCandidateLicenses != null && !alreadyProcessed) {
                if (candidateLicenses != null) {
                    /*
                     * Old candidate licenses present, for which no version has been found yet, but in this line there are other (new) candidate licenses.
                     * So select the default license from the old candidates and add this to the all list. Then make the new candidates the current ones.
                     */
                    addVersionedLicenseFromCandidateList(allLicenses, candidateLicenses, null);
                }
                candidateLicenses = localCandidateLicenses;
                processedCandidateLicenseLists.add(localCandidateLicenses);
                lineCountAfterCandidatesFound = 0;
            }
            if (candidateLicenses != null) {
                /*
                 * If we have candidate licenses, we try to find a version number. Note that this can be on the same line or on a consecutive line.
                 * If we found a version, a license is selected and added to the overall list. Then, since we have processed the current candidate licenses they are set to null.
                 */
                final String version = getMatchedVersionFromLine(lineString);
                if (version != null) {
                    addVersionedLicenseFromCandidateList(allLicenses, candidateLicenses, version);
                    candidateLicenses = null;
                }
            }
        } /* end of loop over lines */
        if (candidateLicenses != null) {
            /*
             * We have candidate licenses, but no version found in the file. Then we select the default license and add it to the all list.
             */
            addVersionedLicenseFromCandidateList(allLicenses, candidateLicenses, null);
        }
        return allLicenses;
    }

    /**
     * @param allLicenses
     * @param candidateLicenses
     * @param version may be null - in this case the first element of the candidate licenses is selected
     */
    private static void addVersionedLicenseFromCandidateList(final Set<License> allLicenses,
                                                             final List<License> candidateLicenses,
                                                             final String version) {
        final License license = getVersionedLicense(candidateLicenses, version);
        if (license != null) {
            allLicenses.add(license);
        }
    }

    /**
     * @param licenseDetectionStringMap
     * @param lineString
     * @return may be null.
     */
    private static List<License> getCandidateLicenses(final Set<Entry<String, List<License>>> licenseDetectionStringMap,
                                                      final String lineString) {
        for (final Map.Entry<String, List<License>> entry : licenseDetectionStringMap) {
            final String licenseString = entry.getKey().toUpperCase();
            if (lineString.contains(licenseString)) {
                return entry.getValue();
            }
        }
        return null; // NOSONAR - intentionally return null for faster comparison
    }

    /**
     * @param lineString
     * @return may return null if no version number has been found.
     */
    /*default*/ static String getMatchedVersionFromLine(final String lineString) {
        final Matcher matcher = VERSION_NUMBER_PATTERN.matcher(lineString.toUpperCase());
        String version = null;
        if (matcher.matches()) {
            if (/*matcher.groupCount() == 3 &&*/ matcher.group(2) != null) {
                version = matcher.group(3);
            } else if (matcher.groupCount() >= 5 && matcher.group(4) != null) {
                version = matcher.group(5);
            }
        }
        return version;
    }

    /**
     * @param candidateLicenses
     * @param version a string containing a version number. May be null. If null, the first element of the candidate licenses is selected and returned
     * @return a license. Can be null if the list of candidate licenses is null or empty.
     */
    private static License getVersionedLicense(final List<License> candidateLicenses, final String version) {
        if (candidateLicenses != null && !candidateLicenses.isEmpty()) {
            if (!StringUtils.isEmpty(version)) {
                for (final License license : candidateLicenses) {
                    if (isVersion(license, version)) {
                        return license;
                    }
                }
            }
            //no version found or no version string
            return candidateLicenses.isEmpty() ? null : candidateLicenses.get(0);
        } // no candidate licenses
        return null;
    }

    /**
     * @param license
     * @param version
     * @return true if the license has the passed version, either as its primary version or as one of its alternative versions, false otherwise
     */
    private static boolean isVersion(final License license, final String version) {
        return license.getVersion().equalsIgnoreCase(version) || license.getAlternativeVersions().contains(version);
    }

    /**
     * Obtains the full name of the license, including its version number, if present.
     * 
     * @param license
     * @return the full name of the license
     */
    public static String getLicenseNameWithVersion(final License license) {
        String name = license.getName();
        if (license.isVersionPresent()) {
            name += " Version " + license.getVersion();
        }
        return name;
    }

    /**
     * Evaluates licenses and sets detection status and legal status for each archive of a collection.
     * 
     * @param checkedArchives information on archives with manually assigned licenses
     * @param archives collection of archives to process
     * @param licenseStoreData 
     * @see #evaluateLicenses(LicenseCheckedList, Archive, LicenseStoreData)
     * 
     * @see DetectionStatus
     * @see LegalStatus
     */
    public static void evaluateLicenses(final LicenseCheckedList checkedArchives, final Collection<Archive> archives,
                                        final LicenseStoreData licenseStoreData) {
        for (final Archive archive : archives) {
            evaluateLicenses(checkedArchives, archive, licenseStoreData);
        }
    }

    /**
     * Evaluates licenses and sets detection status and legal status for an archive.
     * 
     * @param licenseCheckedList information on archives with manually assigned licenses
     * @param archive the archive to evaluate
     * @param licenseStoreData 
     * 
     * @see DetectionStatus
     * @see LegalStatus
     */
    private static void evaluateLicenses(final LicenseCheckedList licenseCheckedList, final Archive archive,
                                         final LicenseStoreData licenseStoreData) {
        final Set<License> licenses = archive.getLicenses();
        final List<License> detectedLicenses = new ArrayList<>(licenses);
        archive.setDetectedLicenses(detectedLicenses);
        final boolean overWriteMode = !licenses.isEmpty();
        final DetectionStatus detectionStatus = addManualLicenses(licenseCheckedList, archive, overWriteMode);
        setDetectionStatus(archive, detectionStatus, licenseStoreData);
        setLegalStatus(archive);
    }

    /**
     * @param licenseCheckedList information on archives with manually assigned licenses
     * @param archive the archive to process
     * @param overWriteMode if true and manual configured licenses are found, only these licenses are used (others are removed) and the returned
     * status is {@link DetectionStatus#MANUAL_SELECTED} (instead of {@link DetectionStatus#MANUAL_DETECTED})
     * @return the detection status to use in {@link #setDetectionStatus(Archive, DetectionStatus, LicenseStoreData)} if the status is not
     * recognized to be {@link DetectionStatus#MANUAL_NOT_DETECTED}. The values can be {@link DetectionStatus#MANUAL_DETECTED},
     * {@link DetectionStatus#MANUAL_SELECTED}, {@link DetectionStatus#DETECTED} or {@link DetectionStatus#MULTIPLE_DETECTED}.
     */
    private static DetectionStatus addManualLicenses(final LicenseCheckedList licenseCheckedList, final Archive archive,
                                                     final boolean overWriteMode) {
        final List<License> allManualLicenses = new ArrayList<>();
        String documentationUrl = null;
        Provider provider = null;
        Notice notice = null;
        if (hasVersion(archive)) {
            final LicenseResult manualLicenses = licenseCheckedList.getManualLicense(archive.getArchiveType(),
                    archive.getFileName(), archive.getVersion());
            if (manualLicenses != null) {
                allManualLicenses.addAll(manualLicenses.getLicenses());
                documentationUrl = manualLicenses.getDocumentationUrl();
                provider = manualLicenses.getProvider();
                notice = manualLicenses.getNotice();
            }
        }
        if (archive.getMessageDigest() != null) {
            final LicenseResult manualLicenses = licenseCheckedList.getManualLicense(archive.getArchiveType(),
                    archive.getFileName(), archive.getMessageDigest());
            if (manualLicenses != null) {
                allManualLicenses.addAll(manualLicenses.getLicenses());
                documentationUrl = manualLicenses.getDocumentationUrl();
                provider = manualLicenses.getProvider();
                notice = manualLicenses.getNotice();
            }
        }
        // do pattern matching only if we haven't found a license yet
        if (allManualLicenses.isEmpty()) {
            final Set<Entry<ArchiveIdentifierPattern, LicenseResult>> archivePatterns = licenseCheckedList
                    .getManualPatternArchives();
            final Iterator<Entry<ArchiveIdentifierPattern, LicenseResult>> iter = archivePatterns.iterator();
            final String name = archive.getFileName();
            final String path = archive.getPath();
            while (iter.hasNext()) {
                final Entry<ArchiveIdentifierPattern, LicenseResult> entry = iter.next();
                final ArchiveIdentifierPattern aip = entry.getKey();
                final Pattern pattern = aip.getPattern();
                final String checkedString = aip.getPatternType() == PatternType.PATTERN_ON_FILENAME ? name : path;
                final Matcher matcher = pattern.matcher(checkedString);
                if (matcher.matches()) {
                    final LicenseResult licenseResult = entry.getValue();
                    final List<License> licenses = licenseResult.getLicenses();
                    allManualLicenses.addAll(licenses);
                    documentationUrl = licenseResult.getDocumentationUrl();
                    provider = licenseResult.getProvider();
                    notice = licenseResult.getNotice();
                }
            }
        }
        if (documentationUrl != null) {
            archive.setDocumentationUrl(documentationUrl);
        }
        if (provider != null) {
            archive.setProvider(provider);
        }
        if (notice != null) {
            archive.setNotice(notice);
        }
        return calculateDetectionStatus(archive, overWriteMode, allManualLicenses);
    }

    /**
     * @param archive
     * @param overWriteMode
     * @param allManualLicenses
     * @return a detection status
     */
    private static DetectionStatus calculateDetectionStatus(final Archive archive, final boolean overWriteMode,
                                                            final List<License> allManualLicenses) {
        final boolean multipleLicensesDetected = archive.getLicenses().size() > 1;
        final DetectionStatus detectionStatus;
        if (!allManualLicenses.isEmpty()) {
            if (overWriteMode) {
                archive.clearLicenses();
                detectionStatus = MANUAL_SELECTED;
            } else {
                detectionStatus = MANUAL_DETECTED;
            }
            addManualLicenses(archive, allManualLicenses);
        } else {
            detectionStatus = multipleLicensesDetected ? MULTIPLE_DETECTED : DETECTED;
        }
        return detectionStatus;
    }

    /**
     * @param archive
     * @param manualLicenses
     * @return true if a license has been added
     */
    private static boolean addManualLicenses(final Archive archive, final List<License> manualLicenses) {
        if (manualLicenses != null && !manualLicenses.isEmpty()) {
            for (final License manualLicense : manualLicenses) {
                final String absolutePath = "[added from list of exceptions]";
                archive.addLicense(manualLicense, absolutePath);
            }
            return true;
        }
        return false;
    }

    private static void setDetectionStatus(final Archive archive, final DetectionStatus manualDetectionStatus,
                                           final LicenseStoreData licenseStoreData) {
        final Set<License> licenses = archive.getLicenses();
        final boolean noManualInformation = licenses
                .contains(licenseStoreData.getLicenseBySpdxIdentifier(LicenseSpdxIdentifier.NO_MANUAL_INFORMATION));
        final boolean hasLicenses = !licenses.isEmpty();
        final DetectionStatus detectionStatus;
        if (noManualInformation) {
            detectionStatus = DetectionStatus.MANUAL_NOT_DETECTED;
            // remove the "no manual information" placeholder license
            archive.clearLicenses();
        } else {
            if (hasLicenses) {
                detectionStatus = manualDetectionStatus;
            } else {
                detectionStatus = DetectionStatus.NOT_DETECTED;
            }
        }
        archive.setDetectionStatus(detectionStatus);
    }

    private static void setLegalStatus(final Archive archive) {
        final Set<License> licenses = archive.getLicenses();
        boolean hasAcceptedLicense = false;
        boolean hasBlacklistedLicense = false;
        boolean hasUnknownLicense = false;
        for (final License license : licenses) {
            switch (license.getLegalStatus()) {
                case ACCEPTED:
                    hasAcceptedLicense = true;
                    break;
                case NOT_ACCEPTED:
                    hasBlacklistedLicense = true;
                    break;
                case UNKNOWN:
                    hasUnknownLicense = true;
                    break;
                case CONFLICTING:
                    // should not appear here
                    break;
            }
        }
        final LegalStatus legalStatus;
        if (hasUnknownLicense) {
            legalStatus = LegalStatus.UNKNOWN;
        } else {
            if (hasBlacklistedLicense) {
                if (hasAcceptedLicense) { //NOSONAR - code is reachable
                    legalStatus = LegalStatus.CONFLICTING;
                } else {
                    legalStatus = LegalStatus.NOT_ACCEPTED;
                }
            } else {
                if (hasAcceptedLicense) {
                    legalStatus = LegalStatus.ACCEPTED;
                } else {
                    legalStatus = LegalStatus.UNKNOWN;
                }
            }
        }
        archive.setLegalStatus(legalStatus);
    }

    private static boolean hasVersion(final Archive archive) {
        return !StringUtils.isEmpty(archive.getVersion());
    }

    /**
     * Calculates statistics for the detection status.
     * 
     * @param archives collection of archives to process
     * @return a statistics object
     * 
     */
    public static IDetectionStatusStatistics calculateDetectionStatusStatistics(final Collection<Archive> archives) {
        final IDetectionStatusStatistics detectionStatusStatistics = new DetectionStatusStatistics();
        for (final DetectionStatus detectioStatus : DetectionStatus.values()) {
            detectionStatusStatistics.put(detectioStatus, 0);
        }
        for (final Archive archive : archives) {
            final DetectionStatus detectionStatus = archive.getDetectionStatus();
            detectionStatusStatistics.put(detectionStatus, detectionStatusStatistics.get(detectionStatus) + 1);
        }
        return detectionStatusStatistics;
    }

    /**
     * Calculates statistics for the legal status.
     * 
     * @param archives collection of archives to process
     * @return a statistics object
     * 
     */
    public static ILegalStatusStatistics calculateLegalStatusStatistics(final Collection<Archive> archives) {
        final ILegalStatusStatistics legalStatusStatistics = new LegalStatusStatistics();
        for (final LegalStatus legalStatus : LegalStatus.values()) {
            legalStatusStatistics.put(legalStatus, 0);
        }
        for (final Archive archive : archives) {
            final LegalStatus legalStatus = archive.getLegalStatus();
            legalStatusStatistics.put(legalStatus, legalStatusStatistics.get(legalStatus) + 1);
        }
        return legalStatusStatistics;
    }

    /**
     * Calculates general statistics over all archives.
     * 
     * @param archives collection of archives to process
     * @return an object containing statistics data
     * 
     */
    public static GeneralStatistics calculateGeneralStatistics(final Collection<Archive> archives) {
        final GeneralStatistics generalStatistics = new GeneralStatistics();
        generalStatistics.setTotalArchiveCount(archives.size());
        int licenseCandidateFileCount = 0;
        for (final Archive archive : archives) {
            licenseCandidateFileCount += archive.getLicenseCandidateFiles().size();
        }
        generalStatistics.setCandidateLicenseFileCount(licenseCandidateFileCount);
        return generalStatistics;
    }

    /**
     * Obtains licenses from the license name mapping.
     * 
     * @param npmLicenseName a license name
     * @param licenseStoreData 
     * @return a list of licenses for the name. An empty list if the license could not be mapped.
     */
    public static List<License> mapNpmLicenseName(final String npmLicenseName,
                                                  final LicenseStoreData licenseStoreData) {
        final License license = licenseStoreData.getLicenseBySpdxIdentifier(npmLicenseName);
        if (license != null) {
            return Arrays.asList(license);
        }
        final List<License> licenseList = licenseStoreData.getLicensesFromNameMapping(npmLicenseName);
        if (licenseList != null) {
            return licenseList;
        }
        return Collections.emptyList();
    }

    /**
     * Tries to find licenses by license URL and associate them with an archive.
     * 
     * @param licenseUrl a license URL
     * @param archive an archive
     * @param licenseFileName the file name to record as the file the license is found in
     * @param licenseStoreData the license store
     * @param log a logger
     * @return true if licenses have been added to the archive, false otherwise
     */
    public static boolean handleLicenseUrl(final String licenseUrl, final Archive archive, final String licenseFileName,
                                           final LicenseStoreData licenseStoreData, final ILFLog log) {
        boolean licenseFound = false;
        final License license = licenseStoreData.getLicenseByPublicUrl(licenseUrl);
        if (license != null) {
            archive.addLicense(license, licenseFileName);
            licenseFound = true;
        } else {
            log.debug("License URL not found in store: " + licenseUrl);
        }
        if (!licenseFound) {
            final List<License> licenses = licenseStoreData.getLicensesFromUrlMapping(licenseUrl);
            if (licenses != null) {
                licenseFound |= addLicensesToArchive(archive, licenses, licenseFileName);
            } else {
                log.info("No license mapping found for URL: " + licenseUrl);
            }
        }
        return licenseFound;
    }

    /**
     * Tries to find licenses by license name and associate them with an archive.
     * 
     * @param licenseName a license name
     * @param archive an archive
     * @param licenseFileName the file name to record as the file the license is found in
     * @param licenseStoreData the license store
     * @param log a logger
     * @return true if licenses have been added to the archive, false otherwise
     */
    public static boolean handleLicenseName(final String licenseName, final Archive archive,
                                            final String licenseFileName, LicenseStoreData licenseStoreData,
                                            final ILFLog log) {
        boolean licenseFound = false;
        final License license = licenseStoreData.getLicenseBySpdxIdentifier(licenseName);
        if (license != null) {
            archive.addLicense(license, licenseFileName);
            licenseFound = true;
        } else {
            log.debug("License name not found in store: " + licenseName);
        }
        if (!licenseFound) {
            final List<License> licenses = licenseStoreData.getLicensesFromNameMapping(licenseName);
            if (licenses != null) {
                licenseFound |= addLicensesToArchive(archive, licenses, licenseFileName);
            } else {
                log.info("No license mapping found for name: " + licenseName);
            }
        }
        return licenseFound;
    }

    /**
     * @param archive
     * @param licenses
     * @param licenseFileName
     * @return true if one or more licenses have been added, false otherwise
     */
    private static boolean addLicensesToArchive(final Archive archive, final List<License> licenses,
                                                final String licenseFileName) {
        boolean licenseFound = false;
        for (final License license : licenses) {
            if (license != null) {
                archive.addLicense(license, licenseFileName);
                licenseFound = true;
            }
        }
        return licenseFound;
    }

}
