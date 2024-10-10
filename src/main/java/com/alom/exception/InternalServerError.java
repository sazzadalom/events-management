package com.alom.exception;

/**
 *  <p> Using this exception class, we can tell user for some database related issue occurred. </p> 
 * 
 * @author sazzad
 * @version 1.0.0
 * @since 10-10-2024
 *
 */
public class InternalServerError extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public InternalServerError (String message) {
		super(message);
	}
}
