package com.example.saved_jobs.linkedin;

import com.example.saved_jobs.SavedJobsProperties;
import com.example.saved_jobs.jobs.Job;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class LinkedinApiCallerTest {
  @Mock
  private SavedJobsProperties applicationProperties;
  private LinkedinApiCaller linkedinApiCaller;

  @BeforeEach
  public void initTests() {
    ObjectMapper objectMapper = new ObjectMapper();
    String jobsResponseString;
    try {
      File jobResponseFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("jobs_response.json")).getFile());
      JsonNode jsonNode = objectMapper.readTree(jobResponseFile);
      jobsResponseString = objectMapper.writeValueAsString(jsonNode);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    WebClient webClient = WebClient.builder()
        .exchangeFunction(clientRequest ->
            Mono.just(ClientResponse.create(HttpStatus.OK)
                .header("content-type", "application/json")
                .body(jobsResponseString)
                .build())
        ).build();
    linkedinApiCaller = new LinkedinApiCaller(webClient, applicationProperties);
  }

  @Test
  public void getAllSavedJobsShouldReturnAvailableJobs() {
    String cookie = "liap=true; JSESSIONID=\"ajax:4580630035586126765\"";
    List<Job> jobs = linkedinApiCaller.getAllSavedJobs(cookie, "clusterId");

    assertThat(jobs.size()).isEqualTo(60);
  }
}