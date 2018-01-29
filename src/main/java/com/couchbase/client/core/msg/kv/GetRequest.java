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

import com.couchbase.client.core.msg.codec.KeyValueCodec;
import io.opentracing.Span;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Optional;

/**
 * A {@link GetRequest} fetches a full document from the server.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public final class GetRequest extends BaseKeyValueRequest<GetResponse> {

  /**
   * The document key.
   */
  private final byte[] key;

  /**
   * Creates a new {@link GetRequest}.
   *
   * @param key the key of the document.
   * @param timeout the timeout used for this request.
   */
  public GetRequest(final byte[] key, final Duration timeout, final Optional<Span> span) {
    super(timeout, span);
    verifyKey(key);
    this.key = key;
  }

  /**
   * Returns the encoded key for this request.
   *
   * @return the encoded key.
   */
  public byte[] key() {
    return key;
  }

  @Override
  public ByteBuffer encode() {
    return KeyValueCodec.encode(this);
  }

}
