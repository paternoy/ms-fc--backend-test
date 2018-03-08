package com.scmspain.controller.command;

import org.hibernate.validator.constraints.NotEmpty;

import com.scmspain.services.validation.ValidTweet;

public class PublishTweetCommand {
	@NotEmpty
	private String publisher;

	@ValidTweet
	private String tweet;

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
}
