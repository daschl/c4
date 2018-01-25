/*
 * Copyright (c) 2017 Couchbase, Inc.
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

package com.couchbase.client.core;

import com.couchbase.client.core.msg.BaseRequest;
import com.couchbase.client.core.msg.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Verifies the functionality of the {@link TimerWheel}.
 *
 * @since 2.0.0
 */
class TimerWheelTest {

  private TimerWheel wheel;

  @BeforeEach
  void setup() {
    wheel = TimerWheel.create();
  }

  @AfterEach
  void teardown() {
    assertNotNull(wheel.shutdown());
  }

  @Test
  void shouldScheduleConsumerForTimeout() throws InterruptedException {
    assertEquals(0, wheel.scheduledTasks());

    CountDownLatch latch = new CountDownLatch(1);
    wheel.schedule(t -> latch.countDown(), Duration.ofMillis(1));

    assertTrue(latch.await(1, TimeUnit.SECONDS));
  }

  @Test
  void shouldScheduleMessageForTimeout() {
    assertEquals(0, wheel.scheduledTasks());

    DummyRequest request = new DummyRequest(Duration.ofMillis(1));
    wheel.scheduleTimeout(request);

    try {
      request.response().get(1, TimeUnit.SECONDS);
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof TimeoutException) {
        assertTrue(true);
      } else {
        fail("Unexpected Exception", ex);
      }
    } catch (Throwable t) {
      fail("Unexpected Throwable", t);
    }

    assertAll("message",
      () -> assertTrue(request.hasCompleted()),
      () -> assertTrue(request.hasFailed()),
      () -> assertFalse(request.hasSucceeded())
    );
  }

  @Test
  void shouldHaveNoPendingTasksIfConsumerCancelled() {
    assertEquals(0, wheel.scheduledTasks());

    Timer timer = wheel.schedule(t -> {}, Duration.ofSeconds(2));

    assertEquals(1, wheel.scheduledTasks());
    timer.cancel();

    assertTimeout(Duration.ofSeconds(1), () -> {
      while(wheel.scheduledTasks() != 0) {
        Thread.sleep(10);
      }
    });
    assertEquals(0, wheel.scheduledTasks());
  }

  @Test
  void shouldShutdownWithNoPendingTimers() throws InterruptedException {
    TimerWheel wheel = TimerWheel.create();

    int numTimers = 10;
    CountDownLatch latch = new CountDownLatch(10);
    for (int i = 0; i < numTimers; i++) {
      wheel.schedule(t -> latch.countDown(), Duration.ofMillis(1));
    }
    assertTrue(latch.await(1, TimeUnit.SECONDS));
    assertEquals(0, wheel.scheduledTasks());
    assertTrue(wheel.shutdown().isEmpty());
  }

  @Test
  void shouldShutdownWithPendingTimers() {
    TimerWheel wheel = TimerWheel.create();

    int numTimers = 10;
    for (int i = 0; i < numTimers; i++) {
      wheel.schedule(t -> {}, Duration.ofSeconds(1));
    }

    assertEquals(numTimers, wheel.scheduledTasks());
    Set<Timer> pending = wheel.shutdown();
    assertEquals(numTimers, pending.size());
  }

  /**
   * Helper class to implement a simple {@link Request}.
   */
  class DummyRequest extends BaseRequest<Integer> {
    DummyRequest(final Duration duration) {
      super(duration, Optional.empty());
    }
  }

}