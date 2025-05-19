package wxorg;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Content-Type", "text/html; charset=utf-8");
        RecursiveParser recursiveParser = new RecursiveParser();
        List<Entry> entryList = recursiveParser.parse();
        String resStr = "";
        resStr += "<html>";
        resStr += "<pre>";
        for (Entry entry : entryList) {
            resStr += String.format("- %s -", entry.uid);
            resStr += String.format("- %s -", entry.dateStr);
            resStr += String.format("- %s -", entry.type);
            resStr += String.format("- %s -", entry.header);
            resStr += "\n";
        }
        resp.getWriter().write(resStr);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}