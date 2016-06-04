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
package com.joptimizer.algebra;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

import com.joptimizer.util.Utils;

/**
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class CholeskyUpperDiagonalFactorizationTest extends TestCase {

	protected Algebra ALG = Algebra.DEFAULT;
	protected DoubleFactory2D F2 = DoubleFactory2D.sparse;
	protected DoubleFactory1D F1 = DoubleFactory1D.sparse;
	private Log log = LogFactory.getLog(this.getClass().getName());

	public void testSimple1() throws Exception {
		log.debug("testSimple1");
		double[][] A = new double[][] { 
				{ 4,  0,  0,  1 }, 
				{ 0,  4,  0, -1 }, 
				{ 0,  0,  6,  1 }, 
				{ 1, -1,  1,  6 } };
		SparseDoubleMatrix2D AMatrix = new SparseDoubleMatrix2D(A);
		int dim = AMatrix.rows();
		
		CholeskyUpperDiagonalFactorization cs = new CholeskyUpperDiagonalFactorization(AMatrix, 3);
		cs.factorize();
		DoubleMatrix2D L = cs.getL();
		log.debug("L : " + ArrayUtils.toString(L.toArray()));
		log.debug("LT: " + ArrayUtils.toString(cs.getLT().toArray()));
		
		//solve A.x = b
		DoubleMatrix1D b = F1.make(new double[]{1, 2, 3, 4});
		DoubleMatrix1D x = cs.solve(b);
		double scaledResidualx = Utils.calculateScaledResidual(AMatrix, x, b);
		log.debug("scaledResidualx: " + scaledResidualx);
		assertEquals(0., scaledResidualx, Utils.getDoubleMachineEpsilon());
		
		//solve A.X = B
		DoubleMatrix2D B = Utils.randomValuesMatrix(dim, 5, -1, 1, 12345L);
		DoubleMatrix2D X = cs.solve(B);
		double scaledResidualX = Utils.calculateScaledResidual(AMatrix, X, B);
		log.debug("scaledResidualX: " + scaledResidualX);
		assertTrue(scaledResidualX < Utils.getDoubleMachineEpsilon());
	}
	
	public void testSimple2() throws Exception {
		log.debug("testSimple2");
		double[][] A = new double[][] { 
				{ 4,    0,    0.1,  1 }, 
				{ 0,    4,    0.2, -1 }, 
				{ 0.1,  0.2,  6,    1 }, 
				{ 1,    -1,   1,    6 } };
		SparseDoubleMatrix2D AMatrix = new SparseDoubleMatrix2D(A);
		int dim = AMatrix.rows();
		
		CholeskyUpperDiagonalFactorization cs = new CholeskyUpperDiagonalFactorization(AMatrix, 2);
		cs.factorize();
		DoubleMatrix2D L = cs.getL();
		log.debug("L : " + ArrayUtils.toString(L.toArray()));
		log.debug("LT: " + ArrayUtils.toString(cs.getLT().toArray()));
		
		//solve A.x = b
		DoubleMatrix1D b = F1.make(new double[]{1, 2, 3, 4});
		DoubleMatrix1D x = cs.solve(b);
		double scaledResidualx = Utils.calculateScaledResidual(AMatrix, x, b);
		log.debug("scaledResidualx: " + scaledResidualx);
		assertEquals(0., scaledResidualx, Utils.getDoubleMachineEpsilon());
		
		//solve A.X = B
		DoubleMatrix2D B = Utils.randomValuesMatrix(dim, 5, -1, 1, 12345L);
		DoubleMatrix2D X = cs.solve(B);
		double scaledResidualX = Utils.calculateScaledResidual(AMatrix, X, B);
		log.debug("scaledResidualX: " + scaledResidualX);
		assertTrue(scaledResidualX < Utils.getDoubleMachineEpsilon());
	}
	
	/**
	 * Test the special decomposition of "S.Boyd and L.Vandenberghe, Convex Optimization, p. 671".
	 */
	public void testSimple3() throws Exception {
		log.debug("testSimple3");
		double[][] A = new double[][] { 
				{ 4,    0,    0.1 }, 
				{ 0,    4,    0.2 }, 
				{ 0.1,  0.2,  1  }};
		SparseDoubleMatrix2D AMatrix = new SparseDoubleMatrix2D(A);
		int dim = AMatrix.rows();
		
		CholeskyUpperDiagonalFactorization cs = new CholeskyUpperDiagonalFactorization(AMatrix, 2);
		cs.factorize();
		DoubleMatrix2D L = cs.getL();
		log.debug("L : " + ArrayUtils.toString(L.toArray()));
		log.debug("LT: " + ArrayUtils.toString(cs.getLT().toArray()));
		
		//solve A.x = b
		DoubleMatrix1D b = F1.make(new double[]{1, 2, 3});
		DoubleMatrix1D x = cs.solve(b);
		double scaledResidualx = Utils.calculateScaledResidual(AMatrix, x, b);
		log.debug("scaledResidualx: " + scaledResidualx);
		assertEquals(0., scaledResidualx, Utils.getDoubleMachineEpsilon());
		
		//solve A.X = B
		DoubleMatrix2D B = Utils.randomValuesMatrix(dim, 5, -1, 1, 12345L);
		DoubleMatrix2D X = cs.solve(B);
		double scaledResidualX = Utils.calculateScaledResidual(AMatrix, X, B);
		log.debug("scaledResidualX: " + scaledResidualX);
		assertTrue(scaledResidualX < Utils.getDoubleMachineEpsilon());
	}
	
	/**
	 * Must not fail, mathematica can factorize this matrix.
	 * Show the use of scaling in Cholesky factorization
	 */
	public void testFromFile10() throws Exception {
		log.debug("testFromFile10");
		String matrixId = "10";
		double[][] A = Utils.loadDoubleMatrixFromFile("factorization" + File.separator	+ "matrix"+matrixId+".csv", ",".charAt(0));
		SparseDoubleMatrix2D AMatrix = new SparseDoubleMatrix2D(A);
		int dim = AMatrix.rows();
		
		CholeskyUpperDiagonalFactorization cs;
		try{
			cs = new CholeskyUpperDiagonalFactorization(AMatrix, dim -1);
			cs.factorize();
		}catch(Exception e){
			log.debug("numeric problem, try to rescale the matrix");
			cs = new CholeskyUpperDiagonalFactorization(AMatrix, dim -1, new Matrix1NormRescaler());
			cs.factorize();
		}
		
		//solve A.x = b
		DoubleMatrix1D b = Utils.randomValuesVector(dim, -1, 1, 12345L);
		DoubleMatrix1D x = cs.solve(b);
		double scaledResidualx = Utils.calculateScaledResidual(AMatrix, x, b);
		log.debug("scaledResidualx: " + scaledResidualx);
		assertTrue(scaledResidualx < 1.e-15);
		
		//solve A.X = B
		DoubleMatrix2D B = Utils.randomValuesMatrix(dim, 5, -1, 1, 12345L);
		DoubleMatrix2D X = cs.solve(B);
		double scaledResidualX = Utils.calculateScaledResidual(AMatrix, X, B);
		log.debug("scaledResidualX: " + scaledResidualX);
		assertTrue(scaledResidualX < 1.e-15);
		
	}
}
