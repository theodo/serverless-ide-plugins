package org.globsframework.saxstack.utils;

import org.globsframework.saxstack.parser.ExceptionHolder;
import org.globsframework.saxstack.parser.XmlNode;
import org.globsframework.saxstack.writer.XmlTag;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class XmlNodeToBuilder implements XmlNode {
  private XmlTag xmlTag;

  public XmlNodeToBuilder(XmlTag xmlTag, Attributes xmlAttrs) throws IOException {
    this.xmlTag = xmlTag;
    if (xmlAttrs == null) {
      return;
    }
    int attrCount = xmlAttrs.getLength();
    Info attrs[] = new Info[attrCount];
    for (int i = 0; i < attrCount; i++) {
      attrs[i] = new Info(xmlAttrs.getLocalName(i), xmlAttrs.getValue(i));
    }
    Arrays.sort(attrs, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Info)o1).attrName.compareTo(((Info)o2).attrName);
      }
    });
    for (int i = 0; i < attrs.length; i++) {
      Info attr = attrs[i];
      xmlTag.addAttribute(attr.attrName, attr.attrValue);
    }
  }

  static class Info {

    public Info(String attrName, String attrValue) {
      this.attrName = attrName;
      this.attrValue = attrValue;
    }

    String attrName;
    String attrValue;
  }

  public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
    try {
      return new XmlNodeToBuilder(xmlTag.createChildTag(childName), xmlAttrs);
    }
    catch (IOException e) {
      throw new ExceptionHolder(e);
    }
  }

  public void setValue(String value) {
    try {
      xmlTag.addValue(value);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void complete() {
    try {
      xmlTag.end();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
