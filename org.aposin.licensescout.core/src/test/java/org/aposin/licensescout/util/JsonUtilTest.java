/**
 * Copyright ${year} ${owner}
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
 *
 */
package org.aposin.licensescout.util;

import org.aposin.licensescout.archive.ArchiveIdentifierVersion;
import org.aposin.licensescout.model.Author;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class JsonUtilTest {

    @Test
    public void getNPMArchiveDescriptionTest() throws Exception {
        File packageFile = getFileFromClasspath("package.json");
        ArchiveIdentifierVersion actual = JsonUtil.getNPMArchiveDescription(packageFile);
        Assert.assertEquals("test-project", actual.getName());
        Assert.assertEquals("1.0.0", actual.getVersion());
    }

    @Test
    public void getNPMArchiveLicenseNameWithSingleLicenseTest() throws Exception {
        File packageFile = getFileFromClasspath("package.json");
        String actual = JsonUtil.getNPMArchiveLicenseName(packageFile);
        Assert.assertEquals("ISC", actual);
    }

    @Test
    public void getNPMArchiveLicenseNameWithNoLicenseTest() throws Exception {
        File packageFile = getFileFromClasspath("package-no-license.json");
        String actual = JsonUtil.getNPMArchiveLicenseName(packageFile);
        Assert.assertEquals("", actual);
    }

    @Test
    public void getNPMArchiveLicenseNameWithMultipleLicensesTest() throws Exception {
        File packageFile = getFileFromClasspath("package-multiple-licenses.json");
        String actual = JsonUtil.getNPMArchiveLicenseName(packageFile);
        Assert.assertEquals("MIT", actual);
    }

    @Test
    public void getNPMArchiveVendorNameTest() throws Exception {
        File packageFile = getFileFromClasspath("package.json");
        String actual = JsonUtil.getNPMArchiveVendorName(packageFile);
        Assert.assertEquals("", actual);
    }

    @Test
    public void getNPMAuthorTest() throws Exception {
        File packageFile = getFileFromClasspath("package.json");
        Author actual = JsonUtil.getNPMAuthor(packageFile);
        Assert.assertEquals("Name", actual.getName());
        Assert.assertEquals("test@test.com", actual.getEmail());
    }

    @Test
    public void getNPMAuthorNullTest() throws Exception {
        File packageFile = getFileFromClasspath("package-no-license.json");
        Author actual = JsonUtil.getNPMAuthor(packageFile);
        Assert.assertEquals(null, actual);
    }

    private File getFileFromClasspath(String fileName) {
        return new File(getClass().getClassLoader().getResource(fileName).getFile());
    }
}
