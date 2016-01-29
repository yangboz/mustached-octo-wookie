package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * Created by yangboz on 1/26/16.
 */
@RestController
@RequestMapping("/info/smartkit/eip/lucene")
public class LucenceController {

    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(LucenceController.class);

    @Autowired
    private Environment env;

    private File getBaseDir() {
//        String modelsDirProp = System.getProperty("model.dir");
        String baseDirProp = env.getProperty("base.dir");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LOG.info("baseDir:" + baseDirProp);
        return new File(classLoader.getResource(baseDirProp).getFile());
    }

    private File getTempDir() {
//        String modelsDirProp = System.getProperty("model.dir");
        String tmpDirProp = env.getProperty("manhout.tmpdir");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LOG.info("tmpDir:" + tmpDirProp);
        return new File(classLoader.getResource(tmpDirProp).getFile());
    }

    @RequestMapping(value = "/indexing", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET"
            , value = "This command builds a Lucene index using the training data you prepared in the previous section."
            , notes = "Building an index that’s used for the TF-IDF algorithm is as simple as changing the -type argument to tfidf.")
    public ResponseEntity<Boolean> creatingIndex() throws IOException {
        //
        Directory directory
                = FSDirectory.open(new File(pathname));
        Analyzer analyzer
                = new EnglishAnalyzer(Version.LUCENE_36);
        if (nGramSize > 1) {
            ShingleAnalyzerWrapper sw
                    = new ShingleAnalyzerWrapper(analyzer,
                    nGramSize, // min shingle size
                    nGramSize, // max shingle size
                    "-", // token separator
                    true, // output unigrams
                    true); // output unigrams if no shingles
            analyzer = sw;
        }
        IndexWriterConfig config
                = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);
        return new ResponseEntity<Boolean>(Boolean.TRUE, org.springframework.http.HttpStatus.OK);
    }
}