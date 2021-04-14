package org.globsframework.saxstack.comparator;

import org.globsframework.saxstack.parser.SaxStackParser;
import org.globsframework.saxstack.parser.XmlNode;
import org.globsframework.saxstack.utils.BootstrapDomXmlNode;
import org.globsframework.saxstack.utils.NodeType;
import org.globsframework.saxstack.utils.NodeTypeFilter;
import org.globsframework.saxstack.utils.XmlUtils;
import org.globsframework.saxstack.writer.SaxStackWriter;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import java.io.*;
import java.util.*;

/**
 * Performs comparisons between XML streams, using several diff policies.
 * Three policies are available:
 * <ul>
 * <li>The {@link #areEqual} methods check that the two streams are strictly identical, with tags in the same order</li>
 * <li>The {@link #areEquivalent} methods check that the two streams are identical but without taking the order
 * of tags into account</li>
 * <li>The {@link #computeDiff} method performs a comparison in which the expected stream is used
 * as a reference for identifying which XML attributes are relevant for the comparison.</li>
 * </ul>
 */
public class XmlComparator {
  public static final String SPECIAL_CHARS = "< > ' \" &";
  public static final String ESCAPED_SPECIAL_CHARS = "&lt; &gt; &apos; &quot; &amp;";

  private XmlComparator() {
    // Static class
  }

  public static boolean areEquivalent(String expectedXml, String resultXml, XMLReader parser) throws Exception {
    if (expectedXml.equals(resultXml)) {
      return true;
    }
    XmlComparableNode aNode = createXmlEquivalentComparableNode(new StringReader(expectedXml), parser);
    XmlComparableNode bNode = createXmlEquivalentComparableNode(new StringReader(resultXml), parser);
    return aNode.equals(bNode);
  }

  /**
   * Represents the result of a comparison in which the expected stream is used as a filter
   * for identifying which XML attributes are relevant.
   */
  public static class Diff {
    private String expected;
    private String filteredActual;

    public Diff(String expected, String filteredActual) {
      this.filteredActual = filteredActual;
      this.expected = expected;
    }

    /**
     * Returns the actual stream filtered to show only attributes which are present in the XML stream
     */
    public String getFilteredActual() {
      return filteredActual;
    }

    public String getExpected() {
      return expected;
    }
  }

  public static Diff computeDiff(String expectedXml, String actualXml, XMLReader parser) throws Exception {
    if (expectedXml.equals(actualXml)) {
      return null;
    }
    BootstrapDomXmlNode expectedBootstrap = new BootstrapDomXmlNode();
    BootstrapDomXmlNode actualBootstrap = new BootstrapDomXmlNode();
    SaxStackParser.parse(parser, expectedBootstrap, new StringReader(expectedXml));
    SaxStackParser.parse(parser, actualBootstrap, new StringReader(actualXml));
    if (!expectedBootstrap.contains(actualBootstrap)) {
      NodeType bootstrapNodeType = new NodeType("", Collections.EMPTY_LIST);
      expectedBootstrap.getChild().populateNodeType(bootstrapNodeType);
      StringWriter writer = new StringWriter();
      SaxStackWriter.write(writer, actualBootstrap.getChild().getBuilder(), new NodeTypeFilter(bootstrapNodeType));
      return new Diff(expectedXml, writer.toString());
    }
    return null;
  }

  public static boolean areEquivalent(Reader xmlA, Reader xmlB, XMLReader parser) throws Exception {
    return areEquivalent(getReader(xmlA), getReader(xmlB), parser);
  }

  public static boolean areEqual(String xmlA, String xmlB, XMLReader parser) throws Exception {
    XmlComparableNode aNode = createXmlEqualComparableNode(new StringReader(xmlA), parser);
    XmlComparableNode bNode = createXmlEqualComparableNode(new StringReader(xmlB), parser);
    return aNode.equals(bNode);
  }

  public static boolean areEqual(Reader xmlA, Reader xmlB, XMLReader parser) throws Exception {
    return createXmlEqualComparableNode(xmlA, parser).equals(createXmlEqualComparableNode(xmlB, parser));
  }

  static String getReader(Reader reader) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    BufferedReader bufferedReaderA = new BufferedReader(reader);
    String readingString = bufferedReaderA.readLine();
    while (readingString != null) {
      stringBuffer.append(readingString);
      readingString = bufferedReaderA.readLine();
    }
    return stringBuffer.toString();
  }

  private static XmlComparableNode createXmlEqualComparableNode(Reader reader, XMLReader parser) throws Exception {
    XmlEqualComparator comparableNode = new XmlEqualComparator();
    SaxStackParser.parse(parser, comparableNode, reader);
    return comparableNode;
  }

  private static XmlComparableNode createXmlEquivalentComparableNode(Reader reader, XMLReader parser) throws Exception {
    XmlEquivalentComparator comparableNode = new XmlEquivalentComparator();
    SaxStackParser.parse(parser, comparableNode, reader);
    return comparableNode;
  }

  static abstract class XmlComparableNode implements XmlNode {
    protected String tag = "";
    protected Map attributes;
    protected Map childrenOccurences = new HashMap();
    protected List children = new ArrayList();
    protected String text = "";

    protected abstract boolean comparator(Object o);

    public XmlComparableNode(String tag, Attributes attributes) {
      this.tag = tag;
      this.attributes = map(attributes);
    }

    public String toString() {
      if ("root".equals(tag)) {
        return childrenString();
      }
      else if ((childrenOccurences.size() > 0) || (text.length() > 0)) {
        return "<" + tag + attributeString() + ">" + childrenString() + text + "</" + tag + ">";
      }
      else {
        return "<" + tag + attributeString() + "/>";
      }
    }

    public String getCurrentTagName() {
      return null;
    }

    public void setValue(String value) {
      text = XmlUtils.convertEntities(value
        .replaceAll("\n", "")
        .replaceAll(XmlUtils.LINE_SEPARATOR, "")
        .replaceAll("[ ]+", " ")
        .trim());
    }

    public void complete() {
    }

    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof XmlComparableNode)) {
        return false;
      }
      final XmlComparableNode comparableXml = (XmlComparableNode)o;

      if (attributes != null ? !attributes.equals(comparableXml.attributes) : comparableXml.attributes != null) {
        return false;
      }
      if (tag != null ? !tag.equals(comparableXml.tag) : comparableXml.tag != null) {
        return false;
      }
      if (text != null ? !text.equals(comparableXml.text) : comparableXml.text != null) {
        return false;
      }

      return comparator(o);
    }

    public int hashCode() {
      int result = tag.hashCode();
      result = 31 * result + attributes.hashCode();
      return result;
    }

    private Map map(Attributes xmlAttrs) {
      Map map = new HashMap();
      if (xmlAttrs == null) {
        return map;
      }
      for (int i = 0; i < xmlAttrs.getLength(); i++) {
        String localName = xmlAttrs.getLocalName(i);
        map.put(localName, xmlAttrs.getValue(i));
      }
      return map;
    }

    protected void addChild(XmlComparableNode child) {
      int val = 1;
      if (childrenOccurences.containsKey(child)) {
        Object occurence = childrenOccurences.get(child);
        if (occurence != null) {
          val = ((Integer)occurence).intValue() + 1;
        }
      }
      childrenOccurences.put(child, IntegerPool.get(val));
      children.add(child);
    }

    private String childrenString() {
      StringBuffer sb = new StringBuffer();
      for (Iterator iterator = childrenOccurences.keySet().iterator(); iterator.hasNext();) {
        XmlComparableNode node = (XmlComparableNode)iterator.next();
        sb.append(node);
        sb.append("\n");
      }
      return sb.toString();
    }

    private String attributeString() {
      StringBuffer sb = new StringBuffer();
      for (Iterator iterator = attributes.entrySet().iterator(); iterator.hasNext();) {
        Map.Entry e = (Map.Entry)iterator.next();
        sb.append(" ").append(e.getKey()).append("=\"").append(e.getValue()).append('"');
      }
      return sb.toString();
    }
  }

  static class XmlEquivalentComparator extends XmlComparableNode {
    public XmlEquivalentComparator(String tag, Attributes attributes) {
      super(tag, attributes);
    }

    public XmlEquivalentComparator() {
      super("root", null);
    }

    public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
      XmlComparableNode o = new XmlEquivalentComparator(childName, xmlAttrs);
      addChild(o);
      return o;
    }

    protected boolean comparator(Object o) {
      final XmlComparableNode comparableXml = (XmlComparableNode)o;
      return childrenOccurences != null ?
             childrenOccurences.equals(comparableXml.childrenOccurences)
             : comparableXml.childrenOccurences == null;
    }
  }

  static class XmlEqualComparator extends XmlComparableNode {
    public XmlEqualComparator(String tag, Attributes attributes) {
      super(tag, attributes);
    }

    public XmlEqualComparator() {
      super("root", null);
    }

    public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) {
      XmlComparableNode o = new XmlEqualComparator(childName, xmlAttrs);
      addChild(o);
      return o;
    }

    protected boolean comparator(Object o) {
      final XmlComparableNode comparableXml = (XmlComparableNode)o;
      return children != null ? children.equals(comparableXml.children) : (comparableXml.children == null);
    }
  }

  static class IntegerPool {
    static Integer pool[] = new Integer[0];

    private static Integer[] alloc(int val) {
      if (pool.length < val) {
        Integer tmp[] = pool;
        pool = new Integer[val + 10];
        System.arraycopy(tmp, 0, pool, 0, tmp.length);
        for (int i = tmp.length; i < pool.length; i++) {
          pool[i] = new Integer(i);
        }
      }
      return pool;
    }

    public static Object get(int val) {
      alloc(val);
      return pool[val];
    }
  }

}
