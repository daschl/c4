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

import com.couchbase.client.core.msg.BaseRequest;
import io.opentracing.Span;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all {@link KeyValueRequest KeyValueRequests}.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 * @param <R> the response type.
 */
public abstract class BaseKeyValueRequest<R>
    extends BaseRequest<R>
    implements KeyValueRequest<R> {

  /**
   * A ever-increasing, unique message ID for every message created.
   */
  private static final AtomicInteger OPAQUE = new AtomicInteger(0);

  /**
   * The opaque for this request.
   */
  private final int opaque;

  /**
   * The partition for this request.
   */
  private volatile short partition;

  protected BaseKeyValueRequest(final Duration timeout, final Optional<Span> span) {
    super(timeout, span);
    opaque = OPAQUE.incrementAndGet();
  }

  @Override
  public int opaque() {
    return opaque;
  }

  @Override
  public KeyValueRequest<R> partition(short partition) {
    this.partition = partition;
    return this;
  }

  @Override
  public short partition() {
    return partition;
  }

}
