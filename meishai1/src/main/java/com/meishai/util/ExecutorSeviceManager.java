package com.meishai.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理器
 * 
 * @author wmz
 * 
 */
public class ExecutorSeviceManager {
	// private static ExecutorService executorService = Executors
	// .newCachedThreadPool();
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(3);

	/**
	 * 获取线程池实例
	 * 
	 * @return
	 */
	public static ExecutorService getExecutorInstance() {
		return executorService;
	}

	public static void execute(Runnable command) {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.execute(command);
		}
	}

	public static void shutdownNow() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
		}
	}

	public static void shutdown() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
	}
}
