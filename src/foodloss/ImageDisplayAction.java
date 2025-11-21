package foodloss;
import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.MerchandiseImage;
import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

public class ImageDisplayAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        System.out.println("===== ImageDisplayAction START =====");

        String idStr = req.getParameter("imageId");
        System.out.println("Received imageId parameter: " + idStr);

        if (idStr == null || idStr.isEmpty()) {
            System.out.println("ERROR: imageId is null or empty");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "画像IDが指定されていません");
            return;
        }

        int imageId = Integer.parseInt(idStr);
        System.out.println("Parsed imageId: " + imageId);

        Connection con = new DBManager().getConnection();
        System.out.println("DB Connection established");

        MerchandiseImageDAO dao = new MerchandiseImageDAO(con);
        MerchandiseImage img = dao.selectById(imageId);

        if (img == null) {
            System.out.println("ERROR: Image not found for ID: " + imageId);
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            con.close();
            return;
        }

        System.out.println("Image found, data length: " + (img.getImageData() != null ? img.getImageData().length : 0));

        res.setContentType("image/jpeg");
        OutputStream out = res.getOutputStream();
        out.write(img.getImageData());
        out.flush();

        con.close();
        System.out.println("===== ImageDisplayAction END =====");
    }
}