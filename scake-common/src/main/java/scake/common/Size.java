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

import space.lingu.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author RollW
 */
public class Size implements Comparable<Size>, Serializable {
    private final long size;
    private final SizeUnit unit;

    public Size(long size, @NonNull SizeUnit unit) {
        Objects.requireNonNull(unit, "unit must not be null.");
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative.");
        }
        this.size = size;
        this.unit = unit;
    }

    public long getSize() {
        return size;
    }

    public SizeUnit getUnit() {
        return unit;
    }

    public long toBytes() {
        return unit.toBytes(size);
    }

    @Override
    public int compareTo(@NonNull Size o) {
        return Long.compare(toBytes(), o.toBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size)) return false;
        Size size1 = (Size) o;
        return size == size1.size && unit == size1.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, unit);
    }

    @Override
    public String toString() {
        return "Size{" +
                "size=" + size +
                ", unit=" + unit +
                '}';
    }

    @NonNull
    public static Size ofBytes(long size) {
        return new Size(size, SizeUnit.BYTES);
    }

    @NonNull
    public static Size ofKilobytes(long size) {
        return new Size(size, SizeUnit.KILOBYTES);
    }

    @NonNull
    public static Size ofMegabytes(long size) {
        return new Size(size, SizeUnit.MEGABYTES);
    }

    @NonNull
    public static Size ofGigabytes(long size) {
        return new Size(size, SizeUnit.GIGABYTES);
    }

}
