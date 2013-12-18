package org.vertx.ceylon.platform.impl;

import org.jboss.modules.Module;
import org.vertx.java.core.Vertx;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.Verticle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Julien Viet
 */
public class CeylonVerticle extends Verticle {

  /** . */
  private final Object verticle;

  /** . */
  private final Class<?> verticleClass;

  public CeylonVerticle(Module module, Vertx vertx, Container container) throws Exception {

    Class<?> verticleClass = null;
    Object verticle = null;
    try {
      ClassLoader loader = module.getClassLoader();
      Class<?> vertxWrapperClass = loader.loadClass("io.vertx.ceylon.Vertx");
      Constructor vertxWrapperCtor = vertxWrapperClass.getConstructor(Vertx.class);
      Object vertxWrapper = vertxWrapperCtor.newInstance(vertx);
      Class<?> containerWrapperClass = loader.loadClass("io.vertx.ceylon.platform.Container");
      Constructor containerWrapperCtor = containerWrapperClass.getConstructor(Container.class);
      Object containerWrapper = containerWrapperCtor.newInstance(container);

      //
      verticleClass = loader.loadClass(module.getIdentifier().getName()+ ".Server");
      Constructor verticleCtor = verticleClass.getConstructor(vertxWrapperClass, containerWrapperClass);
      verticle = verticleCtor.newInstance(vertxWrapper, containerWrapper);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    //
    this.verticle = verticle;
    this.verticleClass = verticleClass;
  }

  @Override
  public void start() {
    _invoke("start");
  }

  @Override
  public void stop() {
    _invoke("stop");
  }

  private void _invoke(String method) {
    try {
      Method start = verticleClass.getMethod(method);
      start.invoke(verticle);
    }
    catch (Exception e) {
      container.logger().error("Could not " + method +" the verticle", e);
    }
  }
}
