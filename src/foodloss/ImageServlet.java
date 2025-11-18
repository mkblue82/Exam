package foodloss;


import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.MerchandiseImage;
import dao.MerchandiseImageDAO;
import tool.DBManager;


@WebServlet("/image")
public class ImageServlet extends HttpServlet {
private static final long serialVersionUID = 1L;


@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException {


String idStr = request.getParameter("id");
if (idStr == null) {
response.sendError(HttpServletResponse.SC_BAD_REQUEST);
return;
}


try {
int imageId = Integer.parseInt(idStr);


MerchandiseImageDAO dao = new MerchandiseImageDAO(DBManager.getInstance().getConnection());
MerchandiseImage image = dao.selectById(imageId);


if (image != null && image.getImageData() != null) {
response.setContentType("image/jpeg");
OutputStream out = response.getOutputStream();
out.write(image.getImageData());
out.close();
} else {
response.sendError(HttpServletResponse.SC_NOT_FOUND);
}


} catch (Exception e) {
e.printStackTrace();
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}
}
}