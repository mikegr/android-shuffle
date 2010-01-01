package org.dodgybits.shuffle.web.server.persistence;

import javax.servlet.*;
import java.io.IOException;

/**
 * Ensures we properly release persistence resources even in
 * the face of exceptions.
 */
public class PersistenceFilter implements javax.servlet.Filter {
  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws ServletException, IOException {
    try {
      chain.doFilter(req, resp);
    } finally {
      JdoUtils.closePm();
    }
  }

  public void init(FilterConfig config) throws ServletException {
  }
}
