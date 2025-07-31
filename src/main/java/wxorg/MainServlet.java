package wxorg;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wxorg.actions.AddAction;
import wxorg.actions.EditAction;
import wxorg.actions.ListAction;
import wxorg.template.Template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MainServlet extends HttpServlet {

    private final DataSourceService dataSourceService;

    List<String> entryTypes; // ← можно добавлять свои

    RecursiveParser recursiveParser;

    String dir;

    ParserXmlFile parserEntry;

    ListAction listAction;

    AddAction addAction;

    EditAction editAction;

    public MainServlet() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("xorg.cf"));
        dir = properties.getProperty("dir").split(";")[0];
        dir = expandPath(dir);
        entryTypes = Arrays.asList("Note", "Bookmark", "Task", "Reminder"); // ← можно добавлять свои

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        String editTemplatePath = "templates/edit.html";
        //String editTemplate = Files.readString(Path.of(editTemplatePath));

        String xmlTemplatePath = "templates/xml.xml";
        String xmlTemplate = Files.readString(Path.of(xmlTemplatePath));

        //Template editTemplate = new Template();

        parserEntry = new ParserXmlFile(entryTypes, xmlMapper);
        recursiveParser = new RecursiveParser(dir, parserEntry);
        dataSourceService = new DataSourceService(recursiveParser, dir);
        listAction = new ListAction(dataSourceService);
        addAction = new AddAction(dir);
        editAction = new EditAction(dataSourceService, dir, xmlMapper, editTemplatePath);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        request.setCharacterEncoding("UTF-8");
        String act = request.getParameter("act");
        // todo switch ()
        if ("list".equals(act)) {
            listAction.service(request, response);
        } else if ("add".equals(act)) {
            addAction.service(request, response);
        } else if ("edit".equals(act)) {
            editAction.service(request, response);
        } else if ("del".equals(act)) {
            dataSourceService.delete(request.getParameter("id"));
            response.sendRedirect("?act=list&sortField=sdate&sortOrder=desc");
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
