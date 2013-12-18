package org.vertx.ceylon.platform.impl;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.Verticle;
import org.vertx.java.platform.VerticleFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Julien Viet
 */
public class CeylonVerticleFactory implements VerticleFactory {

  /** . */
  private ClassLoader loader;

  /** . */
  private Vertx vertx;

  /** . */
  private Container container;

  /** . */
  private File sysRep;

  /** . */
  private Method loadMethod;

  @Override
  public void init(Vertx vertx, Container container, ClassLoader cl) {

    // Init the module system
    String envCeylonHome = System. getenv("CEYLON_HOME");
    if (envCeylonHome == null) {
      throw new RuntimeException("No CEYLON_HOME found");
    }
    File ceylonHome = new File(envCeylonHome);
    File sysRep = new File(ceylonHome, "repo");
    System.setProperty("module.path", sysRep.getAbsolutePath());

    //
    this.loader = cl;
    this.vertx = vertx;
    this.container = container;
    this.sysRep = sysRep;
  }

  private Method getLoadMethod(Module runtimeModule) throws Exception {

    //
    String launcherClassName = "org.vertx.ceylon.platform.impl.Launcher";
    List<String> classNames = Arrays.asList(
        launcherClassName,
        "org.vertx.ceylon.platform.impl.ClassLoaderLocalLoader",
        "org.vertx.ceylon.platform.impl.URLResource",
        "org.vertx.ceylon.platform.impl.PrivilegedActionImpl",
        "org.vertx.ceylon.platform.impl.CeylonModuleLoaderExt");
    final HashMap<String, byte[]> classBytes = new HashMap<String, byte[]>();
    for (String className : classNames) {
      classBytes.put(className, loadBytes(className));
    }

    // Load the launcher from a class loader that has the runtime module as parent
    // so it sees the same classes than the module
    ClassLoader launcherLoader = new ClassLoader(runtimeModule.getClassLoader()) {
      @Override
      protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.get(name);
        if (bytes != null) {
          return defineClass(name, bytes, 0, bytes.length);
        } else {
          return super.findClass(name);
        }
      }
    };
    Class launcherClass = launcherLoader.loadClass(launcherClassName);
    return launcherClass.getMethod("loadModule", File.class, ClassLoader.class, ModuleIdentifier.class);
  }

  @Override
  public Verticle createVerticle(String main) throws Exception {
    if (main.endsWith(".module")) {

      //
      if (loadMethod == null) {
        Module runtimeModule;
        try {
          ModuleLoader ml = Module.getBootModuleLoader();
          runtimeModule = ml.loadModule(ModuleIdentifier.create("ceylon.runtime", "1.0.0"));
        }
        catch (ModuleLoadException e) {
          throw new RuntimeException("Module system initialization error", e);
        }
        try {
          loadMethod = getLoadMethod(runtimeModule);
        }
        catch (Exception e) {
          throw new RuntimeException("Initialization error", e);
        }
      }

      // Load the module
      ModuleIdentifier moduleIdentifier = ModuleIdentifier.fromString(main.substring(0, main.length() - ".module".length()).replace('/', ':'));
      Module module;
      try {
        module = (Module)loadMethod.invoke(null, sysRep, loader, moduleIdentifier);
      }
      catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
      catch (Error err) {
        err.printStackTrace();
        throw err;
      }

      //
      CeylonVerticle verticle = new CeylonVerticle(module, vertx, container);
      verticle.setVertx(vertx);
      verticle.setContainer(container);
      return verticle;
    } else {
      throw new Exception("Incorrect module name " + main);
    }
  }

  @Override
  public void reportException(Logger logger, Throwable t) {
    logger.error("Exception in Java verticle", t);
  }

  @Override
  public void close() {
  }

  private byte[] loadBytes(String className) throws IOException {
    InputStream in = getClass().getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class");
    byte[] buffer = new byte[512];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    while (true) {
      int l = in.read(buffer, 0, buffer.length);
      if (l == -1) {
        break;
      } else {
        baos.write(buffer, 0, l);
      }
    }
    return baos.toByteArray();
  }
}
