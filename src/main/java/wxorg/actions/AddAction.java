package wxorg.actions;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wxorg.util.RandomString;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddAction {

    private final String dir;

    public AddAction(String dir) {
        this.dir = dir;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getMethod().equals("POST") && request.getParameter("save") != null) {
            save(request);
            response.sendRedirect("?act=list&sortField=sdate&sortOrder=desc");
            return;
        }
        view(request, response);
    }

    void view(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String url = request.getParameter("url");
        String body = request.getParameter("body");
        // todo tags, type

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm");
        String id = RandomString.random();
        String type = "Note";
        String tags = "Work";
        response.getWriter().write("resStr");
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
        //String lines = Files.readAllLines(path);

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