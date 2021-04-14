package org.globsframework.saxstack.writer;

import org.globsframework.saxstack.utils.XmlUtils;

import java.io.IOException;
import java.io.Writer;

// TODO: addCData

public final class PrettyPrintXmlTag extends XmlTag {
  private String tagName;
  private Writer writer;
  private XmlTag parent;
  private boolean closed = false;
  private PrettyPrintXmlTag child;
  private int level;
  private int attributeCount;
  private int attributeCountOnLine;
  private static final String EMPTY =
    "                                                                                                            ";

  PrettyPrintXmlTag(Writer writer, XmlTag parent, String tag, int level, int attributeCountOnLine) {
    this.attributeCountOnLine = attributeCountOnLine;
    this.writer = writer;
    this.parent = parent;
    init(tag, level);
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
    if (attributeCountOnLine == attributeCount) {
      writer.write('\n');
      writeSpace(level + tagName.length() + 1);
      attributeCount = 0;
    }
    else {
      attributeCount++;
    }

    writer.write(' ');
    writer.write(attrName);
    writer.write("=\"");
    writer.write(normalize(attrValue.toString()));
    writer.write('"');
    return this;
  }

  private void writeSpace(int len) throws IOException {
    while (len > EMPTY.length()) {
      writer.write(EMPTY);
      len = len - EMPTY.length();
    }
    writer.write(EMPTY, 0, len);
  }

  // TODO: renvoyer une interface limit�e puisqu'on ne peut plus faire addAttribute (closeSup d�j� fait)
  public XmlTag addValue(String value) throws IOException {
    value = value.replaceAll("\n", "").trim();
    if (value.length() != 0) {
      closeAndIndent();
      writer.write(normalize(value));
    }
    return this;
  }

  private void closeAndIndent() throws IOException {
    closeSup();
    writer.write("\n");
    writeSpace(level + 2);
  }

  public XmlTag addCDataValue(String value) throws IOException {
    closeAndIndent();
    writer.write(XmlUtils.addInCDataValue(value));
    return this;
  }

  public XmlTag createChildTag(String tagName) throws IOException {
    closeAndIndent();
    writer.write("<");
    writer.write(tagName);
    if (child == null) {
      child = new PrettyPrintXmlTag(writer, this, tagName, level + 2, attributeCountOnLine);
    }
    else {
      child.init(tagName, level + 2);
    }
    return child;
  }

  public XmlTag end() throws IOException {
    if (closed) {
      writer.write("\n");
      writeSpace(level);
      writer.write("</");
      writer.write(tagName);
      writer.write(">");
    }
    else {
      writer.write("/>");
    }
    return parent;
  }

  private void init(String tagName, int level) {
    this.level = level;
    this.tagName = tagName;
    closed = false;
    attributeCount = 0;
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
