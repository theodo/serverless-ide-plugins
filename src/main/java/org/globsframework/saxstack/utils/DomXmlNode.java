package org.globsframework.saxstack.utils;

import org.globsframework.saxstack.parser.ExceptionHolder;
import org.globsframework.saxstack.parser.XmlNode;
import org.globsframework.saxstack.writer.XmlNodeBuilder;
import org.globsframework.saxstack.writer.XmlRootBuilder;
import org.globsframework.saxstack.writer.XmlTag;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.util.*;

public class DomXmlNode implements XmlNode {
   private Map attrs = new TreeMap();
   private List children = new ArrayList();
   private String text = "";
   private String xmlTagName = "";

   public DomXmlNode(String xmlTagName, Attributes xmlAttrs) {
      this.xmlTagName = xmlTagName;
      for (int i = 0; i < xmlAttrs.getLength(); i++) {
         attrs.put(xmlAttrs.getLocalName(i), xmlAttrs.getValue(i));
      }
   }

   public DomXmlNode() {
   }

   public XmlNode getSubNode(String childName, Attributes xmlAttrs, String uri, String fullName) throws ExceptionHolder {
      DomXmlNode expectedXmlNode = new DomXmlNode(childName, xmlAttrs);
      children.add(expectedXmlNode);
      return expectedXmlNode;
   }

   public void setValue(String value) throws ExceptionHolder {
      text = XmlUtils.convertEntities(
         value.replaceAll("\n", "")
            .replaceAll(XmlUtils.LINE_SEPARATOR, "")
            .replaceAll("[ ]+", " ")
            .trim());
   }

   public void complete() throws ExceptionHolder {
   }

   public boolean contains(DomXmlNode actualRoot) {
      if (!xmlTagName.equals(actualRoot.xmlTagName)) {
         return false;
      }
      if (!text.equals(actualRoot.text)) {
         return false;
      }
      for (Iterator iterator = attrs.entrySet().iterator(); iterator.hasNext(); ) {
         Map.Entry o = (Map.Entry)iterator.next();
         if (!actualRoot.attrs.containsKey(o.getKey())) {
            return false;
         }
         if (!actualRoot.attrs.get(o.getKey()).equals(o.getValue())) {
            return false;
         }
      }
      if (children.size() != actualRoot.children.size()) {
         return false;
      }

      List tempList = new ArrayList(actualRoot.children);
      for (Iterator iterator = children.iterator(); iterator.hasNext(); ) {
         DomXmlNode expected = (DomXmlNode)iterator.next();
         for (Iterator actualIterator = tempList.iterator(); actualIterator.hasNext(); ) {
            DomXmlNode actual = (DomXmlNode)actualIterator.next();
            if (expected.contains(actual)) {
               actualIterator.remove();
               break;
            }
         }
      }
      return tempList.isEmpty();
   }

   public XmlRootBuilder getBuilder() {
      return new XmlRootBuilder() {
         public String getTagName() {
            return DomXmlNode.this.xmlTagName;
         }

         public XmlNodeBuilder[] process(XmlTag rootTag) throws IOException {
            DomXmlNodeBuilder.dumpAttributes(rootTag, DomXmlNode.this.attrs);
            Iterator children = DomXmlNode.this.children.iterator();
            return new XmlNodeBuilder[]{
               new DomXmlNodeBuilder(children)
            };
         }
      };
   }

   private static class DomXmlNodeBuilder implements XmlNodeBuilder {
      private final Iterator children;
      private DomXmlNode child;

      public DomXmlNodeBuilder(Iterator children) {
         this.children = children;
      }

      public boolean hasNext() {
         return children.hasNext();
      }

      public String getNextTagName() {
         child = (DomXmlNode)children.next();
         return child.xmlTagName;
      }

      public XmlNodeBuilder[] processNext(XmlTag tag) throws IOException {
         dumpAttributes(tag, child.attrs);
         return new XmlNodeBuilder[]{
            new DomXmlNodeBuilder(child.children.iterator())
         };
      }

      public void levelDone() {

      }

      static private void dumpAttributes(XmlTag tag, Map attrs) throws IOException {
         Collection collection = attrs.entrySet();
         for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            tag.addAttribute((String)entry.getKey(), (String)entry.getValue());
         }
      }
   }

   public void populateNodeType(NodeType parent) {
      NodeType child = parent.getOrCreate(xmlTagName, this.attrs.keySet());
      for (Iterator iterator = children.iterator(); iterator.hasNext(); ) {
         DomXmlNode node = (DomXmlNode)iterator.next();
         node.populateNodeType(child);
      }
   }
}
