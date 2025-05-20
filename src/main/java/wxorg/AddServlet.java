package wxorg;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddServlet extends ServletWrapper {

    private EntriesService entriesService;

    public AddServlet(String servletPath, EntriesService entriesService) {
        this.entriesService = entriesService;
        this.servletPath = servletPath;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Type", "text/html; charset=utf-8");

        Map<String, String> query = QueryParser.parse(request.getQueryString());

        String title = URLDecoder.decode(query.get("title"));
        String url = URLDecoder.decode(query.get("url"));

//        if(App.allUrls.contains(url)) {
//            System.out.println("Doublicate url = " + url);
//            return;
//        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm");
        String textarea = "";
        textarea += String.format("Bookmark: %s %s %s\n", title, RandomString.random(), simpleDateFormat.format(new Date()));
        textarea += String.format("Url:      %s\n", url);
        textarea += String.format("Tags:     %s\n", "");
        String resStr = EditTemplate.get(textarea);
        response.getWriter().write(resStr);
    }

}