package org.vertx.ceylon.platform.impl;

import org.jboss.modules.LocalLoader;
import org.jboss.modules.Resource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class ClassLoaderLocalLoader implements LocalLoader {

  private static final Method getPackage;

  private final ClassLoader classLoader;

  static {
    getPackage = AccessController.doPrivileged(new PrivilegedActionImpl());
  }

  /**
   * Construct a new instance.
   *
   * @param classLoader the classloader to which we delegate
   */
  ClassLoaderLocalLoader(final ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  // Public members

  public Class<?> loadClassLocal(final String name, final boolean resolve) {
    try {
      return Class.forName(name, resolve, classLoader);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  public Package loadPackageLocal(final String name) {
    try {
      return (Package) getPackage.invoke(classLoader, name);
    } catch (IllegalAccessException e) {
      throw new IllegalAccessError(e.getMessage());
    } catch (InvocationTargetException e) {
      try {
        throw e.getCause();
      } catch (RuntimeException re) {
        throw re;
      } catch (Error er) {
        throw er;
      } catch (Throwable throwable) {
        throw new UndeclaredThrowableException(throwable);
      }
    }
  }

  public List<Resource> loadResourceLocal(final String name) {
    final Enumeration<URL> urls;
    ClassLoader classLoader = this.classLoader;
    try {
      if (classLoader == null) {
        urls = ClassLoader.getSystemResources(name);
      } else {
        urls = classLoader.getResources(name);
      }
    } catch (IOException e) {
      return Collections.emptyList();
    }
    final List<Resource> list = new ArrayList<Resource>();
    while (urls.hasMoreElements()) {
      list.add(new URLResource(urls.nextElement()));
    }
    return list;
  }
}
