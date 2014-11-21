
package org.zarroboogs.study.net;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class BroserContent {

    private CookieStore mCookieStore = new BasicCookieStore();

    private CloseableHttpClient mHttpClient = (CloseableHttpClient) HttpClientFactory
            .creteHttpClient(mCookieStore);

    private static BroserContent mBroserContent = null;

    public static synchronized BroserContent getInstance() {
        if (mBroserContent == null) {
            mBroserContent = new BroserContent();
        }
        return mBroserContent;
    }

    public CookieStore getCookieStore() {
        return mCookieStore;
    }

    public CloseableHttpClient getHttpClient() {
        return mHttpClient;
    }

    // HttpClient
    public static class HttpClientFactory {
        private static HttpClient customerHttpClient;

        /**
         * @param mProxyHost
         * @return
         */
        public static synchronized HttpClient creteHttpClient(CookieStore mCookieStore, HttpHost... mProxyHost) {
            if (null == customerHttpClient) {
                HttpClientBuilder customBuilder = HttpClients.custom();

                PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
                manager.setMaxTotal(10);
                manager.setDefaultMaxPerRoute(10);
                customBuilder.setConnectionManager(manager);

                LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
                customBuilder.setRedirectStrategy(redirectStrategy);
                customBuilder.setDefaultCookieStore(mCookieStore);

                for (HttpHost mHost : mProxyHost) {
                    customBuilder.setProxy(mHost);
                }
                customerHttpClient = customBuilder.build();

            }
            return customerHttpClient;
        }
    }
}
