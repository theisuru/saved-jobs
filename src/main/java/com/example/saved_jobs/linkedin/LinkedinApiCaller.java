package com.example.saved_jobs.linkedin;

import com.example.saved_jobs.CookieUtils;
import com.example.saved_jobs.SavedJobsProperties;
import com.example.saved_jobs.jobs.Job;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class LinkedinApiCaller {
  private static final String PAGING_AND_SEARCH_QUERY_TEMPLATE =
      "(start:{0},count:{1},query:(flagshipSearchIntent:SEARCH_MY_ITEMS_JOB_SEEKER,queryParameters:List((key:cardType,value:List({2})))))";
  private static final String CLUSTER_ID_QUERY_TEMPLATE = "voyagerSearchDashClusters.{0}";
  private static final List<String> JOB_STATUS_LIST = List.of("APPLIED");
  private static final int PAGE_SIZE = 20;

  private final WebClient webClient;
  private final SavedJobsProperties applicationProperties;

  public List<Job> getAllSavedJobs(String cookie, String clusterId) {
    int start = 0;
    String response = getSavedJobsPage(start, cookie, clusterId);
    int totalCount = JobsResponseMapper.extractTotalJobCount(response);
    log.info("Retrieving {} saved jobs from LinkedIn", totalCount);

    List<Job> jobs = JobsResponseMapper.extractJobList(response);
    start += PAGE_SIZE;
    while (start < totalCount) {
      response = getSavedJobsPage(start, cookie, clusterId);
      jobs.addAll(JobsResponseMapper.extractJobList(response));
      start += PAGE_SIZE;
    }

    return jobs;
  }

  private String getSavedJobsPage(int start, String cookie, String clusterId) {
    String pageAndSearchQuery = getPaginationAndSearchQuery(start);
    String clusterIdQuery = getSearchClusterQuery(clusterId);
    String csrfToken = extractCsrfTokenFromCookie(cookie);

    ResponseEntity<String> response = webClient.get()
        .uri(uriBuilder -> uriBuilder.queryParam("variables", pageAndSearchQuery)
            .queryParam("queryId", clusterIdQuery)
            .build())
        .headers(headers -> {
          headers.set("Cookie", cookie);
          headers.set("csrf-token", csrfToken);
        })
        .retrieve().toEntity(String.class).block();

    if (response == null || response.getStatusCode() != HttpStatusCode.valueOf(200)) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve data from LinkedIn API");
    }

    return response.getBody();
  }

  private String getPaginationAndSearchQuery(int start) {
    String statusListString = String.join(",", JOB_STATUS_LIST);
    return MessageFormat.format(PAGING_AND_SEARCH_QUERY_TEMPLATE, start, PAGE_SIZE, statusListString);
  }

  private String getSearchClusterQuery(String clusterId) {
    clusterId = StringUtils.isEmpty(clusterId) ? applicationProperties.getDefaultCluster() : clusterId;
    return MessageFormat.format(CLUSTER_ID_QUERY_TEMPLATE, clusterId);
  }

  private String extractCsrfTokenFromCookie(String cookie) {
    return CookieUtils.extractJSessionIdFromCookie(cookie)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not extract JSESSIONID from the cookie. Please provide a valid cookie."));
  }
}
