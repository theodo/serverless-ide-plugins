package org.globsframework.saxstack.writer;

import java.util.Collection;
import java.util.Iterator;

public abstract class IteratorBasedXmlNodeBuilder implements XmlNodeBuilder {
  private String tagName;
  private final Iterator iterator;

  protected IteratorBasedXmlNodeBuilder(String tagName, Iterator iterator) {
    this.tagName = tagName;
    this.iterator = iterator;
  }

  protected IteratorBasedXmlNodeBuilder(String tagName, Collection collection) {
    this(tagName, collection.iterator());
  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  public String getNextTagName() {
    return tagName;
  }

  protected Object getNextItem() {
    return iterator.next();
  }
}
