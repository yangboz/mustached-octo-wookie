package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import info.smartkit.spring_data_solr.repository.BookRepository;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangboz on 1/23/16.
 */
@RestController
@RequestMapping("/info/smartkit/eip/onlp")
public class OpenNLPcontroller {
    private static Logger LOG = LogManager.getLogger(OpenNLPcontroller.class);
    // ==============
    // PRIVATE FIELDS
    // ==============

    // Autowire an object of type BookRepository
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private Environment env;

    private File getModelDir() {
//        String modelsDirProp = System.getProperty("model.dir");
        String modelsDirProp = env.getProperty("model.dir");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LOG.info("modelsDirProp:" + modelsDirProp);
        return new File(classLoader.getResource(modelsDirProp).getFile());
    }

    private File getPersonModel() {
        return new File(getModelDir(), "en-ner-person.bin");
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of book info that is successfully get or not."
            , notes = "use OpenNLP to identify people by writing a few lines of Java code.")
    public List<Map> findNames() throws IOException {
        List<Map> results = new ArrayList<Map>();
        String[] sentences = {
                "Former first lady Nancy Reagan was taken to a " +
                        "suburban Los Angeles " +
                        "hospital as a precaution Sunday after a " +
                        "fall at her home, an " +
                        "aide said. ",
                "The 86-year-old Reagan will remain overnight for " +
                        "observation at a hospital in Santa Monica, California, " +
                        "said Joanne " +
                        "Drake, chief of staff for the Reagan Foundation."};
        NameFinderME finder = new NameFinderME(new TokenNameFinderModel(new FileInputStream(getPersonModel())));
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        //
        for (int si = 0; si < sentences.length; si++) {
            String[] tokens = tokenizer.tokenize(sentences[si]);
            Span[] names = finder.find(tokens);
//            displayNames(names, tokens);
            Map item = new HashMap();
            item.put("names", names);
            item.put("tokens", tokens);
            results.add(item);
        }
        finder.clearAdaptiveData();
        //
        return results;
    }

}
