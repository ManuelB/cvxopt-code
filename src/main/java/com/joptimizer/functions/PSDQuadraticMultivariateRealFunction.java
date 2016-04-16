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
package com.joptimizer.functions;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 * 1/2 * x.P.x + q.x + r,
 * P symmetric and positive semi-definite
 * 
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class PSDQuadraticMultivariateRealFunction extends QuadraticMultivariateRealFunction implements ConvexMultivariateRealFunction {

	public PSDQuadraticMultivariateRealFunction(double[][] PMatrix,	double[] qVector, double r) {
		this(PMatrix, qVector, r, false);
	}
	
	public PSDQuadraticMultivariateRealFunction(double[][] PMatrix,	double[] qVector, double r, boolean checkPSD) {
		super(PMatrix, qVector, r);
		if(checkPSD){
			EigenvalueDecomposition eDecomp = new EigenvalueDecomposition(P);
			DoubleMatrix1D realEigenvalues = eDecomp.getRealEigenvalues();
			for (int i = 0; i < realEigenvalues.size(); i++) {
				if (realEigenvalues.get(i) < 0) {
					throw new IllegalArgumentException("Not positive semi-definite matrix");
				}
			}
		}
	}
}
