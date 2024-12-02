package com.example.saved_jobs;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieUtils {
  private static final Pattern JSESSIONID_PATTERN = Pattern.compile("JSESSIONID=\"(ajax:\\d+)\"");

  public static Optional<String> extractJSessionIdFromCookie(String cookieString) {
    Matcher matcher = JSESSIONID_PATTERN.matcher(cookieString);
    return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
  }
}
