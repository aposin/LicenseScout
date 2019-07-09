package org.aposin.licensescout.main;

import org.junit.Test;

public class MainTest {

	@Test(expected = IllegalArgumentException.class)
	public void testWrongNumberOfArguments() throws Exception {
		Main.main(new String[0]);
	}
	
}