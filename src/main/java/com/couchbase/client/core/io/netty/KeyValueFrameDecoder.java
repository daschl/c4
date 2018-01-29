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

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * The {@link KeyValueFrameDecoder} is a subclass of the {@link LengthFieldBasedFrameDecoder}
 * which chops a byte stream into the right size chunks.
 *
 * <p>The way it is configured for the memcached binary protocol is that it looks at
 * the total body length in the header and calculates the frame length.</p>
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
class KeyValueFrameDecoder extends LengthFieldBasedFrameDecoder {

  /**
   * Max number of bytes for the frame, which also acts as a rough check that
   * we are not balooning over the 20MB docs + header. So lets stick with
   * 25MB to give some wiggle room.
   */
  private static final int MAX_FRAME_LENGTH = 25 * 1024 * 1024;

  /**
   * In the KV header, the body length starts at byte offset 8.
   */
  private static final int BODY_LENGTH_OFFSET = 8;

  /**
   * The body length is 4 bytes (note that our max frame length is smaller).
   */
  private static final int BODY_LENGTH_FIELD_LENGTH = 4;

  /**
   * Length adjustment represents the number of bytes from the end of the
   * body length field to the end of the header so netty can account
   * for it.
   */
  private static final int LENGTH_ADJUSTMENT = 12;

  /**
   * We don't need to strip initial bytes.
   */
  private static final int BYTES_TO_STRIP = 0;

  /**
   * Create a new {@link KeyValueFrameDecoder}.
   */
  KeyValueFrameDecoder() {
    super(
        MAX_FRAME_LENGTH,
        BODY_LENGTH_OFFSET,
        BODY_LENGTH_FIELD_LENGTH,
        LENGTH_ADJUSTMENT,
        BYTES_TO_STRIP
    );
  }

}
