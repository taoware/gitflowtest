package com.irengine.sandbox.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irengine.sandbox.domain.Order;
import com.irengine.sandbox.repository.OrderRepository;
import com.irengine.sandbox.web.rest.util.HeaderUtil;
import com.irengine.sandbox.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing Order.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    @Inject
    private OrderRepository orderRepository;

    /**
     * POST  /orders -> Create a new order.
     */
    @RequestMapping(value = "/orders",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Order> create(@RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (order.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new order cannot already have an ID").body(null);
        }
        Order result = orderRepository.save(order);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("order", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /orders -> Updates an existing order.
     */
    @RequestMapping(value = "/orders",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Order> update(@RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to update Order : {}", order);
        if (order.getId() == null) {
            return create(order);
        }
        Order result = orderRepository.save(order);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("order", order.getId().toString()))
                .body(result);
    }

    /**
     * GET  /orders -> get all the orders.
     */
    @RequestMapping(value = "/orders",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Order>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Order> page = orderRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private static final String Limit = "per_page";
    private static final String Offset = "page";
    private static final String Sorting = "sort";
    private static final String Filtering = "filter";

    /**
     * GET  /orders/q -> query orders.
     */
    @RequestMapping(value = "/orders/q",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> query(@RequestParam Map<String, Object> params) {

        // pagination
        Pageable pageable;
        Integer offset = Integer.parseInt(params.get(Offset).toString());
        Integer limit = Integer.parseInt(params.get(Limit).toString());

        // sorting
        if (params.containsKey(Sorting) && !params.get(Sorting).toString().isEmpty()) {

            Map<String, String> sort = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            try {
                sort = mapper.readValue(params.get(Sorting).toString(), new TypeReference<Map<String, String>>(){});
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
        if (params.containsKey(Filtering) && !params.get(Filtering).toString().isEmpty()) {

            Map<String, Map<String, String>> filter = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            try {
                filter = mapper.readValue(params.get(Filtering).toString(), new TypeReference<Map<String, Map<String, String>>>(){});
            } catch (Exception e) {
                e.printStackTrace();
            }

            log.debug("request with filtering");
        }

        Page<Order> page = orderRepository.findAll(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * GET  /orders/:id -> get the "id" order.
     */
    @RequestMapping(value = "/orders/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Order> get(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        return Optional.ofNullable(orderRepository.findOne(id))
            .map(order -> new ResponseEntity<>(
                order,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /orders/:id -> delete the "id" order.
     */
    @RequestMapping(value = "/orders/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("order", id.toString())).build();
    }
}
