package com.scmspain.services.exception;

public class TweetNotFoundServiceException extends ServiceException {
	public TweetNotFoundServiceException(Long id) {
		super(String.format("No tweet was found with id %d", id));
	}
}
