package wxorg;

import jakarta.servlet.http.HttpServlet;

public class ServletWrapper extends HttpServlet {

    protected String servletPath;

    public String getServletPath() {
        return servletPath;
    }
}
