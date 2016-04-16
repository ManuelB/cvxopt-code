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



/**
 * Convex Optimizer.
 * 
 * The algorithm selection is implemented as a Chain of Responsibility pattern,
 * and this class is the client of the chain.
 * 
 * @see "S.Boyd and L.Vandenberghe, Convex Optimization"
 * @author <a href="mailto:alberto.trivellato@gmail.com">alberto trivellato</a>
 */
public class JOptimizer {

	public static final int DEFAULT_MAX_ITERATION = 500;
	public static final double DEFAULT_FEASIBILITY_TOLERANCE = 1.E-6;
	public static final double DEFAULT_TOLERANCE = 1.E-5;
	public static final double DEFAULT_TOLERANCE_INNER_STEP = 1.E-5;
	public static final double DEFAULT_KKT_TOLERANCE = 1.E-9;
	public static final double DEFAULT_ALPHA = 0.055;
	public static final double DEFAULT_BETA = 0.55;
	public static final double DEFAULT_MU = 10;
	public static final String BARRIER_METHOD = "BARRIER_METHOD";
	public static final String PRIMAL_DUAL_METHOD = "PRIMAL_DUAL_METHOD";
	public static final String DEFAULT_INTERIOR_POINT_METHOD = PRIMAL_DUAL_METHOD;
	public static final String INFEASIBLE_PROBLEM = "Infeasible problem";
	
	private OptimizationRequest request = null;
	private OptimizationResponse response = null;
	
	public int optimize() throws Exception {
		//start with the first step in the chain.
		OptimizationRequestHandler handler = new NewtonUnconstrained(true);
		handler.setOptimizationRequest(request);
		int retCode = handler.optimize();
		this.response = handler.getOptimizationResponse();
		return retCode;
	}
	
	public void setOptimizationRequest(OptimizationRequest or) {
		this.request = or;
	}

	public OptimizationResponse getOptimizationResponse() {
		return response;
	}
	
}
