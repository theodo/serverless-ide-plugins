package org.globsframework.saxstack.parser;

import org.xml.sax.Attributes;

public class XmlBootstrapNode implements XmlNode {
  private XmlNode rootNode;
  private String rootNodeName;

  public XmlBootstrapNode(XmlNode rootNode, String rootNodeName) {
    this.rootNode = rootNode;
    this.rootNodeName = rootNodeName;
  }

  public String getCurrentTagName() {
    return null;
  }

  public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
    if (childName.equals(rootNodeName)) {
      return rootNode;
    }
    throw new XmlParsingException("Found unexpected XML root tag " + childName + " instead of " + rootNodeName);
  }

  public void setValue(String value) {
  }

  public void complete() {
  }
}
