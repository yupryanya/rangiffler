package guru.qa.rangiffler.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serializable;

public class SpecificRequestDumperFilter extends GenericFilter implements Filter, FilterConfig, Serializable {

  private final String[] urlPatterns;
  private final GenericFilter decorate;

  public SpecificRequestDumperFilter(GenericFilter decorate, String... urlPatterns) {
    this.decorate = decorate;
    this.urlPatterns = urlPatterns;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest hRequest) {
      for (String urlPattern : urlPatterns) {
        if (hRequest.getRequestURI().matches(urlPattern)) {
          decorate.doFilter(request, response, chain);
          return;
        }
      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    decorate.destroy();
  }
}
