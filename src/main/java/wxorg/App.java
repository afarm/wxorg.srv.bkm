package wxorg;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class App {

    List<String> entryTypes; // ← можно добавлять свои

    RecursiveParser recursiveParser;

    String dir;

    List<Entry> allFilesEntries;

    Context context;

    Tomcat tomcat;

    EntriesService entriesService;

    ParserFile parserEntry;

    public static void main(String[] args) throws LifecycleException, IOException {
        new App().run();
    }

    public void run() throws LifecycleException, IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("xorg.cf"));

        dir = properties.getProperty("dir").split(";")[0];
        dir = expandPath(dir);
        entryTypes = Arrays.asList("Note", "Bookmark", "Task", "Reminder"); // ← можно добавлять свои
        parserEntry = new ParserFile(entryTypes);
        recursiveParser = new RecursiveParser(dir, parserEntry);
        entriesService  = new EntriesService(recursiveParser);

        tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        Connector connector = tomcat.getConnector();
        connector.setPort(9000);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        context = tomcat.addContext(contextPath, docBase);

        ListServlet listServlet = new ListServlet("/bookmarks", entriesService);
        addServlet(listServlet);

        tomcat.start();
        tomcat.getService().addConnector(connector);
        tomcat.getServer().await();
    }

    void addServlet(ServletWrapper servletWrapper) {
        String servletName = servletWrapper.getClass().getSimpleName();
        tomcat.addServlet(context, servletName, servletWrapper);
        context.addServletMappingDecoded(servletWrapper.getServletPath(), servletName);

    }

    public static String expandPath(String path) {
        if (path.startsWith("~" + File.separator) || path.equals("~")) {
            String home = System.getProperty("user.home");
            return home + path.substring(1);
        }
        return path;
    }
}
