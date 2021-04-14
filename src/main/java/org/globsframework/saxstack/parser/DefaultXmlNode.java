package org.globsframework.saxstack.parser;

import org.xml.sax.Attributes;

public class DefaultXmlNode implements XmlNode {

  public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
    return SilentXmlNode.INSTANCE;
  }

  public void setValue(String value) {
  }

  /**
   * Default implementation does nothing.
   */
  public void complete() {
  }

}
