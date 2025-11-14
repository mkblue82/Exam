package foodloss;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import dao.MerchandiseDAO;
import tool.Action;

public class MerchandiseRegisterExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Connection connection = null;

        try {
            request.setCharacterEncoding("UTF-8");

            // ✅ セッションから店舗情報を取得（ユーザーログインの場合は仮ID）
            HttpSession session = request.getSession();
            bean.Store store = (bean.Store) session.getAttribute("store");

            int storeId = 0;

            if (store != null) {
                storeId = store.getStoreId();
                System.out.println("✅ セッションからstoreId取得: " + storeId);
            } else {
                // 🔧 テスト用：ユーザーログインの場合は仮のstoreId=2を使用
                bean.User user = (bean.User) session.getAttribute("user");
                if (user != null) {
                    storeId = 2;  // テスト用の店舗ID（データベースに存在する店舗IDに変更）
                    System.out.println("⚠️ ユーザーログイン中のため、テスト用storeId=2 を使用");
                } else {
                    // どちらもログインしていない
                    request.setAttribute("errorMessage", "ログインしてください");
                    response.sendRedirect(request.getContextPath() + "/foodloss/Login_Store.action");
                    return;
                }
            }

            String name = request.getParameter("merchandiseName");
            String quantityStr = request.getParameter("quantity");
            String expirationDateStr = request.getParameter("expirationDate");
            String tags = request.getParameter("tags");

            System.out.println("★ merchandiseName = [" + name + "]");
            System.out.println("★ quantity = [" + quantityStr + "]");
            System.out.println("★ expirationDate = [" + expirationDateStr + "]");
            System.out.println("★ tags = [" + tags + "]");

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "商品名を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "個数を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            if (expirationDateStr == null || expirationDateStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "消費期限を入力してください");
                request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
                return;
            }

            int stock = Integer.parseInt(quantityStr);
            java.sql.Date useByDate = java.sql.Date.valueOf(expirationDateStr);

            Merchandise m = new Merchandise();
            m.setStoreId(storeId);
            m.setMerchandiseName(name);
            m.setStock(stock);
            m.setUseByDate(useByDate);
            m.setMerchandiseTag(tags != null ? tags : "");
            m.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
            m.setBookingStatus(false);

            System.out.println("★ Merchandise設定完了: name=" + m.getMerchandiseName() + ", storeId=" + storeId);

            // tool.Actionのメソッドを使用
            connection = getConnection();

            MerchandiseDAO dao = new MerchandiseDAO(connection);
            dao.insert(m);

            System.out.println("✅ 商品登録成功！");
            response.sendRedirect(request.getContextPath() + "/store_jsp/merchandise_list_store.jsp");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "個数は数値で入力してください");
            request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "消費期限の形式が正しくありません");
            request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}