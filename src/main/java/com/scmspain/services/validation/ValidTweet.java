package com.scmspain.services.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = TweetValidator.class)
@Target({ TYPE, FIELD })
@Retention(RUNTIME)
public @interface ValidTweet {

	String message() default "Tweet must not be greater than 140 characters";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}