package org.aposin.licensescout.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.aposin.licensescout.archive.Archive;
import org.aposin.licensescout.util.NullLog;
import org.junit.Assert;
import org.junit.Test;

public class VendorArchiveListFilterTest {

	private static final String FILTERED_VENDOR = "DO NOT INCLUDE";
	private static final String INCLUDED_VENDOR = "INCLUDED";
	
	@Test
	public void testFilterVendor() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Collections.singletonList(FILTERED_VENDOR), new NullLog(), true);
		doTestFilter(filter, getMockedArchivesForVendor(FILTERED_VENDOR), 0);
	}
	
	@Test
	public void testNoFilterVendor() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Collections.singletonList(FILTERED_VENDOR), new NullLog(), true);
		doTestFilter(filter, getMockedArchivesForVendor(INCLUDED_VENDOR), 1);
	}
	
	@Test
	public void testNoFilterSimilarVendor() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Collections.singletonList(FILTERED_VENDOR), new NullLog(), true);
		doTestFilter(filter, getMockedArchivesForVendor(FILTERED_VENDOR + " 2"), 1);
	}
	
	@Test
	public void testFilterSeveral() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Arrays.asList(FILTERED_VENDOR, FILTERED_VENDOR + " 2"), new NullLog(), true);
		doTestFilter(filter, getMockedArchivesForVendor(FILTERED_VENDOR, INCLUDED_VENDOR, FILTERED_VENDOR + " 2", INCLUDED_VENDOR), 2);
		
	}
	
	private static void doTestFilter(final VendorArchiveListFilter filter, final List<Archive> archives, final int expectedSizeAfterFilter) {
		filter.filter(archives);
		Assert.assertEquals(expectedSizeAfterFilter,  archives.size());
	}
	
	private static final List<Archive> getMockedArchivesForVendor(final String... vendors) {
		final List<Archive> archives = new ArrayList<>(vendors.length);
		
		for (final String vendor: vendors) {
			final Archive archive = new Archive(null, null, null, null);
			archive.setVendor(vendor);
			archives.add(archive);
		}
		
		return archives;
	}

}
