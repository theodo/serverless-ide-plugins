package org.globsframework.saxstack.writer;

import org.globsframework.saxstack.utils.XmlUtils;

import java.io.IOException;
import java.io.Writer;

// TODO: addCData

public final class WriterXmlTag extends XmlTag {
  private String tagName;
  private Writer writer;
  private XmlTag parent;
  private boolean closed = false;
  private WriterXmlTag child;

  WriterXmlTag(Writer writer, XmlTag parent, String tag) {
    this.writer = writer;
    this.parent = parent;
    init(tag);
  }

  public String getTagName() {
    return tagName;
  }

  public XmlTag addAttribute(String attrName, Object attrValue) throws IOException {
    if (attrValue == null) {
      return this;
    }
    if (closed) {
      throw new RuntimeException("Bad use of 'addAttribute' method after tag closure");
    }
    writer.write(' ');
    writer.write(attrName);
    writer.write("=\"");
    writer.write(normalize(attrValue.toString()));
    writer.write('"');
    return this;
  }

  // TODO: renvoyer une interface limit�e puisqu'on ne peut plus faire addAttribute (closeSup d�j� fait)
  public XmlTag addValue(String value) throws IOException {
    closeSup();
    writer.write(normalize(value));
    return this;
  }

  public XmlTag addCDataValue(String value) throws IOException {
    closeSup();
    writer.write(XmlUtils.addInCDataValue(value));
    return this;
  }

  public XmlTag createChildTag(String tagName) throws IOException {
    closeSup();
    writer.write("<");
    writer.write(tagName);
    if (child == null) {
      child = new WriterXmlTag(writer, this, tagName);
    }
    else {
      child.init(tagName);
    }
    return child;
  }

  public XmlTag end() throws IOException {
    if (closed) {
      writer.write("</");
      writer.write(tagName);
      writer.write(">");
    }
    else {
      writer.write("/>");
    }
    return parent;
  }

  private void init(String tagName) {
    this.tagName = tagName;
    closed = false;
  }

  private void closeSup() throws IOException {
    if (!closed) {
      closed = true;
      writer.write(">");
    }
  }

  private String normalize(String value) {
    return XmlUtils.convertEntities(value);
  }

  public XmlTag addXmlSubtree(String xml) throws IOException {
    closeSup();
    writer.write(xml);
    return this;
  }
}
