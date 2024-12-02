package com.example.saved_jobs.linkedin;

import com.example.saved_jobs.jobs.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class JobsResponseMapperTest {
  static String jobsResponse;

  @BeforeAll
  static void init() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      File jobResponseFile = new File(Objects.requireNonNull(
          JobsResponseMapperTest.class.getClassLoader().getResource("jobs_response.json")).getFile());
      JsonNode jsonNode = objectMapper.readTree(jobResponseFile);
      jobsResponse = objectMapper.writeValueAsString(jsonNode);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void extractTotalJobCountShouldExtractRightNumberFromPageMetadata() {
    int expectedCount = 60;
    int realCount = JobsResponseMapper.extractTotalJobCount(jobsResponse);
    assertThat(realCount).isEqualTo(expectedCount);
  }

  @Test
  void extractJobListShouldExtractAllJobsInPage() {
    int expectedCount = 20;
    String expectedFirstJobCompany = "Test Company";
    List<Job> realJobList = JobsResponseMapper.extractJobList(jobsResponse);
    assertThat(realJobList.size()).isEqualTo(expectedCount);
    assertThat(realJobList.get(0).company()).isEqualTo(expectedFirstJobCompany);
  }
}