package org.globsframework.saxstack.parser;

import org.xml.sax.Attributes;

public interface XmlNode {

  XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) throws ExceptionHolder;

  void setValue(String value) throws ExceptionHolder;

  void complete() throws ExceptionHolder;
}
