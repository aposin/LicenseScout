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

import static org.aposin.licensescout.archive.ArchiveType.JAVASCRIPT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.aposin.licensescout.archive.ArchiveIdentifierVersion;
import org.aposin.licensescout.model.Author;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utilities for obtaining information from JSON files.
 * 
 */
public class JsonUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private JsonUtil() {
        //  DO NOTHING
    }

    /**
     * @param file the package.json file
     * @return on object identifying the archive by name and version
     * @throws IOException if an error occurred while reading the JSON file
     */
    public static ArchiveIdentifierVersion getNPMArchiveDescription(final File file) throws IOException {
        final JSONObject obj = readJsconFile(file);
        final String name = obj.getString("name");
        final String version = obj.getString("version");
        return new ArchiveIdentifierVersion(JAVASCRIPT, name, version);
    }

    /**
     * @param file the package.json file
     * @return a license name or an empty string if the package.json does not contain a 'license' tag
     * @throws IOException if an error occurred while reading the JSON file
     */
    public static String getNPMArchiveLicenseName(final File file) throws IOException {
        final JSONObject obj = readJsconFile(file);
        final JSONArray licensesArray = obj.optJSONArray("licenses");
        if (licensesArray != null) {
            final Iterator<Object> iter = licensesArray.iterator();
            while (iter.hasNext()) {
                Object o = iter.next();
                if (o instanceof JSONObject) {
                    final JSONObject jobj = (JSONObject) o;
                    final String typeValue = jobj.optString("type");
                    if (typeValue != null) {
                        return typeValue;
                    }
                }
            }
        }
        final JSONObject licenseObject = obj.optJSONObject("license");
        if (licenseObject != null) {
            return licenseObject.optString("type");
        }
        return obj.optString("license");
    }

    /**
     * Obtains the vendor name from a package.json file.
     * 
     * @param file the package.json file
     * @return a vendor name. If no vendor name is present in the file, an empty string is returned.
     * @throws IOException if an error occurred while reading the JSON file
     */
    public static String getNPMArchiveVendorName(final File file) throws IOException {
        final JSONObject obj = readJsconFile(file);
        return obj.optString("vendor");
    }

    /**
     * TODO (DOC)
     * @param file the package.json file
     * @return on object identifying the archive by name and version
     * @throws IOException if an error occurred while reading the JSON file
     */
    public static Author getNPMAuthor(final File file) throws IOException {
        final JSONObject obj = readJsconFile(file);
        final JSONObject authorObject = obj.optJSONObject("author");
        if (authorObject != null) {
            String name = authorObject.optString("name");
            String email = authorObject.optString("email");
            return new Author(name, email);
        }
        return null;
    }

    private static JSONObject readJsconFile(final File file) throws IOException {
        final StringBuilder jsonData = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonData.append(line).append("\n");
            }
        }
        return new JSONObject(jsonData.toString());
    }

}
