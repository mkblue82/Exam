package foodloss;

import java.sql.Connection;
import java.util.ArrayList;
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

public class StoreMerchandiseAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        System.out.println("===== StoreMerchandiseAction START =====");

        String storeIdParam = req.getParameter("storeId");
        System.out.println("店舗ID: [" + storeIdParam + "]");

        Store store = null;
        List<Merchandise> merchandiseList = new ArrayList<>();

        try (Connection con = new DBManager().getConnection()) {
            StoreDAO storeDAO = new StoreDAO(con);
            MerchandiseDAO merchDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);

            if (storeIdParam != null && !storeIdParam.trim().isEmpty()) {
                int storeId = Integer.parseInt(storeIdParam);

                // 店舗情報を取得
                System.out.println("店舗情報取得実行");
                store = storeDAO.selectById(storeId);

                if (store != null) {
                    System.out.println("店舗情報取得成功: " + store.getStoreName());

                    // 店舗の商品を取得
                    System.out.println("商品取得実行");
                    merchandiseList = merchDAO.selectByStoreId(storeId);
                    System.out.println("商品取得結果: " + merchandiseList.size() + "件");

                    // 各商品に画像をセット
                    for (Merchandise m : merchandiseList) {
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
                } else {
                    System.out.println("店舗情報が見つかりません: storeId=" + storeId);
                }

            } else {
                System.out.println("店舗IDがnullまたは空です");
            }

            // 結果をリクエストスコープにセット
            req.setAttribute("store", store);
            req.setAttribute("merchandiseList", merchandiseList);

            System.out.println("店舗情報セット: " + (store != null ? store.getStoreName() : "null"));
            System.out.println("商品リストセット: " + merchandiseList.size() + "件");

        } catch (NumberFormatException e) {
            System.out.println("店舗IDの形式エラー: " + storeIdParam);
            e.printStackTrace();
            req.setAttribute("errorMessage", "無効な店舗IDです");
        } catch (Exception e) {
            System.out.println("StoreMerchandiseActionで例外発生:");
            e.printStackTrace();
            req.setAttribute("errorMessage", "店舗情報の取得中にエラーが発生しました: " + e.getMessage());
        }

        // store_merchandise.jspにフォワード
        System.out.println("store_merchandise.jspへフォワード");
        req.getRequestDispatcher("/jsp/store_merchandise.jsp").forward(req, res);
        System.out.println("===== StoreMerchandiseAction END =====");
    }
}