package info.smartkit.spring_data_solr.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import info.smartkit.spring_data_solr.domain.Book;
import info.smartkit.spring_data_solr.repository.BookRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

/**
 * Created by yangboz on 1/17/16.
 */
@RestController
@RequestMapping("/info/smartkit/eip/solr/tamingText")
public class TamingTextController {
    private static Logger LOG = LogManager.getLogger(TamingTextController.class);
    // ==============
    // PRIVATE FIELDS
    // ==============

    // Autowire an object of type BookRepository
    @Autowired
    private BookRepository bookRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the book info is successfully created or not.")
    public Book create(@RequestBody @Valid Book book) {
        return bookRepository.save(book);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a list describing all of book info that is successfully get or not.")
    public Iterable<Book> list() {
        return this.bookRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the book info id is successfully get or not.")
    public Book get(@PathVariable("id") String id) {
        return this.bookRepository.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(httpMethod = "PUT", value = "Response a string describing if the  book info is successfully updated or not.")
    public Book update(@PathVariable("id") String id, @RequestBody @Valid Book book) {
//		BlackBank find = this._blackBankDao.findOne(id);
//        bookRepository.setId(id);
        return this.bookRepository.save(book);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "Response a string describing if the book info is successfully delete or not.")
    public ResponseEntity<Boolean> delete(@PathVariable("id") String id) {
        this.bookRepository.delete(id);
        return new ResponseEntity<Boolean>(Boolean.TRUE, org.springframework.http.HttpStatus.OK);
    }

}
