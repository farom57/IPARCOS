/*
 *  This file is part of INDI for Java.
 *  
 *  INDI for Java is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *  
 *  INDI for Java is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with INDI for Java.  If not, see 
 *  <http://www.gnu.org/licenses/>.
 */
package laazotea.indi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A class to instantiate other classes based on their name and constructor
 * parameters.
 *
 * @author S. Alonso (Zerjillo) [zerjioi at ugr.es]
 * @version 1.13, April 4, 2012
 */
public class ClassInstantiator {

  public static Object instantiate(String[] possibleClassNames, Object[] arguments) throws INDIException {
    Class[] argumentClasses = new Class[arguments.length];

    for (int i = 0 ; i < argumentClasses.length ; i++) {
      argumentClasses[i] = arguments[i].getClass();
    }

    for (int i = 0 ; i < possibleClassNames.length ; i++) {
      String className = possibleClassNames[i];

      try {
        Class theClass = Class.forName(className);
        Constructor[] constructors = theClass.getConstructors();

        Constructor constructor = getSuitableConstructor(constructors, arguments);
        Object obj = constructor.newInstance(arguments);

        return obj;  // If the object could be instantiated return it.
      } catch (ClassNotFoundException ex) {
     //   ex.printStackTrace();
      } catch (InstantiationException ex) {
     //   ex.printStackTrace();
      } catch (IllegalAccessException ex) {
     //   ex.printStackTrace();
      } catch (InvocationTargetException ex) {
     //   ex.printStackTrace();
      }
    }

    throw new INDIException("No suitable class to instantiate. Probably some libraries are missing in the classpath.");
  }

  private static Constructor getSuitableConstructor(Constructor[] constructors, Object[] arguments) throws INDIException {
    for (int i = 0 ; i < constructors.length ; i++) {
      Constructor c = constructors[i];

      Class[] cClassParam = c.getParameterTypes();
      boolean match = true;
      if (cClassParam.length != arguments.length) {
        match = false;
      }
      for (int h = 0 ; h < arguments.length ; h++) {
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
