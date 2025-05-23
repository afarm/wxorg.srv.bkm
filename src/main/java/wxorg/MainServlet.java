package wxorg;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MainServlet extends HttpServlet {

    private final EntriesService entriesService;

    List<String> entryTypes; // ← можно добавлять свои

    RecursiveParser recursiveParser;

    String dir;

    ParserFile parserEntry;

    ListView listView;

    AddView addView;

    EditView editView;

    public MainServlet() throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("xorg.cf"));

        dir = properties.getProperty("dir").split(";")[0];
        dir = expandPath(dir);
        entryTypes = Arrays.asList("Note", "Bookmark", "Task", "Reminder"); // ← можно добавлять свои
        parserEntry = new ParserFile(entryTypes);
        recursiveParser = new RecursiveParser(dir, parserEntry);
        entriesService = new EntriesService(recursiveParser);
        listView = new ListView(entriesService);
        addView = new AddView();
        editView = new EditView(entriesService, dir);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String act = request.getParameter("act");
        // todo switch ()
        if ("list".equals(act)) {
            listView.service(request, response);
        } else if ("add".equals(act)) {
            addView.service(request, response);
        } else if ("edit".equals(act)) {
            editView.service(request, response);
        }
    }

    public static String expandPath(String path) {
        if (path.startsWith("~" + File.separator) || path.equals("~")) {
            String home = System.getProperty("user.home");
            return home + path.substring(1);
        }
        return path;
    }
}
