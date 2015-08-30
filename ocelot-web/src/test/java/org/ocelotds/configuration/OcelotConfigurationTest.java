/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ocelotds.configuration;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author hhfrancois
 */
public class OcelotConfigurationTest {
	
	/**
	 * Test of getStacktracelength method, of class OcelotConfiguration.
	 */
	@Test
	public void testGetSetStacktracelength() {
		System.out.println("testGetSetStacktracelength");
		OcelotConfiguration instance = new OcelotConfiguration();
		int expResult = 50;
		int result = instance.getStacktracelength();
		assertThat(result).isEqualTo(expResult);
		expResult = 10;
		instance.setStacktracelength(expResult);
		result = instance.getStacktracelength();
		assertThat(result).isEqualTo(expResult);
	}
}