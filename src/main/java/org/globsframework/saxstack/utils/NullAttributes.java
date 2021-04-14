package org.globsframework.saxstack.utils;

import org.xml.sax.Attributes;

/**
 * Empty implementation of the XML {@link Attributes} interface
 */
public class NullAttributes implements Attributes {
  public int getLength() {
    return 0;
  }

  public String getLocalName(int index) {
    return null;
  }

  public String getQName(int index) {
    return null;
  }

  public String getType(int index) {
    return null;
  }

  public String getURI(int index) {
    return null;
  }

  public String getValue(int index) {
    return null;
  }

  public int getIndex(String qName) {
    return 0;
  }

  public String getType(String qName) {
    return null;
  }

  public String getValue(String qName) {
    return null;
  }

  public int getIndex(String uri, String localName) {
    return 0;
  }

  public String getType(String uri, String localName) {
    return null;
  }

  public String getValue(String uri, String localName) {
    return null;
  }
}
