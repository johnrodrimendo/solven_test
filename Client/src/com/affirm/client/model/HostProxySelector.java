package com.affirm.client.model;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by jarmando on 05/01/17.
 */
public class HostProxySelector
        extends ProxySelector {
    ProxySelector origSelector = null;
    ArrayList<URI> proxiedLocations = null;
    ArrayList<Proxy> proxies = null;

    public HostProxySelector(ProxySelector def, ArrayList<URI> proxiedUrls, ArrayList<Proxy> proxies) {
        this.origSelector = def;
        this.proxiedLocations = proxiedUrls;
        this.proxies = proxies;
    }

    @Override
    public List<Proxy> select(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null");
        }
        if (this.proxiedLocations != null && this.proxies != null) {
            for (int i = 0; i < this.proxiedLocations.size(); ++i) {
                URI location = this.proxiedLocations.get(i);
                if (!uri.getHost().equals(location.getHost()) || !uri.getScheme().equals(location.getScheme())) continue;
                return this.proxies;
            }
        }
        if (this.origSelector != null) {
            return this.origSelector.select(uri);
        }
        ArrayList<Proxy> l = new ArrayList<Proxy>();
        l.add(Proxy.NO_PROXY);
        return l;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        if (uri == null || sa == null || ioe == null) {
            throw new IllegalArgumentException("Arguments can't be null");
        }
        if (this.origSelector != null) {
            this.origSelector.connectFailed(uri, sa, ioe);
        }
    }
}