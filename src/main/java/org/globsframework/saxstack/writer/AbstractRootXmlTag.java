package org.globsframework.saxstack.writer;

import java.io.IOException;

public abstract class AbstractRootXmlTag extends XmlTag {
  public String getTagName() {
    throw new XmlWriterException();
  }

  public XmlTag addAttribute(String attrName, Object attrValue) throws IOException {
    throw new XmlWriterException();
  }

  public XmlTag addValue(String value) throws IOException {
    throw new XmlWriterException();
  }

  public XmlTag addCDataValue(String value) throws IOException {
    throw new XmlWriterException();
  }

  public XmlTag end() throws IOException {
    throw new XmlWriterException();
  }

  public XmlTag addXmlSubtree(String xml) throws IOException {
    throw new XmlWriterException();
  }
}
