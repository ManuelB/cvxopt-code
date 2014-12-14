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
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

import com.joptimizer.util.ColtUtils;
import com.joptimizer.util.LPNetlibProblem;
import com.joptimizer.util.MPSParser;
import com.joptimizer.util.Utils;

/**
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class LPPrimalDualMethodNetlibTest extends TestCase {

	private Map<String, LPNetlibProblem> problemsMap;
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public LPPrimalDualMethodNetlibTest(){
		try{
			this.problemsMap = LPNetlibProblem.loadAllProblems();
		}catch(Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}

	public void testDummy() throws Exception {
		log.debug("testDummy");
		assertTrue(true);
	}
	
	/**
	 * Test the 25fv47 netlib problem.
	 */
	public void xxxtest25fv47() throws Exception {
		log.debug("test25fv47");
		
		String problemName = "25fv47";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceFeas(1.E-5);
//		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		//or.setCheckProgressConditions(true);
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the 80bau3b netlib problem.
	 */
	public void xxxtest80bau3b() throws Exception {
		log.debug("test80bau3b");
		
		String problemName = "80bau3b";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		//or.setCheckProgressConditions(true);
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the adlittle netlib problem.
	 */
	public void testAdlittle() throws Exception {
		log.debug("testAdlittle");
		
		String problemName = "adlittle";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		//or.setCheckProgressConditions(true);
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the afiro netlib problem.
	 */
	public void testAfiro() throws Exception {
		log.debug("testAfiro");
		
		String problemName = "afiro";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceKKT(1.E-12);
		or.setToleranceFeas(1.E-8);
		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the (cplex) presolved afiro netlib problem.
	 */
	public void testAfiroPresolved() throws Exception {
		log.debug("testAfiroPresolved");
		
		String problemName = "afiroPresolved";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceKKT(1.E-12);
		or.setToleranceFeas(1.E-8);
		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		or.setPresolvingDisabled(true);
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the agg netlib problem.
	 */
	public void testAgg() throws Exception {
		log.debug("testAgg");
		
		String problemName = "agg";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +9999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the agg2 netlib problem.
	 */
	public void testAgg2() throws Exception {
		log.debug("testAgg2");
		
		String problemName = "agg2";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +9999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the agg3 netlib problem.
	 */
	public void testAgg3() throws Exception {
		log.debug("testAgg3");
		
		String problemName = "agg3";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +9999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the aggPresolved netlib problem.
	 * This is the original agg after a CPlex presolving preprocessing.
	 */
//	public void xxxtestAggPresolved() throws Exception {
//		log.debug("testAggPresolved");
//		
//		String problemName = "aggPresolved";
//		LPNetlibProblem problem = problemsMap.get(problemName);
//				
//		double minLb = 0;
//		double maxUb = +9999999;
//		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
//		or.setCheckKKTSolutionAccuracy(true);
//		//or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
//		or.setTolerance(0.0042);
//		or.setDumpProblem(true);
//		//or.setPresolvingDisabled(true);//already presolved by cplex
//		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
//		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
//		
//		//optimization
//		LPOptimizationResponse response = doOptimization(problem, or, minLb, maxUb);
//		
////		//look for more accuracy:
////		//fix the active constraints
////		for(int i=0; i<or.getLb().size(); i++){
////			if((response.getSolution()[i] - or.getLb().getQuick(i)) < or.getTolerance()){
////				or.getUb().setQuick(i, or.getLb().getQuick(i));
////			}else if((or.getUb().getQuick(i) - response.getSolution()[i]) < or.getTolerance()){
////				or.getLb().setQuick(i, or.getUb().getQuick(i));
////			}
////		}
////		//or.setInitialPoint(response.getSolution());
////		or.setTolerance(1.E-8);
////		doOptimization(problem, or);
//	}
	
	/**
	 * Test the bandm netlib problem.
	 */
	public void testBandm() throws Exception {
		log.debug("testBandm");
		
		String problemName = "bandm";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the beaconfd netlib problem.
	 */
	public void testBeaconfd() throws Exception {
		log.debug("testBeaconfd");
		
		String problemName = "beaconfd";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the blend netlib problem.
	 */
	public void testBlend() throws Exception {
		log.debug("testBlend");
		
		String problemName = "blend";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the bnl1 netlib problem.
	 */
	public void xxxtestBnl1() throws Exception {
		log.debug("testBnl1");
		
		String problemName = "bnl1";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-6);
//		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the bnl2 netlib problem.
	 */
	public void xxxtestBnl2() throws Exception {
		log.debug("testBnl2");
		
		String problemName = "bnl2";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-6);
//		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the boeing1 netlib problem.
	 * This test does not run because JOptimizer does not support ranges
	 */
	public void xxxtestBoeing1() throws Exception {
		log.debug("testBoeing1");
		
		String problemName = "boeing1";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the boeing2 netlib problem.
	 * This test does not run because JOptimizer does not support ranges
	 */
	public void xxxtestBoeing2() throws Exception {
		log.debug("testBoeing2");
		
		String problemName = "boeing2";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the bore3d netlib problem.
	 */
	public void testBore3d() throws Exception {
		log.debug("testBore3d");
		
		String problemName = "bore3d";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the brandy netlib problem.
	 * This problem shows an issue with the primal norm, that grows during the iterations.
	 * The cplex presolved behaves better, indicating that the loss arises in the resolving phase. 
	 */
	public void testBrandy() throws Exception {
		log.debug("testBrandy");
		
		String problemName = "brandy";
		LPNetlibProblem problem = problemsMap.get(problemName);

		double minLb = 0;
		double maxUb = 9999;//the value 99999 shows numerical issues, unless we use more tolerance	
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-10);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		//or.setRescalingDisabled(true);
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the (cplex) presolved brandy netlib problem.
	 */
	public void testBrandyPresolved() throws Exception {
		log.debug("testBrandyPresolved");
		
		String problemName = "brandyPresolved";
		LPNetlibProblem problem = problemsMap.get(problemName);

		double minLb = -9999;
		double maxUb = 9999;//the value 99999 shows numerical issues, unless we use more tolerance	
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-10);
		or.setToleranceFeas(1.E-7);
		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the capri netlib problem.
	 */
	public void testCapri() throws Exception {
		log.debug("testCapri");
		
		String problemName = "capri";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-7);
		or.setTolerance(5.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the cre-a netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestCreA() throws Exception {
		log.debug("testCreA");
		
		String problemName = "cre-a";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the (cplex) presolved cre-a netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestCreAPresolved() throws Exception {
		log.debug("testCreAPresolved");
		
		String problemName = "cre-aPresolved";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the cre-b netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestCreB() throws Exception {
		log.debug("testCreB");
		
		String problemName = "cre-b";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the cre-c netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestCreC() throws Exception {
		log.debug("testCreC");
		
		String problemName = "cre-c";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the cre-d netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestCreD() throws Exception {
		log.debug("testCreD");
		
		String problemName = "cre-d";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the cycle netlib problem.
	 */
	public void xxxtestCycle() throws Exception {
		log.debug("testCycle");
		
		String problemName = "cycle";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the czprob netlib problem.
	 */
	public void xxxtestCzprob() throws Exception {
		log.debug("testCzprob");
		
		String problemName = "czprob";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the d2q06c netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestD2q06c() throws Exception {
		log.debug("testD2q06c");
		
		String problemName = "d2q06c";
		LPNetlibProblem problem = problemsMap.get(problemName);

		double minLb = 0;
		double maxUb = +1.e9;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the d6cube netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestD6cube() throws Exception {
		log.debug("testD6cube");
		
		String problemName = "d6cube";
		LPNetlibProblem problem = problemsMap.get(problemName);

		double minLb = -1.e12;
		double maxUb = +1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the degen2 netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestDegen2() throws Exception {
		log.debug("testDegen2");
		
		String problemName = "degen2";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the degen3 netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestDegen3() throws Exception {
		log.debug("testDegen3");
		
		String problemName = "degen3";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the dfl001 netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestDfl001() throws Exception {
		log.debug("testDfl001");
		
		String problemName = "dfl001";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the e226 netlib problem.
	 */
	public void testE226() throws Exception {
		log.debug("testE226");
		
		String problemName = "e226";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the etamacro netlib problem.
	 */
	public void testEtamacro() throws Exception {
		log.debug("testEtamacro");
		
		String problemName = "etamacro";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-7);
		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the fffff800 netlib problem.
	 */
	public void testFffff800() throws Exception {
		log.debug("testFffff800");
		
		String problemName = "fffff800";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -1.e8;
		double maxUb = +1.e8;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-6);
		//or.setToleranceFeas(1.E-7);
		//or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the finnis netlib problem.
	 * NOTE: Mathematica 5.0 cannot solve this with its interior point method solver.
	 */
	public void testFinnis() throws Exception {
		log.debug("testFinnis");
		
		String problemName = "finnis";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the fit1d netlib problem.
	 */
	public void xxxtestFit1d() throws Exception {
		log.debug("testFit1d");
		
		String problemName = "fit1d";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-6);
//		or.setToleranceFeas(1.E-6);
//		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the fit1p netlib problem.
	 */
	public void xxxtestFit1p() throws Exception {
		log.debug("testFit1p");
		
		String problemName = "fit1p";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the fit2d netlib problem.
	 */
	public void xxxtestFit2d() throws Exception {
		log.debug("testFit2d");
		
		String problemName = "fit2d";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the fit2p netlib problem.
	 */
	public void xxxtestFit2p() throws Exception {
		log.debug("testFit2p");
		
		String problemName = "fit2p";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the forplan netlib problem.
	 * @TODO: solve this problem (RANGES)
	 */
	public void xxxtestForplan() throws Exception {
		log.debug("testForplan");
		
		String problemName = "forplan";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-3);
		or.setTolerance(1.E-3);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the ganges netlib problem.
	 */
	public void xxxtestGanges() throws Exception {
		log.debug("testGanges");
		
		String problemName = "ganges";
		LPNetlibProblem problem = problemsMap.get(problemName);

		double minLb = -1.e9;
		double maxUb = +1.e9;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the gfrd-pnc netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestGfrdPnc() throws Exception {
		log.debug("testGfrdPnc");
		
		String problemName = "gfrd-pnc";
		LPNetlibProblem problem = problemsMap.get(problemName);

		double minLb = 0;
		double maxUb = +1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the greenbea netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestGreenbea() throws Exception {
		log.debug("testGreenbea");
		
		String problemName = "greenbea";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -10;
		double maxUb = +1.e11;				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(5.E-4);
		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the greenbeb netlib problem.
	 */
	public void xxxtestGreenbeb() throws Exception {
		log.debug("testGreenbeb");
		
		String problemName = "greenbeb";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = 0;
		double maxUb = +99999;			
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(5.E-6);
		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the grow15 netlib problem.
	 */
	public void testGrow15() throws Exception {
		log.debug("testGrow15");
		
		String problemName = "grow15";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -1.e9;
		double maxUb = +1.e9;			
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the grow22 netlib problem.
	 */
	public void testGrow22() throws Exception {
		log.debug("testGrow22");
		
		String problemName = "grow22";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -1.e9;
		double maxUb = +1.e9;		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(1.E-5);
//		or.setTolerance(1.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the grow7 netlib problem.
	 */
	public void testGrow7() throws Exception {
		log.debug("testGrow7");
		
		String problemName = "grow7";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -1.e9;
		double maxUb = +1.e9;		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the israel netlib problem.
	 */
	public void testIsrael() throws Exception {
		log.debug("testIsrael");
		
		String problemName = "israel";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -1.e9;
		double maxUb = +1.e9;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the kb2 netlib problem.
	 */
	public void testKb2() throws Exception {
		log.debug("testKb2");
		
		String problemName = "kb2";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		or.setCheckProgressConditions(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the ken-07 netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestKen07() throws Exception {
		log.debug("testKen07");
		
		String problemName = "ken-07";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the ken-11 netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestKen11() throws Exception {
		log.debug("testKen11");
		
		String problemName = "ken-11";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the ken-13 netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestKen13() throws Exception {
		log.debug("testKen13");
		
		String problemName = "ken-13";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the lotfi netlib problem.
	 */
	public void testLotfi() throws Exception {
		log.debug("testLotfi");
		
		String problemName = "lotfi";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +1.e6;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-6);
		or.setToleranceFeas(5.E-6);
		or.setTolerance(5.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the maros-r7 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestMarosR7() throws Exception {
		log.debug("testMarosR7");
		
		String problemName = "maros-r7";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = -1.e12;
		double maxUb = +1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the maros netlib problem.
	 * @TODO: solve this problem
	 * NOTE: the initial point recovered from the solution given by Mathematica 
	 * with its interior point method solver relative to the standard form 
	 * of this problem is not feasible (setPresolvingDisabled = true to see this behaviour).
	 * The initial point for JOptimizer is given (see LPStandardConverter.getStandardComponents) 
	 * by taking straight the values of Math for the non-slack variables, and the residuals of the slack equalities (the inequalities
	 * of the original problem that has turned into equalities by the standard converter)
	 * for the slack variables (so that we have A.x=b): some of this residuals are <0, 
	 * and so the relative slack variable violates its lower bound (=0).
	 * There is the need to investigate what are the conflicting contraints and why they are
	 * not detected in the presolving phase.
	 */
	public void xxxtestMaros() throws Exception {
		log.debug("testMaros");
		
		String problemName = "maros";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = -9999999;
		double maxUb = +9999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setPresolvingDisabled(true);
		//or.setAvoidPresolvingFillIn(true);
		//or.setInitialPoint(loadExpectedSolution(problem));
		//or.setAvoidPresolvingIncreaseSparsity(true);
//		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-5);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the modszk1 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestModszk1() throws Exception {
		log.debug("testModszk1");
		
		String problemName = "modszk1";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = -9999999;
		double maxUb = +9999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the nesm netlib problem.
	 * @TODO: solve this problem (RANGES)
	 */
	public void xxxtestNesm() throws Exception {
		log.debug("testNesm");
		
		String problemName = "nesm";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the osa-07 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestOsa07() throws Exception {
		log.debug("testOsa07");
		
		String problemName = "osa-07";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the osa-14 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestOsa14() throws Exception {
		log.debug("testOsa14");
		
		String problemName = "osa-14";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the osa-30 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestOsa30() throws Exception {
		log.debug("testOsa30");
		
		String problemName = "osa-30";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the pds-02 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestPds02() throws Exception {
		log.debug("testPds02");
		
		String problemName = "pds-02";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the pds-06 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestPds06() throws Exception {
		log.debug("testPds06");
		
		String problemName = "pds-06";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the pds-10 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestPds10() throws Exception {
		log.debug("testPds10");
		
		String problemName = "pds-10";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the perold netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestPerold() throws Exception {
		log.debug("testPerold");
		
		String problemName = "perold";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-5);
//		or.setToleranceFeas(1.E-8);
//		or.setTolerance(1.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the pilot netlib problem.
	 */
	public void xxxtestPilot() throws Exception {
		log.debug("testPilot");
		
		String problemName = "pilot";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +1.e9;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the pilot4 netlib problem.
	 */
	public void testPilot4() throws Exception {
		log.debug("testPilot4");
		
		String problemName = "pilot4";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-5);
//		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the pilot87 netlib problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestPilot87() throws Exception {
		log.debug("testPilot87");
		
		String problemName = "pilot87";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = -9999;
		double maxUb = +1.e6;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the pilot-ja netlib problem.
	 */
	public void xxxtestPilotJa() throws Exception {
		log.debug("testPilotJa");
		
		String problemName = "pilot-ja";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the pilotnov netlib problem.
	 */
	public void xxxtestPilotnov() throws Exception {
		log.debug("testPilotnov");
		
		String problemName = "pilotnov";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(1.E-5);
//		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the pilot-we netlib problem.
	 */
	public void xxxtestPilotWe() throws Exception {
		log.debug("testPilotWe");
		
		String problemName = "pilot-we";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = 0;
		double maxUb = +1.e6;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the recipe netlib problem.
	 */
	public void testRecipe() throws Exception {
		log.debug("testRecipe");
		
		String problemName = "recipe";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-6);
		//or.setToleranceFeas(1.E-6);
		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}

	/**
	 * Test the recipePresolved netlib problem.
	 * This is the recipe input after a CPlex presolving preprocessing.
	 */
	public void testRecipePresolved() throws Exception {
		log.debug("testRecipePresolved");
		
		String problemName = "recipePresolved";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(5.E-6);
		//or.setToleranceFeas(5.E-6);
		or.setTolerance(1.E-6);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sc105 netlib problem.
	 */
	public void testSc105() throws Exception {
		log.debug("testSc105");
		
		String problemName = "sc105";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setInitialPoint(loadExpectedSolution(problem));
		
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sc205 netlib problem.
	 */
	public void testSc205() throws Exception {
		log.debug("testSc205");
		
		String problemName = "sc205";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setInitialPoint(null);
		
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sc50a netlib problem.
	 */
	public void testSc50a() throws Exception {
		log.debug("testSc50a");
		
		String problemName = "sc50a";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setInitialPoint(null);
		
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sc50b netlib problem.
	 */
	public void testSc50b() throws Exception {
		log.debug("testSc50b");
		
		String problemName = "sc50b";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setPresolvingDisabled(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scagr25 netlib problem.
	 */
	public void testScagr25() throws Exception {
		log.debug("testScagr25");
		
		String problemName = "scagr25";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-5);
//		or.setToleranceFeas(1.E-5);
//		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scagr7 netlib problem.
	 */
	public void testScagr7() throws Exception {
		log.debug("testScagr7");
		
		String problemName = "scagr7";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scfxm1 netlib problem.
	 */
	public void testScfxm1() throws Exception {
		log.debug("testScfxm1");
		
		String problemName = "scfxm1";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scfxm2 netlib problem.
	 */
	public void testScfxm2() throws Exception {
		log.debug("testScfxm2");
		
		String problemName = "scfxm2";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}

  /**
	 * Test the scfxm3 netlib problem.
	 */
	public void xxxtestScfxm3() throws Exception {
		log.debug("testScfxm3");
		
		String problemName = "scfxm3";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}	
	
	/**
	 * Test the scorpion netlib problem.
	 */
	public void testScorpion() throws Exception {
		log.debug("testScorpion");
		
		String problemName = "scorpion";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setInitialPoint(loadExpectedSolution(problem));
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scrs8 netlib problem.
	 */
	public void xxxtestScrs8() throws Exception {
		log.debug("testScrs8");
		
		String problemName = "scrs8";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
		
	/**
	 * Test the scsd1 netlib problem.
	 */
	public void testScsd1() throws Exception {
		log.debug("testScsd1");
		
		String problemName = "scsd1";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scsd6 netlib problem.
	 */
	public void xxxtestScsd6() throws Exception {
		log.debug("testScsd6");
		
		String problemName = "scsd6";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the scsd8 netlib problem.
	 */
	public void xxxtestScsd8() throws Exception {
		log.debug("testScsd8");
		
		String problemName = "scsd8";
		LPNetlibProblem problem = problemsMap.get(problemName);

		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sctap1 netlib problem.
	 */
	public void testSctap1() throws Exception {
		log.debug("testSctap1");
		
		String problemName = "sctap1";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
//		or.setTolerance(5.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sctap2 netlib problem.
	 */
	public void xxxtestSctap2() throws Exception {
		log.debug("testSctap2");
		
		String problemName = "sctap2";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
//		or.setTolerance(5E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sctap3 netlib problem.
	 */
	public void xxxtestSctap3() throws Exception {
		log.debug("testSctap3");
		
		String problemName = "sctap3";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
//		or.setTolerance(5E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the seba netlib problem.
	 * @TODO: solve this problem (RANGES)
	 */
	public void xxxtestSeba() throws Exception {
		log.debug("testSeba");
		
		String problemName = "seba";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
		or.setTolerance(5E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the share1b netlib problem.
	 * NOTE: this problem shows trouble with the duplicated rows reduction in presolving, 
	 * because of a loss of precision in doing so much sums of rows.
	 * Switching off the duplicated rows reduction in presolving, the tet is positive.
	 */
	public void testShare1b() throws Exception {
		log.debug("testShare1b");
		
		String problemName = "share1b";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = 0;
		double maxUb = +1.e7;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setToleranceKKT(1.E-5);
		or.setToleranceFeas(5.E-6);
		or.setTolerance(1.E-5);
		or.setAvoidPresolvingIncreaseSparsity(true);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the share2b netlib problem.
	 */
	public void testShare2b() throws Exception {
		log.debug("testShare2b");
		
		String problemName = "share2b";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the shell netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestShell() throws Exception {
		log.debug("testShell");
		
		String problemName = "shell";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = 0;
		double maxUb = 1.e12;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the ship04l netlib problem.
	 */
	public void xxxtestShip04l() throws Exception {
		log.debug("testShip04l");
		
		String problemName = "ship04l";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -999999;
		double maxUb = +999999;		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(5.E-5);
		or.setTolerance(5.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the ship04s netlib problem.
	 */
	public void xxxtestShip04s() throws Exception {
		log.debug("testShip04s");
		
		String problemName = "ship04s";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = 0;
		double maxUb = 999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the ship08l netlib problem.
	 * @TODO: solve this test (issue with the surrogate duality gap)
	 */
	public void xxxtestShip08l() throws Exception {
		log.debug("testShip08l");
		
		String problemName = "ship08l";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -0;
		double maxUb = +99;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the ship08s netlib problem.
	 * @TODO: solve this test (problem with the surrogate duality gap)
	 */
	public void xxxtestShip08s() throws Exception {
		log.debug("testShip08s");
		
		String problemName = "ship08s";
		LPNetlibProblem problem = problemsMap.get(problemName);
		
		double minLb = -9999999;
		double maxUb = +9999999;		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the ship12l netlib problem.
	 */
	public void xxxtestShip12l() throws Exception {
		log.debug("testShip12l");
		
		String problemName = "ship12l";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(5.E-6);
		or.setTolerance(5.E-5);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the ship12s netlib problem.
	 * @TODO: solve this test (problem with the surrogate duality gap)
	 */
	public void xxxtestShip12s() throws Exception {
		log.debug("testShip12s");
		
		String problemName = "ship12s";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
		or.setToleranceFeas(1.E-5);
		or.setTolerance(1.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the sierra netlib problem.
	 * This problem cannot be solved by JOptimizer, that requires 
	 * a full rank equalities matrices.
	 */
	public void xxxtestSierra() throws Exception {
		log.debug("testSierra");
		
		String problemName = "sierra";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(1.E-7);
//		or.setTolerance(1.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the stair netlib problem.
	 */
	public void testStair() throws Exception {
		log.debug("testStair");
		
		String problemName = "stair";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
		or.setToleranceKKT(1.E-8);
		or.setToleranceFeas(1.E-5);
//		or.setTolerance(5.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}

  /**
	 * Test the standata netlib problem.
	 */
	public void xxxtestStandata() throws Exception {
		log.debug("testStandata");
		
		String problemName = "standata";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
//		or.setTolerance(5.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}	
	
	/**
	 * Test the standmps netlib problem.
	 */
	public void xxxtestStandmps() throws Exception {
		log.debug("testStandmps");
		
		String problemName = "standmps";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-7);
//		or.setTolerance(5.E-7);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the stocfor1 netlib problem.
	 */
	public void testStocfor1() throws Exception {
		log.debug("testStocfor1");
		
		String problemName = "stocfor1";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-6);
//		or.setTolerance(5.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the stocfor2 netlib problem.
	 */
	public void xxxtestStocfor2() throws Exception {
		log.debug("testStocfor2");
		
		String problemName = "stocfor2";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-6);
//		or.setTolerance(5.E-4);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the tuff problem.
	 */
	public void testTuff() throws Exception {
		log.debug("testTuff");
		
		String problemName = "tuff";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(5.E-8);
//		or.setTolerance(5.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	/**
	 * Test the vtp-base problem.
	 */
	public void testVtpbase() throws Exception {
		log.debug("testVtpbase");
		
		String problemName = "vtp-base";
		LPNetlibProblem problem = problemsMap.get(problemName);
			
		double minLb = -999999;
		double maxUb = +999999;
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-7);
//		or.setToleranceFeas(5.E-6);
//		or.setTolerance(5.E-6);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the wood1p problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestWood1p() throws Exception {
		log.debug("testWood1p");
		
		String problemName = "wood1p";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		double minLb = -1.e9;
		double maxUb = +1.e9;		
		LPOptimizationRequest or = loadLPOptimizationRequest(problem, minLb, maxUb);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(5.E-8);
//		or.setTolerance(5.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or, minLb, maxUb);
	}
	
	/**
	 * Test the woodw problem.
	 * @TODO: solve this problem
	 */
	public void xxxtestWoodw() throws Exception {
		log.debug("testWoodw");
		
		String problemName = "woodw";
		LPNetlibProblem problem = problemsMap.get(problemName);
				
		LPOptimizationRequest or = loadLPOptimizationRequest(problem);
		or.setCheckKKTSolutionAccuracy(true);
//		or.setToleranceKKT(1.E-8);
//		or.setToleranceFeas(5.E-8);
//		or.setTolerance(5.E-8);
		or.setDumpProblem(true);
		//or.setCheckOptimalDualityConditions(true);//just for debugging, not mandatory
		or.setCheckOptimalLagrangianBounds(true);//just for debugging, not mandatory
		
		//optimization
		doOptimization(problem, or);
	}
	
	private LPOptimizationResponse doOptimization(LPNetlibProblem problem, LPOptimizationRequest or) throws Exception{
		double minLb = LPPrimalDualMethod.DEFAULT_MIN_LOWER_BOUND;
		double maxUb = LPPrimalDualMethod.DEFAULT_MAX_UPPER_BOUND;
		return doOptimization(problem, or, minLb, maxUb);
	}
	
	private LPOptimizationResponse doOptimization(LPNetlibProblem problem, LPOptimizationRequest or, double minLb, double maxUb) throws Exception{
		long t0 = System.currentTimeMillis();
		LPPrimalDualMethod opt = new LPPrimalDualMethod(minLb, maxUb);
		
		opt.setLPOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		log.info("doOptimization time: " + (System.currentTimeMillis() - t0));
		
		if(returnCode != OptimizationResponse.SUCCESS){
			fail();
		}
		
		LPOptimizationResponse response = opt.getLPOptimizationResponse();
		checkSolution(problem, or, response);
		return response;
	}
	
	private LPOptimizationRequest loadLPOptimizationRequest(LPNetlibProblem problem) throws Exception{
		double minLb = LPPrimalDualMethod.DEFAULT_MIN_LOWER_BOUND;
		double maxUb = LPPrimalDualMethod.DEFAULT_MAX_UPPER_BOUND;
		return loadLPOptimizationRequest(problem, minLb, maxUb);
	}
	
	private LPOptimizationRequest loadLPOptimizationRequest(LPNetlibProblem problem, double minLb, double maxUb) throws Exception{
		String problemName = problem.name;
		log.debug("problemName   : " + problemName);
		File f = Utils.getClasspathResourceAsFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ problemName + ".mps");
		MPSParser mpsParser = new MPSParser();
		mpsParser.parse(f);

		log.debug("name: " + mpsParser.getName());
		log.debug("meq : " + mpsParser.getMeq());
		log.debug("mieq: " + mpsParser.getMieq());
		log.debug("nz  : " + (mpsParser.getNzG() + mpsParser.getNzA()));
		log.debug("rows: " + (mpsParser.getMeq()+mpsParser.getMieq()));
		log.debug("cols: " + mpsParser.getN());
		
		assertEquals(problem.nz, (mpsParser.getNzG() + mpsParser.getNzA()));
		assertEquals(problem.rows, (mpsParser.getMeq()+mpsParser.getMieq()));
		assertEquals(problem.columns, mpsParser.getN());
		
		DoubleMatrix1D c = mpsParser.getC();
		DoubleMatrix2D G = mpsParser.getG();
		DoubleMatrix1D h = mpsParser.getH();
		DoubleMatrix2D A = mpsParser.getA();
		DoubleMatrix1D b = mpsParser.getB();
		DoubleMatrix1D lb = mpsParser.getLb();
		DoubleMatrix1D ub = mpsParser.getUb();
		
		//the unbounded bounds are saved on the files with NaN values, so substitute them with acceptable values
		lb = ColtUtils.replaceValues(lb, Double.NaN, minLb);
		ub = ColtUtils.replaceValues(ub, Double.NaN, maxUb);
		
		LPOptimizationRequest or = new LPOptimizationRequest();
		or.setC(c);
		or.setG(G);
		or.setH(h);
		or.setA(A);
		or.setB(b);
		or.setLb(lb);
		or.setUb(ub);
		
		return or;
	}
	
	private double[] loadExpectedSolution(LPNetlibProblem problem)
			throws Exception {
		//load the standard solution
		double[] expectedSolution = Utils.loadDoubleArrayFromFile("lp"
				+ File.separator + "netlib" + File.separator + problem.name
				+ File.separator + "standardSolution.txt");
		//take the elements excluding the slack variables
		int nOfSlackVariables = (int)Utils.loadDoubleArrayFromFile("lp"
				+ File.separator + "netlib" + File.separator + problem.name
				+ File.separator + "standardS.txt")[0];
		double[] ret = new double[expectedSolution.length - nOfSlackVariables];
		for(int i=nOfSlackVariables; i<expectedSolution.length; i++){
			ret[i - nOfSlackVariables] = expectedSolution[i];
		}
		return ret;
	}
	
	private void checkSolution(LPNetlibProblem problem, LPOptimizationRequest or,
			LPOptimizationResponse response) throws Exception{
		
		double expectedvalue = problem.optimalValue;
		log.debug("expectedvalue : "	+ expectedvalue);
		double[] sol = response.getSolution();
		RealVector cVector = new ArrayRealVector(or.getC().toArray());
		RealVector solVector = new ArrayRealVector(sol);
		double value = cVector.dotProduct(solVector);
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ value);
		
		//check constraints
		assertEquals(or.getLb().size(), sol.length);
		assertEquals(or.getUb().size(), sol.length);
		RealVector x = MatrixUtils.createRealVector(sol);
		
		//x >= lb
		double maxLbmx = -Double.MAX_VALUE;
		for(int i=0; i<or.getLb().size(); i++){
			double di = Double.isNaN(or.getLb().getQuick(i))? -Double.MAX_VALUE : or.getLb().getQuick(i);
			maxLbmx = Math.max(maxLbmx, di - x.getEntry(i));
			assertTrue(di <= x.getEntry(i) + or.getTolerance());
		}
		log.debug("max(lb - x): " + maxLbmx);
		
		//x <= ub
		double maxXmub = -Double.MAX_VALUE;
		for(int i=0; i<or.getUb().size(); i++){
			double di = Double.isNaN(or.getUb().getQuick(i))? Double.MAX_VALUE : or.getUb().getQuick(i);
			maxXmub = Math.max(maxXmub, x.getEntry(i) - di);
			assertTrue(di + or.getTolerance() >= x.getEntry(i));
		}
		log.debug("max(x - ub): " + maxXmub);
		
		//G.x <h
		if(or.getG()!=null && or.getG().rows()>0){
			RealMatrix GMatrix = MatrixUtils.createRealMatrix(or.getG().toArray()); 
			RealVector hvector = MatrixUtils.createRealVector(or.getH().toArray());
			RealVector Gxh = GMatrix.operate(x).subtract(hvector);
			double maxGxh = -Double.MAX_VALUE;
			for(int i=0; i<Gxh.getDimension(); i++){
				maxGxh = Math.max(maxGxh, Gxh.getEntry(i));
				assertTrue(Gxh.getEntry(i) - or.getTolerance() <= 0);
			}
			log.debug("max(G.x - h): " + maxGxh);
		}
		
		//A.x = b
		if(or.getA()!=null && or.getA().rows()>0){
			RealMatrix AMatrix = MatrixUtils.createRealMatrix(or.getA().toArray());
			RealVector bvector = MatrixUtils.createRealVector(or.getB().toArray());
			RealVector Axb = AMatrix.operate(x).subtract(bvector);
			double norm = Axb.getNorm();
			log.debug("||A.x -b||: " + norm);
			assertEquals(0., norm, or.getToleranceFeas());
		}
		
		double percDelta = Math.abs((expectedvalue-value)/expectedvalue);
		log.debug("percDelta: " + percDelta);
		//assertEquals(0., percDelta, or.getTolerance());
		//assertEquals(expectedvalue, value, or.getTolerance());
		assertTrue(value < expectedvalue + or.getTolerance());//can even beat other optimizers! the rebel yell...
	}
}
