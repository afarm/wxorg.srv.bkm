package wxorg;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

public class AddServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> query = QueryParser.parse(req.getQueryString());

        String title = URLDecoder.decode(query.get("title"));
        String url = URLDecoder.decode(query.get("url"));

//        if(App.allUrls.contains(url)) {
//            System.out.println("Doublicate url = " + url);
//            return;
//        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("test");

    }
}