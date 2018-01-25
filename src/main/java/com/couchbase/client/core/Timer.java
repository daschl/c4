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

import com.couchbase.client.core.msg.Request;
import io.netty.util.Timeout;

/**
 * Represents a handle into a scheduled timer from the {@link TimerWheel}.
 *
 * <p>Right now this class just wraps the underlying netty {@link Timeout}
 * but only exposes the bits and pieces needed for this library. It also
 * decouples the actual implementation of the timer wheel from the types
 * throughout the library (like the {@link Request}.</p>
 *
 * @since 2.0.0
 */
public class Timer {

  /**
   * The internally wrapped netty timeout handle.
   */
  private final Timeout timeout;

  /**
   * Constructor to create this {@link Timer}.
   *
   * @param timeout the wrapped timeout.
   */
  private Timer(final Timeout timeout) {
    this.timeout = timeout;
  }

  /**
   * Wrap a netty {@link Timeout}.
   *
   * @param timeout the wrapped timeout.
   * @return the newly created {@link Timer}.
   */
  public static Timer wrap(final Timeout timeout) {
    return new Timer(timeout);
  }

  /**
   * Proactively cancels the current {@link Timer}.
   *
   * @return true if the cancellation completed successfully.
   */
  public boolean cancel() {
    return timeout.cancel();
  }

}
