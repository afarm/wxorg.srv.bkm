package wxorg;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wxorg.util.EntrySorter;
import wxorg.util.QueryParser;
import wxorg.view.AddView;
import wxorg.view.EditView;
import wxorg.view.ListView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainServlet extends HttpServlet {

    private final DataSourceService dataSourceService;

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
        dataSourceService = new DataSourceService(recursiveParser, dir);
        listView = new ListView(dataSourceService);
        addView = new AddView();
        editView = new EditView(dataSourceService, dir);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        request.setCharacterEncoding("UTF-8");

        String act = request.getParameter("act");
        String uid = request.getParameter("uid");

        // todo switch ()
        String data = request.getParameter("data");
        if (data != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + uid + ".txt", StandardCharsets.UTF_8));
            writer.write(data);
            writer.close();
            response.sendRedirect("?act=list&sortField=date&sortOrder=desc");
            return;
        } else if ("list".equals(act)) {
            listView.service(request, response);
        } else if ("add".equals(act)) {
            addView.service(request, response);
        } else if ("edit".equals(act)) {
            editView.service(request, response);
        } else if ("del".equals(act)) {
            dataSourceService.delete(request.getParameter("uid"));
            response.sendRedirect("?act=list&sortField=date&sortOrder=desc");
            return;
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
