package com.scmspain.services;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import com.scmspain.entities.Tweet;
import com.scmspain.services.exception.ServiceException;

public class TweetServiceTest {
	private EntityManager entityManager;
	private MetricWriter metricWriter;
	private TweetService tweetService;

	@Before
	public void setUp() throws Exception {
		this.entityManager = mock(EntityManager.class);
		this.metricWriter = mock(MetricWriter.class);

		this.tweetService = new TweetService(entityManager, metricWriter);
	}

	@Test
	public void shouldInsertANewTweet() throws Exception {
		tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");

		verify(entityManager).persist(any(Tweet.class));
	}

	@Test(expected = ServiceException.class)
	public void shouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {
		tweetService.publishTweet("Pirate",
				"LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
	}

}
