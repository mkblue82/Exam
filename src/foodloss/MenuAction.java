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

        System.out.println("===== MenuAction START =====");

        HttpSession session = req.getSession();

        Object storeObj = session.getAttribute("store");
        Object userObj = session.getAttribute("user");

        try (Connection con = new DBManager().getConnection()) {

            System.out.println("DB接続成功");

            StoreDAO storeDAO = new StoreDAO(con);
            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(con);

            // 全店舗取得
            List<Store> storeList = storeDAO.selectAll();
            System.out.println("店舗数: " + storeList.size());

            // 店舗ごとの商品リスト作成
            Map<Store, List<Merchandise>> shopMerchMap = new LinkedHashMap<>();

            for (Store store : storeList) {
                System.out.println("店舗ID " + store.getStoreId() + " の商品を取得します…");

                List<Merchandise> merchList = merchandiseDAO.selectByStoreId2(store.getStoreId());

                System.out.println(" → 商品数: " + merchList.size());

                // 商品名一覧も表示
                for (Merchandise m : merchList) {
                    System.out.println("    商品: " + m.getMerchandiseName() + " / 価格: " + m.getPrice());
                }

                shopMerchMap.put(store, merchList);
            }

            req.setAttribute("shopMerchMap", shopMerchMap);

            System.out.println("===== MenuAction END =====");

        } catch (Exception e) {
            System.out.println("MenuActionで例外発生:");
            e.printStackTrace();
        }

        // 店舗ログイン
        if (storeObj != null) {
            req.getRequestDispatcher("/store_jsp/main_store.jsp").forward(req, res);
            return;
        }

        // ユーザーログイン
        if (userObj != null) {
            req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
            return;
        }

        // 未ログイン
        res.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
    }
}
