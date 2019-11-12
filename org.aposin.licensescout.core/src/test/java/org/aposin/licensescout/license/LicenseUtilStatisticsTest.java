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

import java.util.ArrayList;
import java.util.Collection;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.archive.ArchiveType;
import org.aposin.licensescout.exporter.GeneralStatistics;
import org.aposin.licensescout.exporter.IDetectionStatusStatistics;
import org.aposin.licensescout.exporter.ILegalStatusStatistics;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link LicenseUtil}.
 * 
 * 
 * @see LicenseUtil
 * @see LicenseUtilGetMatchedVersionFromLineTest
 * @see LicenseUtilDetectLicensesTest
 *
 */
public class LicenseUtilStatisticsTest {

    /**
     * 
     */
    @Test
    public void testCalculateGeneralStatisticsEmptyList() {
        final Collection<Archive> archives = new ArrayList<>();
        final GeneralStatistics generalStatistics = LicenseUtil.calculateGeneralStatistics(archives);
        Assert.assertEquals("candidateLicenseFileCount", 0, generalStatistics.getCandidateLicenseFileCount());
        Assert.assertEquals("totalArchiveCount", 0, generalStatistics.getTotalArchiveCount());
    }

    /**
     * Test for {@link LicenseUtil#calculateGeneralStatistics(Collection)}.
     */
    @Test
    public void testCalculateGeneralStatisticsRealList() {
        final Collection<Archive> archives = new ArrayList<>();
        final int expectedTotalArchiveCount = 3;
        int expectedCandidateLicenseFileCount = 0;
        for (int i = 1; i <= expectedTotalArchiveCount; i++) {
            expectedCandidateLicenseFileCount += i;
            Archive archive = new Archive(ArchiveType.JAVA, "archive" + i, "", "");
            for (int j = 1; j <= i; j++) {
                archive.addLicenseCandidateFile("candidateFile" + j);
            }
            archives.add(archive);
        }
        final GeneralStatistics generalStatistics = LicenseUtil.calculateGeneralStatistics(archives);
        Assert.assertEquals("candidateLicenseFileCount", expectedCandidateLicenseFileCount,
                generalStatistics.getCandidateLicenseFileCount());
        Assert.assertEquals("totalArchiveCount", expectedTotalArchiveCount, generalStatistics.getTotalArchiveCount());
    }

    /**
     * Test for {@link LicenseUtil#calculateDetectionStatusStatistics(Collection)}.
     */
    @Test
    public void testCalculateDetectionStatusStatisticsEmptyList() {
        final Collection<Archive> archives = new ArrayList<>();
        final IDetectionStatusStatistics detectionStatusStatistics = LicenseUtil
                .calculateDetectionStatusStatistics(archives);
        Assert.assertEquals("detectionStatusStatistics (DETECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.DETECTED));
        Assert.assertEquals("detectionStatusStatistics (MANUAL_DETECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.MANUAL_DETECTED));
        Assert.assertEquals("detectionStatusStatistics (MANUAL_NOT_DETECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.MANUAL_NOT_DETECTED));
        Assert.assertEquals("detectionStatusStatistics (MANUAL_SELECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.MANUAL_SELECTED));
        Assert.assertEquals("detectionStatusStatistics (MULTIPLE_DETECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.MULTIPLE_DETECTED));
        Assert.assertEquals("detectionStatusStatistics (NOT_DETECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.NOT_DETECTED));
    }

    /**
     * Test for {@link LicenseUtil#calculateDetectionStatusStatistics(Collection)}.
     */
    @Test
    public void testCalculateDetectionStatusStatisticsRealList() {
        final Collection<Archive> archives = new ArrayList<>();
        for (DetectionStatus detectionStatus : DetectionStatus.values()) {
            final int ordinal = detectionStatus.ordinal();
            // NOTE: zero count for DetectionStatus DETECTED is intentional to test the case of no archive of a certain DetectionStatus.
            for (int i = 0; i < ordinal; i++) {
                final Archive archive = new Archive(ArchiveType.JAVA, "archive" + i, "", "");
                archive.setDetectionStatus(detectionStatus);
                archives.add(archive);
            }
        }
        final IDetectionStatusStatistics detectionStatusStatistics = LicenseUtil
                .calculateDetectionStatusStatistics(archives);
        Assert.assertEquals("detectionStatusStatistics (DETECTED)", 0,
                (int) detectionStatusStatistics.get(DetectionStatus.DETECTED));
        Assert.assertEquals("detectionStatusStatistics (MULTIPLE_DETECTED)", 1,
                (int) detectionStatusStatistics.get(DetectionStatus.MULTIPLE_DETECTED));
        Assert.assertEquals("detectionStatusStatistics (NOT_DETECTED)", 2,
                (int) detectionStatusStatistics.get(DetectionStatus.NOT_DETECTED));
        Assert.assertEquals("detectionStatusStatistics (MANUAL_DETECTED)", 3,
                (int) detectionStatusStatistics.get(DetectionStatus.MANUAL_DETECTED));
        Assert.assertEquals("detectionStatusStatistics (MANUAL_SELECTED)", 4,
                (int) detectionStatusStatistics.get(DetectionStatus.MANUAL_SELECTED));
        Assert.assertEquals("detectionStatusStatistics (MANUAL_NOT_DETECTED)", 5,
                (int) detectionStatusStatistics.get(DetectionStatus.MANUAL_NOT_DETECTED));
    }

    /**
     * Test for {@link LicenseUtil#calculateLegalStatusStatistics(Collection)}.
     */
    @Test
    public void testCalculateLegalStatusStatisticsEmptyList() {
        final Collection<Archive> archives = new ArrayList<>();
        final ILegalStatusStatistics legalStatusStatistics = LicenseUtil.calculateLegalStatusStatistics(archives);
        Assert.assertEquals("legalStatusStatistics (ACCEPTED)", 0,
                (int) legalStatusStatistics.get(LegalStatus.ACCEPTED));
        Assert.assertEquals("legalStatusStatistics (NOT_ACCEPTED)", 0,
                (int) legalStatusStatistics.get(LegalStatus.NOT_ACCEPTED));
        Assert.assertEquals("legalStatusStatistics (CONFLICTING)", 0,
                (int) legalStatusStatistics.get(LegalStatus.CONFLICTING));
        Assert.assertEquals("legalStatusStatistics (UNKNOWN)", 0, (int) legalStatusStatistics.get(LegalStatus.UNKNOWN));
    }

    /**
     * Test for {@link LicenseUtil#calculateLegalStatusStatistics(Collection)}.
     */
    @Test
    public void testCalculateLegalStatusStatisticsRealList() {
        final Collection<Archive> archives = new ArrayList<>();
        // NOTE: zero count for LegalStatus ACCEPTED is intentional to test the case of no archive of a certain LegalStatus.
        for (LegalStatus legalStatus : LegalStatus.values()) {
            final int ordinal = legalStatus.ordinal();
            for (int i = 0; i < ordinal; i++) {
                final Archive archive = new Archive(ArchiveType.JAVA, "archive" + i, "", "");
                archive.setLegalStatus(legalStatus);
                archives.add(archive);
            }
        }
        final ILegalStatusStatistics legalStatusStatistics = LicenseUtil.calculateLegalStatusStatistics(archives);
        Assert.assertEquals("legalStatusStatistics (ACCEPTED)", 0,
                (int) legalStatusStatistics.get(LegalStatus.ACCEPTED));
        Assert.assertEquals("legalStatusStatistics (NOT_ACCEPTED)", 1,
                (int) legalStatusStatistics.get(LegalStatus.NOT_ACCEPTED));
        Assert.assertEquals("legalStatusStatistics (CONFLICTING)", 2,
                (int) legalStatusStatistics.get(LegalStatus.CONFLICTING));
        Assert.assertEquals("legalStatusStatistics (UNKNOWN)", 3, (int) legalStatusStatistics.get(LegalStatus.UNKNOWN));
    }

}
