package foodloss;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.MerchandiseImage;
import bean.Store;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
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

        Map<Store, List<Merchandise>> shopMerchMap = new LinkedHashMap<>();
        List<Merchandise> defaultList = new ArrayList<>(); // ★追加

        try (Connection con = new DBManager().getConnection()) {
            StoreDAO storeDAO = new StoreDAO(con);
            MerchandiseDAO merchDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);

            // 全店舗取得
            List<Store> storeList = storeDAO.selectAll();
            System.out.println("店舗数: " + storeList.size());

            for (Store store : storeList) {
                // 商品取得
                List<Merchandise> merchList = merchDAO.selectByStoreId2(store.getStoreId());
                System.out.println("店舗ID " + store.getStoreId() + " の商品数: " + merchList.size());

                // 商品ごとに画像を取得してセット
                for (Merchandise m : merchList) {
                    List<MerchandiseImage> images = imageDAO.selectByMerchandiseId(m.getMerchandiseId());
                    m.setImages(images);

                    // ★ defaultListに全商品を追加
                    defaultList.add(m);

                    // デバッグ出力
                    System.out.println("  商品: " + m.getMerchandiseName() + " / 画像数: " + images.size());
                    for (MerchandiseImage img : images) {
                        System.out.println("    画像ファイル名: " + img.getFileName());
                    }
                }

                shopMerchMap.put(store, merchList);
            }

            req.setAttribute("shopMerchMap", shopMerchMap);
            req.setAttribute("defaultList", defaultList); // ★追加

        } catch (Exception e) {
            System.out.println("MenuActionで例外発生:");
            e.printStackTrace();
            // エラー時は共通エラーページにフォワード
            req.setAttribute("errorMessage", "メニューの取得中にエラーが発生しました。");
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, res);
            return;
        }

        // JSPへ遷移
        if (storeObj != null) {
            req.getRequestDispatcher("/store_jsp/main_store.jsp").forward(req, res);
        } else if (userObj != null) {
            req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
        } else {
            res.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
        }

        System.out.println("===== MenuAction END =====");
    }
}