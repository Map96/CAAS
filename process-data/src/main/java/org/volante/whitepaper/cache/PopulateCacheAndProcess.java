package org.volante.whitepaper.cache;

import javax.servlet.*;

import static org.volante.whitepaper.cache.CreateAndInsertSamples.*;
import static org.volante.whitepaper.cache.FetchCacheAndProcess.*;
import static org.volante.whitepaper.cache.StartCDC.*;

public class PopulateCacheAndProcess implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        pumpDataToDb();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startCDC();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startCacheOps();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
