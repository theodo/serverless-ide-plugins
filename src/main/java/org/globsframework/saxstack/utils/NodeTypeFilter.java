package org.globsframework.saxstack.utils;

import org.globsframework.saxstack.writer.Filter;
import org.globsframework.saxstack.writer.XmlTag;

import java.io.IOException;
import java.util.Collections;
import java.util.Stack;

public class NodeTypeFilter implements Filter {
  Stack nodeType = new Stack();
  public static final NodeType NULL_NODE_TYPE = new NodeType("", Collections.EMPTY_LIST);

  public NodeTypeFilter(NodeType root) {
    nodeType.push(root);
  }

  public XmlTag enter(XmlTag parent, String tagName) throws IOException {
    NodeType current = (NodeType)this.nodeType.peek();
    NodeType child = current.findChild(tagName);
    if (child == null) {
      nodeType.push(NULL_NODE_TYPE);
      return parent.createChildTag(tagName);
    }
    nodeType.push(child);
    return new FilteredXmlTag(parent, parent.createChildTag(child.getName()), child);
  }

  public void leave() {
    nodeType.pop();
  }

  static public class FilteredXmlTag extends XmlTag {
    private XmlTag tag;
    private NodeType nodeType;
    private XmlTag parentTag;

    public FilteredXmlTag(XmlTag parentTag, XmlTag tag, NodeType nodeType) {
      this.parentTag = parentTag;
      this.tag = tag;
      this.nodeType = nodeType;
    }

    public String getTagName() {
      return tag.getTagName();
    }

    public XmlTag addAttribute(String attrName, Object attrValue) throws IOException {
      if (nodeType.containAttr(attrName)) {
        tag.addAttribute(attrName, attrValue);
      }
      return this;
    }

    public XmlTag addValue(String value) throws IOException {
      tag.addValue(value);
      return this;
    }

    public XmlTag addCDataValue(String value) throws IOException {
      tag.addCDataValue(value);
      return this;
    }

    public XmlTag createChildTag(String tagName) throws IOException {
      return tag.createChildTag(tagName);
    }

    public XmlTag end() throws IOException {
      tag.end();
      return parentTag;
    }

    public XmlTag addXmlSubtree(String xml) throws IOException {
      tag.addXmlSubtree(xml);
      return this;
    }
  }
}
