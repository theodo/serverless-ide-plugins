package org.globsframework.saxstack.writer;

import java.io.IOException;

/**
 * represent 0 to n xmltag.
 * It work like an iterator : hasNext is called, then if true, getNextTagName and process are called for the given
 * tag.
 * getNextTagName is call to allow this kind of Xml :
 * <root>
 * <A/>
 * <B/>
 * </root>
 * <p/>
 * Where A and B are represented by the same XmlNodeBuilder.
 */

public interface XmlNodeBuilder {

  boolean hasNext();

  String getNextTagName();

  /**
   * @param tag is the current tag. it is possible to add attribute on the xml tag : <A attr='value'/>
   *            if startChild is called the child tag must be closed before the
   * @return
   * @throws IOException
   */
  XmlNodeBuilder[] processNext(XmlTag tag) throws IOException;

  void levelDone();
}
