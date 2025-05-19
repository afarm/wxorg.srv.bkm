package wxorg;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddServlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("test");
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}