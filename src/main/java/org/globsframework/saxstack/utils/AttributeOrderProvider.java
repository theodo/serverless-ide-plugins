package org.globsframework.saxstack.utils;

public interface AttributeOrderProvider {
  AttributeOrderProvider NULL_ATTRIBUTE = new AttributeOrderProvider() {
    public String getOrderedAttribute(String xmlTagName) {
      return null;
    }
  };

  String getOrderedAttribute(String xmlTagName);
}
