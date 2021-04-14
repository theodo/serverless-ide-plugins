package org.globsframework.saxstack.parser;

public class XmlParsingException extends RuntimeException {
  public XmlParsingException(String message) {
    super(message);
  }

  public XmlParsingException(Throwable t) {
    super(t);
  }
}
