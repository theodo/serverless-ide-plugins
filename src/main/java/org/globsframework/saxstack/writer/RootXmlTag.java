package org.globsframework.saxstack.writer;

import java.io.IOException;
import java.io.Writer;

public class RootXmlTag extends AbstractRootXmlTag {
  private Writer writer;

  public RootXmlTag(Writer writer) {
    this.writer = writer;
  }

  public XmlTag createChildTag(String tagName) throws IOException {
    writer.write('<');
    writer.write(tagName);
    return new WriterXmlTag(writer, null, tagName);
  }

}
