package com.irengine.sandbox.web.rest;

import com.irengine.sandbox.Application;
import com.irengine.sandbox.domain.Activity;
import com.irengine.sandbox.repository.ActivityRepository;

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
 * Test class for the ActivityResource REST controller.
 *
 * @see ActivityResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ActivityResourceTest {

    private static final String DEFAULT_CODE = "SAMPLE_TEXT";
    private static final String UPDATED_CODE = "UPDATED_TEXT";

    @Inject
    private ActivityRepository activityRepository;

    private MockMvc restActivityMockMvc;

    private Activity activity;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActivityResource activityResource = new ActivityResource();
        ReflectionTestUtils.setField(activityResource, "activityRepository", activityRepository);
        this.restActivityMockMvc = MockMvcBuilders.standaloneSetup(activityResource).build();
    }

    @Before
    public void initTest() {
        activity = new Activity();
        activity.setCode(DEFAULT_CODE);
    }

    @Test
    @Transactional
    public void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity
        restActivityMockMvc.perform(post("/api/activitys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activity)))
                .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activitys = activityRepository.findAll();
        assertThat(activitys).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activitys.get(activitys.size() - 1);
        assertThat(testActivity.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    public void getAllActivitys() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activitys
        restActivityMockMvc.perform(get("/api/activitys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc.perform(get("/api/activitys/{id}", activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get("/api/activitys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

		int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        activity.setCode(UPDATED_CODE);
        restActivityMockMvc.perform(put("/api/activitys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activity)))
                .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activitys = activityRepository.findAll();
        assertThat(activitys).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activitys.get(activitys.size() - 1);
        assertThat(testActivity.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    public void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

		int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Get the activity
        restActivityMockMvc.perform(delete("/api/activitys/{id}", activity.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Activity> activitys = activityRepository.findAll();
        assertThat(activitys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
