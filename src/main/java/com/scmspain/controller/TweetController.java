package com.scmspain.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scmspain.controller.command.PublishTweetCommand;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import com.scmspain.services.exception.ServiceException;

@RestController
public class TweetController {

	private static final Logger logger = LoggerFactory.getLogger(TweetController.class);
	private TweetService tweetService;

	public TweetController(TweetService tweetService) {
		this.tweetService = tweetService;
	}

	@GetMapping("/tweet")
	public List<Tweet> listAllTweets() {
		return this.tweetService.listAllTweets();
	}

	@PostMapping("/tweet")
	@ResponseStatus(CREATED)
	public void publishTweet(@Valid @RequestBody PublishTweetCommand publishTweetCommand) throws BindException {
		this.tweetService.publishTweet(publishTweetCommand.getPublisher(), publishTweetCommand.getTweet());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	public Object invalidArgumentException(IllegalArgumentException ex) {
		return new Object() {
			public String message = ex.getMessage();
			public String exceptionClass = ex.getClass().getSimpleName();
		};
	}

	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Object unexpectedServiceException(ServiceException ex) {
		return new Object() {
			public String message = ex.getMessage();
			public String exceptionClass = ex.getClass().getSimpleName();
		};
	}

}
