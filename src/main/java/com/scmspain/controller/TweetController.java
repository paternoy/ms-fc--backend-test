package com.scmspain.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scmspain.controller.command.DiscardTweetCommand;
import com.scmspain.controller.command.PublishTweetCommand;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import com.scmspain.services.exception.ServiceException;
import com.scmspain.services.exception.TweetNotFoundServiceException;

@RestController
public class TweetController {
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

	@PostMapping("/discarded")
	@ResponseStatus(CREATED)
	public void discardTweet(@RequestBody DiscardTweetCommand discardTweetCommand)
			throws TweetNotFoundServiceException {
		this.tweetService.discardTweet(discardTweetCommand.getTweet());
	}
	
	@GetMapping("/discarded")
	public List<Tweet> listAllDiscardedTweets() {
		return this.tweetService.listAllDiscardedTweets();
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
	
	@ExceptionHandler(TweetNotFoundServiceException.class)
	@ResponseStatus(NOT_FOUND)
	@ResponseBody
	public Object tweetNotFoundServiceException(ServiceException ex) {
		return new Object() {
			public String message = ex.getMessage();
			public String exceptionClass = ex.getClass().getSimpleName();
		};
	}
}
