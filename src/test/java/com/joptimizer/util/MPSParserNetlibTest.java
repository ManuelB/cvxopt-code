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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * MPS parsing test.
 * 
 * @see "http://en.wikipedia.org/wiki/MPS_%28format%29"
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class MPSParserNetlibTest extends TestCase {

	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Tests the parsing of a netlib problem.
	 */
	public void xxxtestSingleNetlib() throws Exception {
		log.debug("testSingleNetlib");
		//String problemName = "afiro";
		//String problemName = "afiroPresolved";
		//String problemName = "adlittle";
		//String problemName = "kb2";
		//String problemName = "sc50a";
		//String problemName = "sc50b";
		//String problemName = "blend";
		//String problemName = "scorpion";
		//String problemName = "recipe";
		//String problemName = "recipePresolved";
		//String problemName = "sctap1";
		//String problemName = "fit1d";
		//String problemName = "israel";
		//String problemName = "grow15";
		//String problemName = "etamacro";
		//String problemName = "pilot";
		//String problemName = "pilot4";
		//String problemName = "osa-14";
		//String problemName = "brandyPresolved";
		String problemName = "maros";
		
		File f = Utils.getClasspathResourceAsFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ problemName + ".mps");
		MPSParser mpsParser = new MPSParser();
		mpsParser.parse(f);
		
		Properties expectedSolProps = null;
		try{
			//this is the solution of the mps problem given by Mathematica
			expectedSolProps = load(Utils.getClasspathResourceAsFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ "sol.txt"));
		}catch(Exception e){}

		log.debug("name: " + mpsParser.getName());
		log.debug("n   : " + mpsParser.getN());
		log.debug("meq : " + mpsParser.getMeq());
		log.debug("mieq: " + mpsParser.getMieq());
		log.debug("meq+mieq: " + (mpsParser.getMeq()+mpsParser.getMieq()));
		List<String> variablesNames = mpsParser.getVariablesNames();
		log.debug("x: " + ArrayUtils.toString(variablesNames));		
//		log.debug("c: " + ArrayUtils.toString(p.getC()));
//		log.debug("G: " + ArrayUtils.toString(p.getG()));
//		log.debug("h: " + ArrayUtils.toString(p.getH()));
//		log.debug("A: " + ArrayUtils.toString(p.getA()));
//		log.debug("b: " + ArrayUtils.toString(p.getB()));
//		log.debug("lb:" + ArrayUtils.toString(p.getLb()));
//		log.debug("ub:" + ArrayUtils.toString(p.getUb()));
		
		//check consistency: if the problem was correctly parsed, the expectedSol must be its solution
		double delta = 1.e-7;
		if(expectedSolProps!=null){
			//key = variable name
			//value = sol value
			assertEquals(expectedSolProps.size(), variablesNames.size());
			RealVector expectedSol = new ArrayRealVector(variablesNames.size());
			for(int i=0; i<variablesNames.size(); i++){
				expectedSol.setEntry(i, Double.parseDouble(expectedSolProps.getProperty(variablesNames.get(i))));
			}
			log.debug("expectedSol: " + ArrayUtils.toString(expectedSol.toArray()));
			
			//check objective function value
			Map<String, LPNetlibProblem> problemsMap = LPNetlibProblem.loadAllProblems();
			LPNetlibProblem problem = problemsMap.get(problemName);
			RealVector c = new ArrayRealVector(mpsParser.getC().toArray());
			double value = c.dotProduct(expectedSol);
			log.debug("optimalValue: " + problem.optimalValue);
			log.debug("value       : " + value);
			assertEquals(problem.optimalValue, value, delta);
			
			//check G.x < h
			if(mpsParser.getG()!=null){
				RealMatrix G = new Array2DRowRealMatrix(mpsParser.getG().toArray());
				RealVector h = new ArrayRealVector(mpsParser.getH().toArray());
				RealVector Gxh = G.operate(expectedSol).subtract(h);
				double maxGxh = -Double.MAX_VALUE;
				for(int i=0; i<Gxh.getDimension(); i++){
					//log.debug(i);
					maxGxh = Math.max(maxGxh, Gxh.getEntry(i));
					assertTrue(Gxh.getEntry(i) <= 0);
				}
				log.debug("max(G.x - h): " + maxGxh);
			}
			
			//check A.x = b
			if(mpsParser.getA()!=null){
				RealMatrix A = new Array2DRowRealMatrix(mpsParser.getA().toArray());
				RealVector b = new ArrayRealVector(mpsParser.getB().toArray());
				RealVector Axb = A.operate(expectedSol).subtract(b);
				double norm = Axb.getNorm();
				log.debug("||A.x -b||: " + norm);
				assertEquals(0., norm, delta * mpsParser.getN());//some more tolerance
			}
			
			//check upper and lower bounds
			for(int i=0; i<mpsParser.getLb().size(); i++){
				double di = Double.isNaN(mpsParser.getLb().getQuick(i))? -Double.MAX_VALUE : mpsParser.getLb().getQuick(i);
				assertTrue(di <= expectedSol.getEntry(i));
			}
			for(int i=0; i<mpsParser.getUb().size(); i++){
				double di = Double.isNaN(mpsParser.getUb().getQuick(i))? Double.MAX_VALUE : mpsParser.getUb().getQuick(i);
				assertTrue(di >= expectedSol.getEntry(i));
			}
		}
		
		Utils.writeDoubleArrayToFile(mpsParser.getC().toArray(), "target" + File.separator	+ "c.txt");
		Utils.writeDoubleMatrixToFile(mpsParser.getG().toArray(), "target" + File.separator	+ "G.csv");
		Utils.writeDoubleArrayToFile(mpsParser.getH().toArray(), "target" + File.separator	+ "h.txt");
		Utils.writeDoubleMatrixToFile(mpsParser.getA().toArray(), "target" + File.separator	+ "A.csv");
		Utils.writeDoubleArrayToFile(mpsParser.getB().toArray(), "target" + File.separator	+ "b.txt");
		Utils.writeDoubleArrayToFile(mpsParser.getLb().toArray(), "target" + File.separator	+ "lb.txt");
		Utils.writeDoubleArrayToFile(mpsParser.getUb().toArray(), "target" + File.separator	+ "ub.txt");
	} 
	
	/**
	 * Tests the parsing of all the netlib problems.
	 */
	public void testBulkNetlib() throws Exception {
		log.debug("testBulkNetlib");
		Map<String, LPNetlibProblem> problemsMap = LPNetlibProblem.loadAllProblems();
		//List<NetlibLPResult> list = NetlibLPResult.getProblemsOrderedByName(resultsMap);
		//List<NetlibLPResult> list = NetlibLPResult.getProblemsOrderedByNumberOfRows(resultsMap);
		List<LPNetlibProblem> list = LPNetlibProblem.getProblemsOrderedByNumberOfColumns(problemsMap);
		for(LPNetlibProblem problem : list){
			log.debug("problem: " + problem);
			//System.out.println(res.name);
			File f = Utils.getClasspathResourceAsFile("lp" + File.separator	+ "netlib" + File.separator + problem.name + File.separator	+ problem.name + ".mps");
			MPSParser p = new MPSParser();
			p.parse(f);
		}
	}
	
	public Properties load(File propsFile) throws IOException {
	    Properties props = new Properties();
	    FileInputStream fis = new FileInputStream(propsFile);
	    props.load(fis);    
	    fis.close();
	    return props;
	}
}
