package com.meishai.util;

import android.os.Handler;

/**
 * 连接服务器管理器
 * 
 * @author wmz
 * 
 */
public class ConnectServerManager {

	/**
	 * 登录
	 * 
	 * @param url
	 * @param handler
	 * @param what
	 */
	public static void login(final String url, final Handler handler,
							 final int what) {
		execute(url, handler, what);
	}

	public static void isOnline(final String url, final Handler handler,
								final int what) {
		execute(url, handler, what);
	}

	/**
	 * 检查版本
	 * 
	 * @param url
	 * @param handler
	 * @param what
	 */
	public static void checkVersion(final String url, final Handler handler,
									final int what) {
		execute(url, handler, what);
	}

	/**
	 * 获取广告信息
	 * 
	 * @param url
	 * @param handler
	 * @param what
	 */
	public static void getAd(final String url, final Handler handler,
							 final int what) {
		execute(url, handler, what);
	}

	/**
	 * 获取验证票信息
	 * 
	 * @param url
	 * @param handler
	 * @param what
	 */
	public static void getTicketMsg(final String url, final Handler handler,
									final int what) {
		execute(url, handler, what);
	}
//	public static void getTicketMsg(final String url, Map<String,String> params,final Handler handler,
//			final int what) {
//		execute(url, params, handler, what);
////		execute(url, handler, what);
//	}

	/**
	 * 发送过闸票信息
	 * 
	 * @param url
	 * @param ticketNo
	 * @param handler
	 * @param what
	 */
	public static void sendTicketMsg(final String url, final Handler handler,
									 final int what) {
		execute(url, handler, what);
	}

	public static void sendFingerprint(final String url, final Handler handler,
									   final int what) {
		execute(url, handler, what);
	}

	public static void sendFingerprint(final String url, final String params,
									   final Handler handler, final int what) {
		execute(url, params, handler, what);
	}

	public static void sendMatchFingerprint(final String url,
											final Handler handler, final int what) {
		execute(url, handler, what);
	}

	public static void sendFingerprintRecord(final String url,
											 final Handler handler, final int what) {
		execute(url, handler, what);
	}

	/**
	 * 执行访问后台数据
	 * 
	 * @param url
	 * @param handler
	 * @param what
	 */
	public static void execute(final String url, final Handler handler,
							   final int what) {
		ExecutorSeviceManager.execute(new Runnable() {

			@Override
			public void run() {
				String response = NetUtils
						.getStringFromHttpURLConnectionByGet(url);
				HandlerUtils.sendHandle(handler, response, what);
			}
		});
	}

	/**
	 * 执行访问后台数据
	 * 
	 * @param url
	 * @param params
	 * @param handler
	 * @param what
	 */
	public static void execute(final String url, final String params,
							   final Handler handler, final int what) {
		ExecutorSeviceManager.execute(new Runnable() {

			@Override
			public void run() {
				String response = NetUtils
						.getStringFromHttpURLConnectionByPost(url, params);
				HandlerUtils.sendHandle(handler, response, what);
			}
		});
	}

	public static void shutdownNow() {
		ExecutorSeviceManager.shutdownNow();
	}

	public static void shutdown() {
		ExecutorSeviceManager.shutdown();
	}
}
