package org.globsframework.saxstack.writer;

import java.io.IOException;
import java.io.Writer;

public class PrettyPrintRootXmlTag extends AbstractRootXmlTag {
  private Writer writer;
  private int attributeCountOnLine;

  public PrettyPrintRootXmlTag(Writer writer, int attributeCountOnLine) {
    this.writer = writer;
    this.attributeCountOnLine = attributeCountOnLine;
  }

  public XmlTag createChildTag(String rootTag) throws IOException {
    writer.write('<');
    writer.write(rootTag);
    return new PrettyPrintXmlTag(writer, null, rootTag, 0, attributeCountOnLine);
  }
}
