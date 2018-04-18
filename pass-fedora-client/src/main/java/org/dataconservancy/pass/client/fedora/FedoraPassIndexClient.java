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
package org.dataconservancy.pass.client.fedora;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
 *
 * @author Karen Hanson
 */
public class FedoraPassIndexClient {


    private static final Logger LOG = LoggerFactory.getLogger(FedoraPassIndexClient.class);
    
    /**
     * Template for a search attribute e.g. AND fldname:"something"
     */
    private static final String QS_ATTRIB_TEMPLATE  = "AND %s:\"%s\"";
    
    /**
     * Template for a query string e.g. (@type:Submission AND fldname:"something")
     * where the second %s could be one or more QS_ATTRIB_TEMPLATES
     */
    private static final String QS_TEMPLATE = "(@type:%s %s)"; 

    /**
     * URL of indexer
     */
    private HttpHost[] hosts;    
    
    public FedoraPassIndexClient() {
        Set<URL> indexerUrls = IndexerConfig.getIndexerHostUrl();      
        hosts = new HttpHost[indexerUrls.size()];
        int count = 0;
        for (URL url : indexerUrls) {
            hosts[count] = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            count = count+1;
        }
    }
    
    /**
     * @see org.dataconservancy.pass.client.PassClient#findByAttribute(Class, String, Object)
     */
    public <T extends PassEntity> URI findByAttribute(Class<T> modelClass, String attribute, Object value) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (attribute==null || attribute.length()==0) {throw new IllegalArgumentException("attribute cannot be null");}
        if (value==null) {throw new IllegalArgumentException("value cannot be null");}
        
        URI passEntityUri = null;
        
        String indexType = null;
        
        if (PassEntityType.getTypeByName(modelClass.getSimpleName())!=null) {
            indexType = PassEntityType.getTypeByName(modelClass.getSimpleName()).getName();
        }
        
        String attribs = String.format(QS_ATTRIB_TEMPLATE, attribute, value.toString());
        String querystring = String.format(QS_TEMPLATE, indexType, attribs);
                
        try {
            Iterator<SearchHit> hitsIt = getIndexerResults(querystring);
            if (hitsIt.hasNext()){
                String idField = hitsIt.next().getSourceAsMap().get("@id").toString();
                passEntityUri = new URI(idField);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Something was wrong with the record returned from the indexer. The ID could not be recognized as a URI");
        }
        return passEntityUri;
    }

    
    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttribute(Class, String, Object)
     */
    public <T extends PassEntity> Set<URI> findAllByAttribute(Class<T> modelClass, String attribute, Object value) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (attribute==null || attribute.length()==0) {throw new IllegalArgumentException("attribute cannot be null");}
        if (value==null) {throw new IllegalArgumentException("value cannot be null");}
        
        Set<URI> passEntityUris = new HashSet<URI>();
        
        String indexType = null;
        
        if (PassEntityType.getTypeByName(modelClass.getSimpleName())!=null) {
            indexType = PassEntityType.getTypeByName(modelClass.getSimpleName()).getName();
        }
        
        String attribs = String.format(QS_ATTRIB_TEMPLATE, attribute, value.toString());
        String querystring = String.format(QS_TEMPLATE, indexType, attribs);
                
        try {
            Iterator<SearchHit> hitsIt = getIndexerResults(querystring);
            while (hitsIt.hasNext()){
                String idField = hitsIt.next().getSourceAsMap().get("@id").toString();
                passEntityUris.add(new URI(idField));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Something was wrong with the record returned from the indexer. The ID could not be recognized as a URI");
        }
        return passEntityUris;
    }


    /**
     * @see org.dataconservancy.pass.client.PassClient#findAllByAttributes(Class, Map<String, Object>)
     */
    public <T extends PassEntity> Set<URI> findAllByAttributes(Class<T> modelClass, Map<String, Object> valueAttributesMap) {
        if (modelClass==null) {throw new IllegalArgumentException("modelClass cannot be null");}
        if (valueAttributesMap==null || valueAttributesMap.size()==0) {throw new IllegalArgumentException("valueAttributesMap cannot be empty");}
        //TODO:check map fields are valid

        LOG.debug("Searching for {} using multiple filters", modelClass.getSimpleName());
        Set<URI> passEntityUris = new HashSet<URI>();
        
        String indexType = null;
        
        if (PassEntityType.getTypeByName(modelClass.getSimpleName())!=null) {
            indexType = PassEntityType.getTypeByName(modelClass.getSimpleName()).getName();
        }
        
        StringBuilder attribs = new StringBuilder("");
        for(Entry<String,Object> attr : valueAttributesMap.entrySet()) {
            attribs.append(String.format(QS_ATTRIB_TEMPLATE, attr.getKey(), attr.getValue().toString()));
        }
        String querystring = String.format(QS_TEMPLATE, indexType, attribs);
                
        try {
            Iterator<SearchHit> hitsIt = getIndexerResults(querystring);
            while (hitsIt.hasNext()){
                String idField = hitsIt.next().getSourceAsMap().get("@id").toString();
                passEntityUris.add(new URI(idField));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Something was wrong with the record returned from the indexer. The ID could not be recognized as a URI");
        }        
        return passEntityUris;
    }
    
    private Iterator<SearchHit> getIndexerResults(String querystring) {
        
        Iterator<SearchHit> hitsIt = null;

        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(hosts));) {
            SearchRequest searchRequest = new SearchRequest(); 
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
            //(content:this OR name:this)
            QueryStringQueryBuilder matchQueryBuilder = new QueryStringQueryBuilder(querystring);
                        
            matchQueryBuilder.defaultOperator(Operator.AND);
            sourceBuilder.query(matchQueryBuilder);
            searchRequest.source(sourceBuilder);
            
            SearchResponse searchResponse = client.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            hitsIt = hits.iterator();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return hitsIt;
        
    }
    
    
}
