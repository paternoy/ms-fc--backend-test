package com.scmspain.services.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TweetValidator implements ConstraintValidator<ValidTweet, String> {
	private int maxTweetLength = 140;
	private static final String PATTERN = "((https?)://\\S+)";

	public TweetValidator() {
		super();
	}

	public TweetValidator(int maxTweetLength) {
		this();
		this.maxTweetLength = maxTweetLength;

	}

	private int getLength(String tweet) {
		if (tweet == null)
			return 0;
		String cleaned = tweet.replaceAll(PATTERN, "");
		return cleaned.length();
	}

	@Override
	public void initialize(ValidTweet constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		int length = getLength(value);
		return length <= maxTweetLength;
	}
}
