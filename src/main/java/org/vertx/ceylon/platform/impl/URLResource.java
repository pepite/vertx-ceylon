package org.vertx.ceylon.platform.impl;

import org.jboss.modules.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

final class URLResource implements Resource {
  private final URL url;

  public URLResource(final URL url) {
    this.url = url;
  }

  public String getName() {
    return url.getPath();
  }

  public URL getURL() {
    return url;
  }

  public InputStream openStream() throws IOException {
    return url.openStream();
  }

  public long getSize() {
    return 0L;
  }
}
