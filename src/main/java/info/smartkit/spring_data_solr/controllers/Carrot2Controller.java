package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangboz on 1/24/16.
 */
@RestController
@RequestMapping("/info/smartkit/eip/carrot")
public class Carrot2Controller {
    private static final String[] titles = {
            "Red Fox jumps over Lazy Brown Dogs",
            "Mary Loses Little Lamb.  Wolf At Large.",
            "Lazy Brown Dogs Promise Revenge on Red Fox",
            "March Comes in like a Lamb"
    };
    // ==============
    // PRIVATE FIELDS
    // ==============
    private static final String[] snippets = {
            "The sly red fox ran down the canyon, through the woods and over the field, jumping over Farmer Ted's lazy brown dogs as if they were two fallen trees.",
            "In a disastrous turn of events, Mary, the shepherd, lost one of her lambs last night between 10 and 11 PM.  While it can't be proved just yet, the strange disappearance of Mr. Wolf suggests his involvement.",
            "After being thoroughly embarrassed by the red fox yesterday, Farmer Ted's brown dogs came out with a press release vowing vengeance on the fox.",
            "After a cold and blustery February, citizens of Minneapolis were relieved that March entered like a lamb instead of a lion."
    };
    private static Logger LOG = LogManager.getLogger(Carrot2Controller.class);
    protected List<Document> documents;

    @RequestMapping(value = "/process", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET"
            , value = "Carrot2 architecture is implemented as a pipeline. Content is ingested from a document source " +
            "and then handed off to one or more components that modify and cluster the sources, outputting the clusters " +
            "at the other end."
            , notes = "Given a set of documents with these characteristics, it’s straightforward to cluster them")
    public ProcessingResult getProcessResults() {
        //... setup some documents elsewhere
        final Controller controller =
                ControllerFactory.createSimple();
        documents = new ArrayList<Document>();
        for (int i = 0; i < titles.length; i++) {
            Document doc = new Document(titles[i], snippets[i],
                    "file://foo_" + i + ".txt");
            documents.add(doc);
        }
        final ProcessingResult result = controller.process(documents,
                "red fox",
                LingoClusteringAlgorithm.class);
        return result;
    }
}
