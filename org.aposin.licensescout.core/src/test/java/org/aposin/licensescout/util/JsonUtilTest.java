package org.aposin.licensescout.util;

import org.aposin.licensescout.archive.ArchiveIdentifierVersion;
import org.aposin.licensescout.model.Author;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class JsonUtilTest {

    @Test
    public void getNPMArchiveDescriptionTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package.json").getFile());
        ArchiveIdentifierVersion actual = JsonUtil.getNPMArchiveDescription(packageFile);
        Assert.assertEquals("test-project", actual.getName());
        Assert.assertEquals("1.0.0", actual.getVersion());
    }

    @Test
    public void getNPMArchiveLicenseNameWithSingleLicenseTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package.json").getFile());
        String actual = JsonUtil.getNPMArchiveLicenseName(packageFile);
        Assert.assertEquals("ISC", actual);
    }

    @Test
    public void getNPMArchiveLicenseNameWithNoLicenseTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package-no-license.json").getFile());
        String actual = JsonUtil.getNPMArchiveLicenseName(packageFile);
        Assert.assertEquals("", actual);
    }

    @Test
    public void getNPMArchiveLicenseNameWithMultipleLicensesTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package-multiple-licenses.json").getFile());
        String actual = JsonUtil.getNPMArchiveLicenseName(packageFile);
        Assert.assertEquals("MIT", actual);
    }

    @Test
    public void getNPMArchiveVendorNameTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package.json").getFile());
        String actual = JsonUtil.getNPMArchiveVendorName(packageFile);
        Assert.assertEquals("", actual);
    }

    @Test
    public void getNPMAuthorTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package.json").getFile());
        Author actual = JsonUtil.getNPMAuthor(packageFile);
        Assert.assertEquals("Name", actual.getName());
        Assert.assertEquals("test@test.com", actual.getEmail());
    }

    @Test
    public void getNPMAuthorNullTest() throws Exception {
        File packageFile = new File(getClass().getClassLoader().getResource("package-no-license.json").getFile());
        Author actual = JsonUtil.getNPMAuthor(packageFile);
        Assert.assertEquals(null, actual);
    }
}
