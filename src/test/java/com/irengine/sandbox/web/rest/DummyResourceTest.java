package com.irengine.sandbox.web.rest;

import com.irengine.sandbox.Application;
import com.irengine.sandbox.domain.Dummy;
import com.irengine.sandbox.repository.DummyRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
 * Test class for the DummyResource REST controller.
 *
 * @see DummyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DummyResourceTest {

    private static final String DEFAULT_CODE = "SAMPLE_TEXT";
    private static final String UPDATED_CODE = "UPDATED_TEXT";
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    @Inject
    private DummyRepository dummyRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restDummyMockMvc;

    private Dummy dummy;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DummyResource dummyResource = new DummyResource();
        ReflectionTestUtils.setField(dummyResource, "dummyRepository", dummyRepository);
        this.restDummyMockMvc = MockMvcBuilders.standaloneSetup(dummyResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        dummy = new Dummy();
        dummy.setCode(DEFAULT_CODE);
        dummy.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createDummy() throws Exception {
        int databaseSizeBeforeCreate = dummyRepository.findAll().size();

        // Create the Dummy

        restDummyMockMvc.perform(post("/api/dummys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dummy)))
                .andExpect(status().isCreated());

        // Validate the Dummy in the database
        List<Dummy> dummys = dummyRepository.findAll();
        assertThat(dummys).hasSize(databaseSizeBeforeCreate + 1);
        Dummy testDummy = dummys.get(dummys.size() - 1);
        assertThat(testDummy.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testDummy.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllDummys() throws Exception {
        // Initialize the database
        dummyRepository.saveAndFlush(dummy);

        // Get all the dummys
        restDummyMockMvc.perform(get("/api/dummys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(dummy.getId().intValue())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDummy() throws Exception {
        // Initialize the database
        dummyRepository.saveAndFlush(dummy);

        // Get the dummy
        restDummyMockMvc.perform(get("/api/dummys/{id}", dummy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dummy.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDummy() throws Exception {
        // Get the dummy
        restDummyMockMvc.perform(get("/api/dummys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDummy() throws Exception {
        // Initialize the database
        dummyRepository.saveAndFlush(dummy);

		int databaseSizeBeforeUpdate = dummyRepository.findAll().size();

        // Update the dummy
        dummy.setCode(UPDATED_CODE);
        dummy.setName(UPDATED_NAME);
        

        restDummyMockMvc.perform(put("/api/dummys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dummy)))
                .andExpect(status().isOk());

        // Validate the Dummy in the database
        List<Dummy> dummys = dummyRepository.findAll();
        assertThat(dummys).hasSize(databaseSizeBeforeUpdate);
        Dummy testDummy = dummys.get(dummys.size() - 1);
        assertThat(testDummy.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testDummy.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteDummy() throws Exception {
        // Initialize the database
        dummyRepository.saveAndFlush(dummy);

		int databaseSizeBeforeDelete = dummyRepository.findAll().size();

        // Get the dummy
        restDummyMockMvc.perform(delete("/api/dummys/{id}", dummy.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Dummy> dummys = dummyRepository.findAll();
        assertThat(dummys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
