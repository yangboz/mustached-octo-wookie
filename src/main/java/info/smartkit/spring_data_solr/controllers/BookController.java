package info.smartkit.spring_data_solr.controllers;

import info.smartkit.spring_data_solr.domain.Book;
import info.smartkit.spring_data_solr.repository.BookRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.ApiOperation;


import java.util.List;

/**
 * Created by yangboz on 10/21/15.
 */
@RestController
@RequestMapping("/info/smartkit/eip/solr/book")
public class BookController {
    private static Logger LOG = LogManager.getLogger(BookController.class);
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

    //
    @RequestMapping(value = "/name/{name}/desc/{desc}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the book info by name or description is get or not")
    public Page<Book> findByNameOrDescription(@PathVariable("name") String name, @PathVariable("desc") String desc) {
        return this.bookRepository.findByNameOrDescription(name, desc, new PageRequest(0, 10));
    }

    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the book info by name and facet is get or not")
    public FacetPage<Book> findByNameAndFacetOnCategories(@PathVariable("name") String name) {
        return bookRepository.findByNameAndFacetOnCategories(name, new PageRequest(0, 10));
    }


    @RequestMapping(value = "/desc/{desc}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the book info by description and facet is get or not")
    public HighlightPage<Book> findByDescription(@PathVariable("desc") String desc) {
        return bookRepository.findByDescription(desc, new PageRequest(0, 10));
    }

}
