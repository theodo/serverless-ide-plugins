package org.globsframework.saxstack.writer;

import java.io.IOException;

/**
 * this class represent the root of the xml stream : it differ from XmlNodeBuilder has there can be only one root tag.
 */

public interface XmlRootBuilder {

  /**
   * @return the root tag name
   */
  String getTagName();

  /**
   * @param rootTag
   * @return the different type of noeBuilder the root can contain. The root builder do
   */
  XmlNodeBuilder[] process(XmlTag rootTag) throws IOException;
}
