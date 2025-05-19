package wxorg;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class App {

    public static void main(String[] args) throws LifecycleException {
        new App().run();
    }

    public void run() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector1 = tomcat.getConnector();
        connector1.setPort(8080);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);
        String servletName = "ListServlet";
        String urlPattern = "/bookmarks";

        tomcat.addServlet(contextPath, servletName, new ListServlet());
        context.addServletMappingDecoded(urlPattern, servletName);

        tomcat.start();
        tomcat.getService().addConnector(connector1);
        tomcat.getServer().await();
    }
}
