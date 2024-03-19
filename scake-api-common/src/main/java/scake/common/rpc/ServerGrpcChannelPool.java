/*
 * Scake - A high available, scalable distributed file system.
 * Copyright (C) 2024 RollW
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package scake.common.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import scake.server.Server;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class ServerGrpcChannelPool extends GrpcChannelPool<Server> {
    private final RpcConfig rpcConfig;
    private final ChannelConfigure channelConfigure;

    public ServerGrpcChannelPool(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
        this.channelConfigure = null;
    }

    public ServerGrpcChannelPool(RpcConfig rpcConfig,
                                 ChannelConfigure channelConfigure) {
        this.rpcConfig = rpcConfig;
        this.channelConfigure = channelConfigure;
    }

    @Override
    @NonNull
    protected ManagedChannel buildChannel(Server server) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(
                        server.getHost(),
                        server.getPort()
                )
                .usePlaintext()
                .keepAliveTime(300, TimeUnit.DAYS)
                .keepAliveTimeout(30, TimeUnit.MINUTES)
                .maxInboundMessageSize((int) rpcConfig.getMaxRequestSize() * 2);
        if (channelConfigure != null) {
            channelConfigure.configure(builder);
        }
        return builder.build();
    }

    public ManagedChannel forServer(Server target) {
        return getChannel(target);
    }
}
