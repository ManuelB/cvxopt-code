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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import com.joptimizer.algebra.QRSparseFactorization;
import com.joptimizer.util.Utils;

/**
 * LP presolving test.
 * 
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class LPPresolverNetlibTest extends TestCase {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Tests the presolving of the afiro netlib problem.
	 */
	public void testAfiro() throws Exception {
		log.debug("testAfiro");
		String problemName = "afiro";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the agg netlib problem.
	 */
	public void testAgg() throws Exception {
		log.debug("testAgg");
		String problemName = "agg";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the blend netlib problem.
	 */
	public void testBlend() throws Exception {
		log.debug("testBlend");
		String problemName = "blend";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the brandy netlib problem.
	 */
	public void testBrandy() throws Exception {
		log.debug("testBrandy");
		String problemName = "brandy";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the degen2 netlib problem.
	 * This fails because of the degenerate A matrix.
	 */
	public void xxxtestDegen2() throws Exception {
		log.debug("testDegen2");
		String problemName = "degen2";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the etamacro netlib problem.
	 */
	public void testEtamacro() throws Exception {
		log.debug("testEtamacro");
		String problemName = "etamacro";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the fit1d netlib problem.
	 */
	public void testFit1d() throws Exception {
		log.debug("testFit1d");
		String problemName = "fit1d";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the israel netlib problem.
	 */
	public void testIsrael() throws Exception {
		log.debug("testIsrael");
		String problemName = "israel";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the kb2 netlib problem.
	 */
	public void testKb2() throws Exception {
		log.debug("testKb2");
		String problemName = "kb2";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the maros netlib problem.
	 */
	public void testMaros() throws Exception {
		log.debug("testMaros");
		String problemName = "maros";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the pilot4 netlib problem.
	 */
	public void testPilot4() throws Exception {
		log.debug("testPilot4");
		String problemName = "pilot4";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the afiro netlib problem.
	 * @TODO: this fails!!!!
	 */
	public void testRecipe() throws Exception {
		log.debug("testRecipe");
		String problemName = "recipe"; 
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the sc50a netlib problem.
	 */
	public void testSc50a() throws Exception {
		log.debug("testSc50a");
		String problemName = "sc50a";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the sc50b netlib problem.
	 */
	public void testSc50b() throws Exception {
		log.debug("testSc50b");
		String problemName = "sc50b";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the scorpion netlib problem.
	 */
	public void testScorpion() throws Exception {
		log.debug("testScorpion");
		String problemName = "scorpion";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the sctap1 netlib problem.
	 * NB: this is a deterministic problem
	 */
	public void testSctap1() throws Exception {
		log.debug("testSctap1");
		String problemName = "sctap1";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the share1b netlib problem.
	 */
	public void testShare1b() throws Exception {
		log.debug("testShare1b");
		String problemName = "share1b";
		doTesting(problemName, true, 5.0E-5);//a lot of removeDuplicateRow, hence a loss of accuracy
	}

	/**
	 * Tests the presolving of the ship04s netlib problem.
	 */
	public void testShip04s() throws Exception {
		log.debug("testShip04s");
		String problemName = "ship04s";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the shell netlib problem.
	 */
	public void testShell() throws Exception {
		log.debug("testShell");
		String problemName = "shell";
		doTesting(problemName);
	}

	/**
	 * Tests the presolving of the ship08l netlib problem.
	 */
	public void testShip08l() throws Exception {
		log.debug("testShip08l");
		String problemName = "ship08l";
		doTesting(problemName);
	}
	
	public void doTesting(String problemName) throws Exception {
		doTesting(problemName, true, Double.NaN);
	}
	
	/**
	 * Tests the presolving of a netlib problem.
	 * If checkExpectedSolution, the presolving is checked step by step against 
	 * a (beforehand) known solution of the problem. 
	 * NOTE: this known solution might differ from the solution given by the presolver 
	 * (e.g. in the presence of a weakly dominated column @see {@link LPPresolver#removeDominatedColumns}, 
	 * or if it is calculated with simplex method 
	 * or if bounds are not exactly the same), so sometimes it is not a good test. 
	 */
	public void doTesting(String problemName, boolean checkExpectedSolution, double myExpectedTolerance) throws Exception {
		log.debug("doTesting: " + problemName);

		int s = (int) Utils.loadDoubleArrayFromFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ "standardS.txt")[0];
		double[] c = Utils.loadDoubleArrayFromFile("lp" + File.separator + "netlib" + File.separator + problemName + File.separator	+ "standardC.txt");
		double[][] A = Utils.loadDoubleMatrixFromFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ "standardA.csv", ",".charAt(0));
		double[] b = Utils.loadDoubleArrayFromFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ "standardB.txt");
		double[] lb = Utils.loadDoubleArrayFromFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ "standardLB.txt");
		double[] ub = Utils.loadDoubleArrayFromFile("lp" + File.separator	+ "netlib" + File.separator + problemName + File.separator	+ "standardUB.txt");
		double[] expectedSolution = Utils.loadDoubleArrayFromFile("lp"	+ File.separator + "netlib" + File.separator + problemName	+ File.separator + "standardSolution.txt");
		double expectedValue = Utils.loadDoubleArrayFromFile("lp"	+ File.separator + "netlib" + File.separator + problemName	+ File.separator + "standardValue.txt")[0];
		double expectedTolerance = Utils.loadDoubleArrayFromFile("lp"	+ File.separator + "netlib" + File.separator + problemName	+ File.separator + "standardTolerance.txt")[0];
		
		//in order to compare with tha Math results, we must have the same bounds
		for(int i=0; i<lb.length; i++){
			if(Double.isNaN(lb[i])){
				lb[i] = -9999999d; //the same as the notebook value
			}
		}
		for(int i=0; i<ub.length; i++){
			if(Double.isNaN(ub[i])){
				ub[i] = +9999999d; //the same as the notebook value
			}
		}
		
		RealMatrix AMatrix = MatrixUtils.createRealMatrix(A);
		RealVector bVector = MatrixUtils.createRealVector(b);
		//expectedTolerance = Math.max(expectedTolerance,	AMatrix.operate(MatrixUtils.createRealVector(expectedSolution)).subtract(bVector).getNorm());
		expectedTolerance = Math.max(1.e-9, expectedTolerance);

		// must be: A pXn with rank(A)=p < n
		QRSparseFactorization qr = new QRSparseFactorization(new SparseDoubleMatrix2D(A));
		qr.factorize();
		log.debug("p        : " + AMatrix.getRowDimension());
		log.debug("n        : " + AMatrix.getColumnDimension());
		log.debug("full rank: " + qr.hasFullRank());

		LPPresolver lpPresolver = new LPPresolver();
		lpPresolver.setNOfSlackVariables((short) s);
		if(checkExpectedSolution){
			lpPresolver.setExpectedSolution(expectedSolution);// this is just for test!
			lpPresolver.setExpectedTolerance(myExpectedTolerance);// this is just for test!
		}
		// lpPresolver.setAvoidFillIn(true);
		// lpPresolver.setZeroTolerance(1.e-13);
		lpPresolver.presolve(c, A, b, lb, ub);
		int n = lpPresolver.getPresolvedN();
		DoubleMatrix1D presolvedC = lpPresolver.getPresolvedC();
		DoubleMatrix2D presolvedA = lpPresolver.getPresolvedA();
		DoubleMatrix1D presolvedB = lpPresolver.getPresolvedB();
		DoubleMatrix1D presolvedLb = lpPresolver.getPresolvedLB();
		DoubleMatrix1D presolvedUb = lpPresolver.getPresolvedUB();
		DoubleMatrix1D presolvedYlb = lpPresolver.getPresolvedYlb();
		DoubleMatrix1D presolvedYub = lpPresolver.getPresolvedYub();
		DoubleMatrix1D presolvedZlb = lpPresolver.getPresolvedZlb();
		DoubleMatrix1D presolvedZub = lpPresolver.getPresolvedZub();
		log.debug("n  : " + n);
		if (log.isDebugEnabled() && n > 0) {
			log.debug("presolvedC  : " + ArrayUtils.toString(presolvedC.toArray()));
			log.debug("presolvedA  : " + ArrayUtils.toString(presolvedA.toArray()));
			log.debug("presolvedB  : " + ArrayUtils.toString(presolvedB.toArray()));
			log.debug("presolvedLb : " + ArrayUtils.toString(presolvedLb.toArray()));
			log.debug("presolvedUb : " + ArrayUtils.toString(presolvedUb.toArray()));
			log.debug("presolvedYlb: " + ArrayUtils.toString(presolvedYlb.toArray()));
			log.debug("presolvedYub: " + ArrayUtils.toString(presolvedYub.toArray()));
			log.debug("presolvedZlb: " + ArrayUtils.toString(presolvedZlb.toArray()));
			log.debug("presolvedZub: " + ArrayUtils.toString(presolvedZub.toArray()));
		}
		
		if (n == 0) {
			// deterministic problem
			double[] sol = lpPresolver.postsolve(new double[] {});
			assertEquals(expectedSolution.length, sol.length);
			for (int i = 0; i < sol.length; i++) {
				// log.debug("i: " + i);
				assertEquals(expectedSolution[i], sol[i], 1.e-9);
			}
		} else {
			
			Utils.writeDoubleArrayToFile(presolvedC.toArray(), "target"	+ File.separator + "presolvedC_" + problemName + ".txt");
			Utils.writeDoubleMatrixToFile(presolvedA.toArray(), "target" + File.separator + "presolvedA_" + problemName + ".csv");
			Utils.writeDoubleArrayToFile(presolvedB.toArray(), "target"	+ File.separator + "presolvedB_" + problemName + ".txt");
			Utils.writeDoubleArrayToFile(presolvedLb.toArray(), "target" + File.separator + "presolvedLB_" + problemName + ".txt");
			Utils.writeDoubleArrayToFile(presolvedUb.toArray(), "target" + File.separator + "presolvedUB_" + problemName + ".txt");
			
			// check objective function
			double delta = expectedTolerance;
			RealVector presolvedES = MatrixUtils.createRealVector(lpPresolver.presolve(expectedSolution));
			double presolvedEV = MatrixUtils.createRealVector(presolvedC.toArray()).dotProduct(presolvedES);// in general it is different from the optimal value
			log.debug("presolved expected value: " + presolvedEV);
			RealVector postsolvedES = MatrixUtils.createRealVector(lpPresolver.postsolve(presolvedES.toArray()));
			double postsolvedEV = MatrixUtils.createRealVector(c).dotProduct(postsolvedES);
			//assertEquals(expectedValue, postsolvedEV, delta);
			assertTrue(Math.abs((expectedValue - postsolvedEV) / expectedValue) < delta);

			// check postsolved constraints
			for (int i = 0; i < lb.length; i++) {
				double di = Double.isNaN(lb[i]) ? -Double.MAX_VALUE : lb[i];
				assertTrue(di <= postsolvedES.getEntry(i) + delta);
			}
			for (int i = 0; i < ub.length; i++) {
				double di = Double.isNaN(ub[i]) ? Double.MAX_VALUE : ub[i];
				assertTrue(di + delta >= postsolvedES.getEntry(i));
			}
			RealVector Axmb = AMatrix.operate(postsolvedES).subtract(bVector);
			assertEquals(0., Axmb.getNorm(), 1.5 * expectedTolerance);

			// check presolved constraints
			assertEquals(presolvedLb.size(), presolvedES.getDimension());
			assertEquals(presolvedUb.size(), presolvedES.getDimension());
			AMatrix = MatrixUtils.createRealMatrix(presolvedA.toArray());//reassigned to avoid memory consumption
			bVector = MatrixUtils.createRealVector(presolvedB.toArray());
			for (int i = 0; i < presolvedLb.size(); i++) {
				double di = Double.isNaN(presolvedLb.getQuick(i)) ? -Double.MAX_VALUE : presolvedLb.getQuick(i);
				assertTrue(di <= presolvedES.getEntry(i) + delta);
			}
			for (int i = 0; i < presolvedUb.size(); i++) {
				double di = Double.isNaN(presolvedUb.getQuick(i)) ? Double.MAX_VALUE : presolvedUb.getQuick(i);
				assertTrue(di + delta >= presolvedES.getEntry(i));
			}
			Axmb = AMatrix.operate(presolvedES).subtract(bVector);
			assertEquals(0., Axmb.getNorm(), 1.5 * expectedTolerance);

		//check for 0-rows
			List<Integer> zeroRows = new ArrayList<Integer>();
			for(int i=0; i<presolvedA.rows(); i++){
				boolean isNotZero = false;
				for(int j=0;!isNotZero && j<presolvedA.columns(); j++){
					isNotZero = Double.compare(0., presolvedA.getQuick(i, j))!=0;
				}
				if(!isNotZero){
					zeroRows.add(zeroRows.size(), i);
				}
			}
			if(!zeroRows.isEmpty()){
				log.debug("All 0 entries in rows " + ArrayUtils.toString(zeroRows));
				fail();
			}
			
			//check for 0-columns
			List<Integer> zeroCols = new ArrayList<Integer>();
			for(int j=0; j<presolvedA.columns(); j++){
				boolean isNotZero = false;
				for(int i=0;!isNotZero && i<presolvedA.rows(); i++){
					isNotZero = Double.compare(0., presolvedA.getQuick(i, j))!=0;
				}
				if(!isNotZero){
					zeroCols.add(zeroCols.size(), j);
				}
			}
			if(!zeroCols.isEmpty()){
				log.debug("All 0 entries in columns " + ArrayUtils.toString(zeroCols));
				fail();
			}
			
			// check rank(A): must be A pXn with rank(A)=p < n
			qr = new QRSparseFactorization(new SparseDoubleMatrix2D(presolvedA.toArray()));
			qr.factorize();
			boolean isFullRank = qr.hasFullRank();
			log.debug("p        : " + AMatrix.getRowDimension());
			log.debug("n        : " + AMatrix.getColumnDimension());
			log.debug("full rank: " + isFullRank);
			assertTrue(AMatrix.getRowDimension() < AMatrix.getColumnDimension());
			assertTrue(isFullRank);
		}
	}
}
