package org.globsframework.saxstack.parser;

public class ExceptionHolder extends RuntimeException {
  public ExceptionHolder(Exception cause) {
    super(cause);
  }

  public Exception getInner() {
    return (Exception)getCause();
  }
}
