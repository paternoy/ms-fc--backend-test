package com.scmspain.services.validation;

import org.junit.Test;

public class TweetValidatorTest {
	TweetValidator tweetValidator = new TweetValidator(10);

	@Test
	public void testTweetWithMaxLengthOK() {
		assert (tweetValidator.isValid("1234567890", null));
	}

	@Test
	public void testKO() {
		assert (!tweetValidator.isValid("12345678901", null));
	}

	@Test
	public void testEmptyTweetKO() {
		assert (!tweetValidator.isValid("", null));
	}

	@Test
	public void testNullTweetKO() {
		assert (!tweetValidator.isValid(null, null));
	}

	@Test
	public void testStartsWithHyperlinkOK() {
		assert (tweetValidator.isValid("http://domain.com/path/ 123456789", null));
	}

	@Test
	public void testStartsWithHyperlinkKO() {
		assert (!tweetValidator.isValid("http://domain.com/path/ 1234567890", null));
	}

	@Test
	public void testMultipleHyperlinksOK() {
		assert (!tweetValidator.isValid("http://domain.com/path/ 34 http://domain1 http://domain2 050 http://d", null));
	}

	@Test
	public void testMultipleHyperlinksTooLongKO() {
		assert (!tweetValidator.isValid("http://domain.com/path/ 34 http://domain1 http://domain2 :// http://d ",
				null));
	}

}
