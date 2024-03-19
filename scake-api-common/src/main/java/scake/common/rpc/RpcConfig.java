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

import scake.common.Size;

/**
 * @author RollW
 */
public class RpcConfig {
    /**
     * Maximum size of a request in Bytes
     */
    private final long maxRequestSize;

    public RpcConfig(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    public static final class Builder {
        private long maxRequestSize;

        public Builder setMaxRequestSize(long maxRequestSize) {
            this.maxRequestSize = maxRequestSize;
            return this;
        }

        public Builder setMaxRequestSize(Size size) {
            this.maxRequestSize = size.toBytes();
            return this;
        }

        public RpcConfig build() {
            return new RpcConfig(maxRequestSize);
        }
    }
}
