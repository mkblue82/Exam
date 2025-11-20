package foodloss;

import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.MerchandiseImage;
import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

@WebServlet("/ImageDisplay.action")
public class ImageDisplayAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String idStr = req.getParameter("imageId");
        if (idStr == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int imageId = Integer.parseInt(idStr);

        try (Connection con = new DBManager().getConnection()) {
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);
            MerchandiseImage img = imageDAO.selectById(imageId);

            if (img == null || img.getImageData() == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 画像の MIME タイプは拡張子から推測
            String fileName = img.getFileName().toLowerCase();
            if (fileName.endsWith(".png")) {
                res.setContentType("image/png");
            } else if (fileName.endsWith(".gif")) {
                res.setContentType("image/gif");
            } else {
                res.setContentType("image/jpeg");
            }

            res.setContentLength(img.getImageData().length);

            try (OutputStream os = res.getOutputStream()) {
                os.write(img.getImageData());
            }
        }
    }
}
