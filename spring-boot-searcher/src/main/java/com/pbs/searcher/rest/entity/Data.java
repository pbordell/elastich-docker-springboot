/*
 * Copyright (c) 2016 Generalitat de Catalunya.
 *
 * The contents of this file may be used under the terms of the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on
 * an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 *
 * Original authors: Centre de Suport Canigó Contact: oficina-tecnica.canigo.ctti@gencat.cat
 */

package com.altran.galileu.rest.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Data<E> implements Serializable {

  private static final long serialVersionUID = -7766018149936610764L;
  private long total;
  private long limit;
  private long numberOfElements;
  private long offset;
  private transient List<E> included = new ArrayList(0);

  public Data() {
  }

  public Data(long total, long limit, long numberOfElements, long offset, List<E> included) {
    this.total = total;
    this.limit = limit;
    this.numberOfElements = numberOfElements;
    this.offset = offset;
    this.included = included;
  }

}
