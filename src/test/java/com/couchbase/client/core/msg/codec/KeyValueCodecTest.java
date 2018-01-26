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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.couchbase.client.core.msg.kv.GetRequest;
import com.couchbase.client.core.util.Constants;
import io.opentracing.Span;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Optional;

/**
 * Verifies encoding and decoding of Key/Value requests and responses.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
class KeyValueCodecTest {

  private static final Duration TIMEOUT = Duration.ofSeconds(1);
  private static final Optional<Span> SPAN = Optional.empty();

  @Test
  void shouldEncodeGetRequest() {
    byte[] key = "Hello".getBytes(Constants.CHARSET);
    GetRequest request = new GetRequest(key, TIMEOUT, SPAN);
    request.partition((short) 456);

    final ByteBuffer encoded = KeyValueCodec.encode(request);
    assertEquals(KeyValueCodec.HEADER_SIZE + key.length, encoded.position());

    assertAll(
        () -> assertRequestMagic(encoded),
        () -> assertOpcode(encoded, KeyValueCodec.OPCODE_GET),
        () -> assertOpaque(encoded, request.opaque()),
        () -> assertPartition(encoded, request.partition()),
        () -> assertCas(encoded, KeyValueCodec.NO_CAS),
        () -> assertDatatype(encoded, KeyValueCodec.NO_DATATYPE),
        () -> assertKey(encoded, key),
        () -> assertNoExtras(encoded)
    );
  }

  /**
   * Helper method to assert the request has the proper magic set.
   *
   * @param buf the buffer to check.
   */
  private static void assertRequestMagic(final ByteBuffer buf) {
    assertEquals(KeyValueCodec.MAGIC_REQ, buf.get(0));
  }

  /**
   * Helper method to check if the request has the proper opcode set.
   *
   * @param buf the buffer to check.
   * @param opcode the opcode to verify.
   */
  private static void assertOpcode(final ByteBuffer buf, final byte opcode) {
    assertEquals(opcode, buf.get(1));
  }

  /**
   * Helper method to check if the request has the proper partition set.
   *
   * @param buf the buffer to check.
   * @param partition the partition to verify.
   */
  private static void assertPartition(final ByteBuffer buf, final short partition) {
    assertEquals(partition, buf.getShort(6));
  }

  /**
   * Helper method to assert the opaque value set for this buffer.
   *
   * @param buf the buffer to check.
   * @param opaque the opaque to verify.
   */
  private static void assertOpaque(final ByteBuffer buf, final int opaque) {
    assertEquals(opaque, buf.getInt(12));
  }

  /**
   * Helper method to assert the cas value set for this buffer.
   *
   * @param buf the buffer to check.
   * @param cas the cas to verify.
   */
  private static void assertCas(final ByteBuffer buf, final long cas) {
    assertEquals(cas, buf.getLong(16));
  }

  /**
   * Helper method to check if the request has the proper datatype set.
   *
   * @param buf the buffer to check.
   * @param datatype the datatype to verify.
   */
  private static void assertDatatype(final ByteBuffer buf, final byte datatype) {
    assertEquals(datatype, buf.get(5));
  }

  /**
   * Helper method to check if no key is set.
   *
   * @param buf the buffer to check.
   */
  private static void assertNoKey(final ByteBuffer buf) {
    assertKey(buf, new byte[]{ });
  }

  /**
   * Helper method to check if the key is set as well as the key length and
   * the total body length covers it.
   *
   * @param buf the buffer to check.
   * @param key the key to verify against.
   */
  private static void assertKey(final ByteBuffer buf, final byte[] key) {
    assertEquals(key.length, buf.getShort(2));
    assertTrue(buf.getInt(8) >= key.length);

    byte[] keyFound = new byte[key.length];
    ByteBuffer slice = buf.duplicate();
    slice.position(KeyValueCodec.HEADER_SIZE + buf.get(4));
    slice.get(keyFound, 0, key.length);
    assertArrayEquals(key, keyFound);
  }

  /**
   * Helper method to check if no extras are set.
   *
   * @param buf the buffer to check.
   */
  private static void assertNoExtras(final ByteBuffer buf) {
    assertExtras(buf, new byte[]{ });
  }

  /**
   * Helper method to check if the extras are applied properly.
   *
   * @param buf the buffer to check.
   * @param extras the extras to check.
   */
  private static void assertExtras(final ByteBuffer buf, final byte[] extras) {
    assertEquals(extras.length, buf.get(4));
    assertTrue(buf.getInt(8) >= extras.length);

    byte[] extrasFound = new byte[extras.length];
    ByteBuffer slice = buf.duplicate();
    slice.position(KeyValueCodec.HEADER_SIZE);
    slice.get(extrasFound, 0, extras.length);
    assertArrayEquals(extras, extrasFound);
  }

}