package com.shri.ysentiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.sun.jersey.api.client.Client;

public class Main {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final BufferedReader BR = new BufferedReader(
			new InputStreamReader(System.in));
	private static final YouTube YOUTUBE = init();
	private static final Client REST_CLIENT = Client.create();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String PROXY_HOST = "ptproxy.persistent.co.in";
	private static final String PROXY_PORT = "8080";
	private static final String PROXY_USER = "shrinivas_shukla";
	private static final String PROXY_PASSSWORD = "CooperSheldon!398";

	static {
		System.setProperty("http.proxyHost", PROXY_HOST);
		System.setProperty("http.proxyPort", PROXY_PORT);
		System.setProperty("http.proxyUser", PROXY_USER);
		System.setProperty("http.proxyPassword", PROXY_PASSSWORD);
		System.setProperty("https.proxyHost", PROXY_HOST);
		System.setProperty("https.proxyPort", PROXY_PORT);
		System.setProperty("https.proxyUser", PROXY_USER);
		System.setProperty("https.proxyPassword", PROXY_PASSSWORD);
	}

	private Main() {
		// Empty private constructor
	}

	public static void main(String[] args) throws IOException,
			GeneralSecurityException {
		String videoId = getVideoId();
		List<String> comments = getComments(videoId);
		List<Sentiment> sentiments = getSentiments(comments);
		aggregateSentiments(sentiments);
	}

	private static void aggregateSentiments(List<Sentiment> sentiments) {
		int positiveCount = 0;
		int negativeCount = 0;
		int neutralCount = 0;
		for (Sentiment sentiment : sentiments) {
			switch (sentiment.getResult()) {
			case Positive:
				positiveCount++;
				break;
			case Negative:
				negativeCount++;
				break;
			case Neutral:
				neutralCount++;
				break;
			}

		}
		System.out.println("Total    : " + sentiments.size());
		System.out.println("Positive : " + positiveCount);
		System.out.println("Negative : " + negativeCount);
		System.out.println("Neutral  : " + neutralCount);
	}

	private static List<Sentiment> getSentiments(List<String> comments)
			throws IOException {
		String sentimentResult = REST_CLIENT
				.resource("http://sentiment.vivekn.com/api/batch/")
				.type(MediaType.APPLICATION_JSON).entity(comments)
				.post(String.class);
		List<Sentiment> sentiments = OBJECT_MAPPER.readValue(sentimentResult,
				new TypeReference<List<Sentiment>>() {
				});
		return sentiments;
	}

	private static List<String> getComments(String videoId) throws IOException {
		List<CommentThread> commentThreads = YOUTUBE.commentThreads()
				.list("snippet").setVideoId(videoId).setTextFormat("plainText")
				.setMaxResults(100L).execute().getItems();
		List<String> comments = new ArrayList<>();
		for (CommentThread commentThread : commentThreads) {
			CommentSnippet snippet = commentThread.getSnippet()
					.getTopLevelComment().getSnippet();
			comments.add(snippet.getTextDisplay());
		}
		return comments;
	}

	private static YouTube init() {
		return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request)
							throws IOException {
						// Empty, because I don't know what to do here
					}
				})
				.setYouTubeRequestInitializer(
						new YouTubeRequestInitializer(
								"AIzaSyD2d1iwFA_6cIWxRcLImpRPBRNHbQH6yGw"))
				.setApplicationName("YSentiments").build();
	}

	private static String getVideoId() throws IOException {
		System.out.print("Enter videoId : ");
		return BR.readLine();
	}
}
