package com.scmspain.controller;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.hamcrest.Matchers.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
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
		mockMvc.perform(newTweet("Prospect", "Breaking the law"))
				.andExpect(status().is(201));
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
		entityManager.persist(new Tweet("John","Tweet 1",null));
		entityManager.persist(new Tweet("John","Tweet 2",null));
		entityManager.persist(new Tweet("John","Tweet 4",simpleDateFormat.parse("01-01-2018 9:00:00")));
		entityManager.persist(new Tweet("John","Tweet 3",simpleDateFormat.parse("01-01-2018 8:00:00")));
		
		mockMvc.perform(get("/tweet"))
				.andExpect(status().is(200)).andDo(print())
				.andExpect(jsonPath("$",hasSize(4)))
				.andExpect(jsonPath("$[0].tweet", is("Tweet 4")))
				.andExpect(jsonPath("$[1].tweet", is("Tweet 3")))
				.andExpect(jsonPath("$[2].tweet", is("Tweet 2")))
				.andExpect(jsonPath("$[3].tweet", is("Tweet 1")))
				.andReturn();

	}
	

	private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
		return post("/tweet")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
	}

}
