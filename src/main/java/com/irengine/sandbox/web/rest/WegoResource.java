package com.irengine.sandbox.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irengine.sandbox.domain.Wego;
import com.irengine.sandbox.repository.WegoRepository;
import com.irengine.sandbox.web.rest.util.Filter;
import com.irengine.sandbox.web.rest.util.FilterUtil;
import com.irengine.sandbox.web.rest.util.HeaderUtil;
import com.irengine.sandbox.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * REST controller for managing Wego.
 */
@RestController
@RequestMapping("/api")
public class WegoResource {

    private final Logger log = LoggerFactory.getLogger(WegoResource.class);

    @Inject
    private WegoRepository wegoRepository;

    /**
     * POST  /wegos -> Create a new wego.
     */
    @RequestMapping(value = "/wegos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Wego> create(@RequestBody Wego wego) throws URISyntaxException {
        log.debug("REST request to save Wego : {}", wego);
        if (wego.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new wego cannot already have an ID").body(null);
        }
        Wego result = wegoRepository.save(wego);
        return ResponseEntity.created(new URI("/api/wegos/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("wego", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /wegos -> Updates an existing wego.
     */
    @RequestMapping(value = "/wegos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Wego> update(@RequestBody Wego wego) throws URISyntaxException {
        log.debug("REST request to update Wego : {}", wego);
        if (wego.getId() == null) {
            return create(wego);
        }
        Wego result = wegoRepository.save(wego);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("wego", wego.getId().toString()))
                .body(result);
    }

    /**
     * GET  /wegos -> get all the wegos.
     */
    @RequestMapping(value = "/wegos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Wego>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Wego> page = wegoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/wegos", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private static final String LIMIT = "per_page";
    private static final String OFFSET = "page";
    private static final String SORTING = "sort";
    private static final String FILTERING = "filter";

    /**
     * GET  /wegos/q -> query wegos.
     */
    @RequestMapping(value = "/wegos/q",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> query(@RequestParam Map<String, Object> params) {

        // pagination
        Pageable pageable;
        Integer offset = Integer.parseInt(params.get(OFFSET).toString());
        Integer limit = Integer.parseInt(params.get(LIMIT).toString());

        // sorting
        if (params.containsKey(SORTING) && !params.get(SORTING).toString().isEmpty()) {

            Map<String, String> sort = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            try {
                sort = mapper.readValue(params.get(SORTING).toString(), new TypeReference<Map<String, String>>(){});
            } catch (Exception e) {
                e.printStackTrace();
            }

            log.debug("request with pagination and sorting");
            pageable = PaginationUtil.generatePageRequest(offset, limit, sort.get("field"), sort.get("sort"));
        }
        else {
            log.debug("request with pagination");
            pageable = PaginationUtil.generatePageRequest(offset, limit);
        }

        // filtering support angular grid filtering number and text
        /*
        if (params.containsKey(FILTERING) && !params.get(FILTERING).toString().isEmpty()) {

            Map<String, Map<String, String>> filter = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            try {
                filter = mapper.readValue(params.get(FILTERING).toString(), new TypeReference<Map<String, Map<String, String>>>(){});
            } catch (Exception e) {
                e.printStackTrace();
            }

            // sample filtering field,operator,value
            log.debug("request with filtering");

            List<Filter> filters = new ArrayList<>();
            filters.add(new Filter("id", Filter.Operator.EQ, "1"));

            Specification<Wego> specification = FilterUtil.generateSpecifications(filters, Wego.class);

            Page<Wego> page = wegoRepository.findAll(specification, pageable);
            return new ResponseEntity<>(page, HttpStatus.OK);
        }
        */

        Page<Wego> page = wegoRepository.findAll(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * GET  /wegos/:id -> get the "id" wego.
     */
    @RequestMapping(value = "/wegos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Wego> get(@PathVariable Long id) {
        log.debug("REST request to get Wego : {}", id);
        return Optional.ofNullable(wegoRepository.findOne(id))
            .map(wego -> new ResponseEntity<>(
                wego,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /wegos/:id -> delete the "id" wego.
     */
    @RequestMapping(value = "/wegos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Wego : {}", id);
        wegoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("wego", id.toString())).build();
    }

}
