package com.irengine.sandbox.web.rest;

import com.irengine.sandbox.Application;
import com.irengine.sandbox.domain.Test11;
import com.irengine.sandbox.repository.Test11Repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the Test11Resource REST controller.
 *
 * @see Test11Resource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class Test11ResourceTest {


    @Inject
    private Test11Repository test11Repository;

    private MockMvc restTest11MockMvc;

    private Test11 test11;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Test11Resource test11Resource = new Test11Resource();
        ReflectionTestUtils.setField(test11Resource, "test11Repository", test11Repository);
        this.restTest11MockMvc = MockMvcBuilders.standaloneSetup(test11Resource).build();
    }

    @Before
    public void initTest() {
        test11 = new Test11();
    }

    @Test
    @Transactional
    public void createTest11() throws Exception {
        int databaseSizeBeforeCreate = test11Repository.findAll().size();

        // Create the Test11
        restTest11MockMvc.perform(post("/api/test11s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(test11)))
                .andExpect(status().isCreated());

        // Validate the Test11 in the database
        List<Test11> test11s = test11Repository.findAll();
        assertThat(test11s).hasSize(databaseSizeBeforeCreate + 1);
        Test11 testTest11 = test11s.get(test11s.size() - 1);
    }

    @Test
    @Transactional
    public void getAllTest11s() throws Exception {
        // Initialize the database
        test11Repository.saveAndFlush(test11);

        // Get all the test11s
        restTest11MockMvc.perform(get("/api/test11s"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(test11.getId().intValue())));
    }

    @Test
    @Transactional
    public void getTest11() throws Exception {
        // Initialize the database
        test11Repository.saveAndFlush(test11);

        // Get the test11
        restTest11MockMvc.perform(get("/api/test11s/{id}", test11.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(test11.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTest11() throws Exception {
        // Get the test11
        restTest11MockMvc.perform(get("/api/test11s/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTest11() throws Exception {
        // Initialize the database
        test11Repository.saveAndFlush(test11);

		int databaseSizeBeforeUpdate = test11Repository.findAll().size();

        // Update the test11
        restTest11MockMvc.perform(put("/api/test11s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(test11)))
                .andExpect(status().isOk());

        // Validate the Test11 in the database
        List<Test11> test11s = test11Repository.findAll();
        assertThat(test11s).hasSize(databaseSizeBeforeUpdate);
        Test11 testTest11 = test11s.get(test11s.size() - 1);
    }

    @Test
    @Transactional
    public void deleteTest11() throws Exception {
        // Initialize the database
        test11Repository.saveAndFlush(test11);

		int databaseSizeBeforeDelete = test11Repository.findAll().size();

        // Get the test11
        restTest11MockMvc.perform(delete("/api/test11s/{id}", test11.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Test11> test11s = test11Repository.findAll();
        assertThat(test11s).hasSize(databaseSizeBeforeDelete - 1);
    }
}
