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

package com.altran.galileu.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class Config {

  @Value("${elasticsearch.host}")
  protected String elasticsearchHost;

  @Value("${elasticsearch.user}")
  protected String elasticsearchUser;

  @Value("${elasticsearch.password}")
  protected String elasticsearchPassword;

  @Value("${elasticsearch.port}")
  protected String elasticsearchPort;

  @Value("${elasticsearch.ssl}")
  protected boolean elasticsearchSsl;

  @Value("${elasticsearch.client.timeout}")
  protected int elasticsearchClientTimeout;

  @Value("${elasticsearch.socket.timeout}")
  protected int elasticsearchSocketTimeout;

  @Bean(destroyMethod = "close")
  public RestHighLevelClient client() {

    RestClientBuilder restClientBuilder =
        RestClient.builder(
                new HttpHost(
                    elasticsearchHost,
                    (elasticsearchPort != null && !elasticsearchPort.isEmpty())
                        ? Integer.parseInt(elasticsearchPort)
                        : Constants.DEFAULT_PORT,
                    elasticsearchSsl ? Constants.HTTPS : Constants.HTTP))
            .setRequestConfigCallback(
                requestConfigBuilder ->
                    requestConfigBuilder
                        .setConnectTimeout(elasticsearchClientTimeout)
                        .setSocketTimeout(elasticsearchSocketTimeout));

    if (!StringUtils.isEmpty(elasticsearchUser)) {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(
          AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchUser, elasticsearchPassword));
      restClientBuilder.setHttpClientConfigCallback(
          httpClientBuilder ->
              httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    }

    return new RestHighLevelClient(restClientBuilder);
  }

  @Bean
  ElasticsearchRestTemplate elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(client());
  }
}
