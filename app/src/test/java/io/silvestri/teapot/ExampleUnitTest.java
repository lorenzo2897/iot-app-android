package io.silvestri.teapot;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {
	@Test
	public void json_settings() throws Exception {
		String res = MainActivity.makeSettingsJson(80, 4);
		assertEquals("{}", res);
	}
}