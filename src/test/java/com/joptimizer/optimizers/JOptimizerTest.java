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
package com.joptimizer.optimizers;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.math.Mult;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.FunctionsUtils;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.LogTransformedPosynomial;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.functions.PSDQuadraticMultivariateRealFunction;
import com.joptimizer.functions.StrictlyConvexMultivariateRealFunction;
import com.joptimizer.util.Utils;

/**
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class JOptimizerTest extends TestCase {
	private Algebra ALG = Algebra.DEFAULT;
	private DoubleFactory1D F1 = DoubleFactory1D.dense;
	private DoubleFactory2D F2 = DoubleFactory2D.dense;
	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * The simplest test.
	 */
	public void testSimplest() throws Exception {
		log.debug("testSimplest");
		
		// Objective function
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(new double[] { 1. }, 0);
		
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1}, 0.);
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setInitialPoint(new double[] { 1 });
		or.setToleranceFeas(1.E-8);
		or.setTolerance(1.E-9);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ objectiveFunction.value(sol));

		assertEquals(0., sol[0], 0.000000001);
	}	
	
	/**
	 * Quadratic objective, no constraints.
	 */
	public void testNewtownUnconstrained() throws Exception {
		log.debug("testNewtownUnconstrained");
		RealMatrix PMatrix = new Array2DRowRealMatrix(new double[][] { 
				{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } });
		RealVector qVector = new ArrayRealVector(new double[] { 0.018, 0.025, 0.01 });

		// Objective function.
		double theta = 0.01522;
		RealMatrix P = PMatrix.scalarMultiply(theta);
		RealVector q = qVector.mapMultiply(-1);
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P.getData(), q.toArray(), 0);
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.04, 0.50, 0.46 });
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ objectiveFunction.value(sol));

		// we already know the analytic solution of the problem
		// sol = -invQ * C
		CholeskyDecomposition cFact = new CholeskyDecomposition(P);
		RealVector benchSol = cFact.getSolver().solve(q).mapMultiply(-1);
		log.debug("benchSol   : " + ArrayUtils.toString(benchSol.toArray()));
		log.debug("benchValue : " + objectiveFunction.value(benchSol.toArray()));

		assertEquals(benchSol.getEntry(0), sol[0], or.getTolerance());
		assertEquals(benchSol.getEntry(1), sol[1], or.getTolerance());
		assertEquals(benchSol.getEntry(2), sol[2], or.getTolerance());
	}
	
	/**
	 * Quadratic objective with linear equality constraints and feasible starting point.
	 */
	public void testNewtonLEConstrainedFSP() throws Exception {
		log.debug("testNewtonLEConstrainedFSP");
		DoubleMatrix2D PMatrix = F2.make(new double[][] { 
				{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } });
		DoubleMatrix1D qVector = F1.make(new double[] { 0.018, 0.025, 0.01 });
		
		// Objective function.
		double theta = 0.01522;
		DoubleMatrix2D P = PMatrix.assign(Mult.mult(theta));
		DoubleMatrix1D q = qVector.assign(Mult.mult(-1));
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P.toArray(), q.toArray(), 0);
		
		//equalities
		double[][] A = new double[][]{{1,1,1}};
		double[] b = new double[]{1};
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.04, 0.50, 0.46 });
		or.setA(A);
		or.setB(b);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(0.04632311555988555, sol[0], 0.000000000000001);
		assertEquals(0.5086308460954377,  sol[1], 0.000000000000001);
		assertEquals(0.44504603834467693, sol[2], 0.000000000000001);
	}
	
	/**
	 * Quadratic objective with linear equality constraints and infeasible starting point.
	 */
	public void testNewtonLEConstrainedISP() throws Exception {
		log.debug("testNewtonLEConstrainedISP");
		DoubleMatrix2D PMatrix = F2.make(new double[][] { 
				{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } });
		DoubleMatrix1D qVector = F1.make(new double[] { 0.018, 0.025, 0.01 });

		// Objective function (Risk-Aversion).
		double theta = 0.01522;
		DoubleMatrix2D P = PMatrix.assign(Mult.mult(theta));
		DoubleMatrix1D q = qVector.assign(Mult.mult(-1));
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P.toArray(), q.toArray(), 0);

		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 1, 1, 1 });
		or.setA(new double[][] { { 1, 1, 1 } });
		or.setB(new double[] { 1 });
		
	  	//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(0.04632311555988555, sol[0], 0.000000000000001);
		assertEquals(0.5086308460954377,  sol[1], 0.000000000000001);
		assertEquals(0.44504603834467693, sol[2], 0.000000000000001);
	}
	
	/**
	 * Quadratic objective with linear eq and ineq.
	 */
	public void testPrimalDualMethod1() throws Exception {
		log.debug("testPrimalDualMethod1");
		DoubleMatrix2D PMatrix = F2.make(new double[][] { 
    		{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } });
		DoubleMatrix1D qVector = F1.make(new double[] { 0.018, 0.025, 0.01 });
		
		// Objective function.
		double theta = 0.01522;
		DoubleMatrix2D P = PMatrix.assign(Mult.mult(theta));
		DoubleMatrix1D q = qVector.assign(Mult.mult(-1));
    	PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P.toArray(), q.toArray(), 0);

    	//equalities
    	double[][] A = new double[][]{{1,1,1}};
		double[] b = new double[]{1};

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[3];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0, 0}, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1, 0}, 0);
		inequalities[2] = new LinearMultivariateRealFunction(new double[]{0, 0, -1}, 0);

		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.2, 0.2, 0.6 });
		or.setFi(inequalities);
		or.setA(A);
		or.setB(b);
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(0.04632311555988555, sol[0], 0.000000001);
		assertEquals(0.5086308460954377,  sol[1], 0.000000001);
		assertEquals(0.44504603834467693, sol[2], 0.000000001);
  }
	
	/**
	 * The same as {@link JOptimizerTest#testPrimalDualMethod()}, 
	 * with some changes in constraints.
	 */
	public void testPrimalDualMethod2() throws Exception {
		log.debug("testPrimalDualMethod2");
		
	  // Objective function.
		double[][] P = new double[][] { 
    		{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } };
		
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

    //equalities
    double[][] A = new double[][]{{1,1,1}, {0,0,1}};
		double[] b = new double[]{1, 0};

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0, 0}, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1, 0}, 0);
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.4, 0.6, 0.0 });
		or.setFi(inequalities);
		or.setA(A);
		or.setB(b);
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(0.6723716381417573, sol[0], 0.000000001);
		assertEquals(0.3276283618582427, sol[1], 0.000000001);
		assertEquals(0.0,                sol[2], 0.000000001);
  }
	
	/**
	 * The same as {@link JOptimizerTest#testPrimalDualMethod()}, but with barrier-method.
	 * Quadratic objective with linear eq and ineq.
	 */
	public void testBarrierMethod() throws Exception {
		log.debug("testBarrierMethod");
		DoubleMatrix2D PMatrix = F2.make(new double[][] { 
    		{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } });
		DoubleMatrix1D qVector = F1.make(new double[] { 0.018, 0.025, 0.01 });
		
		// Objective function (Risk-Aversion).
		double theta = 0.01522;
		DoubleMatrix2D P = PMatrix.assign(Mult.mult(theta));
		DoubleMatrix1D q = qVector.assign(Mult.mult(-1));
    	PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P.toArray(), q.toArray(), 0);

    	//equalities
    	double[][] A = new double[][]{{1,1,1}};
    	double[] b = new double[]{1};

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[3];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0, 0}, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1, 0}, 0);
		inequalities[2] = new LinearMultivariateRealFunction(new double[]{0, 0, -1}, 0);

		OptimizationRequest or = new OptimizationRequest();
		or.setInteriorPointMethod(JOptimizer.BARRIER_METHOD);
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.3, 0.3, 0.4 });
		or.setFi(inequalities);
		or.setA(A);
		or.setB(b);
		or.setTolerance(1.E-12);
		or.setToleranceInnerStep(1.E-5); 
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(0.04632311555988555, sol[0], 0.00000001);
		assertEquals(0.5086308460954377,  sol[1], 0.00000001);
		assertEquals(0.44504603834467693, sol[2], 0.00000001);
  }
	
	/**
		 * Very simple linear.
		 */
		public void testSimpleLinear() throws Exception{
			log.debug("testSimpleLinear");
		  // Objective function (plane)
			LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(new double[] { 1., 1. }, 0.);
	
		  //equalities
			//DoubleMatrix2D AMatrix = F2.make(new double[][]{{1,-1}});
			//DoubleMatrix1D BVector = F1.make(new double[]{0});
			
			//inequalities
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[4];
			inequalities[0] = new LinearMultivariateRealFunction(new double[]{ 1., 0.}, -3.);
			inequalities[1] = new LinearMultivariateRealFunction(new double[]{-1., 0.},  0.);
			inequalities[2] = new LinearMultivariateRealFunction(new double[]{ 0., 1.}, -3.);
			inequalities[3] = new LinearMultivariateRealFunction(new double[]{ 0.,-1.},  0.);
			
			//optimization problem
			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			or.setFi(inequalities);
	//		or.setInitialPoint(new double[] {1., 1.});//initial feasible point, not mandatory
			or.setToleranceFeas(1.E-12);
			or.setTolerance(1.E-12);
			
			//optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);
			int returnCode = opt.optimize();
			
			if(returnCode == OptimizationResponse.FAILED){
				fail();
			}
			
			OptimizationResponse response = opt.getOptimizationResponse();
			double[] sol = response.getSolution();
			log.debug("sol: " + ArrayUtils.toString(sol));
			log.debug("value  : " + objectiveFunction.value(sol));
			assertEquals(0.0, sol[0], 0.000000000001);
			assertEquals(0.0, sol[1], 0.000000000001);
		}

	/**
		 * Linear objective, linear constrained.
		 * It simulates the type of optimization occurring in feasibility searching
		 * in a problem with constraints:
		 * -x < 0
		 *  x -1 < 0
		 */
		public void testLinearProgramming() throws Exception {
			log.debug("testLinearProgramming");
			
			// Objective function (linear (x,s)->s)
			double[] c0 = new double[] { 0, 1 };
			LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(c0, 0);
	
			//inequalities
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
			//-x -s < 0
			double[] c1 = new double[] { -1, -1 };
			inequalities[0] = new LinearMultivariateRealFunction(c1, 0);
			// x -s -1 < 0
			double[] c2 = new double[] { 1, -1 };
			inequalities[1] = new LinearMultivariateRealFunction(c2, -1);
			
			//optimization problem
			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			or.setInitialPoint(new double[] { 1.4, 0.5});
			//or.setInitialPoint(new double[] {-0.1,-0.989});
			//or.setInitialPoint(new double[] {1.2, 2.});
			or.setFi(inequalities);
			//or.setInitialLagrangian(new double[]{0.005263, 0.1});
			or.setMu(100d); 
			
			//optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);
			int returnCode = opt.optimize();
			
			if(returnCode == OptimizationResponse.FAILED){
				fail();
			}
			
			OptimizationResponse response = opt.getOptimizationResponse();
			double[] sol = response.getSolution();
			log.debug("sol   : " + ArrayUtils.toString(sol));
			log.debug("value : " + objectiveFunction.value(sol));
			assertEquals( 0.5, sol[0], or.getTolerance());
			assertEquals(-0.5, sol[1], or.getTolerance());
	  }

	/**
	 * Linear programming in 2D.
	 */
	public void testLinearProgramming2D() throws Exception {
		log.debug("testLinearProgramming2D");
		
	  // START SNIPPET: LinearProgramming-1
		
		// Objective function (plane)
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(new double[] { -1., -1. }, 4);

		//inequalities (polyhedral feasible set G.X<H )
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[4];
		double[][] G = new double[][] {{4./3., -1}, {-1./2., 1.}, {-2., -1.}, {1./3., 1.}};
		double[] h = new double[] {2., 1./2., 2., 1./2.};
		inequalities[0] = new LinearMultivariateRealFunction(G[0], -h[0]);
		inequalities[1] = new LinearMultivariateRealFunction(G[1], -h[1]);
		inequalities[2] = new LinearMultivariateRealFunction(G[2], -h[2]);
		inequalities[3] = new LinearMultivariateRealFunction(G[3], -h[3]);
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		//or.setInitialPoint(new double[] {0.0, 0.0});//initial feasible point, not mandatory
		or.setToleranceFeas(1.E-9);
		or.setTolerance(1.E-9);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
	  // END SNIPPET: LinearProgramming-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(1.5, sol[0], 0.000000001);
		assertEquals(0.0, sol[1], 0.000000001);
  }
	
	public void testLinearProgramming7D() throws Exception {
			log.debug("testLinearProgramming7D");
			
			double[] CVector = new double[]{0.0, 0.0, 0.0, 1.0, 0.833, 0.833, 0.833};
		    double[][] AMatrix = new double[][]{{1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0}};
		    double[] BVector = new double[]{1.0};
		    double[][] GMatrix = new double[][]{
		    		  {0.014,  0.009,  0.021, 1.0, 1.0, 0.0, 0.0},
				      {0.001,  0.002, -0.002, 1.0, 0.0, 1.0, 0.0},
				      {0.003, -0.005,  0.002, 1.0, 0.0, 0.0, 1.0},
				      {0.006,  0.002,  0.007, 0.0, 0.0, 0.0, 0.0},
				      {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
				      {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0},
				      {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0},
				      {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0},
				      {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0},
				      {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0},
				      {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0}};
		    double[] HVector = new double[]{0.0, 0.0, 0.0, 0.0010, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			
			// Objective function (plane)
			LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(CVector, 0.0);
	
			//inequalities (polyhedral feasible set -G.X-H<0 )
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[GMatrix.length];
			for(int i=0; i<GMatrix.length; i++){
				inequalities[i] = new LinearMultivariateRealFunction(new ArrayRealVector(GMatrix[i]).mapMultiply(-1.).toArray(), -HVector[i]);
			}
			
			//optimization problem
			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			or.setFi(inequalities);
			or.setA(AMatrix);
			or.setB(BVector);
			or.setInitialPoint(new double[] {0.25, 0.25, 0.5, 0.01, 0.01, 0.01, 0.01});
			
			//optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);
			int returnCode = opt.optimize();
			
			if(returnCode==OptimizationResponse.FAILED){
				fail();
			}
			
			OptimizationResponse response = opt.getOptimizationResponse();
			double[] sol = response.getSolution();
			log.debug("sol: " + ArrayUtils.toString(sol));
			log.debug("value  : " + objectiveFunction.value(sol));
			assertTrue(sol[0] > 0);
			assertTrue(sol[1] > 0);
			assertTrue(sol[2] > 0);
			assertTrue(sol[4] > 0);
			assertTrue(sol[5] > 0);
			assertTrue(sol[6] > 0);
			assertEquals(sol[0]+sol[1]+sol[2], 1., 0.00000001);
			assertTrue(0.006*sol[0]+0.002*sol[1]+0.007*sol[2] > 0.0010);
	  }
	
	/**
	 * Infeasible LP.
	 * 
	 * @see LPPrimalDualMethodTest#testInfeasible1Red()
	 */
	public void testInfeasible1() throws Exception {
		log.debug("testInfeasible1");

		double[] c = new double[] { 1.0, 1.0, 1.0 };
		double[][] A = new double[][] { { 1.0, 1.0, 1.0 } };
		double[] b = new double[] { 1.0 };
		double[][] G = new double[][] { 
				{ 1.0, 1.0, 0.0 },
				{ 0.0, 1.0, 1.0 } };
		double[] h = new double[] { 0.3, 0.4 };

		OptimizationRequest or = new OptimizationRequest();
		or.setF0(new LinearMultivariateRealFunction(c, 0));
		or.setA(A);
		or.setB(b);
		
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		inequalities[0] = new LinearMultivariateRealFunction(G[0], -h[0]);
		inequalities[1] = new LinearMultivariateRealFunction(G[1], -h[1]);
		or.setFi(inequalities);
		
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);

		try{
			opt.optimize();
			//unexpected behavior: the problem is infeasible
			fail("the problem is expected to be infeasible");
			
		}catch(Exception e){
			//expected behavior: the problem is infeasible 
			assertTrue(true);
		}
	}

	/**
	 * LP problem with dim=26.
	 * A more appropriate solution is given in {@link LPPrimalDualMethodTest#testCGhAb1()}.
	 * Submitted 01/09/2013 by Chris Myers.
	 */
	public void testCGhAb1() throws Exception {
		log.debug("testCGhAb1");
		
		String problemId = "1";
		
		//the original problem: ok until precision 1.E-7
		double[] c = Utils.loadDoubleArrayFromFile("lp"+File.separator+"c"+problemId+".txt");
		double[][] G = Utils.loadDoubleMatrixFromFile("lp"+File.separator+"G"+problemId+".csv", ",".charAt(0));
		double[] h = Utils.loadDoubleArrayFromFile("lp"+File.separator+"h"+problemId+".txt");;
		double[][] A = Utils.loadDoubleMatrixFromFile("lp"+File.separator+"A"+problemId+".csv", ",".charAt(0));
		double[] b = Utils.loadDoubleArrayFromFile("lp"+File.separator+"b"+problemId+".txt");
		double[] expectedSol = Utils.loadDoubleArrayFromFile("lp"+File.separator+"sol"+problemId+".txt");
		double expectedvalue = Utils.loadDoubleArrayFromFile("lp"+File.separator+"value"+problemId+".txt")[0];
		
		// Objective function
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(c, 0);
		
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];
		for(int i=0; i<G.length;i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setA(A);
		or.setB(b);
		or.setCheckKKTSolutionAccuracy(true);
		//or.setInitialPoint(new double[] { 1 });
		//or.setToleranceFeas(1.E-8);
		//or.setTolerance(1.E-9);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		double value = objectiveFunction.value(sol);
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ objectiveFunction.value(sol));
	
		//check constraints
		RealMatrix AMatrix = MatrixUtils.createRealMatrix(A);
		RealVector bVector = new ArrayRealVector(b);
		RealMatrix GMatrix = MatrixUtils.createRealMatrix(G);
		RealVector hVector = new ArrayRealVector(h);
		
		//joptimizer solution
		RealVector joptSol = new ArrayRealVector(sol);
		//A.x = b
		assertEquals(0., AMatrix.operate(joptSol).subtract(bVector).getNorm(), 1.E-7);
		//G.x < h
		RealVector GjoptSol = GMatrix.operate(joptSol).subtract(hVector);
		for(int i=0; i<G.length; i++){
			assertTrue(GjoptSol.getEntry(i) < 0);
		}
		
		assertEquals(expectedvalue, value, or.getTolerance());
	}
	
	/**
	 * Linear problem in the form 
	 * min(c.x) s.t. 
	 * G.x < h 
	 * lb < x < ub
	 * 
	 * Submitted by Philip Walton 12/11/2014
	 */
	public void testCGhLbUb9() throws Exception {
		log.debug("testCGhLbUb9");
		
		String problemId = "9";
		
		//the original problem: ok until precision 1.E-7
		double[] c = Utils.loadDoubleArrayFromFile("lp"+File.separator+"c"+problemId+".txt");
		double[][] G = Utils.loadDoubleMatrixFromFile("lp"+File.separator+"G"+problemId+".csv", ",".charAt(0));
		double[] h = Utils.loadDoubleArrayFromFile("lp"+File.separator+"h"+problemId+".txt");;
		double[] lb = Utils.loadDoubleArrayFromFile("lp"+File.separator+"lb"+problemId+".txt");;
		double[] ub = Utils.loadDoubleArrayFromFile("lp"+File.separator+"ub"+problemId+".txt");;
		double[] expectedSol = Utils.loadDoubleArrayFromFile("lp"+File.separator+"sol"+problemId+".txt");
		double expectedvalue = Utils.loadDoubleArrayFromFile("lp"+File.separator+"value"+problemId+".txt")[0];

		LPOptimizationRequest or = new LPOptimizationRequest();
		or.setC(c);
		or.setG(G);
		or.setH(h);
		or.setLb(lb);
		or.setUb(ub);
		//or.setToleranceFeas(1.E-9);
		//or.setTolerance(1.E-9);
		//or.setCheckKKTSolutionAccuracy(true);
		or.setDumpProblem(true);
		
		//optimization
		double minLb = -150000;
		double maxUb = +150000;
		LPPrimalDualMethod opt = new LPPrimalDualMethod(minLb, maxUb);
		
		opt.setLPOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		LPOptimizationResponse response = opt.getLPOptimizationResponse();
		double[] sol = response.getSolution();
		RealVector cVector = new ArrayRealVector(c);
		RealVector solVector = new ArrayRealVector(sol);
		double value = cVector.dotProduct(solVector);
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ value);
		
		assertEquals(expectedvalue, value, or.getTolerance());
	}

	/**
	 * min(100 * y) s.t.
	 *   x -y = 1
	 *	-x < 0 
	 * Submitted 19/10/2012 by Noreen Jamil.
	 */
	public void testLP() throws Exception {
		log.debug("testLP");
		
		// Objective function
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(new double[] { 0., 100. }, 0);
		
		double[][] A = new double[1][2];
		A[0] = new double[]{1.0, -1.0};
		double[] b = new double[]{1.0};
	
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0}, 0.);
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setA(A);
		or.setB(b);
		or.setFi(inequalities);
		or.setRescalingDisabled(true);
		//or.setInitialPoint(new double[] { 3, 2 });
		//or.setNotFeasibleInitialPoint(new double[] { 3, 2 });
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ objectiveFunction.value(sol));
	
		assertEquals( 0., sol[0], 0.000000001);
		assertEquals(-1., sol[1], 0.000000001);
	}
	
	/**
	 * Problem in the form
	 * min(c.x) s.t.
	 * A.x = b
	 * 0 < x[0] < 50000000000
	 * 0 < x[1] < 1600000000
	 * 0 < x[2] < 4000000000
	 * 0 < x[3] < 350000000
	 * 0 < x[4] < 5000000000
	 * 
	 * Submitted by Juan Jose Sanchez Arroyo 07/11/2014
	 */
	public void testLP2() throws Exception {
		log.debug("testLP2");
		
		double[] c = new double[] { 0.0025,0.0027,0.0032,0.0024,0.0035 };
		double[][] A = new double[][] {{1,1,1,1,1}};
		double[] b = new double[] {58000000000d};
		
		LPOptimizationRequest or = new LPOptimizationRequest();
		or.setC(c);
		or.setA(A);
		or.setB(b);
		or.setLb(new double[]{0, 0, 0, 0, 0});
		or.setUb(new double[]{50000000000d, 1600000000d, 4000000000d, 350000000d, 5000000000d});
		or.setDumpProblem(true);
		
		//optimization
		double minLb = LPPrimalDualMethod.DEFAULT_MIN_LOWER_BOUND;
		double maxUb = 58000000000d;
		LPPrimalDualMethod opt = new LPPrimalDualMethod(minLb, maxUb);
		
		opt.setLPOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		LPOptimizationResponse response = opt.getLPOptimizationResponse();
		double[] sol = response.getSolution();
		RealVector cVector = new ArrayRealVector(c);
		RealVector solVector = new ArrayRealVector(sol);
		double value = cVector.dotProduct(solVector);
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ value);
		
		assertEquals( 5, sol.length);
	}
	
	/**
	 * Minimize x subject to 
	 * -x <= -2; (x >= 2)
	 * x < 10
	 * x > -10
	 * 
	 * Submitted by Tingfan Wu 30/07/2014
	 */
	public void testLP3() throws Exception {
		log.debug("testLP3");
		OptimizationRequest or = new OptimizationRequest();

		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(
				new double[] { 1.0 }, 0);

		or.setF0(objectiveFunction);

		//G.x < h
		double[][] G = new double[][]{
				{-1},
				{1},//upper bound on x
				{-1}};//lower bound on x
		double[] h = new double[]{
				-2,
				10,//upper bound on x
				10};//lower bound on x
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];
		for(int i=0; i<G.length; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
		or.setFi(inequalities);
		or.setToleranceFeas(1.E-6);
		or.setTolerance(2.E-6);
		or.setMaxIteration(50);

		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}

		double[] sol = opt.getOptimizationResponse().getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		assertEquals(2.0, sol[0], or.getTolerance());
	}
	
	/**
	 Problem in the form
	 * min(c.x) s.t.
	 * A.x = b
	 * G.x < h
	 * lb < x
	 * x[ < ub
	 * 
	 * c = x0+x1+x2+x3
	 * 
	 * Submitted by Lars Ohmann 04/02/2016
	 */
	public void testLP4() throws Exception {
		log.debug("testLP4");

		// Objective function
		double[] c = new double[] { 1., 1., 1., 1., 0., 0., 0., 0., 0., 0., 0., }; // u1+u2+u3+u4+0x1+0x2+0x3+0x4+0c+0d+0e

		// Inequalities constraints
		double[][] G = new double[][] {
				{ -1., 0., 0., 0., 1., 0., 0., 0., 0., 0., 0. },
				{ -1., 0., 0., 0., -1., 0., 0., 0., 0., 0., 0. },
				{ 0., -1., 0., 0., 1., 0., 0., 0., 0., 0., 0. },
				{ 0., -1., 0., 0., -1., 0., 0., 0., 0., 0., 0. },
				{ 0., 0., -1., 0., 1., 0., 0., 0., 0., 0., 0. },
				{ 0., 0., -1., 0., -1., 0., 0., 0., 0., 0., 0. },
				{ 0., 0., 0., -1., 1., 0., 0., 0., 0., 0., 0. },
				{ 0., 0., 0., -1., -1., 0., 0., 0., 0., 0., 0. },

		};
		double[] h = new double[] { 0.585, -0.585, 0.327, -0.327, 0.043, -0.043,
				0.0124, -0.0124 };

		double[][] A = new double[][] {
				{ 0., 0., 0., 0., -1., 0., 0., 0., 1., 0.51, 0. },
				{ 0., 0., 0., 0., 0., -1., 0., 0., 0., 0.40, 1. },
				{ 0., 0., 0., 0., 0., 0., -1., 0., 0., 0.07, 0. },
				{ 0., 0., 0., 0., 0., 0., 0., -1., 0., 0.02, 0. },
				{ 0., 0., 0., 0., 0., 0., 0., 0., 1., 1., 1. }, };

		double[] b = new double[] { 0., 0., 0., 0., 1. };

		// Bounds on variables
		double[] lb = new double[] {0,0,0,0,0,0,0,0,0,0,0};
		double[] ub = new double[] {10,10,10,10,10,10,10,10,10,10,10};

		// optimization problem
		LPOptimizationRequest or = new LPOptimizationRequest();

		or.setC(c);
		or.setG(G);
		or.setH(h);
		or.setA(A);
		or.setB(b);
		or.setLb(lb);
		or.setUb(ub);
		or.setDumpProblem(true);
		
		LPPrimalDualMethod opt = new LPPrimalDualMethod();

		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}

		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		RealVector cVector = new ArrayRealVector(c);
		RealVector solVector = new ArrayRealVector(sol);
		double value = cVector.dotProduct(solVector);
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : "	+ value);
		
		assertEquals(11, sol.length);
	}

	/**
	 * Quadratic programming in 2D.
	 */
	public void testQuadraticProgramming2D() throws Exception {
		log.debug("testQuadraticProgramming2D");
		
		// START SNIPPET: QuadraticProgramming-1
		
		// Objective function
		double[][] P = new double[][] {{ 1., 0.4 }, { 0.4, 1. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//equalities
		double[][] A = new double[][]{{1,1}};
		double[] b = new double[]{1};

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0}, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1}, 0);
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.1, 0.9});
		//or.setFi(inequalities); //if you want x>0 and y>0
		or.setA(A);
		or.setB(b);
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
	  // END SNIPPET: QuadraticProgramming-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(0.5, sol[0], 0.0000000000001);
		assertEquals(0.5, sol[1], 0.0000000000001);
  }
	
	/**
	 * Quadratic programming in 2D.
	 * Same as {@link JOptimizerTest#testQuadraticProgramming2D()} but with an additional linear inequality constraint.
	 * Submitted 28/02/2014 by Ashot Ordukhanyan.
	 */
	public void testQuadraticProgramming2Dmc() throws Exception {
		log.debug("testQuadraticProgramming2Dmc");
		
		// Objective function
		double[][] P = new double[][] {{ 1., 0.4 }, { 0.4, 1. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);
	
		//equalities
		double[][] A = new double[][]{{1,1}};
		double[] b = new double[]{1};
	
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[3];
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0}, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1}, 0);
		inequalities[2] = new LinearMultivariateRealFunction(new double[]{1,1}, 20d);
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.1, 0.9});
		//or.setFi(inequalities); //if you want x>0 and y>0
		or.setA(A);
		or.setB(b);
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
	  if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(0.5, sol[0], 0.0000000000001);
		assertEquals(0.5, sol[1], 0.0000000000001);
	}

	/**
	 * Test QP.
	 * Submitted 12/07/2012 by Katharina Schwaiger.
	 */
	public void testQPScala() throws Exception {
		log.debug("testQPScala");
		
		double[][] P = new double[2][2];
	    P[0] = new double[]{1.0, 0.4};
	    P[1] = new double[]{0.4, 1.0};
	
	    PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);
	
	    double[][] A = new double[1][2];
	    A[0] = new double[]{1.0, 1.0};
	    double[] b = new double[]{1.0};
	
	    ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[4];
	    inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0}, -0.2);// -x1 -0.2 < 0
	    inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1}, -0.2);// -x2 -0.2 < 0
	    
	    inequalities[2] = new LinearMultivariateRealFunction(new double[]{-1, -1}, 0.9);// -x1 -x2 +0.9 < 0
	    inequalities[3] = new LinearMultivariateRealFunction(new double[]{1, 1},   -1.1);//   x1 +x2 -1.1 < 0
	
	    OptimizationRequest OR = new OptimizationRequest();
	    OR.setF0(objectiveFunction);
	    OR.setA(A);
	    OR.setB(b);
	    OR.setFi(inequalities);
	    OR.setToleranceFeas(1.E-12);
	    OR.setTolerance(1.E-12);
	
	    JOptimizer opt = new JOptimizer();
	    opt.setOptimizationRequest(OR);
	    int returnCode = opt.optimize();
	    if(returnCode==OptimizationResponse.FAILED){
	 			fail();
	 		}
	
	    double[] res = opt.getOptimizationResponse().getSolution();
	
	    int status = opt.getOptimizationResponse().getReturnCode();
	    log.debug("status : " + status);
	    log.debug("sol   : " + ArrayUtils.toString(res));
	}

	/**
		 * Test QP.
		 * Submitted 12/07/2012 by Katharina Schwaiger.
		 */
		public void testQPScala2() throws Exception {
			log.debug("testQPScala2");
			
			double[][] P = new double[4][4];
		    P[0] = new double[]{0.08, -0.05, -0.05, -0.05};
		    P[1] = new double[]{-0.05, 0.16, -0.02, -0.02};
		    P[2] = new double[]{-0.05, -0.02, 0.35, 0.06};
		    P[3] = new double[]{-0.05, -0.02, 0.06, 0.35};
		    
		    PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);
	
	//	    double[][] A = new double[1][2];
	//	    A[0] = new double[]{1.0, 1.0};
	//	    double[] b = new double[]{1.0};
	
		    ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[6];
		    inequalities[0] = new LinearMultivariateRealFunction(new double[]{1, 1, 1, 1}, -10000);// x1+x2+x3+x4+1000 < 0
		    inequalities[1] = new LinearMultivariateRealFunction(new double[]{-0.05, 0.2, -0.15, -0.3}, -1000);// -0.05x1+0.2x2-0.15x3-0.3x4-1000 < 0
		    inequalities[2] = new LinearMultivariateRealFunction(new double[]{-1, 0, 0, 0}, 0);// -x1 < 0
		    inequalities[3] = new LinearMultivariateRealFunction(new double[]{0, -1, 0, 0}, 0);// -x2 < 0
		    inequalities[4] = new LinearMultivariateRealFunction(new double[]{0, 0, -1, 0}, 0);// -x3 < 0
		    inequalities[5] = new LinearMultivariateRealFunction(new double[]{0, 0, 0, -1}, 0);// -x4 < 0
		    
		    OptimizationRequest OR = new OptimizationRequest();
		    OR.setF0(objectiveFunction);
		    OR.setFi(inequalities);
		    OR.setToleranceFeas(1.E-12);
		    OR.setTolerance(1.E-12);
	
		    JOptimizer opt = new JOptimizer();
		    opt.setOptimizationRequest(OR);
		    int returnCode = opt.optimize();
		    if(returnCode==OptimizationResponse.FAILED){
		 			fail();
		 		}
	
		    double[] res = opt.getOptimizationResponse().getSolution();
	
		    int status = opt.getOptimizationResponse().getReturnCode();
		    log.debug("status : " + status);
		    log.debug("sol   : " + ArrayUtils.toString(res));
		}

		/**
		 * Test QP in 3-dim
		 * Min( 1/2 * xT.x) s.t.
	   * 	x1 <= -10
	   * This problem can't be solved without an initial point, 
	   * because the relative PhaseI problem is undetermined.
		 * Submitted 01/06/2012 by Klaas De Craemer
		 */
		public void testQP1() throws Exception {
			log.debug("testQP1");
			
			// Objective function
	        double[][] pMatrix = new double[][] {
	        		{ 1, 0, 0 },
	        		{ 0, 1, 0 },
	        		{ 0, 0, 1 }};
	        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(pMatrix, null, 0);
	
	        //inequalities
	        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
	        inequalities[0] = new LinearMultivariateRealFunction(new double[]{1, 0, 0}, 10);// x1 <= -10
	        
	        //optimization problem
	        OptimizationRequest or = new OptimizationRequest();
	        or.setF0(objectiveFunction);
	        or.setInitialPoint(new double[]{-11, 1, 1});
	        or.setFi(inequalities);
	//        or.setToleranceFeas(1.E-12);
	//        or.setTolerance(1.E-12);
	        
	        //optimization
	        JOptimizer opt = new JOptimizer();
	        opt.setOptimizationRequest(or);
	        int returnCode = opt.optimize();
	        
	        if(returnCode==OptimizationResponse.FAILED){
				fail();
			}
	        
	        OptimizationResponse response = opt.getOptimizationResponse();
			double[] sol = response.getSolution();
			double value = objectiveFunction.value(sol);
			log.debug("sol   : " + ArrayUtils.toString(sol));
			log.debug("value : " + value);
			assertEquals(-10., sol[0], 1.E-6);
			assertEquals(  0., sol[1], 1.E-6);
			assertEquals(  0., sol[2], 1.E-6);
			assertEquals( 50.,  value, 1.E-6);
		}

		/**
		 * Problem in the form 
		 * min(0.5 * x.P.x) s.t. 
		 * G.x < h
		 * A.x = b
		 * Submitted by Leon Roisenberg 26/09/2014
		 */
		public void testQP2() throws Exception {
			log.debug("testQP2");

	    // Objective function
	    double[][] P = new double[][] {
	    		{ 1., 0.4 }, 
	    		{ 0.4, 1. }};
	    PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

	    //equalities
		double[][] A = new double[][] { { 1, 1 } };
		double[] b = new double[] { 1 };

	    //inequalities
	    ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
	    inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1,  0}, 0);
	    inequalities[1] = new LinearMultivariateRealFunction(new double[]{ 0, -1}, 0);

	    //optimization problem
	    OptimizationRequest or = new OptimizationRequest();
	    or.setF0(objectiveFunction);
	    or.setInitialPoint(new double[] { 0.1, 0.9});
	    or.setFi(inequalities); //if you want x>0 and y>0
	    or.setA(A);
	    or.setB(b);
	    or.setToleranceFeas(1.E-12);
	    or.setTolerance(1.E-12);

	    //optimization
	    JOptimizer opt = new JOptimizer();
	    opt.setOptimizationRequest(or);
	    
	    int returnCode = opt.optimize();
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}

	    double[] sol = opt.getOptimizationResponse().getSolution();
	    log.debug("sol: " + ArrayUtils.toString(sol));

	 }
		
		/**
		 * Problem in the form 
		 * min(0.5 * x.P.x + q.x) s.t. 
		 * G.x < h
		 * A.x = b
		 * Submitted by Leon Roisenberg 26/09/2014
		 */
		public void testQP3() throws Exception {
			log.debug("testQP3");

			// Objective function
			double[][] P = new double[][]{ 
					{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
					{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
					{ 0.0, 0.0, 1.0, 0.0, 0.0, 0.0 },
					{ 0.0, 0.0, 0.0, 1.0, 0.0, 0.0 },
					{ 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 },
					{ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }
			};

			double[] qVector = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
			PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, qVector, 0.0);

			// equalities
			double[][] A = new double[][]	{ 
					{ 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 },
					{ 0.2, 0.5, 1.0, 0.0, 0.0, 0.0 },
					{ 0.3, 0.6, 0.0, 1.0, 0.0, 0.0 },
					{ 0.4, 0.7, 0.0, 0.0, 1.0, 0.0 },
					{ 0.2, 0.4, 0.0, 0.0, 0.0, 1.0 }};

			double[] b = new double[] { 1.0, 0.2, 0.4, 0.8, 0.6 };

			// inequalities
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[4];
			inequalities[0] = new LinearMultivariateRealFunction(new double[] {  1.0,	0.0, 0.0, 0.0, 0.0, 0.0 }, -1.0);
			inequalities[1] = new LinearMultivariateRealFunction(new double[] {  0.0,	1.0, 0.0, 0.0, 0.0, 0.0 }, -1.0);
			inequalities[2] = new LinearMultivariateRealFunction(new double[] { -1.0,	0.0, 0.0, 0.0, 0.0, 0.0 },  0.0);
			inequalities[3] = new LinearMultivariateRealFunction(new double[] {  0.0,  -1.0, 0.0, 0.0, 0.0, 0.0 },  0.0);
			double[] etfInitial = new double[] { 0.5, 0.5, -0.15, -0.05, 0.25, 0.3 };
			OptimizationRequest or = new OptimizationRequest();

			or.setF0(objectiveFunction);
			or.setInitialPoint(etfInitial);
			or.setFi(inequalities);
			or.setA(A);
			or.setB(b);
			or.setToleranceFeas(1.E-9);
			or.setTolerance(1.E-9);

			// optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);

			int returnCode = opt.optimize();
			if(returnCode == OptimizationResponse.FAILED){
				fail();
			}

			double[] sol = opt.getOptimizationResponse().getSolution();
			log.debug("sol: " + ArrayUtils.toString(sol));
		}
		
		/**
		 * Quadratic programming in the form of:
		 * min(0.5 * x.P.x) s.t.
		 * G.x < h
		 * 
		 * Submitted by Isuru Dasanayake 29/07/2014
		 */
		public void testQP4() throws Exception {
			log.debug("testQP4");
			
			// Objective function
			double[][] Q = new double[][] { 
					{ +1.00000E+00, +0.00000E+00 },
					{ +0.00000E+00, +1.00000E+00 } };
			PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(
					Q, null, 0);

			// inequalities
			double[][] G = new double[][] { 
					{ -1,  -1},
					{ -1,   1},
					{  1,   0},//upper bound on x
					{ -1,   0},//lower bound on x
					{  0,   1},//upper bound on y
					{  0,  -1}};//lower bound on y
			double[] h = new double[] { -2, 0, 10, 10, 10, 10 };
			ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];

			for (int i = 0; i < G.length; i++) {
				inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
			}

			// optimization problem
			OptimizationRequest or = new OptimizationRequest();
			or.setF0(objectiveFunction);
			or.setFi(inequalities); // if you want x>0 and y>0
			or.setToleranceFeas(1.E-7);
			or.setTolerance(1.E-6);

			// optimization
			JOptimizer opt = new JOptimizer();
			opt.setOptimizationRequest(or);
			
			int returnCode = opt.optimize();
			if(returnCode == OptimizationResponse.FAILED){
				fail();
			}
			
			double[] sol = opt.getOptimizationResponse().getSolution();
			double value = objectiveFunction.value(sol);
			log.debug("sol: " + ArrayUtils.toString(sol));
			log.debug("value  : " + value);
			//assertEquals(1., sol[0], or.getTolerance());
			//assertEquals(1., sol[1], or.getTolerance());
			assertEquals(1., value, or.getTolerance());
		}
		
		/**
		 * Quadratic programming in the form of:
		 * min((x0-x1)^2) s.t.
		 * x0>0, x1>0
		 * 
		 * It is convex but not strictly convex (something like a wing with 0 on x0 = x1) 
		 * and without inequalities the problem will fail because the solver will be
		 * NewtonUnconstrained that requires a strictly convex objective function.
		 * 
		 * Submitted by Luke Lindsay 02/12/2015
		 */
	public void testQP5() throws Exception {
		log.debug("testQP5");

		// Objective function f(x0,x1)=(x0-x1)^2
		ConvexMultivariateRealFunction objectiveFunction = new ConvexMultivariateRealFunction() {

			public double value(double[] X) {
				double v = X[0] - X[1];
				return v * v;
			}

			public double[] gradient(double[] X) {
				double[] gradient = { 2 * X[0] - 2 * X[1], -2 * X[0] + 2 * X[1] };
				return gradient;
			}

			public double[][] hessian(double[] X) {
				double[][] hessian = { { 2d, -2d }, { -2d, 2d } };
				return hessian;
			}

			public int getDim() {
				return 2;
			}
		};

		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		inequalities[0] = new LinearMultivariateRealFunction(
				new double[] { -1, 0 }, 0);
		inequalities[1] = new LinearMultivariateRealFunction(
				new double[] { 0, -1 }, 0);

		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setInitialPoint(new double[] { 0.4, 0.2 });

		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();

		if (returnCode == OptimizationResponse.FAILED) {
			fail();
		}

		double[] sol = opt.getOptimizationResponse().getSolution();
		double value = objectiveFunction.value(sol);
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + value);
		assertEquals(sol[0], sol[1], or.getTolerance());// minimum on x0 = x1
		assertEquals(0., value, or.getTolerance());
	}
		
		/**
	 * Problem in the form
	 * min(0.5 * x.P.x) s.t.
	 * G.x < h
	 * A.x = b
	 */
	public void testPGhAb1() throws Exception {
		log.debug("testPGhAb1");
		
		String problemId = "1";
		
		double[][] P = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"P"+problemId+".csv", ",".charAt(0));
		//double[] c = Utils.loadDoubleArrayFromFile("qp"+File.separator+"c"+problemId+".txt");
		double[][] G = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"G"+problemId+".csv", ",".charAt(0));
		double[] h = Utils.loadDoubleArrayFromFile("qp"+File.separator+"h"+problemId+".txt");;
		double[][] A = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"A"+problemId+".csv", ",".charAt(0));
		double[] b = Utils.loadDoubleArrayFromFile("qp"+File.separator+"b"+problemId+".txt");

		// Objective function
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//inequalities G.x < h
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];
		for(int i=0; i<G.length; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
			
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		double[] nfip = new double[]{1,0,0,0,0,0};
		Arrays.fill(nfip, 1./P.length);
		//or.setNotFeasibleInitialPoint(new double[]{1,0,0,0,0,0});
		or.setA(A);
		or.setB(b);
			
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
			
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
			
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		
		RealVector x = MatrixUtils.createRealVector(sol);
		RealMatrix AMatrix = MatrixUtils.createRealMatrix(A);
		RealVector bVector = MatrixUtils.createRealVector(b);
		double rPriNorm = AMatrix.operate(x).subtract(bVector).getNorm();
		assertTrue(rPriNorm < or.getToleranceFeas());
		for(int i=0; i<G.length; i++){
			assertTrue(MatrixUtils.createRealVector(G[i]).dotProduct(x) < h[i]);
		}
		
		//RealVector cVector = MatrixUtils.createRealVector(c);
		//log.debug("c.x : " + cVector.dotProduct(x));
	}
	
	/**
	 * Problem in the form
	 * min(0.5 * x.P.x + q.x) s.t.
	 * G.x < h
	 * lb < x < ub
	 * NB: P is PSD
	 * Submitted by Ayan Das 05/09/2014
	 */
	public void testPqGhLbUb4() throws Exception {
		log.debug("testPqGhLbUb4");
		
		String problemId = "4";
		
		double[][] P = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"P"+problemId+".csv");
		double[] q = Utils.loadDoubleArrayFromFile("qp"+File.separator+"q"+problemId+".txt");
		double[][] G = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"G"+problemId+".csv");
		double[] h = Utils.loadDoubleArrayFromFile("qp"+File.separator+"h"+problemId+".txt");
		double[] lb = Utils.loadDoubleArrayFromFile("qp"+File.separator+"lb"+problemId+".txt");
		double[] ub = Utils.loadDoubleArrayFromFile("qp"+File.separator+"ub"+problemId+".txt");
		double expectedValue = Utils.loadDoubleArrayFromFile("qp"+File.separator+"value"+problemId+".txt")[0];

		// Objective function
		PSDQuadraticMultivariateRealFunction objectiveFunction = new PSDQuadraticMultivariateRealFunction(P, q, 0, true);

		//inequalities G.x < h
		//and upper bounds
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length + 2*P.length];
		for(int i=0; i<G.length; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
		//upper bounds
		for(int i=0; i<P.length; i++){
			double[] row = new double[P.length];
			row[i] = 1;
			inequalities[G.length + i] = new LinearMultivariateRealFunction(row, lb[i]);
		}
		//lower bounds
		for(int i=0; i<P.length; i++){
			double[] row = new double[P.length];
			row[i] = -1;
			inequalities[G.length + P.length + i] = new LinearMultivariateRealFunction(row, -ub[i]);
		}
			
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
			
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
			
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
			
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		double value = objectiveFunction.value(sol);
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("jotimizer   value  : " + value);
		log.debug("mathematica value  : " + expectedValue);
		
		assertTrue(expectedValue + or.getTolerance() > value);
	}
	
	/**
	 * 25/10/2014
	 * Problem in the form
	 * min(0.5 * x.P.x) s.t.
	 * G.x < h
	 * A.x = b
	 * 
	 * JOptimizer cannot find a feasible starting point for this problem if we flip the first and the last inequalities.
	 * This shows that variables and constraints ordering could be worth in numerical complex problems.
	 */
	public void testPGhAb5() throws Exception {
		log.debug("testPGhAb5");
		
		String problemId = "5";
		
		double[][] P = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"P"+problemId+".csv", ",".charAt(0));
		double[] ip = Utils.loadDoubleArrayFromFile("qp"+File.separator+"ip"+problemId+".txt");
		double[][] G = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"G"+problemId+".csv", ",".charAt(0));
		double[] h = Utils.loadDoubleArrayFromFile("qp"+File.separator+"h"+problemId+".txt");;
		double[][] A = Utils.loadDoubleMatrixFromFile("qp"+File.separator+"A"+problemId+".csv", ",".charAt(0));
		double[] b = Utils.loadDoubleArrayFromFile("qp"+File.separator+"b"+problemId+".txt");

		// Objective function
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//inequalities G.x < h
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];
		for(int i=0; i<G.length; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
			
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		//or.setInitialPoint(ip);
		or.setA(A);
		or.setB(b);
			
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
			
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
			
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		
		RealVector x = MatrixUtils.createRealVector(sol);
		RealMatrix AMatrix = MatrixUtils.createRealMatrix(A);
		RealVector bVector = MatrixUtils.createRealVector(b);
		double rPriNorm = AMatrix.operate(x).subtract(bVector).getNorm();
		assertTrue(rPriNorm < or.getToleranceFeas());
		for(int i=0; i<G.length; i++){
			assertTrue(MatrixUtils.createRealVector(G[i]).dotProduct(x) < h[i]);
		}
	}
	
	/**
	 * Problem in the form
	 * min(0.5 * x.P.x + q.x + r) s.t.
	 * G.x < h
	 * Submitted by Ashot Ordukhanyan 17/06/2014
	 */
	public void testPqrGh6() throws Exception {
		log.debug("testPqrGh6");
		
		String problemId = "6";
		
		double[][] P = Utils.loadDoubleMatrixFromFile("qp" + File.separator	+ "P" + problemId + ".csv", ",".charAt(0));
		double[] q = Utils.loadDoubleArrayFromFile("qp" + File.separator + "q" + problemId + ".txt");
		double r = Utils.loadDoubleArrayFromFile("qp" + File.separator + "r" + problemId + ".txt")[0];
		double[][] G = Utils.loadDoubleMatrixFromFile("qp" + File.separator + "G" + problemId + ".csv", ",".charAt(0));
		double[] h = Utils.loadDoubleArrayFromFile("qp" + File.separator + "h" + problemId + ".txt");
		double value = Utils.loadDoubleArrayFromFile("qp" + File.separator + "value" + problemId + ".txt")[0];
		
		PSDQuadraticMultivariateRealFunction objectiveFunction = new PSDQuadraticMultivariateRealFunction(P, q, r);
		
		// Inequalities G.x < h:
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];
		for(int i=0; i<G.length; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
		
		JOptimizer optimizer = new JOptimizer();
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setToleranceFeas(1e2);
		or.setTolerance(1e2);
		optimizer.setOptimizationRequest(or);
		optimizer.optimize();
		OptimizationResponse response = optimizer.getOptimizationResponse();
		
		int returnCode = response.getReturnCode();
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		double[] sol = response.getSolution();
		double min = objectiveFunction.value(sol);
    log.debug("sol: " + ArrayUtils.toString(sol));
    log.debug("minFromJOpt: " + min);
    
    double minFromMath = value;
		log.debug("minFromMath: " + minFromMath);
	}
	
	/**
	 * Problem in the form
	 * min(0.5 * x.P.x + q.x + r) s.t.
	 * G.x < h
	 * Submitted by Ashot Ordukhanyan 17/06/2014
	 * Apparently, Mathematica cannot solve this.
	 */
	public void testPqrGh7() throws Exception {
		log.debug("testPqrGh7");
		
		String problemId = "7";
		
		double[][] P = Utils.loadDoubleMatrixFromFile("qp" + File.separator	+ "P" + problemId + ".csv", ",".charAt(0));
		double[] q = Utils.loadDoubleArrayFromFile("qp" + File.separator + "q" + problemId + ".txt");
		double r = Utils.loadDoubleArrayFromFile("qp" + File.separator + "r" + problemId + ".txt")[0];
		double[][] G = Utils.loadDoubleMatrixFromFile("qp" + File.separator + "G" + problemId + ".csv", ",".charAt(0));
		double[] h = Utils.loadDoubleArrayFromFile("qp" + File.separator + "h" + problemId + ".txt");
		//double value = Utils.loadDoubleArrayFromFile("qp" + File.separator + "value" + problemId + ".txt")[0];
		
		PSDQuadraticMultivariateRealFunction objectiveFunction = new PSDQuadraticMultivariateRealFunction(P, q, r);
		
		// Inequalities G.x < h:
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[G.length];
		for(int i=0; i<G.length; i++){
			inequalities[i] = new LinearMultivariateRealFunction(G[i], -h[i]);
		}
		
		double[] initialGuess = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		JOptimizer optimizer = new JOptimizer();
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(initialGuess);
		or.setFi(inequalities);
		or.setToleranceFeas(5.E-4);
		or.setTolerance(5.E-4); 
		optimizer.setOptimizationRequest(or);
		optimizer.optimize();
		OptimizationResponse response = optimizer.getOptimizationResponse();
		
		int returnCode = response.getReturnCode();
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		double[] sol = response.getSolution();
		double min = objectiveFunction.value(sol);
    log.debug("sol: " + ArrayUtils.toString(sol));
    log.debug("minFromJOpt: " + min);
    
//    double minFromMath = value;
//		log.debug("minFromMath: " + minFromMath);
	}
	
	/**
	 * Minimize -x-y s.t. 
	 * x^2 + y^2 <= 4 (1/2 [x y] [I] [x y]^T - 2 <= 0)
	 */
	public void testLinearCostQuadraticInequalityOptimizationProblem() throws Exception {
		log.debug("testLinearCostQuadraticInequalityOptimizationProblem");

		double[] minimizeF = new double[] { -1.0, -1.0 };
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(minimizeF, 0.0);

		// inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		double[][] PMatrix = new double[][] { { 1.0, 0.0 }, { 0.0, 1.0 } };
		double[] qVector = new double[] { 0.0, 0.0 };
		double r = -2;

		inequalities[0] = new PDQuadraticMultivariateRealFunction(PMatrix, qVector, r); // x^2+y^2 <=4
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}

		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(Math.sqrt(2.0), sol[0], or.getTolerance());
		assertEquals(Math.sqrt(2.0), sol[1], or.getTolerance());
	}
	
	/**
	 * min (-e.x)
	 * s.t.
	 * 1/2 x.P.x < v
	 * x + y + z = 1
	 * x > 0
	 * y > 0
	 * z > 0 
	 */
	public void testLinearObjectiveQuadraticConstraints() throws Exception {
		log.debug("testLinearObjectiveQuadraticConstraints");
		
		//linear objective function
		double[] e = { -0.018, -0.025, -0.01 };
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(e, 0);
		
		//quadratic constraint: 0.5 * x.P.x -v < 0 
		double[][] P = {
				{ 1.68, 0.34, 0.38 },
				{ 0.34, 3.09, -1.59 }, 
				{ 0.38, -1.59, 1.54 } };
		double v = 0.3;//quadratic constraint limit
		PDQuadraticMultivariateRealFunction qc0 = new PDQuadraticMultivariateRealFunction(P, null, -v);
		
		//linear constraints
		//x>0
		LinearMultivariateRealFunction lc0 = new LinearMultivariateRealFunction(new double[]{-1, 0, 0}, 0);
		//y>0
		LinearMultivariateRealFunction lc1 = new LinearMultivariateRealFunction(new double[]{0, -1, 0}, 0);
		//z>0
		LinearMultivariateRealFunction lc2 = new LinearMultivariateRealFunction(new double[]{0, 0, -1}, 0);
		ConvexMultivariateRealFunction[] constraints = new ConvexMultivariateRealFunction[]{qc0, lc0, lc1,lc2}; 
		
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(constraints);
		or.setA(new double[][] { { 1, 1, 1 } });//equality constraint
		or.setB(new double[] { 1 });//equality constraint value
		or.setToleranceFeas(1.e-6);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		
		//assertions
		assertEquals(1., sol[0] + sol[1] + sol[2], 1.e-6);
		assertTrue(sol[0] > 0);
    assertTrue(sol[1] > 0);
		assertTrue(sol[2] > 0);
		RealVector xVector = MatrixUtils.createRealVector(sol);
		RealMatrix PMatrix = MatrixUtils.createRealMatrix(P);
		double xPx = xVector.dotProduct(PMatrix.operate(xVector));
		assertTrue(0.5 * xPx < v);
	}
	
	/**
	 * Minimize x s.t. 
	 * x+y=4
	 * y >= x^2. 
	 */
	public void testLinearCostLinearEqualityQuadraticInequality()	throws Exception {
		log.debug("testLinearCostLinearEqualityQuadraticInequality");
		double[] minimizeF = new double[] { 1.0, 0.0 };
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(minimizeF, 0.0);

		// Equalities:
		double[][] equalityAMatrix = new double[][] { { 1.0, 1.0 } };
		double[] equalityBVector = new double[] { 4.0 };

		// inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		double[][] PMatrix = new double[][] { { 2.0, 0.0 }, { 0.0, 0.0 } };
		double[] qVector = new double[] { 0.0, -1.0 };
		double r = 0.0;

		inequalities[0] = new PSDQuadraticMultivariateRealFunction(PMatrix, qVector, r); // x^2 - y < 0
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setA(equalityAMatrix);
		or.setB(equalityBVector);
		//or.setInitialPoint(new double[]{-0.5,4.5});
		//or.setNotFeasibleInitialPoint(new double[]{4,0});
		//or.setInteriorPointMethod(JOptimizer.BARRIER_METHOD);
		or.setCheckKKTSolutionAccuracy(true);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}

		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(1.0 / 2.0 * (-1.0 - Math.sqrt(17.0)), sol[0], 1e-5);
		assertEquals(1.0 / 2.0 * ( 9.0 + Math.sqrt(17.0)), sol[1], 1e-5);
	}
	
	/**
	 * Quadratically constrained quadratic programming in 2D.
	 */
	public void testSimpleQCQuadraticProgramming() throws Exception {
		log.debug("testSimpleQCQuadraticProgramming");
		
		// Objective function
		double[][] P = new double[][] { { 2., 0. },{ 0., 2. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//equalities
		double[][] A = new double[][]{{1,1}};
		double[] b = new double[]{1};

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = FunctionsUtils.createCircle(2, 2, new double[]{0., 0.});
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.2, 0.8});
		or.setA(A);
		or.setB(b);
		or.setFi(inequalities);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(0.5, sol[0], or.getTolerance());//NB: this is driven by the equality constraint
		assertEquals(0.5, sol[1], or.getTolerance());//NB: this is driven by the equality constraint
  }
	
	/**
	 * Linear objective, quadratically constrained.
	 * It simulates the type of optimization occurring in feasibility searching
	 * in a problem with constraints:
	 * x^2 < 1
	 */
	public void testQCQuadraticProgramming() throws Exception {
		log.debug("testQCQuadraticProgramming");
		
		// Objective function (linear (x,s)->s)
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(new double[] { 0, 1 }, 0);
		
		//inequalities x^2 < 1 + s
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		double[][] P1 = new double[][] { { 2., 0. },{ 0., 0. }};
		double[] c1 = new double[] { 0, -1 };
		inequalities[0] = new PSDQuadraticMultivariateRealFunction(P1, c1, -1);
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		//or.setInitialPoint(new double[] { 2, 5});//@FIXME: why this shows a poor convergence?
		//or.setInitialPoint(new double[] {-0.1,-0.989});
		or.setInitialPoint(new double[] {1.2, 2.});
		or.setFi(inequalities);
		or.setCheckKKTSolutionAccuracy(true);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals( 0., sol[0], or.getTolerance());
		assertEquals(-1., sol[1], or.getTolerance());
  }
	
	/**
	 * Quadratic objective, no constraints.
	 */
	public void testQCQuadraticProgramming2() throws Exception {
		log.debug("testQCQuadraticProgramming2");
		
		// Objective function
		double[][] P = new double[][] { { 1., 0. },{ 0., 1. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 2., 2.});
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode == OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol: " + ArrayUtils.toString(sol));
		log.debug("value  : " + objectiveFunction.value(sol));
		assertEquals(0., sol[0], 0.000000000000001);
		assertEquals(0., sol[1], 0.000000000000001);
  }
	
	/**
	 * Quadratically constrained quadratic programming in 2D.
	 */
	public void testQCQuadraticProgramming2D() throws Exception {
		log.debug("testQCQuadraticProgramming2D");
		
		// START SNIPPET: QCQuadraticProgramming-1
		
		// Objective function
		double[][] P = new double[][] { { 1., 0.4 },{ 0.4, 1. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = FunctionsUtils.createCircle(2, 1.75, new double[]{-2, -2});
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { -2., -2.});
		or.setFi(inequalities);
		or.setCheckKKTSolutionAccuracy(true);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
	  // END SNIPPET: QCQuadraticProgramming-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(-2 + 1.75/Math.sqrt(2), sol[0], 0.0000001);//-0.762563132923542
		assertEquals(-2 + 1.75/Math.sqrt(2), sol[1], 0.0000001);//-0.762563132923542
  }
	
	/**
	 * The same as {@link JOptimizerTest#testQCQuadraticProgramming2D()}, but without initial point.
	 */
	public void testQCQuadraticProgramming2DNoInitialPoint() throws Exception {
		log.debug("testQCQuadraticProgramming2DNoInitialPoint");
		
		// Objective function
		double[][] P = new double[][] { { 1., 0.4 },{ 0.4, 1. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = FunctionsUtils.createCircle(2, 1.75, new double[]{-2, -2});
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setCheckKKTSolutionAccuracy(true);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(-2 + 1.75/Math.sqrt(2), sol[0], or.getTolerance());//-0.762563132923542
		assertEquals(-2 + 1.75/Math.sqrt(2), sol[1], or.getTolerance());//-0.762563132923542
  }
	
	/**
	 * The basic PhaseI problem relative to {@link JOptimizerTest#testQCQuadraticProgramming2DNoInitialPoint()}. 
	 * min(s) s.t.
	 * (x+2)^2 + (y+2)^2 -1.75 < s 
	 * This problem can't be solved without an initial point, because the relative PhaseI problem
	 * min(r) s.t.
	 * (x+2)^2 + (y+2)^2 -1.75 -s < r
	 * is unbounded.
	 */
	public void testQCQuadraticProgramming2DNoInitialPointPhaseI() throws Exception {
		log.debug("testQCQuadraticProgramming2DNoInitialPointPhaseI");
		
		// Objective function
		double[] f0 = new double[]{0, 0, 1};//s
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(f0, 0);

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = new ConvexMultivariateRealFunction() {
			
			public double value(double[] X) {
				double x = X[0];
				double y = X[1];
				double s = X[2];
				return Math.pow(x+2, 2)+Math.pow(y+2, 2)-1.75-s;
			}
			
			public double[] gradient(double[] X) {
				double x = X[0];
				double y = X[1];
				double s = X[2];
				return new double[]{2*(x+2), 2*(y+2), -1 };
			}
			
			public double[][] hessian(double[] X) {
				double x = X[0];
				double y = X[1];
				double s = X[2];
				double[][] ret = new double[3][3];
				ret[0][0] = 2;
				ret[1][1] = 2;
				return ret;
			}
			
			public int getDim() {
				return 3;
			}
		};
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		//or.setInitialPoint(new double[] {0.5,0.5,94375.0});
		or.setInitialPoint(new double[] {-2, -2, 10});
		or.setFi(inequalities);
		or.setCheckKKTSolutionAccuracy(true);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(-2.  , sol[0], or.getTolerance());
		assertEquals(-2.  , sol[1], or.getTolerance());
		assertEquals(-1.75, sol[2], or.getTolerance());
  }
	
	/**
	 * Simple geometric programming.
	 * Solve the following LP
	 * 	min x s.t.
	 * 	 2<x<3
	 * as a GP.	
	 */
	public void testGeometricProgramming1() throws Exception {
		log.debug("testGeometricProgramming1");
		
		// Objective function (variables y, dim = 1)
		double[] a01 = new double[]{1};
		double b01 = 0;
		double[] a11 = new double[]{-1};
		double b11 = Math.log(2);
		double[] a21 = new double[]{1};
		double b21 = Math.log(1./3.);
		ConvexMultivariateRealFunction objectiveFunction = new LogTransformedPosynomial(new double[][]{a01}, new double[]{b01});
		
		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		inequalities[0] = new LogTransformedPosynomial(new double[][]{a11}, new double[]{b11});
		inequalities[1] = new LogTransformedPosynomial(new double[][]{a21}, new double[]{b21});
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setInitialPoint(new double[]{Math.log(2.5)});
		or.setTolerance(1.E-12);
		//or.setInteriorPointMethod(JOptimizer.BARRIER_METHOD);//if you prefer the barrier-method
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
        
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(Math.log(2), sol[0], 0.00000000001);
  }
	
	/**
	 * Geometric programming with dim=2.
	 */
	public void testGeometricProgramming2D() throws Exception {
		log.debug("testGeometricProgramming2D");
		
		// START SNIPPET: GeometricProgramming-1
		
		// Objective function (variables (x,y), dim = 2)
		double[] a01 = new double[]{2,1};
		double b01 = 0;
		double[] a02 = new double[]{3,1};
		double b02 = 0;
		ConvexMultivariateRealFunction objectiveFunction = new LogTransformedPosynomial(new double[][]{a01, a02}, new double[]{b01, b02});
		
		//constraints
		double[] a11 = new double[]{1,0};
		double b11 = Math.log(1);
		double[] a21 = new double[]{0,1};
		double b21 = Math.log(1);
		double[] a31 = new double[]{-1,-1.};
		double b31 = Math.log(0.7);
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[3];
		inequalities[0] = new LogTransformedPosynomial(new double[][]{a11}, new double[]{b11});
		inequalities[1] = new LogTransformedPosynomial(new double[][]{a21}, new double[]{b21});
		inequalities[2] = new LogTransformedPosynomial(new double[][]{a31}, new double[]{b31});
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setFi(inequalities);
		or.setInitialPoint(new double[]{Math.log(0.9), Math.log(0.9)});
		//or.setInteriorPointMethod(JOptimizer.BARRIER_METHOD);//if you prefer the barrier-method
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		// END SNIPPET: GeometricProgramming-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
        
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		assertEquals(Math.log(0.7), sol[0], or.getTolerance());//-0,35667494
		assertEquals(Math.log(1),   sol[1], or.getTolerance());// 0.0
  }
	
	/**
	 * Exponential objective with quadratic ineq. 
	 * f0 = exp[z^2], z=(x-1, y-2) 
	 * f1 = x^2+y^2 < 3^2
	 */
	public void testOptimize7() throws Exception {
		log.debug("testOptimize7");
		// START SNIPPET: JOptimizer-1
		
		//you can implement the function definition using whatever linear algebra library you want, you are not tied to Colt
		StrictlyConvexMultivariateRealFunction objectiveFunction = new StrictlyConvexMultivariateRealFunction() {

			public double value(double[] X) {
				DoubleMatrix1D Z = F1.make(new double[] { X[0] - 1, X[1] - 2, });
				return Math.exp(Z.zDotProduct(Z));
			}

			public double[] gradient(double[] X) {
				DoubleMatrix1D Z = F1.make(new double[] { X[0] - 1, X[1] - 2, });
				return Z.assign(Mult.mult(2 * Math.exp(Z.zDotProduct(Z)))).toArray();
			}

			public double[][] hessian(double[] X) {
				DoubleMatrix1D Z = F1.make(new double[] { X[0] - 1, X[1] - 2, });
				double d = Math.exp(Z.zDotProduct(Z));
				DoubleMatrix2D ID = F2.identity(2);
				DoubleMatrix2D ret = ALG.multOuter(Z, Z, null).assign(ID, Functions.plus).assign(Mult.mult(2 * d));
				return ret.toArray();
			}

			public int getDim() {
				return 2;
			}
		};

		// Inquality constraints
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
		inequalities[0] = FunctionsUtils.createCircle(2, 3);//dim=2, radius=3, center=(0,0)

		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setInitialPoint(new double[] { 0.2, 0.2 });
		or.setFi(inequalities);

		// optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
		// END SNIPPET: JOptimizer-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		double value = objectiveFunction.value(sol);
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + value);
		assertEquals(1., sol[0], or.getTolerance());
		assertEquals(2., sol[1], or.getTolerance());
	}
	
	/**
	 * Linear fractional programming in 2D.
	 * Original problem is:
	 * <br>min (c.X/e.X) s.t.
	 * <br>	G.X < h
	 * <br>with
	 * <br> X = {x,y}
	 * <br> c = {2,4}
	 * <br> e = {2,3}
	 * <br> G = {{-1,1},{3,1},{1/5,-1}}
	 * <br> h = {0,3.2,-0.32}
	 */
	public void testLFProgramming2D() throws Exception {
		log.debug("testLFProgramming2D");
		
		// START SNIPPET: LFP-1
		
		// Objective function (variables y0, y1, z)
		double[] n = new double[] { 2., 4., 0.};
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(n, 0);

		//inequalities (G.y-h.z<0, z>0)
		double[][] Gmh = new double[][]{{-1.0, 1., 0.},
										{ 3.0, 1.,-3.2},
										{ 0.2,-1., 0.32},
										{ 0.0, 0.,-1.0}};
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[4];
		for(int i=0; i<4;i++){
			inequalities[i] = new LinearMultivariateRealFunction(Gmh[i], 0);
		}
		
		//equalities (e.y+f.z=1)
		double[][] Amb = new double[][]{{ 2.,  3.,  0.}};
		double[] bm= new double[]{1};
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setA(Amb);
		or.setB(bm);
		or.setFi(inequalities);
		or.setTolerance(1.E-6);
		or.setToleranceFeas(1.E-6);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
	  // END SNIPPET: LFP-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		double x = sol[0]/sol[2];
		double y = sol[1]/sol[2];
		assertEquals(0.9, x, 0.00001);
		assertEquals(0.5, y, 0.00001);
	}
	
	/**
	 * Convex-concave fractional programming in 2D.
	 * Original problem is:
	 * <br>min (c.X/e.X) s.t.
	 * <br>	(x-c0)^2 + (y-c1)^2 < R^2
	 * <br>with
	 * <br> X = {x,y}
	 * <br> c = {2,4}
	 * <br> e = {2,3}
	 * <br> c0 = 0.65
	 * <br> c1 = 0.65
	 * <br> R = 0.25
	 */
	public void testCCFProgramming2D() throws Exception {
		log.debug("testCCFProgramming2D");
		
		// START SNIPPET: CCFP-1
		
		// Objective function (variables y0, y1, t)
		double[] n = new double[] { 2., 4., 0.};
		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(n, 0);

		//inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		//t > 0
		double[][] Gmh = new double[][]{{0.0, 0.0,-1.0}};//t>0
		inequalities[0] = new LinearMultivariateRealFunction(Gmh[0], 0);
		
		//perspective function of (x-c0)^2 + (y-c1)^2 - R^2 < 0
		//this is t*((y0/t - c0)^2 + (y1/t - c1)^2 -R^2)
		//we do not multiply by t, because it would make the function no more convex
		final double c0 = 0.65;
		final double c1 = 0.65;
		final double R = 0.25;
		inequalities[1] = new ConvexMultivariateRealFunction() {
			
			public double value(double[] X) {
				double y0 = X[0];
				double y1 = X[1];
				double t =  X[2];
				return t * (Math.pow(y0 / t - c0, 2) + Math.pow(y1 / t - c1, 2) - Math.pow(R, 2));
			}
			
			public double[] gradient(double[] X) {
				double y0 = X[0];
				double y1 = X[1];
				double t =  X[2];
				double[] ret = new double[3];
				ret[0] = 2 * (y0/t - c0);
				ret[1] = 2 * (y1/t - c1);
				ret[2] = Math.pow(c0, 2) + Math.pow(c1, 2) - Math.pow(R, 2) - (Math.pow(y0, 2) + Math.pow(y1, 2))/Math.pow(t, 2);
				return ret;
			}
			
			public double[][] hessian(double[] X) {
				double y0 = X[0];
				double y1 = X[1];
				double t  = X[2];
				double[][] ret = {
					{                2/t,                   0, -2*y0/Math.pow(t,2)}, 
					{                  0,                 2/t, -2*y1/Math.pow(t,2)}, 
					{-2*y0/Math.pow(t,2), -2*y1/Math.pow(t,2),  2*(Math.pow(y0,2) + Math.pow(y1,2))/Math.pow(t,3)}};
				return ret;
			}
			
			public int getDim() {
				return 3;
			}
		};
		
		//equalities (e.y+f.t=1), f is 0
		double[][] Amb = new double[][]{{ 2.,  3.,  0.}};
		double[] bm= new double[]{1};
		
		//optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		or.setA(Amb);
		or.setB(bm);
		or.setFi(inequalities);
		or.setTolerance(1.E-6);
		or.setToleranceFeas(1.E-6);
		or.setNotFeasibleInitialPoint(new double[] { 0.6, -0.2/3., 0.1 });
		or.setCheckKKTSolutionAccuracy(true);
		
		//optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		int returnCode = opt.optimize();
		
	  // END SNIPPET: CCFP-1
		
		if(returnCode==OptimizationResponse.FAILED){
			fail();
		}
		
		OptimizationResponse response = opt.getOptimizationResponse();
		double[] sol = response.getSolution();
		log.debug("sol   : " + ArrayUtils.toString(sol));
		log.debug("value : " + objectiveFunction.value(sol));
		double x = sol[0]/sol[2];
		double y = sol[1]/sol[2];
		assertEquals(0.772036, x, 0.000001);
		assertEquals(0.431810, y, 0.000001);
  }
}
