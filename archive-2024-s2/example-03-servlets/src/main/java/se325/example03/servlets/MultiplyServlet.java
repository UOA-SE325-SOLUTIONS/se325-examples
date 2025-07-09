package se325.example03.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MultiplyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int num1 = Integer.parseInt(req.getParameter("num1"));
        int num2 = Integer.parseInt(req.getParameter("num2"));

        int result = num1 * num2;

        resp.setContentType("text/plain");
        resp.getWriter().print(result);

//        resp.setContentType("application/octet-stream");
//        DataOutputStream out = new DataOutputStream(resp.getOutputStream());
//        out.writeInt(result);

    }
}
