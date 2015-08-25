package com.irengine.sandbox.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.irengine.sandbox.domain.Test11;
import com.irengine.sandbox.repository.Test11Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Test11.
 */
@RestController
@RequestMapping("/api")
public class Test11Resource {

    private final Logger log = LoggerFactory.getLogger(Test11Resource.class);

    @Inject
    private Test11Repository test11Repository;

    /**
     * POST  /test11s -> Create a new test11.
     */
    @RequestMapping(value = "/test11s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Test11 test11) throws URISyntaxException {
        log.debug("REST request to save Test11 : {}", test11);
        if (test11.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new test11 cannot already have an ID").build();
        }
        test11Repository.save(test11);
        return ResponseEntity.created(new URI("/api/test11s/" + test11.getId())).build();
    }

    /**
     * PUT  /test11s -> Updates an existing test11.
     */
    @RequestMapping(value = "/test11s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Test11 test11) throws URISyntaxException {
        log.debug("REST request to update Test11 : {}", test11);
        if (test11.getId() == null) {
            return create(test11);
        }
        test11Repository.save(test11);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /test11s -> get all the test11s.
     */
    @RequestMapping(value = "/test11s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Test11> getAll() {
        log.debug("REST request to get all Test11s");
        return test11Repository.findAll();
    }

    /**
     * GET  /test11s/:id -> get the "id" test11.
     */
    @RequestMapping(value = "/test11s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Test11> get(@PathVariable Long id) {
        log.debug("REST request to get Test11 : {}", id);
        return Optional.ofNullable(test11Repository.findOne(id))
            .map(test11 -> new ResponseEntity<>(
                test11,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /test11s/:id -> delete the "id" test11.
     */
    @RequestMapping(value = "/test11s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Test11 : {}", id);
        test11Repository.delete(id);
    }
}
