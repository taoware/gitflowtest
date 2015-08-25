package com.irengine.sandbox.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.irengine.sandbox.domain.Dummy;
import com.irengine.sandbox.repository.DummyRepository;
import com.irengine.sandbox.web.rest.util.HeaderUtil;
import com.irengine.sandbox.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
 * REST controller for managing Dummy.
 */
@RestController
@RequestMapping("/api")
public class DummyResource {

    private final Logger log = LoggerFactory.getLogger(DummyResource.class);

    @Inject
    private DummyRepository dummyRepository;

    /**
     * POST  /dummys -> Create a new dummy.
     */
    @RequestMapping(value = "/dummys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Dummy> create(@RequestBody Dummy dummy) throws URISyntaxException {
        log.debug("REST request to save Dummy : {}", dummy);
        if (dummy.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new dummy cannot already have an ID").body(null);
        }
        Dummy result = dummyRepository.save(dummy);
        return ResponseEntity.created(new URI("/api/dummys/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("dummy", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /dummys -> Updates an existing dummy.
     */
    @RequestMapping(value = "/dummys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Dummy> update(@RequestBody Dummy dummy) throws URISyntaxException {
        log.debug("REST request to update Dummy : {}", dummy);
        if (dummy.getId() == null) {
            return create(dummy);
        }
        Dummy result = dummyRepository.save(dummy);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("dummy", dummy.getId().toString()))
                .body(result);
    }

    /**
     * GET  /dummys -> get all the dummys.
     */
    @RequestMapping(value = "/dummys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Dummy>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Dummy> page = dummyRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dummys", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dummys/:id -> get the "id" dummy.
     */
    @RequestMapping(value = "/dummys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Dummy> get(@PathVariable Long id) {
        log.debug("REST request to get Dummy : {}", id);
        return Optional.ofNullable(dummyRepository.findOne(id))
            .map(dummy -> new ResponseEntity<>(
                dummy,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dummys/:id -> delete the "id" dummy.
     */
    @RequestMapping(value = "/dummys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Dummy : {}", id);
        dummyRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("dummy", id.toString())).build();
    }
}
