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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * Provides the classes necessary to create INDI Clients, connections to INDI Servers and to inspect and modify INDI properties of Devices.
 * <p>
 * Usually a INDI Client will instantiate at least one <code>INDIServerConnection</code> to connect to a INDI Server. Additionally it can add some listeners to the INDI Devices,
 * INDI Properties or INDI Elements to be notified about changes in them. Additionally the values can be changed and those changes sent to the INDI Server. 
 * Note that most of the other classes are not usually instantiated in a direct way.
 * <p>
 * Please check the examples module for some examples of the creation of simple Clients. If you want a sample Client with a Graphical UI, please check the 
 * INDI for Java Client UI library.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]

 */

package org.indilib.i4j.client;

