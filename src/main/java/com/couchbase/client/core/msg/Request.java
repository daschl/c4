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

/**
 * The {@link Request} is the common parent interface for all requests flowing through
 * this library.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public interface Request<R extends Response> {

  /**
   * Returns the response represented by a {@link CompletableFuture} for this {@link Request}.
   *
   * @return the response which is completed once the future completes.
   */
  CompletableFuture<R> response();

  /**
   * Returns true if the {@link Request Requests} underlying response has been completed,
   * either with success or with a failure.
   *
   * @return true if completed, false otherwise.
   */
  default boolean hasCompleted() {
    return  hasSucceeded() || hasFailed();
  }

  /**
   * Returns true if the {@link Request Requests} underlying response has been completed
   * successfully.
   *
   * @return true if it has, false otherwise.
   */
  boolean hasSucceeded();

  /**
   * Returns true if the {@link Request Requests} underlying response has completed, but
   * with a failure.
   *
   * @return true if it has failed, false otherwise.
   */
  boolean hasFailed();

  /**
   * Completes this {@link Request} with a successful value.
   *
   * @param value the success value to complete with.
   */
  void succeed(R value);

  /**
   * Completes this {@link Request} with a failure value.
   *
   * @param error the failure value to complete with.
   */
  void fail(Throwable error);

  /**
   * Returns the relative timeout duration of this {@link Request}.
   *
   * @return the timeout duration of this request.
   */
  Duration timeout();

  /**
   * A unique ID for each {@link Request}.
   *
   * @return the id for this request.
   */
  long id();

  /**
   * If set, contains a {@link Span} for tracing.
   *
   * @return the span for tracing, none if not set.
   */
  Optional<Span> span();

}
