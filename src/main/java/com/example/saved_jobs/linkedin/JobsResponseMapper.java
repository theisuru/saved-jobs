package com.example.saved_jobs.linkedin;

import com.example.saved_jobs.jobs.Job;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JobsResponseMapper {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static int extractTotalJobCount(String linkedinResponse) {
    try {
      JsonNode jsonNode = objectMapper.readTree(linkedinResponse);
      return jsonNode.at("/data/data/searchDashClustersByAll/paging/total").asInt();
    } catch (JsonProcessingException e) {
      log.error("Could not extract total job count from LinkedIn response", e);
      throw new RuntimeException("Could not extract job count from response", e);
    }
  }

  public static List<Job> extractJobList(String linkedinResponse) {
    List<Job> jobs = new ArrayList<>();
    try {
      JsonNode jsonNode = objectMapper.readTree(linkedinResponse);
      JsonNode resultsArrayNode = jsonNode.get("included");

      if (resultsArrayNode.isArray()) {
        for (JsonNode element : resultsArrayNode) {
          if (isJobPostingElement(element)) {
            Job job = extractJobFromJobPostingResponse(element);
            jobs.add(job);
          }
        }
      }
    } catch (JsonProcessingException e) {
      log.error("Could not extract jobs from LinkedIn response", e);
      throw new RuntimeException("Could not extract jobs from response", e);
    }

    return jobs;
  }

  private static boolean isJobPostingElement(JsonNode element) {
    return element.get("entityUrn").asText().startsWith("urn:li:fsd_entityResultViewModel:(urn:li:jobPosting");
  }

  private static Job extractJobFromJobPostingResponse(JsonNode jobPostingNode) {
    String jobTitle = jobPostingNode.at("/title/text").asText();
    String company = jobPostingNode.at("/primarySubtitle/text").asText();
    String applied = jobPostingNode.at("/insightsResolutionResults/0/simpleInsight/title/text").asText();
    String location = jobPostingNode.at("/secondarySubtitle/text").asText();
    return new Job(jobTitle, company, location, applied);
  }
}
