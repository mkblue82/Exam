package foodloss;

import java.sql.Connection;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Merchandise;
import bean.MerchandiseImage;
import bean.Store;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import dao.StoreDAO;
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
            StoreDAO storeDAO = new StoreDAO(con);

            // 商品情報取得
            Merchandise merch = merchDAO.selectById(merchId);
            List<MerchandiseImage> imageList = imageDAO.selectByMerchandiseId(merchId);
            merch.setImages(imageList);

            // 店舗情報取得
            Store store = storeDAO.selectById(merch.getStoreId());

            // リクエストに設定
            req.setAttribute("merch", merch);
            req.setAttribute("store", store);

            // 割引情報を判定して設定
            if (store != null) {
                Time discountTime = store.getDiscountTime();
                int discountRate = store.getDiscountRate();

                // 現在時刻が割引時間以降かつ割引率が設定されているか判定
                boolean isDiscountApplied = false;
                if (discountTime != null && discountRate > 0) {
                    LocalTime now = LocalTime.now();
                    LocalTime discountStart = discountTime.toLocalTime();
                    isDiscountApplied = now.isAfter(discountStart) || now.equals(discountStart);
                }

                req.setAttribute("isDiscountApplied", isDiscountApplied);
                req.setAttribute("discountRate", discountRate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("/jsp/merch_detail.jsp").forward(req, res);
    }
}