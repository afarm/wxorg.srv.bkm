package wxorg;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Map;

public class EditServlet extends ServletWrapper {

    private EntriesService entriesService;

    public EditServlet(String servletPath, EntriesService entriesService) {
        this.entriesService = entriesService;
        this.servletPath = servletPath;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");

        Map<String, String> query = QueryParser.parse(request.getQueryString());

        String uid = URLDecoder.decode(query.get("uid"));
        Entry entry = entriesService.getById(uid);

        String textarea = "";
        String resStr = "--";
        if (entry != null) {
            textarea += String.format("Bookmark: %s %s %s\n", entry.header, entry.uid, entry.dateStr);
            textarea += String.format("Url:      %s\n", entry.url);
            textarea += String.format("Tags:     %s\n", entry.tags);
            resStr = EditTemplate.get(textarea);
        }
        response.getWriter().write(resStr);
    }

}