package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by yangboz on 1/17/16.
 */
@RestController
@RequestMapping("/info/smartkit/eip/solr/tt")
public class TamingTextController {
    private static Logger LOG = LogManager.getLogger(TamingTextController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============

    private SolrServer solrServer;

    public TamingTextController() throws MalformedURLException {
        this.solrServer = new CommonsHttpSolrServer("http://localhost:8983/solr");
    }


    @RequestMapping(value = "/addDocument", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a RequestHandler that uses the using SolrJ to add documents to Solr",
            notes = "There you have it, the basics of Solr indexing using XML. Now let’s look at indexing common file formats.")
    public ResponseEntity<Boolean> addDocuement() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "http://tortoisehare5k.tamingtext.com");
//        document.addField("mimeType", "text/plain");//Tika will determinate it.
        document.addField("title", "Tortoise beats Hare! Hare wants rematch.", 5);//Add a Title field to the document and boost it to be 5 times as important as other fields.
//        Date now = new Date();
//        document.addField("date", DateUtil.getThreadLocalDateFormat().format(now));
        document.addField("description", "Taming text description");
        document.addField("categories_t", "Fairy Tale, Sports");//A dynamic field allows for the addition of unknown fields to Solr. The _t tells Solr this should be treated as a text field.
        solrServer.add(document);//Send the newly created document to Solr. Solr takes care of creating a correctly formatted XML message and sending it to Solr using Apache Jakarta Commons HTTPClient.
        solrServer.commit();//After you’ve added all your documents and wish to make them available for searching,send a commit message to Solr.
        return new ResponseEntity<Boolean>(Boolean.TRUE, org.springframework.http.HttpStatus.OK);
    }

    //DisMax query
    @RequestMapping(value = "/query/dismax", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a RequestHandler that uses the DismaxQParser for query parsing",
            notes = "DisMax parser searches across fields given by the qf parameter and boosts terms accordingly.")
    public SolrDocumentList queryDismax() throws MalformedURLException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("lazy");
        solrQuery.setParam("defType", "dismax");
        solrQuery.setParam("qf", "title^3 description^10");
        LOG.debug("SolrQuery: " + solrQuery);

        QueryResponse queryResponse = solrServer.query(solrQuery);
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        LOG.info("queryResponse: " + queryResponse);
        return solrDocumentList;
    }

    //MoreLikeThis query
    @RequestMapping(value = "/query/mlt", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a RequestHandler that uses the MoreLikeThis for query parsing",
            notes = "When a user requests a More Like This query, Solr will take the input document,\n" +
                    "look up the terms in the specified Fields, figure out which are the most\n" +
                    "important, and generate a new query.")
    public SolrDocumentList queryMoreLikeThis() throws MalformedURLException, SolrServerException {
        SolrQuery queryParams = new SolrQuery();
        queryParams.setQueryType("/mlt");
        queryParams.setQuery("description:number");
        queryParams.set("mlt.match.offset", "0");
        queryParams.setRows(1);
        queryParams.set("mlt.fl", "description, title");
        QueryResponse response = solrServer.query(queryParams);
        LOG.debug("response is null and it shouldn't be" + response);
        SolrDocumentList results =
                (SolrDocumentList) response.getResponse().get("match");
        LOG.info("results Size: " + results.size() + " is not: " + 1);
        return results;
    }
}
