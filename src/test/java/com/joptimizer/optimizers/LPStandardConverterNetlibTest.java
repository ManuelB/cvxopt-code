/*
 * Copyright 2011-2014 JOptimizer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.joptimizer.optimizers;

import java.io.File;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joptimizer.util.LPNetlibProblem;
import com.joptimizer.util.MPSParser;
import com.joptimizer.util.Utils;

/**
 * Standard form conversion test.
 * 
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class LPStandardConverterNetlibTest extends TestCase {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	/**
	 * Tests the standardization of all the netlib problems.
	 */
	public void xxxtestAllNetlib() throws Exception {
		log.debug("testAllNetlib");
		Map<String, LPNetlibProblem> resultsMap = LPNetlibProblem.loadAllProblems();
		//List<NetlibLPResult> list = NetlibLPResult.getProblemsOrderedByName(resultsMap);
		//List<NetlibLPResult> list = NetlibLPResult.getProblemsOrderedByNumberOfRows(resultsMap);
		List<LPNetlibProblem> list = LPNetlibProblem.getProblemsOrderedByNumberOfColumns(resultsMap);
		for(LPNetlibProblem problem : list){
			log.debug("problem: " + problem);
			System.out.println(problem.name);
			doTesting(problem.name);
		}
	}
	
	/**
	 * Tests the standardization of the afiro netlib problem.
	 */
	public void testAfiro() throws Exception {
		log.debug("testAfiro");
	  
		String problemName = "afiro";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the agg netlib problem.
	 */
	public void testAgg() throws Exception {
		log.debug("testAgg");
	
		String problemName = "agg";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the blend netlib problem.
	 */
	public void testBlend() throws Exception {
		log.debug("testBlend");
	
		String problemName = "blend";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the brandyPresolved netlib problem.
	 */
	public void testBrandyPresolved() throws Exception {
		log.debug("testBrandyPresolved");
	
		String problemName = "brandyPresolved";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the cre-aPresolved netlib problem.
	 */
	public void testCreaPresolved() throws Exception {
		log.debug("testCreaPresolved");
	
		String problemName = "cre-aPresolved";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the etamacro netlib problem.
	 */
	public void testEtamacro() throws Exception {
		log.debug("testEtamacro");
	
		String problemName = "etamacro";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the fit1d netlib problem.
	 */
	public void testFit1d() throws Exception {
		log.debug("testFit1d");
	
		String problemName = "fit1d";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the israel netlib problem.
	 */
	public void testIsrael() throws Exception {
		log.debug("testIsrael");
	
		String problemName = "israel";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the kb2 netlib problem.
	 */
	public void testKb2() throws Exception {
		log.debug("testKb2");
	  
		String problemName = "kb2";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the maros netlib problem.
	 */
	public void testMaros() throws Exception {
		log.debug("testMaros");
	
		String problemName = "maros";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the pilot4 netlib problem.
	 */
	public void testPilot4() throws Exception {
		log.debug("testPilot4");
	
		String problemName = "pilot4";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the recipe netlib problem.
	 */
	public void testRecipe() throws Exception {
		log.debug("testRecipe");
	
		String problemName = "recipe";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the sc50a netlib problem.
	 */
	public void testSc50a() throws Exception {
		log.debug("testSc50a");
	  
		String problemName = "sc50a";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the sc50b netlib problem.
	 */
	public void testSc50b() throws Exception {
		log.debug("testSc50b");
	  
		String problemName = "sc50b";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the scorpion netlib problem.
	 */
	public void testScorpion() throws Exception {
		log.debug("testScorpion");
	  
		String problemName = "scorpion";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the sctap1 netlib problem.
	 */
	public void testSctap1() throws Exception {
		log.debug("testSctap1");
	  
		String problemName = "sctap1";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the shell netlib problem.
	 */
	public void testShell() throws Exception {
		log.debug("testShell");
	
		String problemName = "shell";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the ship04s netlib problem.
	 */
	public void testShip04s() throws Exception {
		log.debug("testShip04s");
	  
		String problemName = "ship04s";
		doTesting(problemName);
	}
	
	/**
	 * Tests the standardization of the ship08l netlib problem.
	 */
	public void testShip08l() throws Exception {
		log.debug("testShip08l");
	
		String problemName = "ship08l";
		doTesting(problemName);
	}

	/**
	 * Tests the standardization of the sierra netlib problem.
	 */
	public void testSierra() throws Exception {
		log.debug("testSierra");
	  
		String problemName = "sierra";
		doTesting(problemName);
	}
	
	private void doTesting(String problemName) throws Exception {
		log.debug("doTesting: " + problemName);
		File f = Utils.getClasspathResourceAsFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ problemName + ".mps");
		MPSParser mps = new MPSParser();
		mps.parse(f);

		//standard form conversion
		LPStandardConverter lpParser = new LPStandardConverter();
		lpParser.toStandardForm(mps.getC(), mps.getG(), mps.getH(), mps.getA(), mps.getB(), mps.getLb(), mps.getUb());
		
		int n = lpParser.getStandardN();
		int s = lpParser.getStandardS();
		double[] c = lpParser.getStandardC().toArray();
		double[][] A = lpParser.getStandardA().toArray();
		double[] b = lpParser.getStandardB().toArray();
		double[] lb = lpParser.getStandardLB().toArray();
		double[] ub = lpParser.getStandardUB().toArray();
		log.debug("n : " + n);
		log.debug("s : " + s);
		
		assertEquals(mps.getG().rows(), s);
		assertEquals(s + lpParser.getOriginalN(), n);
		assertEquals(lb.length, n);
		assertEquals(ub.length, n);
		
//			String outDir = "." + File.separator	+ "src" + File.separator	+ "test" + File.separator	+ "resources"+ File.separator	+"lp" + File.separator	+ "netlib";  
//			Utils.writeDoubleArrayToFile(new double[]{s}, outDir + File.separator + problem.name + File.separator	+ "standardS.txt");
//			Utils.writeDoubleArrayToFile(c, outDir + File.separator + problem.name + File.separator	+ "standardC.txt");
//			Utils.writeDoubleMatrixToFile(A, outDir + File.separator + problem.name + File.separator	+ "standardA.txt");
//			Utils.writeDoubleArrayToFile(b, outDir + File.separator + problem.name + File.separator	+ "standardB.txt");
//			Utils.writeDoubleArrayToFile(lb, outDir + File.separator + problem.name + File.separator	+ "standardLB.txt");
//			Utils.writeDoubleArrayToFile(ub, outDir + File.separator + problem.name + File.separator	+ "standardUB.txt");
			
	}
}
