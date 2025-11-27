package foodloss;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class SearchAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        System.out.println("===== SearchAction START =====");

        String keyword = req.getParameter("keyword");
        System.out.println("検索キーワード: [" + keyword + "]");

        List<Merchandise> merchandiseResults = new ArrayList<>();
        List<Store> storeResults = new ArrayList<>();
        Map<Store, List<Merchandise>> storeToMerchMap = new LinkedHashMap<>();

        try (Connection con = new DBManager().getConnection()) {
            MerchandiseDAO merchDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);
            StoreDAO storeDAO = new StoreDAO(con);

            if (keyword != null && !keyword.trim().isEmpty()) {
                String trimmedKeyword = keyword.trim();

                // 1. 商品検索
                System.out.println("商品検索実行");
                merchandiseResults = merchDAO.searchByKeyword(trimmedKeyword);
                System.out.println("商品検索結果: " + merchandiseResults.size() + "件");

                // 商品に画像をセット
                for (Merchandise m : merchandiseResults) {
                    try {
                        List<MerchandiseImage> images = imageDAO.selectByMerchandiseId(m.getMerchandiseId());
                        m.setImages(images);
                        System.out.println("  商品: " + m.getMerchandiseName() + " / 画像数: " + images.size());
                    } catch (Exception imgEx) {
                        System.out.println("  商品ID " + m.getMerchandiseId() + " の画像取得エラー");
                        imgEx.printStackTrace();
                        m.setImages(new ArrayList<>());
                    }
                }

                // 2. 店舗検索
                System.out.println("店舗検索実行");
                storeResults = storeDAO.searchByKeyword(trimmedKeyword);
                System.out.println("店舗検索結果: " + storeResults.size() + "件");

                // 店舗ごとの商品マップを作成（検索結果の店舗に紐づく商品を取得）
                for (Store store : storeResults) {
                    try {
                        List<Merchandise> storeProducts = merchDAO.selectByStoreId(store.getStoreId());

                        // 各商品に画像をセット
                        for (Merchandise m : storeProducts) {
                            try {
                                List<MerchandiseImage> images = imageDAO.selectByMerchandiseId(m.getMerchandiseId());
                                m.setImages(images);
                            } catch (Exception imgEx) {
                                System.out.println("  店舗商品ID " + m.getMerchandiseId() + " の画像取得エラー");
                                m.setImages(new ArrayList<>());
                            }
                        }

                        storeToMerchMap.put(store, storeProducts);
                        System.out.println("  店舗: " + store.getStoreName() + " / 商品数: " + storeProducts.size());
                    } catch (Exception storeEx) {
                        System.out.println("  店舗ID " + store.getStoreId() + " の商品取得エラー");
                        storeEx.printStackTrace();
                    }
                }

            } else {
                System.out.println("キーワードがnullまたは空です");
            }

            // 検索結果をリクエストスコープにセット
            req.setAttribute("itemList", merchandiseResults);        // 商品検索結果
            req.setAttribute("storeList", storeResults);             // 店舗検索結果
            req.setAttribute("storeToMerchMap", storeToMerchMap);   // 店舗→商品マップ
            req.setAttribute("searchKeyword", keyword);

            System.out.println("商品検索結果セット: " + merchandiseResults.size() + "件");
            System.out.println("店舗検索結果セット: " + storeResults.size() + "件");
            System.out.println("店舗マップセット: " + storeToMerchMap.size() + "店舗");

        } catch (Exception e) {
            System.out.println("SearchActionで例外発生:");
            e.printStackTrace();
            req.setAttribute("errorMessage", "検索中にエラーが発生しました: " + e.getMessage());
        }

        // main_user.jspにフォワード
        System.out.println("main_user.jspへフォワード");
        req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
        System.out.println("===== SearchAction END =====");
    }
}