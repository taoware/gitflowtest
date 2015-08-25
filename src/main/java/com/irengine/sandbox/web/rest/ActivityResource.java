package com.irengine.sandbox.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.irengine.sandbox.domain.Activity;
import com.irengine.sandbox.repository.ActivityRepository;
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
 * REST controller for managing Activity.
 */
@RestController
@RequestMapping("/api")
public class ActivityResource {

    private final Logger log = LoggerFactory.getLogger(ActivityResource.class);

    @Inject
    private ActivityRepository activityRepository;

    /**
     * POST  /activitys -> Create a new activity.
     */
    @RequestMapping(value = "/activitys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Activity activity) throws URISyntaxException {
        log.debug("REST request to save Activity : {}", activity);
        if (activity.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new activity cannot already have an ID").build();
        }
        activityRepository.save(activity);
        return ResponseEntity.created(new URI("/api/activitys/" + activity.getId())).build();
    }

    /**
     * PUT  /activitys -> Updates an existing activity.
     */
    @RequestMapping(value = "/activitys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Activity activity) throws URISyntaxException {
        log.debug("REST request to update Activity : {}", activity);
        if (activity.getId() == null) {
            return create(activity);
        }
        activityRepository.save(activity);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /activitys -> get all the activitys.
     */
    @RequestMapping(value = "/activitys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Activity>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Activity> page = activityRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activitys", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /activitys/:id -> get the "id" activity.
     */
    @RequestMapping(value = "/activitys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Activity> get(@PathVariable Long id) {
        log.debug("REST request to get Activity : {}", id);
        return Optional.ofNullable(activityRepository.findOne(id))
            .map(activity -> new ResponseEntity<>(
                activity,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /activitys/:id -> delete the "id" activity.
     */
    @RequestMapping(value = "/activitys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Activity : {}", id);
        activityRepository.delete(id);
    }
}
