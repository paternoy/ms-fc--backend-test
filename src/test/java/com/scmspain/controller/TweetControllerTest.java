package com.scmspain.controller;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@Transactional
public class TweetControllerTest {
	@Autowired
	private WebApplicationContext context;
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Before
	public void setUp() {
		this.mockMvc = webAppContextSetup(this.context).build();
	}

	@Test
	public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
		mockMvc.perform(newTweet("Prospect", "Breaking the law")).andExpect(status().is(201));
	}

	@Test
	public void shouldReturn200WhenInsertingAValidTweetWithLink() throws Exception {
		mockMvc.perform(newTweet("Schibsted Spain",
				"We are Schibsted Spain (look at our home page http://www.schibsted.es/), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome!"))
				.andExpect(status().is(201));
	}

	@Test
	public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
		mockMvc.perform(newTweet("Schibsted Spain",
				"We are Schibsted Spain (look at our home page http://www.schibsted.es/), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Welcome! Loonger tweeets are not welcome"))
				.andExpect(status().is(400));
	}

	@Test
	public void shouldReturnAllPublishedTweetsInOrder() throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		entityManager.persist(new Tweet("John", "Tweet 1", null));
		entityManager.persist(new Tweet("John", "Tweet 2", null));
		entityManager.persist(new Tweet("John", "Tweet 4", simpleDateFormat.parse("01-01-2018 9:00:00")));
		entityManager.persist(new Tweet("John", "Tweet 3", simpleDateFormat.parse("01-01-2018 8:00:00")));

		mockMvc.perform(get("/tweet")).andExpect(status().is(200)).andDo(print()).andExpect(jsonPath("$", hasSize(4)))
				.andExpect(jsonPath("$[0].tweet", is("Tweet 4"))).andExpect(jsonPath("$[1].tweet", is("Tweet 3")))
				.andExpect(jsonPath("$[2].tweet", is("Tweet 2"))).andExpect(jsonPath("$[3].tweet", is("Tweet 1")))
				.andReturn();

	}

	@Test
	public void shouldReturnExcludeDiscardedTweets() throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		entityManager.persist(new Tweet("John", "Tweet 1", null));
		entityManager.persist(new Tweet("John", "Tweet 2", null));
		entityManager.persist(new Tweet("John", "Tweet 4", simpleDateFormat.parse("01-01-2018 9:00:00")));
		entityManager.persist(new Tweet("John", "Tweet 3", simpleDateFormat.parse("01-01-2018 8:00:00")));
		Tweet discarded = new Tweet("John", "Tweet discarded", simpleDateFormat.parse("01-01-2018 8:00:00"));
		discarded.setDiscarded(true);
		entityManager.persist(discarded);

		mockMvc.perform(get("/tweet")).andExpect(status().is(200)).andExpect(jsonPath("$", hasSize(4)))
				.andExpect(jsonPath("$[0].tweet", is("Tweet 4"))).andExpect(jsonPath("$[1].tweet", is("Tweet 3")))
				.andExpect(jsonPath("$[2].tweet", is("Tweet 2"))).andExpect(jsonPath("$[3].tweet", is("Tweet 1")))
				.andReturn();

	}

	@Test
	public void shouldReturnAllDiscardedTweetsInOrder() throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		entityManager.persist(new Tweet("John", "Tweet 1", simpleDateFormat.parse("01-01-2018 4:00:00")));
		Tweet tweet2 = new Tweet("John", "Tweet 2", simpleDateFormat.parse("01-01-2018 6:00:00"));
		tweet2.setDiscarded(true);
		tweet2.setDiscardedDate(simpleDateFormat.parse("03-01-2018 00:00:00"));
		entityManager.persist(tweet2);
		Tweet tweet3 = new Tweet("John", "Tweet 3", simpleDateFormat.parse("01-01-2018 6:00:00"));
		tweet3.setDiscarded(true);
		tweet3.setDiscardedDate(simpleDateFormat.parse("02-01-2018 00:00:00"));
		entityManager.persist(tweet3);

		mockMvc.perform(get("/discarded")).andExpect(status().is(200)).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].tweet", is("Tweet 2"))).andExpect(jsonPath("$[1].tweet", is("Tweet 3")))
				.andReturn();

	}

	@Test
	public void shouldReturn200WhenDiscardingAValidTweet() throws Exception {
		Tweet tweet = new Tweet("John", "Tweet 1", null);
		entityManager.persist(tweet);
		Long tweetId = tweet.getId();

		mockMvc.perform(post("/discarded").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(format("{\"tweet\": \"%d\"}", tweetId)))
				.andExpect(status().is(200));
	}
	
	@Test
	public void shouldReturn400WhenOmitingTweetId() throws Exception {
		mockMvc.perform(post("/discarded").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{\"tweet\": \"\"}"))
				.andExpect(status().is(400));
	}
	
	@Test
	public void shouldReturn404WhenWrongTweetId() throws Exception {
		mockMvc.perform(post("/discarded").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{\"tweet\": \"8\"}"))
				.andExpect(status().is(404));
	}

	private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
		return post("/tweet").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
	}

}
