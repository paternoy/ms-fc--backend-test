package com.scmspain.services;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import com.scmspain.entities.Tweet;
import com.scmspain.services.exception.ServiceException;
import com.scmspain.services.validation.TweetValidator;

public class TweetServiceTest {
	private static final String TWEET = "I am Guybrush Threepwood, mighty pirate.";
	private static final String LONG_TWEET = "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.";
	private EntityManager entityManager;
	private MetricWriter metricWriter;
	private TweetService tweetService;
	private TweetValidator tweetValidator;

	@Before
	public void setUp() throws Exception {
		this.entityManager = mock(EntityManager.class);
		this.metricWriter = mock(MetricWriter.class);
		this.tweetValidator = mock(TweetValidator.class);

		this.tweetService = new TweetService(entityManager, metricWriter, tweetValidator);
	}

	@Test
	public void shouldInsertANewTweet() throws Exception {
		when(tweetValidator.isValid(eq(TWEET), anyObject())).thenReturn(true);
		tweetService.publishTweet("Guybrush Threepwood", TWEET);

		verify(entityManager).persist(any(Tweet.class));
		verify(tweetValidator).isValid(eq(TWEET), anyObject());
	}

	@Test(expected = ServiceException.class)
	public void shouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {

		when(tweetValidator.isValid(eq(LONG_TWEET), anyObject()))
				.thenThrow(new ServiceException("Tweet content exceeds maximum length"));
		tweetService.publishTweet("Pirate", LONG_TWEET);
		verify(entityManager, never()).persist(any(Tweet.class));
		verify(tweetValidator).isValid(eq(LONG_TWEET), anyObject());
	}

}
