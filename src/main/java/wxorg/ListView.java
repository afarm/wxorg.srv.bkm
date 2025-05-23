package wxorg;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ListView  {

    private final EntriesService entriesService;

    public ListView(EntriesService entriesService) throws IOException {

        this.entriesService = entriesService;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");

        String queryString = request.getQueryString();
        List<Entry> resEntries = entriesService.initAllFilesEntries();

        // tag filter

        if (queryString != null) {
            Map<String, String> query = QueryParser.parse(queryString);
            String sortField = query.get("sortField");
            if (sortField != null) {
                // sort
                EntrySorter.sortByField(resEntries, sortField,
                        Objects.equals(query.get("sortOrder"), "true"));
            }
        }

        String resStr = """
                <html>
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
            resStr += String.format("%s ", entry.uid);
            resStr += String.format("<a href='?act=del&uid=%s'>[x]</a> ", entry.uid);
            resStr += String.format("%s ", entry.dateStr);
            resStr += String.format("%-8s ", entry.type);
            resStr += String.format("<a href='/edit?uid=%s'>[edt]</a> ", entry.uid);
            String hdr = StringUtils.abbreviate(entry.header, 80);
            if (entry.url != null) {
                resStr += String.format("<a href='%s'>%-80s</a>", entry.url, hdr);
            } else {
                resStr += String.format("%-80s", hdr);
            }
            resStr += " ";
            resStr += String.format("%-80s", entry.tags);
            resStr += "\n";
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
