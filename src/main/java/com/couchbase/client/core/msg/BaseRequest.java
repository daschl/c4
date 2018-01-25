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

import io.opentracing.Span;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Common parent implementation for all {@link Request Requests}.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public abstract class BaseRequest<R extends Response> implements Request<R> {

  /**
   * Atomic updater for the {@link #state} field.
   */
  private static final AtomicReferenceFieldUpdater<BaseRequest, State> STATE_UPDATER =
      AtomicReferenceFieldUpdater.newUpdater(BaseRequest.class, State.class, "state");

  /**
   * A ever-increasing, unique message ID for every message created.
   */
  private static final AtomicLong MESSAGE_ID = new AtomicLong(0);

  /**
   * Contains the response which will eventually complete.
   */
  private final CompletableFuture<R> response = new CompletableFuture<>();

  /**
   * The stored message ID.
   */
  private final long id;

  /**
   * The span used for tracing.
   */
  private final Optional<Span> span;

  /**
   * The anticipated timeout value for this message.
   */
  private final Duration timeout;

  /**
   * The {@link State} this {@link Request} is in at the moment.
   *
   * <p>Do not rename this field without updating the {@link #STATE_UPDATER}!</p>
   */
  private volatile State state = State.INCOMPLETE;

  /**
   * Creates a new {@link BaseRequest}.
   *
   * <p>Intentionally, this class should only be called by child implementations or
   * test stubs.</p>
   */
  protected BaseRequest(final Duration timeout, final Optional<Span> span) {
    id = MESSAGE_ID.incrementAndGet();
    this.timeout = timeout;
    this.span = span;
  }

  @Override
  public CompletableFuture<R> response() {
    return response;
  }

  @Override
  public boolean hasSucceeded() {
    return state == State.SUCCESS;
  }

  @Override
  public boolean hasFailed() {
    return state == State.FAILURE;
  }

  @Override
  public void succeed(final R value) {
    if (STATE_UPDATER.compareAndSet(this, State.INCOMPLETE, State.SUCCESS)) {
      response.complete(value);
    }
  }

  @Override
  public void fail(final Throwable error) {
    if (STATE_UPDATER.compareAndSet(this, State.INCOMPLETE, State.FAILURE)) {
      response.completeExceptionally(error);
    }
  }

  @Override
  public Duration timeout() {
    return timeout;
  }

  @Override
  public long id() {
    return id;
  }

  @Override
  public Optional<Span> span() {
    return span;
  }

  /**
   * Represents the states this {@link Request} can be in.
   *
   * <p>Right now it is only used to internally track different modes in one volatile
   * variable instead of many.</p>
   */
  private enum State {

    /**
     * This {@link Request} is not complete yet.
     */
    INCOMPLETE,

    /**
     * This request has been completed successfully.
     */
    SUCCESS,

    /**
     * This request has been completed with failure.
     */
    FAILURE

  }

}
