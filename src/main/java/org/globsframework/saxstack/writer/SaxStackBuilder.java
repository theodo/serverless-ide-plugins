package org.globsframework.saxstack.writer;

import java.io.IOException;


public interface SaxStackBuilder {

  void write(XmlRootBuilder xmlBuilder) throws IOException;

  void write(XmlRootBuilder rootBuilder, Filter filter) throws IOException;
}
