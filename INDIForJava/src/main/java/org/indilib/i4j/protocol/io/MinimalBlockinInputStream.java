package org.indilib.i4j.protocol.io;

/*
 * #%L
 * INDI Protocol implementation
 * %%
 * Copyright (C) 2012 - 2014 indiforjava
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;

/**
 * This is a very special class for the system, it protects the system against
 * blocking on input stream reading, especialy if there is somthing to read but
 * not enouth for the buffer. This class will pass all methods throu to the
 * wrapped input stream, only the read is handled differently. It will only read
 * maximal as much as is available on the wrapped input stream, if nothing is
 * available it will only try to read 1 byte. an then check if there is more.
 * 
 * @author Richard van Nieuwenhoven
 */
public class MinimalBlockinInputStream extends InputStream {

    /**
     * the wrapped input stream.
     */
    private final InputStream in;

    /**
     * Constructor for the wrapped input stream.
     * 
     * @param in
     *            the wrapped input stream.
     */
    public MinimalBlockinInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len > 0) {
            // we want to minimize blocking so we will only read as much as is
            // available.
            if (in.available() > 0) {
                return in.read(b, off, Math.min(in.available(), len));
            } else {
                // we wait for one byte to come.
                int count = in.read(b, off, 1);
                // maybe there is more than one byte available now?
                if (in.available() > 0) {
                    return in.read(b, off + count, Math.min(in.available(), len - count)) + count;
                }
                return count;
            }
        } else {
            return 0;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }
}
