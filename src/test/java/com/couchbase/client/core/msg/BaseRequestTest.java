/*
 * Copyright (c) 2018 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.core.msg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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

    assertAll(
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

    assertAll(
        () -> assertFalse(request.hasFailed()),
        () -> assertFalse(request.hasSucceeded()),
        () -> assertFalse(request.hasCompleted())
    );

    request.succeed("It worked...");

    assertAll(
        () -> assertFalse(request.hasFailed()),
        () -> assertTrue(request.hasSucceeded()),
        () -> assertTrue(request.hasCompleted()),
        () -> assertEquals("It worked...", request.response().get())
    );
  }

  @Test
  void shouldFailFutureIfProvided() {
    DummyRequest request = new DummyRequest(TIMEOUT);

    assertAll(
        () -> assertFalse(request.hasFailed()),
        () -> assertFalse(request.hasSucceeded()),
        () -> assertFalse(request.hasCompleted())
    );

    TimeoutException expectedException = new TimeoutException();
    request.fail(expectedException);

    assertAll(
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

    assertAll(
        () -> assertFalse(request.hasFailed()),
        () -> assertFalse(request.hasSucceeded()),
        () -> assertFalse(request.hasCompleted()),
        () -> assertNotNull(request.response())
    );

    request.succeed("Much stealth!");

    assertAll(
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
