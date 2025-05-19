package wxorg;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("test1");
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}