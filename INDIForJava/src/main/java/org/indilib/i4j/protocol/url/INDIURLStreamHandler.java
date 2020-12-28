package org.indilib.i4j.protocol.url;

/*
 * #%L
 * INDI for Java Base Library
 * %%
 * Copyright (C) 2013 - 2014 indiforjava
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * A handler for INDI connections. This is still a work in progress
 * 
 * @author Richard van Nieuwenhoven [ritchie [at] gmx.at]
 */
public class INDIURLStreamHandler extends URLStreamHandler {

    /**
     * The protocol name for the normal indi tcp protocol.
     */
    public static final String PROTOCOL = "indi";

    /**
     * The indi default port number.
     */
    private static final int INDI_DEFAULT_PORT = 7624;

    @Override
    protected final int getDefaultPort() {
        return INDI_DEFAULT_PORT;
    }

    @Override
    protected final URLConnection openConnection(final URL url) throws IOException {
        return new INDIURLConnection(url);
    }

    @Override
    protected final void parseURL(final URL u, final String spec, final int start, final int end) {
        super.parseURL(u, spec, start, end);
    }
}
