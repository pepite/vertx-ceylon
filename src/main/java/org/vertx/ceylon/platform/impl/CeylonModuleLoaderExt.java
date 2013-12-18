package org.vertx.ceylon.platform.impl;

import ceylon.modules.jboss.runtime.CeylonModuleLoader;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleSpec;

import java.lang.reflect.Field;

/**
 * @author Julien Viet
 */
public class CeylonModuleLoaderExt extends CeylonModuleLoader {

  /** . */
  private final ClassLoaderLocalLoader loader;

  public CeylonModuleLoaderExt(ClassLoaderLocalLoader loader, RepositoryManager repository) throws Exception {
    super(repository);

    //
    this.loader = loader;
  }

  @Override
  protected ModuleSpec findModule(ModuleIdentifier moduleIdentifier) throws ModuleLoadException {
    ModuleSpec module = super.findModule(moduleIdentifier);
    if (module != null) {
      try {
        Field f = module.getClass().getDeclaredField("fallbackLoader");
        f.setAccessible(true);
        f.set(module, loader);
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return module;
  }
}
