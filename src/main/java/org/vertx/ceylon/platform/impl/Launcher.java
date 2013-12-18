package org.vertx.ceylon.platform.impl;

import ceylon.modules.jboss.runtime.CeylonModuleLoader;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils;
import com.redhat.ceylon.cmr.impl.JULLogger;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * @author Julien Viet
 */
public class Launcher {

  public static Module loadModule(File sysRep, ClassLoader loader, ModuleIdentifier moduleIdentifier) throws Exception {

    //
    Field field = CeylonModuleLoader.class.getDeclaredField("JDK_MODULE_NAMES");
    field.setAccessible(true);
    HashSet<String> a = (HashSet<String>)field.get(null);
    a.add("io.vertx.core");
    a.add("io.vertx.platform");

    //
    CeylonUtils.CeylonRepoManagerBuilder metaBuilder = CeylonUtils.repoManager();
    metaBuilder.systemRepo(sysRep.getAbsolutePath());
    metaBuilder.logger(new JULLogger());
    RepositoryManagerBuilder builder = metaBuilder.buildManagerBuilder();
    RepositoryManager repo = builder.buildRepository();

    // Extend to add system classloader as fallback
    CeylonModuleLoader moduleLoader = new CeylonModuleLoaderExt(new ClassLoaderLocalLoader(loader), repo);

    //
    return moduleLoader.loadModule(moduleIdentifier);
  }
}
