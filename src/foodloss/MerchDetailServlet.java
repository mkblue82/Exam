package foodloss;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.MerchandiseImage;
import bean.Store;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import dao.StoreDAO;
import tool.DBManager;

@WebServlet("/merch/*")
public class MerchDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        System.out.println("===== MerchDetailServlet START =====");

        // セッションチェック
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("ERROR: Session is null or user not found");
            req.setAttribute("errorMessage", "セッションが切れています。");
            req.getRequestDispatcher("/error.jsp").forward(req, res);
            return;
        }
        System.out.println("Session check passed");

        // URLから商品IDを取得: /merch/123 → "123"
        String pathInfo = req.getPathInfo();
        System.out.println("PathInfo: " + pathInfo);

        if (pathInfo == null || pathInfo.length() <= 1) {
            System.out.println("ERROR: No merchandise ID in path");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "商品IDが指定されていません");
            return;
        }

        String idStr = pathInfo.substring(1);
        System.out.println("Merchandise ID string: " + idStr);

        int merchandiseId = 0;
        try {
            merchandiseId = Integer.parseInt(idStr);
            System.out.println("Parsed merchandiseId: " + merchandiseId);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid merchandiseId format: " + idStr);
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "無効な商品ID");
            return;
        }

        Connection con = null;
        try {
            System.out.println("Getting DB connection...");
            con = new DBManager().getConnection();
            System.out.println("DB connection established");

            // 商品情報を取得
            System.out.println("Creating MerchandiseDAO...");
            MerchandiseDAO merchDao = new MerchandiseDAO(con);
            System.out.println("Selecting merchandise by ID: " + merchandiseId);
            Merchandise merch = merchDao.selectById(merchandiseId);

            if (merch == null) {
                System.out.println("ERROR: Merchandise not found for ID: " + merchandiseId);
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "商品が見つかりません");
                return;
            }
            System.out.println("Merchandise found: " + merch.getMerchandiseName());

            // 画像情報を取得
            System.out.println("Getting images for merchandise...");
            MerchandiseImageDAO imgDao = new MerchandiseImageDAO(con);
            List<MerchandiseImage> images = imgDao.selectByMerchandiseId(merchandiseId);
            merch.setImages(images);
            System.out.println("Images found: " + (images != null ? images.size() : 0));

            // 店舗情報を取得
            System.out.println("Getting store info for storeId: " + merch.getStoreId());
            StoreDAO storeDao = new StoreDAO(con);
            Store store = storeDao.selectById(merch.getStoreId());
            if (store != null) {
                System.out.println("Store found: " + store.getStoreName());
            } else {
                System.out.println("WARNING: Store not found");
            }

            // リクエストに設定
            req.setAttribute("merchandise", merch);
            req.setAttribute("store", store);

            System.out.println("Forwarding to merchandise detail page");

            // 商品詳細ページへフォワード
            req.getRequestDispatcher("/jsp/merch_detail.jsp").forward(req, res);

            System.out.println("===== MerchDetailServlet END (SUCCESS) =====");

        } catch (Exception e) {
            System.out.println("ERROR: Exception in MerchDetailServlet");
            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("errorMessage", "商品情報の取得に失敗しました。");
            req.getRequestDispatcher("/error.jsp").forward(req, res);
        } finally {
            if (con != null) {
                try {
                    con.close();
                    System.out.println("DB connection closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}