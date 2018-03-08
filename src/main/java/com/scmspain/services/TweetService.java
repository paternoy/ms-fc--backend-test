package com.scmspain.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import com.scmspain.entities.Tweet;
import com.scmspain.services.exception.ServiceException;
import com.scmspain.services.validation.TweetValidator;

@Service
@Transactional
public class TweetService {
	private EntityManager entityManager;
	private MetricWriter metricWriter;
	private TweetValidator tweetValidator;

	public TweetService(EntityManager entityManager, MetricWriter metricWriter) {
		this.entityManager = entityManager;
		this.metricWriter = metricWriter;
		this.tweetValidator = new TweetValidator();
	}

	public TweetService(EntityManager entityManager, MetricWriter metricWriter, TweetValidator tweetValidator) {
		this.entityManager = entityManager;
		this.metricWriter = metricWriter;
		this.tweetValidator = tweetValidator;
	}

	/**
	 * Push tweet to repository Parameter - publisher - creator of the Tweet
	 * Parameter - text - Content of the Tweet Result - recovered Tweet
	 * 
	 * @throws BindException
	 */
	public void publishTweet(String publisher, String text) throws ServiceException {
		if (StringUtils.isEmpty(publisher))
			throw new ServiceException("Tweet publisher cannot be empty");
		if (StringUtils.isEmpty(text))
			throw new ServiceException("Tweet content cannot be empty");
		if (!tweetValidator.isValid(text, null))
			throw new ServiceException("Tweet content exceeds maximum length");
		
		Tweet tweet = new Tweet();
		tweet.setTweet(text);
		tweet.setPublisher(publisher);
		tweet.setPublicationDate(Calendar.getInstance().getTime());		
		this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
		this.entityManager.persist(tweet);

	}

	/**
	 * Recover tweet from repository Parameter - id - id of the Tweet to retrieve
	 * Result - retrieved Tweet
	 */
	public Tweet getTweet(Long id) {
		return this.entityManager.find(Tweet.class, id);
	}

	/**
	 * Recover tweet from repository Parameter - id - id of the Tweet to retrieve
	 * Result - retrieved Tweet
	 */
	public List<Tweet> listAllTweets() {
		List<Tweet> result = new ArrayList<Tweet>();
		this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
		TypedQuery<Long> query = this.entityManager.createQuery(
				"SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 ORDER BY publicationDate DESC,id DESC",
				Long.class);
		List<Long> ids = query.getResultList();
		for (Long id : ids) {
			result.add(getTweet(id));
		}
		return result;
	}

}
