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
        String idStr = req.getParameter("imageId");
        int imageId = Integer.parseInt(idStr);

        Connection con = new DBManager().getConnection();

        MerchandiseImageDAO dao = new MerchandiseImageDAO(con);
        MerchandiseImage img = dao.selectById(imageId);

        if (img == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        res.setContentType("image/jpeg");
        OutputStream out = res.getOutputStream();
        out.write(img.getImageData());
        out.flush();
    }
}
