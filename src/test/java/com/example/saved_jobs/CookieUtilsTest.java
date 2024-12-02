package com.example.saved_jobs;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class CookieUtilsTest {
  @Test
  public void extractJSessionIdFromCookieShouldReturnExtractedSessionId() {
    String cookie = "bcookie=\"v=2&7aa82174-2334-49-8ddd-de1c4104514d\"; bscookie=\"v=1&20451019134940c67c0b15-3506-" +
        "41ce-8215-476fdcec3258AQFcdfT9epZuaaZG-_NLBO6QTKXfB\"; liap=true; JSESSIONID=\"ajax:4580630035586126765\";" +
        "li_sugr=fad381-ef3f-4c81-8304-3cadc5a1787;";
    String expectedSessionId = "ajax:4580630035586126765";

    Optional<String> actualSessionIdOptional = CookieUtils.extractJSessionIdFromCookie(cookie);

    assertThat(actualSessionIdOptional).isPresent();
    assertThat(actualSessionIdOptional.get()).isEqualTo(expectedSessionId);
  }

  @Test
  public void extractJSessionIdFromCookieShouldReturnEmptyIfSessionIdNotPresent() {
    String cookie = "bcookie=\"v=2&7aa824-8534-4809-8ddd-de1c4104514d\"; bscookie=\"v=1&204510191940c67c0b15-3506-" +
        "41ce-8215-476fdcec3258AQFcdfT9epZuaaZG-_NLBO6QVAKXfB\"; li_sugr=fa85d1-ef3f-4c81-8304-3cad5a10787;";

    Optional<String> actualSessionIdOptional = CookieUtils.extractJSessionIdFromCookie(cookie);

    assertThat(actualSessionIdOptional).isEmpty();
  }
}