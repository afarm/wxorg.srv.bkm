package wxorg.view;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import wxorg.DataSourceService;
import wxorg.Entry;
import wxorg.util.EntrySorter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ListView {

    private final DataSourceService dataSourceService;

    public ListView(DataSourceService entriesService) {
        this.dataSourceService = entriesService;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // tag filter
        List<Entry> resEntries = new ArrayList<>();
        String[] tags = request.getParameterValues("tag");
        if (tags != null) {
            HashSet<Entry> resEntriesSet = new HashSet<>();
            for (String tag : tags) {
                List<Entry> entries = dataSourceService.idxTags().get(tag);
                //if (resEntriesSet.size() > 0) {
                    for (Entry entry : entries) {
                        if (!resEntriesSet.contains(entry)) {
                            resEntriesSet.add(entry);
                        }
                    }
                //}
            }
            resEntries.addAll(resEntriesSet);
        } else {
            resEntries = dataSourceService.buildAllEntries();
        }

        String showBody = request.getParameter("showBody");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (sortField != null) {
            if (sortField != null) {
                // sort
                EntrySorter.sortByField(resEntries, sortField,
                        !Objects.equals(sortOrder, "desc"));
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
                   a:hover { text-decoration: underline; }
                   div:hover { background: #f0f0f0; }
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
        String url = "?act=list&sortField=date&sortOrder=desc";
        for (String tag : dataSourceService.idxTags().keySet()) {
            resStr += "<a href='" + url + "&tag=" + tag + "'>" + (ObjectUtils.isEmpty(tag) ? "--" : tag) + "</a> ";
        }
        resStr += "\n";

        resStr += "Sort: ";
        resStr += "<a href='?act=list&sortField=header&sortOrder=desc'>[-] Header</a> ";
        resStr += "<a href='?act=list&sortField=date&sortOrder=desc'>[^] Date</a> ";
        resStr += "| ";

        resStr += "<a href='?'>[x] Tree</a> ";
        resStr += "<a href='?'>[ ] Text</a> ";
        resStr += "<a href='?'>[ ].arch</a> ";
        resStr += "<a href='?'>[ ].del</a> ";
        resStr += "<a href='?'>Trash</a> ";
        resStr += "\n\n";

        for (Entry entry : resEntries) {
            resStr += String.format("<div> ");
            resStr += String.format("%-8s ", entry.type);
            String hdr = StringUtils.abbreviate(entry.header, 80);
            if (entry.url != null) {
                resStr += String.format("<a href='%s'><b>%-80s</b></a>", entry.url, hdr);
            } else {
                resStr += String.format("%-80s", hdr);
            }
            resStr += String.format("<a href='/?act=edit&uid=%s'>[edt]</a> ", entry.uid);
            resStr += String.format("%s ", entry.uid);
            resStr += String.format("%s ", entry.dateStr);
            resStr += String.format("<a href='?act=del&uid=%s'>[x]</a> ", entry.uid);
            resStr += String.format("%-80s", entry.tags);
            resStr += String.format("</div>");
            if (showBody != null) {
                resStr += String.format("<div style='color: #555' >");
                resStr += String.format("%s", entry.body.indent(10).replaceAll("(?m)^[ \t]*\r?\n", ""));
                resStr += String.format("</div>");
            }
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
