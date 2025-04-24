package com.eeze.app.videoapi.exceptions;

/** Exception thrown when a document is not found. */
public class DataNotFound extends IllegalArgumentException {
  public DataNotFound(String message) {
    super(message);
  }
}
