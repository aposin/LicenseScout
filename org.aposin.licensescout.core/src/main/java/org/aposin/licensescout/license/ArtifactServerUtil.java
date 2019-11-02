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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.util.ILFLog;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Utility methods related to accessing a server that is caching maven central.
 * 
 * <p>
 * The instance of this class checks if the server is reachable under the
 * configured URL.
 */
public class ArtifactServerUtil {

    private final String mavenCentralBaseUrl;
    private final int connectTimeoutInMilliseconds;
    private final ILFLog log;
    private final boolean cachedCheckAccess;

    /**
     * Constructor.
     * 
     * @param mavenCentralBaseUrl
     *            the URL to a locally caching server of maven central
     * @param connectTimeoutInMilliseconds
     *            the connect timeout
     * @param log
     *            the logger
     */
    public ArtifactServerUtil(final String mavenCentralBaseUrl, final int connectTimeoutInMilliseconds,
            final ILFLog log) {
        this.mavenCentralBaseUrl = mavenCentralBaseUrl;
        this.connectTimeoutInMilliseconds = connectTimeoutInMilliseconds;
        this.log = log;
        this.cachedCheckAccess = checkAccess();
    }

    /**
     * Checks if the artifact server is accessible with the base URL configured in
     * the constructor of this class.
     * 
     * @param log
     *            a logger
     * @return true if the artifact server is accessible under the configured base
     *         URL, false otherwise
     */
    private boolean checkAccess() {
        // we check the presence of
        // 'org.apache.maven.plugins:maven-plugin-plugin:jar:3.6.0'
        final String groupId = "org.apache.maven.plugins";
        final String artifactId = "maven-plugin-plugin";
        final String version = "3.6.0";
        final String urlString = getArtifactUrlString(groupId, artifactId, version);
        log.info("Checking access to artifact server using URL " + urlString);
        try {
            final URL url = new URL(urlString);
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(connectTimeoutInMilliseconds);
            try (InputStream is = urlConnection.getInputStream();) {
            }
            return true;
        } catch (IOException e) {
            log.warn("Cannot access artifact server at: " + urlString);
            log.warn("Result is cached, POM resolution is disabled for this run!");
            return false;
        }
    }

    /**
     * @return the cachedCheckAccess
     */
    public final boolean isCachedCheckAccess() {
        return cachedCheckAccess;
    }

    /**
     * @return the mavenCentralBaseUrl
     */
    private final String getMavenCentralBaseUrl() {
        return mavenCentralBaseUrl;
    }

    /**
     * Adds licenses to an archive from information found in a Maven POM file.
     * 
     * @param inputStream
     *            input source of the POM file
     * @param archive
     *            the archive to add the licenses to
     * @param filePath
     *            path of the POM file (for information only)
     * @param licenseStoreData
     *            the license data object
     * @return true if one or more licenses have been added, false otherwise
     */
    public boolean addLicensesFromPom(final InputStream inputStream, final Archive archive, final String filePath,
            final LicenseStoreData licenseStoreData) {
        try {
            log.debug("Checking POM file: " + filePath);
            final Model model = getMavenModel(inputStream);
            boolean licenseFound = evaluatePomLicenses(archive, filePath, licenseStoreData, model);
            // try parent POM resolution only if we know we can reach the artifact server
            if (!licenseFound && isCachedCheckAccess()) {
                // try parent POM from Maven central or a proxy of it on an artifact server
                final Parent parent = model.getParent();
                if (parent != null) {
                    final URL url = getParentUrl(parent);
                    InputStream parentInputStream;
                    try {
                        parentInputStream = getInputStream(url);
                    } catch (FileNotFoundException e) {
                        log.debug("Parent POM not found on artifact server: " + url.toString());
                        return licenseFound;
                    }
                    licenseFound |= processParentLicenses(archive, filePath, licenseStoreData, parent,
                            parentInputStream);
                }
            }
            return licenseFound;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    private boolean processParentLicenses(final Archive archive, final String filePath,
            final LicenseStoreData licenseStoreData, final Parent parent, InputStream parentInputStream)
            throws IOException {
        boolean licenseFoundLocal = false;
        if (parentInputStream != null) {
            try {
                final String newFilePath = filePath + "![parent POM](" + parent.getId() + ")";
                licenseFoundLocal = addLicensesFromPom(parentInputStream, archive, newFilePath, licenseStoreData);
            } finally {
                parentInputStream.close();
            }
        }
        return licenseFoundLocal;
    }

    private boolean evaluatePomLicenses(final Archive archive, final String filePath,
            final LicenseStoreData licenseStoreData, final Model model) {
        boolean licenseFound = false;
        final List<org.apache.maven.model.License> licenses = model.getLicenses();
        for (final org.apache.maven.model.License license : licenses) {
            final String licenseUrl = license.getUrl();
            final String licenseName = license.getName();
            log.debug("License name: " + licenseName);
            log.debug("License URL: " + licenseUrl);
            final boolean licenseFoundForUrl = LicenseUtil.handleLicenseUrl(licenseUrl, archive, filePath,
                    licenseStoreData, log);
            licenseFound |= licenseFoundForUrl;
            boolean licenseFoundForName = false;
            if (!licenseFoundForUrl) {
                licenseFoundForName = LicenseUtil.handleLicenseName(licenseName, archive, filePath, licenseStoreData,
                        log);
                licenseFound |= licenseFoundForName;
            }
            if (!licenseFoundForUrl && !licenseFoundForName) {
                log.warn("Neither license name nor license URL mapping found for name/URL: " + licenseName + " / "
                        + licenseUrl);
            }
        }
        return licenseFound;
    }

    private URL getParentUrl(final Parent parent) throws MalformedURLException {
        final String groupId = parent.getGroupId();
        final String artifactId = parent.getArtifactId();
        final String version = parent.getVersion();
        final String urlString = getArtifactUrlString(groupId, artifactId, version);
        return new URL(urlString);
    }

    private InputStream getInputStream(final URL url) throws IOException {
        InputStream parentInputStream;
        final URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(connectTimeoutInMilliseconds);
        parentInputStream = urlConnection.getInputStream();
        return parentInputStream;
    }

    private Model getMavenModel(final InputStream inputStream) throws IOException, XmlPullParserException {
        final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
        return xpp3Reader.read(inputStream);
    }

    private String getArtifactUrlString(final String groupId, final String artifactId, final String version) {
        return getMavenCentralBaseUrl() + groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/"
                + artifactId + "-" + version + ".pom";
    }

}
