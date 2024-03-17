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

package scake.server;

import java.util.Objects;

/**
 * Represents a server in the system.
 *
 * @author RollW
 */
public class Server {
    private final String host;
    private final int port;
    private final String id;
    private final Type type;

    public Server(String host, int port, String id, Type type) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.type = type;
    }

    /**
     * Get the id of the server. The id is unique within
     * the type of the server.
     *
     * @return the id of the server
     */
    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Type getType() {
        return type;
    }

    /**
     * Get the qualified name of the server. It will be
     * unique within the whole system and can be used to
     * identify the server.
     *
     * @return the qualified name of the server
     */
    public String getQualifiedName() {
        return type.getPrefix() + id;
    }

    public String getAddress() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;
        Server server = (Server) o;
        return port == server.port && Objects.equals(host, server.host) &&
                Objects.equals(id, server.id) && type == server.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, id, type);
    }

    @Override
    public String toString() {
        return "Server[" + host + ":" + port +
                " = " + type + "-" + id +
                "]";
    }

    public enum Type {
        FILE_SERVER("file-"),
        META_SERVER("meta-");

        private final String prefix;

        Type(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
