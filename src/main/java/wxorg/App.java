package wxorg;

import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App {

    public static List<String> entryTypes = Arrays.asList("Note", "Bookmark", "Task", "Reminder"); // ← можно добавлять свои

    public static RecursiveParser recursiveParser = new RecursiveParser();

    public static String dir = "/home/f/xorg/xorg";

    public static List<Entry> allFilesEntries;

    static {
        try {
            allFilesEntries = recursiveParser.parse(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> allUrls = new HashSet<>();

    private Context context;

    private Tomcat tomcat;

    public static void main(String[] args) throws LifecycleException {
        new App().run();
    }

    public void run() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector = tomcat.getConnector();
        connector.setPort(9000);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        context = tomcat.addContext(contextPath, docBase);

        ListServlet listServlet = new ListServlet("/bookmarks");
        addServlet(listServlet);

        tomcat.start();
        tomcat.getService().addConnector(connector);
        tomcat.getServer().await();
    }

    void addServlet(ServletWrapper servletWrapper) {
        String servletName = servletWrapper.getClass().getSimpleName();
        tomcat.addServlet(context, servletName, servletWrapper);
        context.addServletMappingDecoded(servletWrapper.getPath(), servletName);

    }
}
