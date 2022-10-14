package com.pbs.searcher.util;

import java.util.Arrays;

public enum Operator {
  OR("OR"),
  AND("AND");

  private String value;

  Operator(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Operator fromValue(String value) {
    return Arrays.stream(Operator.values())
        .filter(estat -> estat.getValue().equalsIgnoreCase(value))
        .findAny()
        .orElse(null);
  }
}
