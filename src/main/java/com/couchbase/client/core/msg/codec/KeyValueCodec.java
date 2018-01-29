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

package com.couchbase.client.core.msg.codec;

import com.couchbase.client.core.msg.kv.GetRequest;

import java.nio.ByteBuffer;

/**
 * This codec is responsible for encoding and decoding KeyValue requests and responses.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public class KeyValueCodec {

  /**
   * The binary protocol header size.
   */
  static final int HEADER_SIZE = 24;

  /**
   * Signals that no extras are used in this packet.
   */
  static final byte NO_EXTRAS = 0;

  /**
   * Signals that no CAS value is used in this packet.
   */
  static final long NO_CAS = 0;

  /**
   * Signals that no datatype is set in this packet.
   */
  static final byte NO_DATATYPE = 0;

  /**
   * "magic" flag for requests.
   */
  static final byte MAGIC_REQ = (byte) 0x80;

  /**
   * The opcode for a KeyValue get operation.
   */
  static final byte OPCODE_GET = (byte) 0x00;

  /**
   * The offset of bytes for the opaque header field.
   */
  public static final int OPAQUE_OFFSET = 12;

  /**
   * Encodes the given {@link GetRequest} into its {@link ByteBuffer} representation.
   *
   * @param request the request.
   * @return the encoded buffer.
   */
  public static ByteBuffer encode(final GetRequest request) {
    short keyLength = (short) request.key().length;
    ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + keyLength)
        .put(MAGIC_REQ)
        .put(OPCODE_GET)
        .putShort(keyLength)
        .put(NO_EXTRAS)
        .put(NO_DATATYPE)
        .putShort(request.partition())
        .putInt(keyLength)
        .putInt(request.opaque())
        .putLong(NO_CAS)
        .put(request.key());
    return buffer;
  }

}
