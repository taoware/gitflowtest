package com.irengine.sandbox.web.rest;

import com.irengine.sandbox.Application;
import com.irengine.sandbox.domain.Wego;
import com.irengine.sandbox.repository.WegoRepository;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the WegoResource REST controller.
 *
 * @see WegoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class WegoResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final DateTime DEFAULT_START = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_START = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_START_STR = dateTimeFormatter.print(DEFAULT_START);

    private static final DateTime DEFAULT_END = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_END = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_END_STR = dateTimeFormatter.print(DEFAULT_END);
    private static final String DEFAULT_FILE = "SAMPLE_TEXT";
    private static final String UPDATED_FILE = "UPDATED_TEXT";

    @Inject
    private WegoRepository wegoRepository;

    private MockMvc restWegoMockMvc;

    private Wego wego;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WegoResource wegoResource = new WegoResource();
        ReflectionTestUtils.setField(wegoResource, "wegoRepository", wegoRepository);
        this.restWegoMockMvc = MockMvcBuilders.standaloneSetup(wegoResource).build();
    }

    @Before
    public void initTest() {
        wego = new Wego();
        wego.setTitle(DEFAULT_TITLE);
        wego.setDescription(DEFAULT_DESCRIPTION);
        wego.setStart(DEFAULT_START);
        wego.setEnd(DEFAULT_END);
        wego.setFile(DEFAULT_FILE);
    }

    @Test
    @Transactional
    public void createWego() throws Exception {
        int databaseSizeBeforeCreate = wegoRepository.findAll().size();

        // Create the Wego
        restWegoMockMvc.perform(post("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isCreated());

        // Validate the Wego in the database
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(databaseSizeBeforeCreate + 1);
        Wego testWego = wegos.get(wegos.size() - 1);
        assertThat(testWego.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWego.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWego.getStart().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_START);
        assertThat(testWego.getEnd().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_END);
        assertThat(testWego.getFile()).isEqualTo(DEFAULT_FILE);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(wegoRepository.findAll()).hasSize(0);
        // set the field null
        wego.setTitle(null);

        // Create the Wego, which fails.
        restWegoMockMvc.perform(post("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(0);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(wegoRepository.findAll()).hasSize(0);
        // set the field null
        wego.setDescription(null);

        // Create the Wego, which fails.
        restWegoMockMvc.perform(post("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(0);
    }

    @Test
    @Transactional
    public void checkStartIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(wegoRepository.findAll()).hasSize(0);
        // set the field null
        wego.setStart(null);

        // Create the Wego, which fails.
        restWegoMockMvc.perform(post("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(0);
    }

    @Test
    @Transactional
    public void checkEndIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(wegoRepository.findAll()).hasSize(0);
        // set the field null
        wego.setEnd(null);

        // Create the Wego, which fails.
        restWegoMockMvc.perform(post("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(0);
    }

    @Test
    @Transactional
    public void checkFileIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(wegoRepository.findAll()).hasSize(0);
        // set the field null
        wego.setFile(null);

        // Create the Wego, which fails.
        restWegoMockMvc.perform(post("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllWegos() throws Exception {
        // Initialize the database
        wegoRepository.saveAndFlush(wego);

        // Get all the wegos
        restWegoMockMvc.perform(get("/api/wegos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(wego.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START_STR)))
                .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END_STR)))
                .andExpect(jsonPath("$.[*].file").value(hasItem(DEFAULT_FILE.toString())));
    }

    @Test
    @Transactional
    public void getWego() throws Exception {
        // Initialize the database
        wegoRepository.saveAndFlush(wego);

        // Get the wego
        restWegoMockMvc.perform(get("/api/wegos/{id}", wego.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(wego.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START_STR))
            .andExpect(jsonPath("$.end").value(DEFAULT_END_STR))
            .andExpect(jsonPath("$.file").value(DEFAULT_FILE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWego() throws Exception {
        // Get the wego
        restWegoMockMvc.perform(get("/api/wegos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWego() throws Exception {
        // Initialize the database
        wegoRepository.saveAndFlush(wego);

		int databaseSizeBeforeUpdate = wegoRepository.findAll().size();

        // Update the wego
        wego.setTitle(UPDATED_TITLE);
        wego.setDescription(UPDATED_DESCRIPTION);
        wego.setStart(UPDATED_START);
        wego.setEnd(UPDATED_END);
        wego.setFile(UPDATED_FILE);
        restWegoMockMvc.perform(put("/api/wegos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wego)))
                .andExpect(status().isOk());

        // Validate the Wego in the database
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(databaseSizeBeforeUpdate);
        Wego testWego = wegos.get(wegos.size() - 1);
        assertThat(testWego.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testWego.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWego.getStart().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_START);
        assertThat(testWego.getEnd().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_END);
        assertThat(testWego.getFile()).isEqualTo(UPDATED_FILE);
    }

    @Test
    @Transactional
    public void deleteWego() throws Exception {
        // Initialize the database
        wegoRepository.saveAndFlush(wego);

		int databaseSizeBeforeDelete = wegoRepository.findAll().size();

        // Get the wego
        restWegoMockMvc.perform(delete("/api/wegos/{id}", wego.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Wego> wegos = wegoRepository.findAll();
        assertThat(wegos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
