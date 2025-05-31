package wxorg;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;

/**
 * javascript:void window.open('http://localhost:9000/add?url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title)+'&sel='+encodeURIComponent(window.getSelection()))
 * http://localhost:9000/list?sortField=date
 * todo:
 * text in list view
 * all types .*?
 * idxTypes
 *
 *
 */
public class App {

    Context context;

    Tomcat tomcat;

    public static void main(String[] args) throws LifecycleException, IOException {
        new App().run();
    }

    public void run() throws LifecycleException, IOException {
        tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector = tomcat.getConnector();
        connector.setPort(9000);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        context = tomcat.addContext(contextPath, docBase);

        MainServlet mainServlet = new MainServlet();
        String servletName = mainServlet.getClass().getSimpleName();
        tomcat.addServlet(context, servletName, mainServlet);
        context.addServletMappingDecoded("/", servletName);

        tomcat.start();
        tomcat.getService().addConnector(connector);
        tomcat.getServer().await();
    }

}
