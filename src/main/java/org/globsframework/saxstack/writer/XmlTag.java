package org.globsframework.saxstack.writer;

import java.io.IOException;

public abstract class XmlTag {
  public abstract String getTagName();

  /**
   * Adds an attribute value, or nothing if the given object is null
   */
  public abstract XmlTag addAttribute(String attrName, Object attrValue) throws IOException;

  public XmlTag addAttribute(String attrName, int attrValue) throws IOException {
    return addAttribute(attrName, Integer.toString(attrValue));
  }

  public XmlTag addAttribute(String attrName, double attrValue) throws IOException {
    return addAttribute(attrName, Double.toString(attrValue));
  }

  public XmlTag addAttribute(String attrName, long attrValue) throws IOException {
    return addAttribute(attrName, Long.toString(attrValue));
  }

  public XmlTag addAttribute(String attrName, float attrValue) throws IOException {
    return addAttribute(attrName, Float.toString(attrValue));
  }

  public abstract XmlTag addValue(String value) throws IOException;

  public abstract XmlTag addCDataValue(String value) throws IOException;

  public abstract XmlTag createChildTag(String tagName) throws IOException;

  public abstract XmlTag end() throws IOException;

  public abstract XmlTag addXmlSubtree(String xml) throws IOException;
}
