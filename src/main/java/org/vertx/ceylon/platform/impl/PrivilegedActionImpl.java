package org.vertx.ceylon.platform.impl;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/**
 * @author Julien Viet
 */
public class PrivilegedActionImpl implements PrivilegedAction<Method> {

  @Override
  public Method run() {
    for (Method method : ClassLoader.class.getDeclaredMethods()) {
      if (method.getName().equals("getPackage")) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {
          method.setAccessible(true);
          return method;
        }
      }
    }
    throw new IllegalStateException("No getPackage method found on ClassLoader");
  }
}
