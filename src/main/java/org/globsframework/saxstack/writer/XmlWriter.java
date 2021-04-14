package org.globsframework.saxstack.writer;

import java.io.IOException;
import java.io.Writer;

public class XmlWriter {
  public static final String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>";

  public static XmlTag startTag(Writer writer, String rootTag) throws IOException {
    writer.write('<');
    writer.write(rootTag);
    return new WriterXmlTag(writer, null, rootTag);
  }

  private XmlWriter() {
  }
}
