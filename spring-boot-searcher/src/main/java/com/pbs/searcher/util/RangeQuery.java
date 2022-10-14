package com.pbs.searcher.util;

import lombok.Data;


@Data
public class RangeQuery {

  private String field;

  private Object gt;

  private Object gte;

  private Object lt;

  private Object lte;

  private String format;

  private String relation;

  private String timeZone;

  private float boost;
}
