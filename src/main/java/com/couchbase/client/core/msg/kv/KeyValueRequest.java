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

import com.couchbase.client.core.msg.Request;

/**
 * The {@link KeyValueRequest} is the common parent interface for all KeyValue requests
 * flowing through the library.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public interface KeyValueRequest<R> extends Request<R> {

  /**
   * Returns the opaque for this request.
   *
   * @return the opaque, automatically created.
   */
  int opaque();

  /**
   * Allows to set the partition for the this request.
   *
   * @param partition the partition to set.
   * @return this {@link KeyValueRequest} for chaining purposes.
   */
  KeyValueRequest<R> partition(short partition);

  /**
   * Returns the partition, if set, on this request.
   *
   * @return the partition or 0 if not set.
   */
  short partition();
}
