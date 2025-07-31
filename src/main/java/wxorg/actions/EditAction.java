package wxorg.actions;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wxorg.DataSourceService;
import wxorg.template.Template;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditAction {

    private final DataSourceService dataSourceService;

    private final String dir;

    private final XmlMapper xmlMapper;

    private final String editTemplatePath;

    public EditAction(DataSourceService dataSourceService, String dir, XmlMapper xmlMapper, String editTemplatePath) {
        this.dataSourceService = dataSourceService;
        this.dir = dir;
        this.xmlMapper = xmlMapper;
        this.editTemplatePath = editTemplatePath;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getMethod().equals("POST") && request.getParameter("save") != null) {
            save(request);
            response.sendRedirect("?act=list&sortField=sdate&sortOrder=desc");
        } else {
            view(request, response);
        }
    }

    // read xml jackson
    void view(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Template editTemplate = new Template(editTemplatePath);

        String id = request.getParameter("id");
        Map<String, String> entry = dataSourceService.getById(id);
        String path = entry.get("_file");
        // Note or Book ...
        String xmlStr = Files.readString(Path.of(path));

        Matcher matcher = Pattern.compile("<(\\w+) ").matcher(xmlStr);
        String type = "";
        if(matcher.find()) {
            type = matcher.group(1);
        }

        Map<String, String> note = xmlMapper.readValue(xmlStr, Map.class);
        // todo get type
        // todo ~ custom parser
        note.put("_file", path);
        note.put("type", type);

        String resTemplate = editTemplate.substitute(note);

        // todo validate with DS

        response.getWriter().write(resTemplate);
//        Process process = new ProcessBuilder("idea", "file" + entry.id + ".txt").start();
    }

    void save(HttpServletRequest request) throws IOException {
        String id = request.getParameter("id");
        String cdate = request.getParameter("cdate");
        String type = request.getParameter("type");
        String name = request.getParameter("name");
        String body = request.getParameter("body");
        String tags = request.getParameter("tags");
        String url = request.getParameter("url");

        // 1. read file with id (xml as string)
        //    not only in dir

        // 2. for attr-s
        //    find attrName attrVal by paramName as regex
        //    if   find paramVal != attrVal - replace
        //    else add atttname="attrVal" in place?

        BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + id + ".xml", StandardCharsets.UTF_8));

        String resStr = "EditTemplate.get()";
        resStr = resStr.replace("{id}", id);
        resStr = resStr.replace("{cdate}", cdate);
        resStr = resStr.replace("{type}", type);
        resStr = resStr.replace("{name}", name != null ? name : "");
        resStr = resStr.replace("{tags}", tags);
        resStr = resStr.replace("{url}", url != null ? id : "");
        resStr = resStr.replace("{body}", body);
        resStr = resStr.replace("{backRefs}", "backRefs");

        writer.write(resStr);
        writer.close();
    }
}