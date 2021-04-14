package org.globsframework.saxstack.writer;

import org.globsframework.saxstack.utils.XmlUtils;

import java.io.IOException;
import java.io.Writer;

public class XmlPrettyPrinter implements SaxStackBuilder {
  private int attributeCountOnLine;
  private Writer writer;

  public  XmlPrettyPrinter(Writer writer, int attributeCountOnLine) {
    this.writer = writer;
    this.attributeCountOnLine = attributeCountOnLine;
  }

  public void write(XmlRootBuilder xmlBuilder) throws IOException {
    write(xmlBuilder, new FilterNone());
  }

  public void write(XmlRootBuilder rootBuilder, Filter filter)
    throws IOException {
    XmlUtils.build(filter, rootBuilder, new PrettyPrintRootXmlTag(writer, attributeCountOnLine));
    writer.flush();
  }

  static public void write(Writer writer, XmlRootBuilder rootBuilder, Filter filter, int attributeCount) throws IOException {
    new XmlPrettyPrinter(writer, attributeCount).write(rootBuilder, filter);
    writer.flush();
  }

  static public void write(Writer writer, XmlRootBuilder rootBuilder, int attributeCount) throws IOException {
    new XmlPrettyPrinter(writer, attributeCount).write(rootBuilder);
    writer.flush();
  }

}
