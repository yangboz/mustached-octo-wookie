package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.vectors.io.SequenceFileVectorWriter;
import org.apache.mahout.utils.vectors.io.VectorWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangboz on 1/25/16.
 */
@RestController
@RequestMapping("/info/smartkit/eip/manhout")
public class ManhoutController {

    // ==============
    // PRIVATE FIELDS
    // ==============
    private static Logger LOG = LogManager.getLogger(ManhoutController.class);

    @RequestMapping(value = "/serializing", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET"
            , value = "Serializing vectors to a SequenceFile"
            , notes = "Mahout provides the org.apache.mahout.utils.vectors.io.SequenceFileVectorWriter to assist " +
            "in serializing Vectors to the proper format.")
    public ResponseEntity<Boolean> serializingVectors2SequenceFile() throws IOException {
        //
        Vector sparseSame = new SequentialAccessSparseVector(3);
        Vector sparse = new SequentialAccessSparseVector(3000);
        //
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpLoc = new File(tmpDir, "sfvwt");
        tmpLoc.mkdirs();
        File tmpFile = File.createTempFile("sfvwt", ".dat", tmpLoc);
        Path path = new Path(tmpFile.getAbsolutePath());
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        SequenceFile.Writer seqWriter = SequenceFile.createWriter(fs, conf,
                path, LongWritable.class, VectorWritable.class);
        VectorWriter vecWriter = new SequenceFileVectorWriter(seqWriter);
        List<Vector> vectors = new ArrayList<Vector>();
        vectors.add(sparse);
        vectors.add(sparseSame);
        vecWriter.write(vectors);
        vecWriter.close();
        return new ResponseEntity<Boolean>(Boolean.TRUE, org.springframework.http.HttpStatus.OK);
    }
}
