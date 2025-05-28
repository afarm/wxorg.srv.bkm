package wxorg.view;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import wxorg.DataSourceService;
import wxorg.Entry;
import wxorg.util.EntrySorter;
import wxorg.util.QueryParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ListView {

    private final DataSourceService dataSourceService;

    public ListView(DataSourceService entriesService) throws IOException {

        this.dataSourceService = entriesService;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Entry> resEntries = dataSourceService.initAllFilesEntries();

        // tag filter
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (sortField != null) {
            if (sortField != null) {
                // sort
                EntrySorter.sortByField(resEntries, sortField,
                        Objects.equals(sortOrder, "true"));
            }
        }

        String resStr = """
                <!DOCTYPE html>
                <html>
                 <head>
                  <meta charset="utf-8">
                  <title>Ссылки</title>
                  <style>
                   a { text-decoration: none; }
                   div:hover { background: #f0f0f0; }
                   a:hover { text-decoration: underline; }
                  </style>
                 </head>
                 <body>
                 <pre>
                  """;
        resStr += "Type: ";
        resStr += "<a href='?type=Note'>[-] Note</a> ";
        resStr += "<a href='?type=Bookmark'>[-] Bookmark</a> ";
        resStr += ".. \n";

        resStr += "Tag : ";
        resStr += "<a href='?tag=Work'>[-] Work</a> ";
        resStr += "<a href='?tag=Jira'>[-] Jira</a> ";
        resStr += "<a href='?tag=Java'>[-] Java</a> ";
        resStr += ".. \n";

        resStr += "Sort: ";
        resStr += "<a href='?sortField=header'>[-] Header</a> ";
        resStr += "<a href='?sortField=date'>[^] Date</a> ";
        resStr += "| ";

        resStr += "<a href='?'>[x] Tree</a> ";
        resStr += "<a href='?'>[ ] Text</a> ";
        resStr += "<a href='?'>[ ].arch</a> ";
        resStr += "<a href='?'>[ ].del</a> ";
        resStr += "<a href='?'>Trash</a> ";
        resStr += "\n\n";

        for (Entry entry : resEntries) {
            resStr += String.format("<div> ");
            resStr += String.format("%s ", entry.uid);
            resStr += String.format("<a href='?act=del&uid=%s'>[x]</a> ", entry.uid);
            resStr += String.format("%s ", entry.dateStr);
            resStr += String.format("%-8s ", entry.type);
            resStr += String.format("<a href='/?act=edit&uid=%s'>[edt]</a> ", entry.uid);
            String hdr = StringUtils.abbreviate(entry.header, 80);
            if (entry.url != null) {
                resStr += String.format("<a href='%s'>%-80s</a>", entry.url, hdr);
            } else {
                resStr += String.format("%-80s", hdr);
            }
            resStr += " ";
            resStr += String.format("%-80s", entry.tags);
            resStr += String.format("</div>");
            resStr += "";
        }
        response.getWriter().write(resStr);
    }


    public static String expandPath(String path) {
        if (path.startsWith("~" + File.separator) || path.equals("~")) {
            String home = System.getProperty("user.home");
            return home + path.substring(1);
        }
        return path;
    }
}
