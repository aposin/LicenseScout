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
package org.aposin.licensescout.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Utility methods to obtain information from a JAR's MANIFEST.MF file.
 *
 */
public class JarUtil {

    /**
     * Private constructor.
     */
    private JarUtil() {
        // DO NOTHING
    }

    /**
     * Tries to obtains meta information for a MANIFEST.MF that is available as a
     * file.
     * 
     * <p>
     * The method tries to extract the following information from the manifest:
     * </p>
     * <ul>
     * <li>a version number, extracted from the main attributes "Bundle-Version",
     * "Implementation-Version", "Specification-Version" or "Major-Version"</li>
     * <li>a license URL, extracted from the main attribute "Bundle-License"</li>
     * <li>a vendor name, extracted from the main attribute "Bundle-Vendor"</li>
     * </ul>
     * <p>
     * This method is intended for unpacked JARs.
     * </p>
     * 
     * @param file
     *            a manifest file
     * @param log
     *            a logger
     * @return an object containing version, license URL and vendor name. The
     *         information in the object may be <code>null</code>. The object itself
     *         is never <code>null</code>.
     * @throws IOException
     * 
     * @see #getArchiveMetaInformationFromManifest(File, ILFLog)
     * @see #getArchiveMetaInformationFromManifest(InputStream, ILFLog)
     */
    public static ArchiveMetaInformation getArchiveMetaInformationFromManifestFile(final File file, final ILFLog log)
            throws IOException {
        try (final InputStream is = new FileInputStream(file);) {
            final Manifest manifest = new Manifest(is);
            return getArchiveMetaInformationFromManifestObject(manifest, log, file.getAbsolutePath());
        }
    }

    /**
     * Tries to obtains meta information for a JAR from its MANIFEST.MF.
     * 
     * <p>
     * The method tries to extract the following information from the manifest:
     * </p>
     * <ul>
     * <li>a version number, extracted from the main attributes "Bundle-Version",
     * "Implementation-Version", "Specification-Version" or "Major-Version"</li>
     * <li>a license URL, extracted from the main attribute "Bundle-License"</li>
     * <li>a vendor name, extracted from the main attribute "Bundle-Vendor"</li>
     * </ul>
     * <p>
     * This method is intended for packed JARs available as file, i.e. as part of an
     * unpacked JAR.
     * </p>
     * 
     * @param file
     *            a JAR file
     * @param log
     *            a logger
     * @return an object containing version, license URL and vendor name. The
     *         information in the object may be <code>null</code>. The object itself
     *         is never <code>null</code>.
     * @throws IOException
     * 
     * @see #getArchiveMetaInformationFromManifestFile(File, ILFLog)
     * @see #getArchiveMetaInformationFromManifest(InputStream, ILFLog)
     */
    public static ArchiveMetaInformation getArchiveMetaInformationFromManifest(final File file, final ILFLog log)
            throws IOException {
        final String sourceForErrorText = file.getAbsolutePath();
        try (final JarFile jarFile = new JarFile(file);) {
            final Manifest manifest = jarFile.getManifest();
            return getArchiveMetaInformationFromManifestObject(manifest, log, sourceForErrorText);
        }
    }

    /**
     * Tries to obtains meta information for a JAR from its MANIFEST.MF.
     * 
     * <p>
     * The method tries to extract the following information from the manifest:
     * </p>
     * <ul>
     * <li>a version number, extracted from the main attributes "Bundle-Version",
     * "Implementation-Version", "Specification-Version" or "Major-Version"</li>
     * <li>a license URL, extracted from the main attribute "Bundle-License"</li>
     * <li>a vendor name, extracted from the main attribute "Bundle-Vendor"</li>
     * </ul>
     * <p>
     * This method is intended for packed JARs available from inside another packed
     * JAR..
     * </p>
     * 
     * @param inputStream
     *            a JAR file
     * @param log
     *            a logger
     * @return an object containing version, license URL and vendor name. The
     *         information in the object may be <code>null</code>. The object itself
     *         is never <code>null</code>.
     * @throws IOException
     * 
     * @see #getArchiveMetaInformationFromManifestFile(File, ILFLog)
     * @see #getArchiveMetaInformationFromManifest(File, ILFLog)
     */
    public static ArchiveMetaInformation getArchiveMetaInformationFromManifest(final InputStream inputStream,
            final ILFLog log) throws IOException {
        final String sourceForErrorText = "input stream";
        try (final JarInputStream jarFile = new JarInputStream(inputStream);) {
            final Manifest manifest = jarFile.getManifest();
            return getArchiveMetaInformationFromManifestObject(manifest, log, sourceForErrorText);
        }
    }

    private static ArchiveMetaInformation getArchiveMetaInformationFromManifestObject(final Manifest manifest,
            final ILFLog log, final String sourceForErrorText) {
        if (manifest != null) {
            final String version = getVersionFromManifest(manifest, log);
            final String licenseUrl = getLicenseFromManifest(manifest, log);
            final String vendor = getVendorFromManifest(manifest, log);
            return new ArchiveMetaInformation(version, licenseUrl, vendor);
        } else {
            log.info("cannot find manifest in " + sourceForErrorText);
            return ArchiveMetaInformation.NO_INFORMATION;
        }
    }

    /**
     * Tries to find a version number from a manifest.
     * 
     * <p>
     * The method considers the following main attributes of the manifest, in the
     * order listed:
     * </p>
     * <ol>
     * <li>Bundle-Version</li>
     * <li>Implementation-Version</li>
     * <li>Specification-Version</li>
     * <li>Major-Version</li>
     * </ol>
     * <p>
     * The first present is used. If none is present, <code>null</code> is returned.
     * </p>
     * 
     * @param manifest
     *            a manifest from a JAR
     * @param log
     *            a logger
     * @return a string containing a version number or <code>null</code>
     * 
     */
    private static String getVersionFromManifest(final Manifest manifest, final ILFLog log) {
        final Attributes mainAttributes = manifest.getMainAttributes();
        final String bundleVersion = mainAttributes.getValue("Bundle-Version");
        log.debug("Bundle-Version: " + bundleVersion);
        final String implementationVersion = mainAttributes.getValue("Implementation-Version");
        log.debug("Implementation-Version: " + implementationVersion);
        final String specificationVersion = mainAttributes.getValue("Specification-Version");
        log.debug("Specification-Version: " + specificationVersion);
        final String majorVersion = mainAttributes.getValue("Major-Version");
        log.debug("Major-Version: " + majorVersion);
        if (bundleVersion != null) {
            return bundleVersion;
        } else if (implementationVersion != null) {
            return implementationVersion;
        } else if (specificationVersion != null) {
            return specificationVersion;
        } else {
            // may be null
            return majorVersion;
        }
    }

    /**
     * Tries to find a license URL from a manifest.
     * 
     * <p>
     * The method considers the following main attributes of the manifest, in the
     * order listed:
     * </p>
     * <ol>
     * <li>Bundle-License</li>
     * </ol>
     * <p>
     * The first present is used. If none is present, <code>null</code> is returned.
     * </p>
     * 
     * @param manifest
     *            a manifest from a JAR
     * @param log
     *            a logger
     * @return a string containing a license URL or <code>null</code>
     */
    private static String getLicenseFromManifest(final Manifest manifest, final ILFLog log) {
        final Attributes mainAttributes = manifest.getMainAttributes();
        final String bundleLicense = mainAttributes.getValue("Bundle-License");
        log.debug("Bundle-License: " + bundleLicense);
        return bundleLicense;
    }

    /**
     * Tries to find a vendor name from a manifest.
     * 
     * <p>
     * The method considers the following main attributes of the manifest, in the
     * order listed:
     * </p>
     * <ol>
     * <li>Bundle-Vendor</li>
     * </ol>
     * <p>
     * The first present is used. If none is present, <code>null</code> is returned.
     * </p>
     * 
     * @param manifest
     *            a manifest from a JAR
     * @param log
     *            a logger
     * @return a string containing a vendor name or <code>null</code>
     */
    private static String getVendorFromManifest(final Manifest manifest, final ILFLog log) {
        final Attributes mainAttributes = manifest.getMainAttributes();
        final String bundleVendor = mainAttributes.getValue("Bundle-Vendor");
        log.debug("Bundle-Vendor: " + bundleVendor);
        return bundleVendor;
    }
}
