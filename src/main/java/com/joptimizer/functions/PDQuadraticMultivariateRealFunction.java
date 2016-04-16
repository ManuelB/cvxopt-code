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

import cern.colt.matrix.linalg.CholeskyDecomposition;

/**
 * 1/2 * x.P.x + q.x + r,  
 * P symmetric and positive definite
 * 
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 */
public class PDQuadraticMultivariateRealFunction extends PSDQuadraticMultivariateRealFunction implements StrictlyConvexMultivariateRealFunction{

	public PDQuadraticMultivariateRealFunction(double[][] PMatrix, double[] qVector, double r) {
		this(PMatrix, qVector, r, false);
	}
	
	public PDQuadraticMultivariateRealFunction(double[][] PMatrix, double[] qVector, double r, boolean checkPD) {
		super(PMatrix, qVector, r, false);
		if(checkPD){
			try{
				CholeskyDecomposition cDecomp = new CholeskyDecomposition(P);
				if(!cDecomp.isSymmetricPositiveDefinite()){
					throw new Exception();
				}
			}catch(Exception e){
				throw new IllegalArgumentException("P not symmetric positive definite");
			}
		}
	}
}
