package foodloss;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Merchandise;
import bean.MerchandiseImage;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

public class MerchDetailAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String merchIdStr = req.getParameter("merchId");

        if (merchIdStr == null) {
            res.sendRedirect(req.getContextPath() + "/foodloss/Menu.action");
            return;
        }

        int merchId = Integer.parseInt(merchIdStr);

        try (Connection con = new DBManager().getConnection()) {
            MerchandiseDAO merchDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);

            Merchandise merch = merchDAO.selectById(merchId);
            List<MerchandiseImage> imageList = imageDAO.selectByMerchandiseId(merchId);

            merch.setImages(imageList);

            req.setAttribute("merch", merch);

        } catch (Exception e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("/jsp/merch_detail.jsp").forward(req, res);
    }
}
