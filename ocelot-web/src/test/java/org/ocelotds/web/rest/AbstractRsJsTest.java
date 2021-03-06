/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ocelotds.web.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.ocelotds.Constants;
import org.slf4j.Logger;

/**
 *
 * @author hhfrancois
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractRsJsTest {

	@InjectMocks
	@Spy
	AbstractRsJsImpl instance = new AbstractRsJsImpl();
	
	@Mock
	Logger logger;

	/**
	 * Test of getStreams method, of class AbstractRsJs.
	 */
	@Test
	public void testGetStreams() {
		System.out.println("getStreams");
		List<InputStream> result = instance.getStreams();
		assertThat(result).isNotNull();
	}

	/**
	 * Test of getResource method, of class AbstractRsJs.
	 */
	@Test
	public void testGetResource() {
		System.out.println("getResource");
		String name = "/test.js";
		URL result = instance.getResource(name);
		assertThat(result).isNotNull();
	}

	/**
	 * Test of getJsFilename method, of class AbstractRsJs.
	 */
	@Test
	public void testGetJsFilenameFwk() {
		System.out.println("getJsFilename");
		String classname = "p1.p2.p3.Cls1";
		String expResult = "/p1/p2/p3/Cls1.fwk.js";
		String result = instance.getJsFilename(classname, "fwk");
		assertThat(result).isEqualTo(expResult);
	}

	/**
	 * Test of getJsFilename method, of class AbstractRsJs.
	 */
	@Test
	public void testGetJsFilenameNoFwk() {
		System.out.println("getJsFilename");
		String classname = "p1.p2.p3.Cls1";
		String expResult = "/p1/p2/p3/Cls1.js";
		String result = instance.getJsFilename(classname, null);
		assertThat(result).isEqualTo(expResult);
	}

	/**
	 * Test of addStream method, of class AbstractRsJs.
	 * @throws java.io.IOException
	 */
	@Test
	public void testAddStream() throws IOException {
		System.out.println("addStream");
		List<InputStream> streams = new ArrayList<>();
		String filename = "/test.js";
		instance.addStream(streams, filename);
		assertThat(streams).hasSize(1);

		instance.addStream(streams, "unknown.js");
		assertThat(streams).hasSize(1);
	}

	/**
	 * Test of getSequenceInputStream method, of class AbstractRsJs.
	 */
	@Test
	public void testGetSequenceInputStream() {
		System.out.println("getSequenceInputStream");
		InputStream stream1 = new ByteArrayInputStream("body1".getBytes());
		InputStream stream2 = new ByteArrayInputStream("body2".getBytes());
		InputStream expresult = new ByteArrayInputStream("body1body2".getBytes());
		List<InputStream> streams = Arrays.asList(stream1, stream2);
		SequenceInputStream result = instance.getSequenceInputStream(streams);
		assertThat(result).hasSameContentAs(expresult);
	}
	
	class AbstractRsJsImpl extends AbstractRsJs{

		@Override
		List<InputStream> getStreams() {
			return new ArrayList<>();
		}
	}
}