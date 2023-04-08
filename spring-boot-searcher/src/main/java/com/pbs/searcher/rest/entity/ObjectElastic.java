package com.pbs.searcher.rest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectElastic {

  private String id;

  private Object content;
}
