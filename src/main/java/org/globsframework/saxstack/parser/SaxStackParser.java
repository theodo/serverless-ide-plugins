package org.globsframework.saxstack.parser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class SaxStackParser extends DefaultHandler {
  private Deque<XmlNode> nodesStack = new ArrayDeque<XmlNode>();
  private StringBuffer charBuffer;
  private XMLReader parser;

  private SaxStackParser(XMLReader parser) {
    this.parser = parser;
    parser.setErrorHandler(this);
    parser.setContentHandler(this);
    charBuffer = new StringBuffer();
  }

  public static void parse(XMLReader parser, XmlNode rootNode, File xmlFile) throws ExceptionHolder, IOException {
    Reader reader;
    try {
      reader = new BufferedReader(new FileReader(xmlFile));
    }
    catch (IOException e) {
      throw e;
    }
    try {
      parse(parser, rootNode, reader);
    }
    finally {
      reader.close();
    }
  }

  public static void parse(XMLReader parser, XmlNode rootNode, Reader reader) throws ExceptionHolder {
    SaxStackParser saxStackParser = new SaxStackParser(parser);
    saxStackParser.parseXml(rootNode, reader, null);
  }

  public static void parse(XMLReader parser, XmlNode rootNode, Reader reader, EntityResolver entityResolver) throws ExceptionHolder {
    SaxStackParser saxStackParser = new SaxStackParser(parser);
    saxStackParser.parseXml(rootNode, reader, entityResolver);
  }

  private void parseXml(XmlNode rootNode, Reader reader, EntityResolver entityResolver) throws ExceptionHolder {
    nodesStack.clear();
    nodesStack.push(rootNode);
    try {
      if (entityResolver != null) {
        parser.setEntityResolver(entityResolver);
      }
      parser.parse(new InputSource(reader));
    }
    catch (SAXExceptionHolder e) {
      throw e.getExceptionHolder();
    }
    catch (SAXException e) {
      throw new ExceptionHolder(e);
    }
    catch (IOException e) {
      throw new ExceptionHolder(e);
    }
  }

  public void startElement(String uri, String local, String qName, Attributes atts) {
    charBuffer.setLength(0);
    nodesStack.push(nodesStack.peek().getSubNode(local, atts, uri, qName));
  }

  public void characters(char[] chars, int start, int length) throws SAXException {
    charBuffer.append(chars, start, length);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    XmlNode node = nodesStack.pop();
    if (charBuffer.length() != 0) {
      node.setValue(charBuffer.toString());
    }
    charBuffer.setLength(0);
    node.complete();
  }

  static public class SAXExceptionHolder extends SAXException {
    private ExceptionHolder exceptionHolder;

    public SAXExceptionHolder(ExceptionHolder e) {
      super(e.getInner());
      this.exceptionHolder = e;
    }

    public ExceptionHolder getExceptionHolder() {
      return exceptionHolder;
    }

  }

}