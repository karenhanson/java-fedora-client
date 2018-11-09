/*
 * Copyright 2018 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.pass.client.elasticsearch;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpHost;

import org.dataconservancy.pass.model.PassEntity;
import org.dataconservancy.pass.model.PassEntityType;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Communicates with elasticsearch
 * @author Karen Hanson
 */
public class ElasticsearchPassClient {


    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchPassClient.class);
    
    /**
     * Template for a search attribute e.g. AND fldname:"something"
     */
    private static final String QS_ATTRIB_TEMPLATE  = " AND %s:\"%s\"";
    
    /**
     * Template for a query string e.g. (@type:Submission AND fldname:"something")
     * where the second %s could be one or more QS_ATTRIB_TEMPLATES
     */
    private static final String QS_TEMPLATE = "(@type:%s %s)";

    private static final String EXISTS_TEMPLATE = "_exists_:%s";

    private static final String NOT_EXISTS_TEMPLATE = "-" + EXISTS_TEMPLATE;

    private static final String QS_ATTRIB_NOT_EXISTS_TEMPLATE = "AND " + NOT_EXISTS_TEMPLATE;
    
    private static final String ID_FIELDNAME = "@id";

    /**
     * URL(s) of indexer
     */
    private final HttpHost[] hosts;
        
    /** 
     * Default constructor for PASS client
     */
    public ElasticsearchPassClient() {
        Set<URL> indexerUrls = ElasticsearchConfig.getIndexerHostUrl();      
        hosts = new HttpHost[indexerUrls.size()];
        int count = 0;
        for (URL url : indexerUrls) {
            LOG.info("Connecting to index at {}", url);
            hosts[count] = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            count = count+1;
        }
        
    }
    
    /**
     * @see org.dataconservancy.pass.client.PassClient#findByAttribute(Class, String, Object)
     * 
     * @param modelClass modelClass
     * @param attribute attribute
     * @param value value
     * @return URI
     * @param <T> PASS entity type
     */
    public <T extends PassEntity> URI findByAttribute(Class<T> modelClass, String attribute, Object value) {
        validateModelParam(modelClass);
        validateAttribValParams(attribute, value, true);
                
        String indexType = null;
        
        if (PassEntityType.getTypeByName(modelClass.getSimpleName())!=null) {
            indexType = PassEntityType.getTypeByName(modelClass.getSimpleName()).getName();
        }

        String attribs = null;
        if (value != null) {
            attribs = String.format(QS_ATTRIB_TEMPLATE, attribute, value.toString());
        } else {
            attribs = String.format(QS_ATTRIB_NOT_EXISTS_TEMPLATE, attribute);
        }
        String querystring = String.format(QS_TEMPLATE, indexType, attribs);
             
        Set<URI> passEntityUris = getIndexerResults(querystring, 2, 0); //get 2 so we can check only one result matched
        if (passEntityUris.size()>1) {
            throw new RuntimeException(
                    format("More than one results was returned by this query (%s = %s). " + 
                            "findByAttribute() searches should match only one result.  Instead found:\n %s", 
                            attribute, value, 
                            join("\n", passEntityUris.stream().map(URI::toString).collect(toList()))));
        }
        URI passEntityUri = null;
        if (passEntityUris.size()>0) {
            passEntityUri = passEntityUris.iterator().next();
        }
        return passEntityUri;
    }

        
    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttribute(Class, String, Object)
     * 
     * @param modelClass modelClass
     * @param attribute attribute
     * @param value value
     * @return Set of URI
     * @param <T> PASS entity type
     */
    public <T extends PassEntity> Set<URI> findAllByAttribute(Class<T> modelClass, String attribute, Object value) {
        return findAllByAttribute(modelClass, attribute, value, ElasticsearchConfig.getIndexerLimit(), 0);
    }
    
        
    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttribute(Class, String, Object, int, int)
     * 
     * @param modelClass modelClass
     * @param attribute attribute
     * @param value value
     * @param limit limit
     * @param offset offset
     * @return Set of URI
     * @param <T> PASS entity type
     */
    public <T extends PassEntity> Set<URI> findAllByAttribute(Class<T> modelClass, String attribute, Object value, int limit, int offset) {
        validateModelParam(modelClass);
        validateAttribValParams(attribute, value, true);
        validLimitOffsetParams(limit, offset);
                
        String indexType = null;
        
        if (PassEntityType.getTypeByName(modelClass.getSimpleName())!=null) {
            indexType = PassEntityType.getTypeByName(modelClass.getSimpleName()).getName();
        }

        String attribs = null;
        if (value != null) {
            attribs = String.format(QS_ATTRIB_TEMPLATE, attribute, value.toString());
        } else {
            attribs = String.format(NOT_EXISTS_TEMPLATE, attribute);
        }
        String querystring = String.format(QS_TEMPLATE, indexType, attribs);                
        Set<URI> passEntityUris = getIndexerResults(querystring, limit, offset);
        
        return passEntityUris;
    }

    
    /**
     *  @see org.dataconservancy.pass.client.PassClient#findAllByAttributes(Class, Map)
     *  
     * @param modelClass modelClass
     * @param valueAttributesMap valueAttributesMap
     * @return Set of URI
     * @param <T> PASS entity type
     */
    public <T extends PassEntity> Set<URI> findAllByAttributes(Class<T> modelClass, Map<String, Object> valueAttributesMap) {
        return findAllByAttributes(modelClass, valueAttributesMap, ElasticsearchConfig.getIndexerLimit(), 0);
    }
    
    
    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttributes(Class, Map, int, int)
     * 
     * @param modelClass modelClass
     * @param valueAttributesMap valueAttributesMap
     * @param limit limit
     * @param offset offset
     * @return Set of URI
     * @param <T> PASS entity type
     */
    public <T extends PassEntity> Set<URI> findAllByAttributes(Class<T> modelClass, Map<String, Object> valueAttributesMap, int limit, int offset) {
        validateModelParam(modelClass);
        validateAttribMapParam(valueAttributesMap);
        validLimitOffsetParams(limit, offset);
        
        LOG.debug("Searching for {} using multiple filters", modelClass.getSimpleName());
        
        String indexType = null;
        
        if (PassEntityType.getTypeByName(modelClass.getSimpleName())!=null) {
            indexType = PassEntityType.getTypeByName(modelClass.getSimpleName()).getName();
        }
        
        StringBuilder attribs = new StringBuilder("");
        for(Entry<String,Object> attr : valueAttributesMap.entrySet()) {
            if (attr.getValue() != null) {
                attribs.append(String.format(QS_ATTRIB_TEMPLATE, attr.getKey(), attr.getValue().toString()));
            } else {
                attribs.append(String.format(QS_ATTRIB_NOT_EXISTS_TEMPLATE, attr.getKey()));
            }
        }
        String querystring = String.format(QS_TEMPLATE, indexType, attribs);
                
        Set<URI> passEntityUris = getIndexerResults(querystring, limit, offset);
        return passEntityUris;
    }
    
    
    /**
     * Retrieve search results from elasticsearch
     * @param querystring
     * @param limit
     * @param offset
     * @return
     */
    private Set<URI> getIndexerResults(String querystring, int limit, int offset) {
        
        Set<URI> passEntityUris = new HashSet<URI>();
        
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(hosts))){
            
            SearchRequest searchRequest = new SearchRequest(); 
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
            sourceBuilder.from(offset);
            sourceBuilder.size(limit);

            LOG.debug("Searching index using querystring: {}, with limit {} and offset {}", querystring,  limit, offset);
            //(content:this OR name:this)
            QueryStringQueryBuilder matchQueryBuilder = new QueryStringQueryBuilder(querystring);
                        
            matchQueryBuilder.defaultOperator(Operator.AND);
            sourceBuilder.query(matchQueryBuilder);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            Iterator<SearchHit> hitsIt = hits.iterator();
            
            while (hitsIt.hasNext()){
                String idField = hitsIt.next().getSourceAsMap().get(ID_FIELDNAME).toString();
                passEntityUris.add(new URI(idField));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Something was wrong with the record returned from the indexer. The ID could not be recognized as a URI", e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("An error occurred while processing the query: %s", querystring), e);
        }
            
        return passEntityUris;
        
    }
    
    private <T extends PassEntity> void validateAttribMapParam(Map<String,Object> valueAttributesMap) {
        if (valueAttributesMap==null || valueAttributesMap.size()==0) {throw new IllegalArgumentException("valueAttributesMap cannot be empty");}
        for (Entry<String,Object> entry : valueAttributesMap.entrySet()) {
            validateAttribValParams(entry.getKey(), entry.getValue(), true);
        }
    }
    
    private <T extends PassEntity> void validateModelParam(Class<T> modelClass) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (modelClass==PassEntity.class) {throw new IllegalArgumentException("modelClass cannot be the abstract class 'PassEntity.class'");}
    }
    
    private void validLimitOffsetParams(int limit, int offset) {
        if (offset < 0) {throw new IllegalArgumentException("The offset value cannot be less than 0");}       
        if (limit < 0) {throw new IllegalArgumentException("The limit value cannot be less than 0");}        
    }
    
    private void validateAttribValParams(String attribute, Object value, boolean allowNullValues) {
        if (attribute==null || attribute.length()==0) {throw new IllegalArgumentException("attribute cannot be null or empty");}
        if (value instanceof Collection<?>) {throw new IllegalArgumentException("Value for attribute " + attribute + " cannot be a Collection");}
        if (value==null && !allowNullValues) {throw new IllegalArgumentException("Value cannot be null or empty");}
    }
    
}
