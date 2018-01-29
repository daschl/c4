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

import com.couchbase.client.core.msg.Response;
import com.couchbase.client.core.msg.codec.KeyValueCodec;
import com.couchbase.client.core.msg.kv.GetResponse;
import com.couchbase.client.core.msg.kv.KeyValueRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.collection.IntObjectHashMap;

import java.nio.ByteBuffer;
import java.util.List;

public class KeyValueMessageHandler
    extends MessageToMessageCodec<ByteBuf, KeyValueRequest<Response>> {

  private final IntObjectHashMap<KeyValueRequest<Response>> requests;

  public KeyValueMessageHandler() {
    // TODO: this should ideally be the same as the max supported outstanding reqs
    requests = new IntObjectHashMap<>(128);
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, KeyValueRequest<Response> msg, List<Object> out)
    throws Exception {
    ByteBuffer encoded = msg.encode();
    encoded.flip();
    ByteBuf buff = Unpooled.wrappedBuffer(encoded);
    if (requests.putIfAbsent(msg.opaque(), msg) != null) {
      // todo: whooops a requests already existed in that slot
    }
    out.add(buff);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    KeyValueRequest<Response> request = requests.remove(opaqueFromHeader(msg));
    if (request == null) {
      // TODO: whooops got a opaque for a request which we not sent
    }
    request.succeed(new GetResponse());
  }

  /**
   * Helper method to extract the opaque value from a header.
   *
   * @param header the header to check.
   * @return the opaque.
   */
  private static int opaqueFromHeader(final ByteBuf header) {
    return header.getInt(KeyValueCodec.OPAQUE_OFFSET);
  }

}
