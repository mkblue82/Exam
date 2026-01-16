package foodloss;

import java.sql.Connection;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
        List<Merchandise> defaultList = new ArrayList<>();

        // 店舗ごとの割引情報を格納するマップ
        Map<Integer, Boolean> storeDiscountMap = new HashMap<>();
        Map<Integer, Integer> storeDiscountRateMap = new HashMap<>();

        try (Connection con = new DBManager().getConnection()) {
            StoreDAO storeDAO = new StoreDAO(con);
            MerchandiseDAO merchDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);

            // 全店舗取得
            List<Store> storeList = storeDAO.selectAll();
            System.out.println("店舗数: " + storeList.size());

            for (Store store : storeList) {
                try {
                    // 割引情報を判定
                    Time discountTime = store.getDiscountTime();
                    int discountRate = store.getDiscountRate();

                    boolean isDiscountApplied = false;
                    if (discountTime != null && discountRate > 0) {
                        LocalTime now = LocalTime.now();
                        LocalTime discountStart = discountTime.toLocalTime();
                        isDiscountApplied = now.isAfter(discountStart) || now.equals(discountStart);
                    }

                    // 店舗IDをキーに割引情報を保存
                    storeDiscountMap.put(store.getStoreId(), isDiscountApplied);
                    storeDiscountRateMap.put(store.getStoreId(), discountRate);

                    System.out.println("店舗ID " + store.getStoreId() + " - 割引適用: " + isDiscountApplied + ", 割引率: " + discountRate + "%");

                    // 商品取得
                    List<Merchandise> merchList = merchDAO.selectByStoreId(store.getStoreId());
                    System.out.println("店舗ID " + store.getStoreId() + " の商品数: " + merchList.size());

                    // 商品ごとに画像を取得してセット
                    for (Merchandise m : merchList) {
                        try {
                            List<MerchandiseImage> images = imageDAO.selectByMerchandiseId(m.getMerchandiseId());
                            m.setImages(images);

                            // defaultListに全商品を追加
                            defaultList.add(m);

                            // デバッグ出力
                            System.out.println("  商品: " + m.getMerchandiseName() + " / 画像数: " + images.size());
                            for (MerchandiseImage img : images) {
                                System.out.println("    画像ファイル名: " + img.getFileName());
                            }
                        } catch (Exception imgEx) {
                            System.out.println("  商品ID " + m.getMerchandiseId() + " の画像取得でエラー:");
                            imgEx.printStackTrace();
                            // 画像がなくても商品は表示できるようにする
                            m.setImages(new ArrayList<>());
                            defaultList.add(m);
                        }
                    }

                    shopMerchMap.put(store, merchList);
                } catch (Exception storeEx) {
                    System.out.println("店舗ID " + store.getStoreId() + " の処理でエラー:");
                    storeEx.printStackTrace();
                    // この店舗はスキップして次へ
                }
            }

            req.setAttribute("shopMerchMap", shopMerchMap);
            req.setAttribute("defaultList", defaultList);
            req.setAttribute("storeDiscountMap", storeDiscountMap);
            req.setAttribute("storeDiscountRateMap", storeDiscountRateMap);

            System.out.println("defaultList件数: " + defaultList.size());
            System.out.println("shopMerchMap店舗数: " + shopMerchMap.size());

        } catch (Exception e) {
            System.out.println("MenuActionで例外発生:");
            e.printStackTrace();
            // スタックトレース全体を出力
            System.out.println("詳細エラー: " + e.getClass().getName() + ": " + e.getMessage());

            // エラー時は共通エラーページにフォワード
            req.setAttribute("errorMessage", "メニューの取得中にエラーが発生しました: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, res);
            return;
        }

        // JSPへ遷移
        try {
            if (storeObj != null) {
                System.out.println("店舗ユーザーとして main_store.jsp へ");
                req.getRequestDispatcher("/store_jsp/main_store.jsp").forward(req, res);
            } else if (userObj != null) {
                System.out.println("一般ユーザーとして main_user.jsp へ");
                req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
            } else {
                System.out.println("未ログイン、index.jsp へリダイレクト");
                res.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
            }
        } catch (Exception fwdEx) {
            System.out.println("JSPへのフォワードでエラー:");
            fwdEx.printStackTrace();
            throw fwdEx;
        }

        System.out.println("===== MenuAction END =====");
    }
}