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

package com.couchbase.client.core;

import com.couchbase.client.core.env.CoreEnvironment;
import com.couchbase.client.core.msg.Request;
import com.couchbase.client.core.msg.Response;

/**
 * This class is the main entry point when working with this library.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public class Core {

  private final CoreEnvironment environment;
  private final TimerWheel timerWheel;

  public static Core create(final CoreEnvironment environment) {
    return new Core(environment);
  }

  private Core(final CoreEnvironment environment) {
    this.environment = environment;
    this.timerWheel = TimerWheel.create();
  }

  /**
   * Dispatches the given request into the current topology and state.
   *
   * <p>Since everything is async, don't expect an exception to be raised with this call.
   * Instead, from this point on all the communication and signaling is handled from
   * within the future of the request.</p>
   *
   * @param request the request to dispatch.
   */
  <R extends Response> void dispatch(final Request<R> request) {
    timerWheel.scheduleTimeout(request);
    // todo: now dispatch into the topology
  }

}
