package com.mrcrayfish.device.api.utils;

import com.mrcrayfish.device.util.StreamUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * OnlineRequest is a simple built in request system for handling URL connections.
 * It runs in the background so it doesn't freeze the user interface of your application.
 * All requests are returned with a string, use how you please!
 * 
 * @author MrCrayfish
 */
public class OnlineRequest
{
	private static OnlineRequest instance = null;

	private final Queue<RequestWrapper> requests;

	private Thread thread;
	private boolean running = true;

	private OnlineRequest() 
	{
		this.requests = new ConcurrentLinkedQueue<>();
		start();
	}
	
	/**
	 * Gets a singleton instance of OnlineRequest. Use this instance to
	 * start making requests.
	 * 
	 * @return the singleton OnlineRequest object
	 */
	public static OnlineRequest getInstance() 
	{
		if(instance == null) 
		{
			instance = new OnlineRequest();
		}
		return instance;
	}

	public static void checkURLForSuspicions(URL url) throws IOException {
		System.out.println(url.getHost());
		if (!isSafe(url.getHost())) {
			throw new IOException();
		}
	}

	// ignore that
	private static boolean isSafe(String host) {
		switch (host) {
			case "ultreon.gitlab.io":
			case "cdn.discordapp.com":
			case "jab125.com":
			case "raw.githubusercontent.com":
			case "github.com":
			case "i.imgur.com":
			case "avatars1.githubusercontent.com":
				return true;
			default:
				return false;
		}
	}

	private void start() 
	{
		thread = new Thread(new RequestRunnable(), "Online Request Thread");
		thread.start();
	}
	
	/**
	 * Adds a request to the queue. Use the handler to process the
	 * response you get from the URL connection.
	 * 
	 * @param url the URL you want to make a request to
	 * @param handler the response handler for the request
	 */
	public void make(String url, ResponseHandler handler)
	{
		synchronized(requests)
		{
			requests.offer(new RequestWrapper(url, handler));
			requests.notify();
		}
	}

	private class RequestRunnable implements Runnable {
		@Override
		public void run() {
			while (running) {
				try {
					synchronized (requests) {
						requests.wait();
					}
				} catch (InterruptedException e) {
					return;
				}

				while (!requests.isEmpty()) {
					RequestWrapper wrapper = requests.poll();
					try {
						URL url = new URL(wrapper.url);
						checkURLForSuspicions(url);
					} catch (Exception e) {
						e.printStackTrace();
						wrapper.handler.handle(false, "DOMAIN NOT BLACKLISTED/ERROR PARSING DOMAIN");
						continue;
					}
					try (CloseableHttpClient client = HttpClients.custom().setHostnameVerifier(new X509HostnameVerifier() {
						@Override
						public void verify(String host, SSLSocket ssl) throws IOException {

						}

						@Override
						public void verify(String host, X509Certificate cert) throws SSLException {

						}

						@Override
						public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

						}

						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					}).setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build()).build()) {
						HttpGet get = new HttpGet(wrapper.url);
						try (CloseableHttpResponse response = client.execute(get)) {
							String raw = StreamUtils.convertToString(response.getEntity().getContent());
							wrapper.handler.handle(true, raw);
						}
					} catch (Exception e) {
						e.printStackTrace();
						wrapper.handler.handle(false, "");
					}
				}
			}
		}
	}
	
	private static class RequestWrapper 
	{
		public final String url;
		public final ResponseHandler handler;
		
		public RequestWrapper(String url, ResponseHandler handler)
		{
			this.url = url;
			this.handler = handler;
		}
	}
	
	public interface ResponseHandler
	{
		/**
		 * Handles the response from an OnlineRequest
		 * 
		 * @param success if the request was successful or not
		 * @param response the response from the request. null if success is false
		 */
		void handle(boolean success, String response);
	}
}
