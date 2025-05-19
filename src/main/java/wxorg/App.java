package wxorg;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
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

        Class servletClass = ListServlet.class;
        String urlPattern = "/bookmarks";
        tomcat.addServlet(contextPath, servletClass.getSimpleName(), servletClass.getName());
        context.addServletMappingDecoded(urlPattern, servletClass.getSimpleName());

        servletClass = AddServlet.class;
        urlPattern = "/bookmarks";
        tomcat.addServlet(contextPath, servletClass.getSimpleName(), servletClass.getName());
        context.addServletMappingDecoded(urlPattern, servletClass.getSimpleName());

        tomcat.start();
        tomcat.getService().addConnector(connector1);
        tomcat.getServer().await();
    }
}
