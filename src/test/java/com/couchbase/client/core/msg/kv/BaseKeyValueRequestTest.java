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

package com.couchbase.client.core.msg.kv;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.couchbase.client.core.msg.Request;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

/**
 * Verifies the functionality of the {@link BaseKeyValueRequest}.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
class BaseKeyValueRequestTest {

  @Test
  void shouldIncrementOpaque() {
    int lastOpaque = 0;
    for (int i = 0; i < 10; i++) {
      DummyRequest request = new DummyRequest();
      int opaque = request.opaque();
      assertTrue(opaque > 0);
      assertTrue(opaque != lastOpaque);
      lastOpaque = opaque;
    }
  }

  /**
   * Helper class to implement a simple {@link Request}.
   */
  class DummyRequest extends BaseKeyValueRequest<String> {
    DummyRequest() {
      super(Duration.ofSeconds(1), Optional.empty());
    }
  }

}