package com.example.saved_jobs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SavedJobsProperties {
  @Value("${saved-jobs.linkedin.url}")
  private String linkedinUrl;
  @Value("${saved-jobs.linkedin.default-cluster}")
  private String defaultCluster;
}
