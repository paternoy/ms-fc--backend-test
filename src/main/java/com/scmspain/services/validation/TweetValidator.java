package com.scmspain.services.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scmspain.controller.TweetController;

public class TweetValidator implements ConstraintValidator<ValidTweet, String> {
	private int maxTweetLength = 140;

	private static final Logger logger = LoggerFactory.getLogger(TweetController.class);
	// A link is any set of non-whitespace consecutive characters starting with
	// http:// or https:// and finishing with a space
	private static final String PATTERN = "\\b(https?://\\S+)";

	Pattern pattern = Pattern.compile(PATTERN);

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
		Matcher matcher = pattern.matcher(tweet);
		logger.debug("Matching tweet: \"{}\"", tweet);
		int length = tweet.length();
		while (matcher.find()) {
			logger.debug("Link matched: \"{}\"", matcher.group(1));
			length = length - matcher.group(1).length();
		}
		return length;
	}

	@Override
	public void initialize(ValidTweet constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		int length = getLength(value);
		return length >0 && length <= maxTweetLength;
	}
}
