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

import com.couchbase.client.core.io.Endpoint;
import com.couchbase.client.core.msg.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;

/**
 * The netty-based implementation of an {@link Endpoint}.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public class NettyEndpoint implements Endpoint {


  private final Channel channel;

  /**
   * Creates a new endpoint, this needs to be fixed up.
   */
  public NettyEndpoint() {

    final EventLoopGroup group = new NioEventLoopGroup(
        0,
        (Executor) null,
        SelectorProvider.provider(),
        new Ssf()
    );

    Bootstrap bootstrap = new Bootstrap()
        .remoteAddress("127.0.0.1", 11210)
        .channel(NioSocketChannel.class)
        .group(group)
        .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel chan) throws Exception {
            chan.pipeline().addLast(new KeyValueFrameDecoder());
            //chan.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
            chan.pipeline().addLast(new KeyValueMessageHandler());
          }
        });

    channel = bootstrap.connect().awaitUninterruptibly().channel();
  }

  @Override
  public <R> void dispatch(Request<R> request) {
    channel.writeAndFlush(request);
  }

  class Ssf implements SelectStrategyFactory {

    @Override
    public SelectStrategy newSelectStrategy() {
      return (selectSupplier, hasTasks) -> {
        return selectSupplier.get();
        //return hasTasks ? selectSupplier.get() : SelectStrategy.SELECT;
      };
    }
  }


}
