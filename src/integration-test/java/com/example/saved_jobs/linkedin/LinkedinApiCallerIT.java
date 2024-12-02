package com.example.saved_jobs.linkedin;

import com.example.saved_jobs.jobs.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LinkedinApiCallerIT {
  public static ObjectMapper objectMapper;
  public static MockWebServer mockBackEnd;

  @Autowired
  private LinkedinApiCaller linkedinApiCaller;

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry r) {
    r.add("saved-jobs.linkedin.url", () -> "http://localhost:" + mockBackEnd.getPort());
  }

  @BeforeAll
  static void setUp() throws IOException {
    objectMapper = new ObjectMapper();
    mockBackEnd = new MockWebServer();
    mockBackEnd.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockBackEnd.shutdown();
  }

  @Test
  public void getAllSavedJobsShouldReturnAvailableJobs() {
    String jobsResponseString;
    try {
      File jobResponseFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("jobs_response.json")).getFile());
      JsonNode jsonNode = objectMapper.readTree(jobResponseFile);
      jobsResponseString = objectMapper.writeValueAsString(jsonNode);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    mockBackEnd.enqueue(new MockResponse().setBody(jobsResponseString));
    mockBackEnd.enqueue(new MockResponse().setBody(jobsResponseString));
    mockBackEnd.enqueue(new MockResponse().setBody(jobsResponseString));

    String cookie = "liap=true; JSESSIONID=\"ajax:4580630035586126765\"";
    List<Job> jobs = linkedinApiCaller.getAllSavedJobs(cookie, "clusterId");

    String expectedFirstJobLocation = "London Area, United Kingdom (Remote)";
    assertThat(jobs.size()).isEqualTo(60);
    assertThat(jobs.get(0).location()).isEqualTo(expectedFirstJobLocation);
  }
}