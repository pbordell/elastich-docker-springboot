package com.pbs.searcher.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearcherDocument {

  private String id;

  private Object content;
}
