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
		assertFilterSize(filter, getMockedArchivesForVendor(FILTERED_VENDOR), 0);
	}
	
	@Test
	public void testNoFilterVendor() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Collections.singletonList(FILTERED_VENDOR), new NullLog(), true);
		assertFilterSize(filter, getMockedArchivesForVendor(INCLUDED_VENDOR), 1);
	}
	
	@Test
	public void testNoFilterSimilarVendor() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Collections.singletonList(FILTERED_VENDOR), new NullLog(), true);
		assertFilterSize(filter, getMockedArchivesForVendor(FILTERED_VENDOR + " 2"), 1);
	}
	
	@Test
	public void testFilterSeveral() {
		final VendorArchiveListFilter filter = new VendorArchiveListFilter(Arrays.asList(FILTERED_VENDOR, FILTERED_VENDOR + " 2"), new NullLog(), true);
		assertFilterSize(filter, getMockedArchivesForVendor(FILTERED_VENDOR, INCLUDED_VENDOR, FILTERED_VENDOR + " 2", INCLUDED_VENDOR), 2);
		
	}
	
	private static void assertFilterSize(final VendorArchiveListFilter filter, final List<Archive> archives, final int expectedSizeAfterFilter) {
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
