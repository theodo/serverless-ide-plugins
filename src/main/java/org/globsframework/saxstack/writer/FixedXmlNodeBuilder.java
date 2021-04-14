package org.globsframework.saxstack.writer;

import java.io.IOException;

public class FixedXmlNodeBuilder implements XmlNodeBuilder {

  private boolean wasProcessed = false;
  private final String tagName;
  private boolean pruneIfEmpty;
  private final XmlNodeBuilder[] children;

  public FixedXmlNodeBuilder(String tagName, XmlNodeBuilder child, boolean pruneIfEmpty) {
    this(tagName, new XmlNodeBuilder[]{child}, false);
    this.pruneIfEmpty = pruneIfEmpty;
  }

  public FixedXmlNodeBuilder(String tagName, XmlNodeBuilder[] children, boolean pruneIfEmpty) {
    this.children = children;
    this.tagName = tagName;
    this.pruneIfEmpty = pruneIfEmpty;
  }

  public boolean hasNext() {
    if (wasProcessed) {
      return false;
    }
    if (pruneIfEmpty) {
      for (int i = 0; i < children.length; i++) {
        XmlNodeBuilder child = children[i];
        if (child.hasNext()) {
          return true;
        }
      }
    }
    else {
      return true;
    }
    return false;
  }

  public String getNextTagName() {
    return tagName;
  }

  public XmlNodeBuilder[] processNext(XmlTag tag) throws IOException {
    wasProcessed = true;
    return children;
  }

  public void levelDone() {

  }
}
