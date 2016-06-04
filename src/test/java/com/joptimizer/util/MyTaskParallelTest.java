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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import junit.framework.TestCase;

/**
 * Parallelization test.
 * Sum of integer.
 * @see "http://embarcaderos.net/2011/01/23/parallel-processing-and-multi-core-utilization-with-java/"
 * @author alberto trivellato (alberto.trivellato@gmail.com)
 *
 */
public class MyTaskParallelTest extends TestCase {

	private static int NUM_OF_THREADS = Runtime.getRuntime().availableProcessors()-1;
	private static int NUM_OF_TASKS = 10;
	
	public void testDummy(){
		assertTrue(true);
	}

	public void xxxtestParallel() throws Exception {
		long t0 = new java.util.Date().getTime();

		ExecutorService eservice = Executors.newFixedThreadPool(NUM_OF_THREADS, new MyThreadFactory());
		CompletionService<Integer> cservice = new ExecutorCompletionService<Integer>(eservice);

		List<Future<Integer>> futureList = new ArrayList<Future<Integer>>();
		for (int index = 1; index <= NUM_OF_TASKS; index++) {
			Callable<Integer> worker = new MyTask(index);
			Future<Integer> submit = cservice.submit(worker);
			futureList.add(submit);
		}

		int totalResult = 0;
		for (Future<Integer> future : futureList) {
			try {
				int taskResult = future.get();
				System.out.println("got result from " + taskResult);
				totalResult += taskResult;
			} catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
		}
		Double secs = new Double((new java.util.Date().getTime() - t0) * 0.001);
		System.out.println("total result:  " + totalResult);
		System.out.println("total run time " + secs + " secs");
		
		assertEquals( (1+NUM_OF_TASKS) * NUM_OF_TASKS / 2., (double)totalResult);//Gauss formula
	}
	
	private class MyThreadFactory implements ThreadFactory{

		private ThreadFactory innerFactory = Executors.defaultThreadFactory();
		
		public Thread newThread(Runnable arg0) {
			return innerFactory.newThread(arg0);
		}
		
	}

}
