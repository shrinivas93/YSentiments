package com.shri.ysentiments;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

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
import com.google.api.services.youtube.model.PlaylistItem;

public class Main {

	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {

			@Override
			public void initialize(HttpRequest request) throws IOException {
			}
		}).setYouTubeRequestInitializer(new YouTubeRequestInitializer("AIzaSyD2d1iwFA_6cIWxRcLImpRPBRNHbQH6yGw"))
				.setApplicationName("YSentiments").build();
		String newChannelId = youtube.channels().list("contentDetails").setId("UC4Pk-YjgQGwW5uQrMDMlnww").execute()
				.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads();
		List<PlaylistItem> videos = youtube.playlistItems().list("contentDetails").setPlaylistId(newChannelId)
				.setMaxResults(1L).execute().getItems();
		for (PlaylistItem playlistItem : videos) {
			String videoId = playlistItem.getContentDetails().getVideoId();
			List<CommentThread> comments = youtube.commentThreads().list("snippet").setVideoId(videoId)
					.setTextFormat("plainText").setMaxResults(100L).execute().getItems();
			for (CommentThread commentThread : comments) {
				CommentSnippet snippet = commentThread.getSnippet().getTopLevelComment().getSnippet();
				System.out.println(snippet.getAuthorDisplayName() + " : " + snippet.getTextDisplay());
			}
		}
	}
}
