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

package com.couchbase.client.core.util;

import com.couchbase.client.core.msg.BaseRequest;
import com.couchbase.client.core.msg.Request;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Verifies the functionality of the {@link Rx} utility class.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
class RxTest {

  private static final Duration TIMEOUT = Duration.ofSeconds(1);

  @Test
  void shouldSucceedSingleFromRequest() {
    DummyRequest request = new DummyRequest(TIMEOUT);
    Single<String> single = Rx.single(request);
    TestObserver<String> testObserver = single.test();

    testObserver.assertNotComplete();
    testObserver.assertNotTerminated();

    request.succeed("yay!");

    testObserver.assertComplete();
    testObserver.assertTerminated();

    testObserver.assertValue("yay!");
  }

  @Test
  void shouldFailSingleFromRequest() {
    DummyRequest request = new DummyRequest(TIMEOUT);
    Single<String> single = Rx.single(request);
    TestObserver<String> testObserver = single.test();

    testObserver.assertNotComplete();
    testObserver.assertNotTerminated();

    Exception exception = new Exception("error");
    request.fail(exception);

    testObserver.assertTerminated();
    testObserver.assertError(exception);
  }

  @Test
  void shouldFailSingleIfNull() {
    DummyRequest request = new DummyRequest(TIMEOUT);
    Single<String> single = Rx.single(request);
    TestObserver<String> testObserver = single.test();

    testObserver.assertNotComplete();
    testObserver.assertNotTerminated();

    request.succeed(null);

    testObserver.assertTerminated();
    testObserver.assertError(NoSuchElementException.class);
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
