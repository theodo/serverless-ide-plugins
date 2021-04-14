package org.globsframework.saxstack.parser;

import org.xml.sax.Attributes;

public class SilentXmlNode implements XmlNode {
  private static final String TYPE_NAME = "SilentXmlNode";
  public static final SilentXmlNode INSTANCE = new SilentXmlNode();

  private SilentXmlNode() {
  }

  public String getCurrentTagName() {
    return TYPE_NAME;
  }

  public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
    return this;
  }

  public void setValue(String value) {
  }

  public void complete() {
  }
}
