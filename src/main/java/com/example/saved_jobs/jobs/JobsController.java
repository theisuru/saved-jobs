package com.example.saved_jobs.jobs;

import com.example.saved_jobs.linkedin.LinkedinApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobsController {
  private final LinkedinApiCaller linkedinApiCaller;

  @GetMapping("/jobs")
  public ResponseEntity<List<Job>> getSavedJobsByCluster(@RequestHeader("linkedin-cookie") String cookie,
                                                         @RequestParam(required = false) String clusterId) {
    return ResponseEntity.ok(linkedinApiCaller.getAllSavedJobs(cookie, clusterId));
  }

}
