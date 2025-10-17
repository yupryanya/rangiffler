package guru.qa.rangiffler.api.core;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {
  INSTANCE;

  private final ThreadLocal<CookieStore> threadLocalCookieStore = ThreadLocal.withInitial(
      this::inMemoryCookieStore
  );

  private CookieStore inMemoryCookieStore() {
    return new CookieManager().getCookieStore();
  }

  @Override
  public void add(URI uri, HttpCookie httpCookie) {
    getStore().add(uri, httpCookie);
  }

  @Override
  public List<HttpCookie> get(URI uri) {
    return getStore().get(uri);
  }

  @Override
  public List<HttpCookie> getCookies() {
    return getStore().getCookies();
  }

  @Override
  public List<URI> getURIs() {
    return getStore().getURIs();
  }

  @Override
  public boolean remove(URI uri, HttpCookie httpCookie) {
    return getStore().remove(uri, httpCookie);
  }

  @Override
  public boolean removeAll() {
    return getStore().removeAll();
  }

  public String getCookieValue(String name) {
    return getCookies().stream()
        .filter(cookie -> cookie.getName().equals(name))
        .findFirst()
        .map(HttpCookie::getValue)
        .orElseThrow();
  }

  private CookieStore getStore() {
    return threadLocalCookieStore.get();
  }
}
