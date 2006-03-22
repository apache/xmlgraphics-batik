package org.w3c.dom.window;

public interface Location {
    void setHref(String href);
    String getHref();
    void setHash(String hash);
    String getHash();
    void setHost(String host);
    String getHost();
    void setHostname(String host);
    String getHostname();
    void setPathname(String pathname);
    String getPathname();
    void setPort(String port);
    String getPort();
    void setProtocol(String protocol);
    String getProtocol();
    void setSearch(String search);
    String getSearch();

    void assign(String url);
    void replace(String url);
    void reload();

    String toString();
}
