package com.couchbase.client.core.msg;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Verifies the functionality of the {@link BaseRequest}.
 *
 * @since 2.0.0
 */
class BaseRequestTest {

  private static final Duration TIMEOUT = Duration.ofSeconds(1);

  @Test
  void shouldInitializeInIncompleteState() {
    DummyRequest request = new DummyRequest(TIMEOUT);

    assertAll("message",
      () -> assertFalse(request.hasFailed()),
      () -> assertFalse(request.hasSucceeded()),
      () -> assertFalse(request.hasCompleted()),
      () -> assertNotNull(request.response()),
      () -> assertEquals(TIMEOUT, request.timeout()),
      () -> assertTrue(request.id() > 0)
    );
  }

  @Test
  void shouldCompleteFutureIfProvided() {
    DummyRequest request = new DummyRequest(TIMEOUT);

    assertAll("message",
      () -> assertFalse(request.hasFailed()),
      () -> assertFalse(request.hasSucceeded()),
      () -> assertFalse(request.hasCompleted())
    );

    request.succeed("It worked...");

    assertAll("message",
      () -> assertFalse(request.hasFailed()),
      () -> assertTrue(request.hasSucceeded()),
      () -> assertTrue(request.hasCompleted()),
      () -> assertEquals("It worked...", request.response().get())
    );
  }

  @Test
  void shouldFailFutureIfProvided() {
    DummyRequest request = new DummyRequest(TIMEOUT);

    assertAll("message",
      () -> assertFalse(request.hasFailed()),
      () -> assertFalse(request.hasSucceeded()),
      () -> assertFalse(request.hasCompleted())
    );

    TimeoutException expectedException = new TimeoutException();
    request.fail(expectedException);

    assertAll("message",
      () -> assertTrue(request.hasFailed()),
      () -> assertFalse(request.hasSucceeded()),
      () -> assertTrue(request.hasCompleted())
    );

    try {
      request.response().get();
      fail("Exception Expected");
    } catch (ExecutionException exception) {
      if (exception.getCause().equals(expectedException)) {
        assertTrue(true);
      } else {
        fail("Unexpected Exception", exception);
      }
    } catch (Throwable exception) {
      fail("Unexpected Exception", exception);
    }
  }

  @Test
  void shouldWorkIfNeitherConsumerNorFutureAreProvided() {
    DummyRequest request = new DummyRequest(TIMEOUT);

    assertAll("message",
      () -> assertFalse(request.hasFailed()),
      () -> assertFalse(request.hasSucceeded()),
      () -> assertFalse(request.hasCompleted()),
      () -> assertNotNull(request.response())
    );

    request.succeed("Much stealth!");

    assertAll("message",
      () -> assertFalse(request.hasFailed()),
      () -> assertTrue(request.hasSucceeded()),
      () -> assertTrue(request.hasCompleted())
    );
  }

  /**
   * Helper class to implement a simple {@link Request}.
   */
  class DummyRequest extends BaseRequest<String> {
    DummyRequest(final Duration duration) {
      super(duration, Optional.empty());
    }
  }

}
