package org.globsframework.saxstack.writer;

import java.io.IOException;

public interface Filter {
  XmlTag enter(XmlTag parent, String tagName) throws IOException;

  void leave();
}
