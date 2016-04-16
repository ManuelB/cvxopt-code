/*
 * Copyright 2011-2016 joptimizer.com
 *
 * This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 
 * International License. To view a copy of this license, visit 
 *
 *        http://creativecommons.org/licenses/by-nd/4.0/ 
 *
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
package com.joptimizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seventytwomiles.architecturerules.AbstractArchitectureRulesConfigurationTest;

public class ArchitectureRulesUnitTest extends AbstractArchitectureRulesConfigurationTest {

	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	protected String getConfigurationFileName() {
		return "architecture-rules.xml";
	}

	public void testArchitecture() {
		log.debug("testArchitecture");
//		final Configuration configuration = getConfiguration();
//		configuration.setDoCyclicDependencyTest(true);
//		configuration.setThrowExceptionWhenNoPackages(true);

		assertTrue(doTests());
	}
}
