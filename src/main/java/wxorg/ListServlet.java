package wxorg;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static wxorg.App.allFilesEntries;
import static wxorg.App.allUrls;

public class ListServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        allFilesEntries = App.recursiveParser.parse(App.dir);
        for (Entry entry : allFilesEntries) {
            allUrls.add(entry.url);
        }

        String queryString = request.getQueryString();
        if (queryString != null) {
            Map<String, String> query = QueryParser.parse(queryString);
            String sortField = query.get("sortField");
            if (sortField != null) {
                QuerySorter.sortByFiled(allFilesEntries, sortField,
                                      
                                        Objects.equals(query.get("sortOrder"), "true"));
            }
        }

        String resStr = "";
        resStr += "<html>";
        resStr += "<pre>";
        resStr += "<a href='?tag=Work>Work</a> / ";
        resStr += "<a href='?tag=Jira>Jira</a>";
        resStr += " Sort: ";
        resStr += "<a href='?sortFiled=header'>Header</a>";
        resStr += "<a href='?sortFiled=date'>Date</a> | ";
        resStr += "<a href='?'>Tree</a> | ";
        resStr += "<a href='?'>Trash</a>| ";
        for (Entry entry : allFilesEntries) {
            resStr += String.format("%s ", entry.uid);
            resStr += String.format("<a href='?del'>[x]</a> ");
            resStr += String.format("%s ", entry.dateStr);
            resStr += String.format("- %s -", entry.type);
            resStr += String.format("<a href='?edit'>[edt]</a>");
            if (entry.url != null) {
                resStr += String.format("<a href='%s'>%s</a>", entry.url, entry.header);
            } else {
                resStr += String.format("%s", entry.header);
            }
            resStr += "\n";
        }
        resp.getWriter().write(resStr);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}
