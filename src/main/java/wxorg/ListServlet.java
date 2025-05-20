package wxorg;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListServlet extends ServletWrapper {

    private final EntriesService entriesService;

    public ListServlet(String servletPath, EntriesService entriesService) {
        this.entriesService = entriesService;
        this.servletPath = servletPath;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");

        String queryString = request.getQueryString();
        List<Entry> resEntries = entriesService.allFilesEntries();

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

        String resStr = "";
        resStr += "<html>";
        resStr += "<pre>";

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
            resStr += String.format("%s ", entry.type);
            resStr += String.format("<a href='?edit'>[edt]</a> ");
            if (entry.url != null) {
                resStr += String.format("<a href='%s'>%s</a>", entry.url, entry.header);
            } else {
                resStr += String.format("%s", entry.header);
            }
            resStr += "\n";
        }
        response.getWriter().write(resStr);
    }
}
