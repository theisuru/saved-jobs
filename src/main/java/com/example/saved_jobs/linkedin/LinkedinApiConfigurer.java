package com.example.saved_jobs.linkedin;

import com.example.saved_jobs.SavedJobsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class LinkedinApiConfigurer {
  private static final String ACCEPT_HEADER = "application/vnd.linkedin.normalized+json+2.1";

  private final SavedJobsProperties applicationProperties;

  @Bean
  public WebClient getLinkedinWebClient() {
    return WebClient.builder()
        .baseUrl(applicationProperties.getLinkedinUrl())
        .defaultHeaders(httpHeaders -> httpHeaders.set("Accept", ACCEPT_HEADER))
        .build();
  }

}
