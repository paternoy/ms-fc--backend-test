package com.scmspain.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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

	@Test
	@SuppressWarnings("unchecked")
	public void shouldListAllDiscardedTweets() throws Exception {
		TypedQuery<Long> query = mock(TypedQuery.class);
		when(entityManager.createQuery(any(String.class),eq(Long.class))).thenReturn(query);
		when(query.getResultList()).thenReturn(Arrays.asList(1L,2L,6L));
		when(entityManager.find(eq(Tweet.class),eq(1L))).thenReturn(new Tweet("John","Tweet 1",null));
		when(entityManager.find(eq(Tweet.class),eq(2L))).thenReturn(new Tweet("John","Tweet 2",null));
		when(entityManager.find(eq(Tweet.class),eq(6L))).thenReturn(new Tweet("John","Tweet 6",null));
		List<Tweet> discardedTweets = tweetService.listAllDiscardedTweets();

		verify(entityManager).createQuery(any(String.class),eq(Long.class));
		verify(entityManager,times(3)).find(eq(Tweet.class),any(Long.class));
		verify(query).getResultList();
		assertEquals(3,discardedTweets.size());
		assertEquals("Tweet 1",discardedTweets.get(0).getTweet());
		assertEquals("Tweet 2",discardedTweets.get(1).getTweet());
		assertEquals("Tweet 6",discardedTweets.get(2).getTweet());
	}
	
	@Test
	public void shouldListNoDiscardedTweets() throws Exception {
		TypedQuery query = mock(TypedQuery.class);
		when(entityManager.createQuery(any(String.class),eq(Long.class))).thenReturn(query);
		when(query.getResultList()).thenReturn(Collections.emptyList());
		List<Tweet> discardedTweets = tweetService.listAllDiscardedTweets();

		verify(entityManager).createQuery(any(String.class),eq(Long.class));
		verify(entityManager,never()).find(eq(Tweet.class),any(Long.class));
		verify(query).getResultList();
		assertEquals(0,discardedTweets.size());
	}
}
