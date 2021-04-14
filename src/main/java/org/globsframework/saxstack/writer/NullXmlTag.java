package org.globsframework.saxstack.writer;

import java.io.IOException;

public class NullXmlTag extends XmlTag {

  private String tagName;
  private XmlTag parent;

  public NullXmlTag(XmlTag parent, String tagName) {
    this.tagName = tagName;
    this.parent = parent;
  }

  public String getTagName() {
    return tagName;
  }

  public XmlTag addAttribute(String attrName, Object attrValue) throws IOException {
    return this;
  }

  public XmlTag addValue(String value) throws IOException {
    return this;
  }

  public XmlTag addCDataValue(String value) throws IOException {
    return this;
  }

  public XmlTag createChildTag(String tagName) throws IOException {
    return new NullXmlTag(this, tagName);
  }

  public XmlTag end() throws IOException {
    return parent;
  }

  public XmlTag addXmlSubtree(String xml) throws IOException {
    return this;
  }
}
