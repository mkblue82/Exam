package foodloss;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.Store;
import dao.MerchandiseDAO;
import dao.StoreDAO;
import tool.Action;
import tool.DBManager;

public class MenuAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        HttpSession session = req.getSession();

        Object storeObj = session.getAttribute("store");
        Object userObj = session.getAttribute("user");

        // DB接続
        try (Connection con = new DBManager().getConnection()) {
            StoreDAO storeDAO = new StoreDAO(con);
            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(con);

            // 全店舗取得
            List<Store> storeList = storeDAO.selectAll();

            // 店舗ごとの商品リストを作成
            Map<Store, List<Merchandise>> shopMerchMap = new LinkedHashMap<>();
            for (Store store : storeList) {
                List<Merchandise> merchList = merchandiseDAO.selectByStoreId(store.getStoreId());
                shopMerchMap.put(store, merchList);
            }

            // JSPに渡す
            req.setAttribute("shopMerchMap", shopMerchMap);
        }

        // 店舗としてログインしている場合
        if (storeObj != null) {
            req.getRequestDispatcher("/store_jsp/main_store.jsp").forward(req, res);
        }
        // 一般ユーザーとしてログインしている場合
        else if (userObj != null) {
            req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
        }
        // ログインしていない場合
        else {
            res.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
        }
    }
}
