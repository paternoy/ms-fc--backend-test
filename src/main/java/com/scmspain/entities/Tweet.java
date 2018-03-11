package com.scmspain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Tweet {
	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable = false)
	private String publisher;
	@Column(nullable = false)
	private String tweet;
	@Column(nullable = true)
	private Long pre2015MigrationStatus = 0L;
	/**
	 * The date the tweet was published. Allows old tweets with null Publication
	 * Date
	 */
	@Column(nullable = true)
	@JsonIgnore
	private Date publicationDate;
	@Column(nullable = false)
	@JsonIgnore
	private Boolean discarded = false;
	@Column(nullable = true)
	@JsonIgnore
	private Date discardedDate;

	public Tweet() {
		super();
	}

	public Tweet(String publisher, String tweet, Date publicationDate) {
		super();
		this.publisher = publisher;
		this.tweet = tweet;
		this.publicationDate = publicationDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public Long getPre2015MigrationStatus() {
		return pre2015MigrationStatus;
	}

	public void setPre2015MigrationStatus(Long pre2015MigrationStatus) {
		this.pre2015MigrationStatus = pre2015MigrationStatus;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Date getDiscardedDate() {
		return discardedDate;
	}

	public void setDiscardedDate(Date discardedDate) {
		this.discardedDate = discardedDate;
	}

}
