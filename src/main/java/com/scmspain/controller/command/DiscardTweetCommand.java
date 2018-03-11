package com.scmspain.controller.command;

import javax.validation.constraints.NotNull;

public class DiscardTweetCommand {
	@NotNull
	Long tweet;

	public Long getTweet() {
		return tweet;
	}

	public void setTweet(Long tweet) {
		this.tweet = tweet;
	}

}
