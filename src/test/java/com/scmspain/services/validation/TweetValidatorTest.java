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
	public void testStartsWithHyperlinkOK() {
		assert (tweetValidator.isValid("http://domain.com/path/ 123456789", null));
	}

	@Test
	public void testStartsWithHyperlinkKO() {
		assert (!tweetValidator.isValid("http://domain.com/path/ 1234567890", null));
	}

	@Test
	public void testMultipleHyperlinksOK() {
		assert (tweetValidator.isValid("http://domain.com/path/ 34 http://domain1 http://domain2 050 http://d", null));
	}

	@Test
	public void testWrongHyperlinkKO() {
		assert (!tweetValidator.isValid("12345http://domain.com/path/", null));
	}
	
	@Test
	public void testWrongSyntaxHyperlinkKO() {
		assert (!tweetValidator.isValid("12345 http:/domain.com/path/", null));
	}

	@Test
	public void testOnlyHyperlinkKO() {
		assert (!tweetValidator.isValid("http://domain.com/path/", null));
	}

	@Test
	public void testHyperlinkLimitsOK() {
		assert (tweetValidator.isValid("12 (56 http://domain.com/path/) 0", null));
	}

	@Test
	public void testMultipleHyperlinksTooLongKO() {
		assert (!tweetValidator.isValid("http://domain.com/path/ 34 http://domain1 http://domain2 :// http://d ",
				null));
	}

}
