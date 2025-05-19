package wxorg;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;

public class ServletWrapper extends HttpServlet {

    protected String path;

    public String getPath() {
        return path;
    }
}
