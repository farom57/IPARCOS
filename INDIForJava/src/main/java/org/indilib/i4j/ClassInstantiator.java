package org.indilib.i4j;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to instantiate other classes based on their name and constructor
 * parameters.
 * 
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 */
public final class ClassInstantiator {

    /**
     * A logger for the errors.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClassInstantiator.class);

    /**
     * A private constructor to avoid instantiating this utility class.
     */
    private ClassInstantiator() {
    }

    /**
     * Instantiates an object from a list of possible classes. The object will
     * be of the class of the first instantiable class in an array.
     * 
     * @param possibleClassNames
     *            An array of class names from which to try to instantiate a
     *            object.
     * @param arguments
     *            The arguments for the constructors.
     * @return The first object that can be instantiated from a list of classes.
     * @throws INDIException
     *             if there is no suitable class to be instantiated.
     */
    public static Object instantiate(final String[] possibleClassNames, final Object[] arguments) throws INDIException {
        /*
         * Class[] argumentClasses = new Class[arguments.length]; for (int i =
         * 0; i < argumentClasses.length; i++) { argumentClasses[i] =
         * arguments[i].getClass(); }
         */

        for (String className : possibleClassNames) {
            try {
                Class theClass = Class.forName(className);
                Constructor[] constructors = theClass.getConstructors();

                Constructor constructor = getSuitableConstructor(constructors, arguments);
                Object obj = constructor.newInstance(arguments);

                return obj; // If the object could be instantiated return it.
            } catch (ClassNotFoundException ex) {
                LOG.error("Could not instantiate", ex);
            } catch (InstantiationException ex) {
                LOG.error("Could not instantiate", ex);
            } catch (IllegalAccessException ex) {
                LOG.error("Could not instantiate", ex);
            } catch (InvocationTargetException ex) {
                LOG.error("Could not instantiate", ex);
            }
        }

        throw new INDIException("No suitable class to instantiate. Probably some libraries are missing in the classpath.");
    }

    /**
     * Gets a suitable Constructor from a list. It check for the parameters to
     * coincide with a list of parameter classes.
     * 
     * @param constructors
     *            The list of constructors from which to get a suitable one.
     * @param arguments
     *            The array of parameters objects for the constructor.
     * @return The first suitable constructor for a set of parameters.
     * @throws INDIException
     *             If no suitable constructor is found.
     */
    private static Constructor getSuitableConstructor(final Constructor[] constructors, final Object[] arguments) throws INDIException {
        for (Constructor c : constructors) {
            Class[] cClassParam = c.getParameterTypes();
            boolean match = true;
            if (cClassParam.length != arguments.length) {
                match = false;
            }
            for (int h = 0; h < arguments.length; h++) {
                if (!cClassParam[h].isInstance(arguments[h])) {
                    match = false;
                }
            }

            if (match) {
                return c;
            }
        }

        throw new INDIException("No suitable class to instantiate. Probably some libraries are missing in the classpath.");
    }
}
