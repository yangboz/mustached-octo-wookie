package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import info.smartkit.spring_data_solr.repository.BookRepository;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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

    //    private File baseDir = new File("");
    private File destDir = new File("target");

    private File getModelDir() {
//        String modelsDirProp = System.getProperty("model.dir");
        String modelsDirProp = env.getProperty("model.dir");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LOG.info("modelsDirProp:" + modelsDirProp);
        return new File(classLoader.getResource(modelsDirProp).getFile());
    }

    private File getBaseDir() {
//        String modelsDirProp = System.getProperty("model.dir");
        String trainModelsDirProp = env.getProperty("base.dir");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LOG.info("trainModelsDirProp:" + trainModelsDirProp);
        return new File(classLoader.getResource(trainModelsDirProp).getFile());
    }

    private File getPersonModel() {
        return new File(getModelDir(), "en-ner-person.bin");
    }

    @RequestMapping(value = "/models/person", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list<personModel> describing all of book info that is successfully get or not."
            , notes = "use OpenNLP to identify people by writing a few lines of Java code.")
    public List<Map> findByPersonModel() throws IOException {
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

    @RequestMapping(value = "/models/person/location/date", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list<person/location/dateModel> describing all of book info that is successfully get or not."
            , notes = "It also implies that different models can iden- tify names from overlapping sections of text.")
    public List<Annotation> findByPersonLocationDateModel() throws IOException {
        String[] sentences = {
                "Former first lady Nancy Reagan was taken to a " +
                        "suburban Los Angeles " +
                        "hospital as a precaution Sunday after a fall at " +
                        "her home, an " +
                        "aide said. ",
                "The 86-year-old Reagan will remain overnight for " +
                        "observation at a hospital in Santa Monica, California, " +
                        "said Joanne " +
                        "Drake, chief of staff for the Reagan Foundation."};
        NameFinderME[] finders = new NameFinderME[3];
        String[] names = {"person", "location", "date"};
        for (int mi = 0; mi < names.length; mi++) {
            finders[mi] = new NameFinderME(new TokenNameFinderModel(
                    new FileInputStream(
                            new File(getModelDir(), "en-ner-" + names[mi] + ".bin")
                    )));
        }
        //
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        for (int si = 0; si < sentences.length; si++) {
            List<Annotation> allAnnotations = new ArrayList<Annotation>();
            String[] tokens = tokenizer.tokenize(sentences[si]);
//            Initialize new model for identifying people, locations, and dates based on the binary compressed model in the files en-ner- person.bin, en-ner- location.bin, en-ner-date.bin.
//                    Obtain reference to a tokenizer to split sentence into individual words and symbols.
//            Split sentence into array of tokens.
//            Iterate over each name finder (person, location, date).
//                    Get probabilities with associated matches.
            for (int fi = 0; fi < finders.length; fi++) {
                Span[] spans = finders[fi].find(tokens);
                double[] probs = finders[fi].probs(spans);
                for (int ni = 0; ni < spans.length; ni++) {
//                Identify names in sentence and return token - based offsets.
                    allAnnotations.add(
                            new Annotation(names[fi], spans[ni], probs[ni])
                    );
                }
            }
            removeConflicts(allAnnotations);
            return allAnnotations;
        }
        return null;
    }

    private void removeConflicts(List<Annotation> allAnnotations) {
        java.util.Collections.sort(allAnnotations);
        List<Annotation> stack = new ArrayList<Annotation>();
        stack.add(allAnnotations.get(0));
        for (int ai = 1; ai < allAnnotations.size(); ai++) {
            Annotation curr = (Annotation) allAnnotations.get(ai);
            boolean deleteCurr = false;
            for (int ki = stack.size() - 1; ki >= 0; ki--) {
                Annotation prev = (Annotation) stack.get(ki);
                if (prev.getSpan().equals(curr.getSpan())) {
                    if (prev.getProb() > curr.getProb()) {
                        deleteCurr = true;
                        break;
                    } else {
                        allAnnotations.remove(stack.remove(ki));
                        ai--;
                    }
                } else if (prev.getSpan().intersects(curr.getSpan())) {
                    if (prev.getProb() > curr.getProb()) {
                        deleteCurr = true;
                        break;
                    } else {
                        allAnnotations.remove(stack.remove(ki));
                        ai--;
                    }
                } else if (prev.getSpan().contains(curr.getSpan())) {
                    break;
                } else {
                    stack.remove(ki);
                }
            }
            if (deleteCurr) {
                allAnnotations.remove(ai);
                ai--;
                deleteCurr = false;
            } else {
                stack.add(curr);
            }
        }
    }

    @RequestMapping(value = "/models/train/person", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a Training a named-entity model with OpenNLP that is successfully get or not."
            , notes = "OpenNLP provides code for training in NameFinderME.main(), which sup- ports options such as specifying the character encoding and a few other features.")
    public ResponseEntity<Boolean> customTrainPersonModel() throws IOException {
        File inFile = new File(getBaseDir(), "person.train");
        NameSampleDataStream nss = new NameSampleDataStream(
                new PlainTextByLineStream(
                        new java.io.FileReader(inFile)));
        int iterations = 100;
        int cutoff = 5;
        TokenNameFinderModel model = NameFinderME.train(
                "en",
                "person",
                nss,
                (AdaptiveFeatureGenerator) null,
                Collections.<String, Object>emptyMap(),
                iterations,
                cutoff);
        //Save model to file
        File outFile = new File(destDir, "person-custom.bin");
        FileOutputStream outFileStream = new FileOutputStream(outFile);
        model.serialize(outFileStream);
        LOG.info("trained person model:" + outFile.toString());
        //
        return new ResponseEntity<Boolean>(Boolean.TRUE, org.springframework.http.HttpStatus.OK);
    }

    class Annotation implements Comparable<Annotation> {
        private Span span;
        private String type;
        private double prob;

        public Annotation(String type, Span span, double prob) {
            this.span = span;
            this.type = type;
            this.prob = prob;
        }

        public Span getSpan() {
            return span;
        }

        public String getType() {
            return type;
        }

        public double getProb() {
            return prob;
        }

        public int compareTo(Annotation a) {
            int c = span.compareTo(a.span);
            if (c == 0) {
                c = Double.compare(prob, a.prob);
                if (c == 0) {
                    c = type.compareTo(a.type);
                }
            }
            return c;
        }

        public String toString() {
            return type + " " + span + " " + prob;
        }
    }
}
