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
package com.joptimizer.util;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class BitwiseMathTest extends TestCase{
	
	private Log log = LogFactory.getLog(this.getClass().getName());

	public void testAdd() {
		log.debug("testAdd");
		log.debug("2 + 3 = " + BitwiseMath.add(2, 3));
	}

	public void testSubtract() {
		log.debug("testSubtract");
		log.debug("2 - 3 = " + BitwiseMath.subtract(2, 3));
	}
	
	public void testMultiply() {
		log.debug("testMultiply");
		log.debug("7 * 100 = " + BitwiseMath.multiply(7, 100));
	}

	public void testDivide() {
		log.debug("testDivide");
		log.debug("7 / 3 = " + BitwiseMath.divide(7, 3));
	}
	
	public void testDivide2() {
		log.debug("testDivide2");
		log.debug("7*100 / 3 = " + BitwiseMath.divide(7*100, 3));
	}

}
