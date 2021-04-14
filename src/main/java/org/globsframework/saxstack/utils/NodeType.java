package org.globsframework.saxstack.utils;

import java.util.*;

public class NodeType {
  private String name;
  private Map children = new HashMap();
  private List attrs;

  public NodeType(String name, Collection attrs) {
    this.name = name;
    this.attrs = new ArrayList(attrs);
  }

  public String getName() {
    return name;
  }

  NodeType getChild(String name) {
    return (NodeType)children.get(name);
  }

  public NodeType getOrCreate(String name, Collection attrs) {
    if (children.containsKey(name)) {
      NodeType type = ((NodeType)children.get(name));
      type.check(attrs);
      return type;
    }
    NodeType type = new NodeType(name, attrs);
    children.put(name, type);
    return type;
  }

  private void check(Collection attrs) {
    for (Iterator iterator = attrs.iterator(); iterator.hasNext();) {
      String attr = (String)iterator.next();
      if (!this.attrs.contains(attr)) {
        this.attrs.add(attr);
      }
    }
  }

  public NodeType findChild(String name) {
    if (children.containsKey(name)) {
      return (NodeType)children.get(name);
    }
    return null;
  }

  public boolean containAttr(String name) {
    return attrs.contains(name);
  }
}
