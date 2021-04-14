package org.globsframework.saxstack.writer;

import java.io.IOException;

public class FilterNone implements Filter {
  public XmlTag enter(XmlTag parent, String tagName) throws IOException {
    return parent.createChildTag(tagName);
  }

  public void leave() {
  }
}
