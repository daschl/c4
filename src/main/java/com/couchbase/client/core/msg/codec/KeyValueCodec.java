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
import io.netty.buffer.Unpooled;

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
  private static final int HEADER_SIZE = 24;

  /**
   * Signals that no extras are used in this packet.
   */
  private static final int NO_EXTRAS = 0;

  /**
   * Signals that no datatype is set in this packet.
   */
  private static final int NO_DATATYPE = 0;

  /**
   * "magic" flag for requests.
   */
  private static final byte MAGIC_REQ = (byte) 0x80;

  /**
   * The opcode for a KeyValue get operation.
   */
  private static final byte OPCODE_GET = (byte) 0x00;

  /**
   * Encodes the given {@link GetRequest} into its {@link ByteBuffer} representation.
   *
   * @param getRequest the request.
   * @return the encoded buffer.
   */
  public static ByteBuffer encode(final GetRequest getRequest) {
    short keyLength = (short) getRequest.key().length;
    return Unpooled
      .buffer(HEADER_SIZE + keyLength)
      .writeByte(MAGIC_REQ)
      .writeByte(OPCODE_GET)
      .writeShort(keyLength)
      .writeByte(NO_EXTRAS)
      .writeByte(NO_DATATYPE)
      .writeShort(0) // FIXME: vbucket id
      .writeInt(keyLength) // total body length
      .writeInt(0) // FIXME: opaque
      .writeLong(0) // FIXME: CAS
      .nioBuffer();
  }

}
