package com.shri.ysentiments;

public class Sentiment {
	private String comment;
	private float confidence;
	private SentimentType result;

	public Sentiment() {
		// TODO Auto-generated constructor stub
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	public SentimentType getResult() {
		return result;
	}

	public void setResult(SentimentType result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Sentiment [confidence=" + confidence + ", result=" + result
				+ "]";
	}

}
