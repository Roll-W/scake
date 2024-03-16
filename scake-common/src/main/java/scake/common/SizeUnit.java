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

package scake.common;

/**
 * @author RollW
 */
public enum SizeUnit {
    BYTES("B"),
    KILOBYTES("KB"),
    MEGABYTES("MB"),
    GIGABYTES("GB"),
    TERABYTES("TB");

    private final String abbreviation;

    SizeUnit(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public long toBytes(long size) {
        switch (this) {
            case BYTES:
                return size;
            case KILOBYTES:
                return size * 1024;
            case MEGABYTES:
                return size * 1024 * 1024;
            case GIGABYTES:
                return size * 1024 * 1024 * 1024;
            case TERABYTES:
                return size * 1024 * 1024 * 1024 * 1024;
            default:
                throw new IllegalStateException("Unknown size unit: " + this);
        }
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
