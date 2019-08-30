package ibs.sandbox.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/")
public class TestPage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext servletContext = req.getServletContext();
        InputStream res = servletContext.getResourceAsStream("/index.html");
        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");

        int n = 0;
        int bytesRead = 0;
        byte buffer[] = new byte[1000];
        while((n = res.read(buffer) ) != -1) {
            out.write(buffer);
            out.flush();
            bytesRead += n;
        }
        out.close();
        res.close();
    }
}
