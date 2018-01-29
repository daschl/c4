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

package com.couchbase.client.core.io.netty;

import com.couchbase.client.core.msg.kv.GetRequest;
import com.couchbase.client.core.util.Constants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class NettyEndpointTest {

  @Test
  @Disabled
  void foo() throws Exception {
    NettyEndpoint endpoint = new NettyEndpoint();


    while (true) {
      int batch = 100;
      CountDownLatch latch = new CountDownLatch(batch);

      for (int i = 0; i < batch; i++) {
        GetRequest request = new GetRequest(
            ("I_DONT_EXIST0000000000000000000" + i).getBytes(Constants.CHARSET),
            Duration.ofSeconds(2),
            Optional.empty()
        );
        endpoint.dispatch(request);
        request.response().whenComplete((getResponse, throwable) -> latch.countDown());
      }

      latch.await(10, TimeUnit.SECONDS);
    }

    // Thread.sleep(1000000000);

  }

}