package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import dao.DAO;
import dao.StoreDAO;
import tool.Action;

public class StoreEditExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {


        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("storeId") == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        Integer storeId = (Integer) session.getAttribute("storeId");

        try {
            // パラメータ取得
            String storeName = request.getParameter("storeName");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String passwordConfirm = request.getParameter("passwordConfirm");

            // バリデーション
            if (storeName == null || storeName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "店舗名は必須です。");
                DAO db = new DAO();
                Connection con = db.getConnection();
                StoreDAO storeDAO = new StoreDAO(con);
                Store store = storeDAO.selectById(storeId);
                con.close();
                request.setAttribute("store", store);
                request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("errorMessage", "メールアドレスは必須です。");
                DAO db = new DAO();
                Connection con = db.getConnection();
                StoreDAO storeDAO = new StoreDAO(con);
                Store store = storeDAO.selectById(storeId);
                con.close();
                request.setAttribute("store", store);
                request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                return;
            }

            // パスワード変更のバリデーション
            if (password != null && !password.trim().isEmpty()) {
                if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "パスワード確認を入力してください。");
                    DAO db = new DAO();
                    Connection con = db.getConnection();
                    StoreDAO storeDAO = new StoreDAO(con);
                    Store store = storeDAO.selectById(storeId);
                    con.close();
                    request.setAttribute("store", store);
                    request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    request.setAttribute("errorMessage", "パスワードが一致しません。");
                    DAO db = new DAO();
                    Connection con = db.getConnection();
                    StoreDAO storeDAO = new StoreDAO(con);
                    Store store = storeDAO.selectById(storeId);
                    con.close();
                    request.setAttribute("store", store);
                    request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                    return;
                }

                if (password.length() < 6) {
                    request.setAttribute("errorMessage", "パスワードは6文字以上で入力してください。");
                    DAO db = new DAO();
                    Connection con = db.getConnection();
                    StoreDAO storeDAO = new StoreDAO(con);
                    Store store = storeDAO.selectById(storeId);
                    con.close();
                    request.setAttribute("store", store);
                    request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                    return;
                }
            }

            // 店舗情報取得と重複チェック
            DAO db = new DAO();
            Connection con = db.getConnection();
            StoreDAO storeDAO = new StoreDAO(con);
            Store store = storeDAO.selectById(storeId);

            if (store == null) {
                con.close();
                request.setAttribute("errorMessage", "店舗情報が見つかりません。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // メールアドレスの重複チェック（自分以外の店舗で同じメールアドレスが使われていないか）
            Store duplicateEmailStore = storeDAO.selectByEmail(email);
            if (duplicateEmailStore != null && duplicateEmailStore.getStoreId() != storeId) {
                con.close();
                request.setAttribute("errorMessage", "このメールアドレスは既に使用されています。");
                request.setAttribute("store", store);
                request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                return;
            }

            // 電話番号の重複チェック（電話番号が入力されている場合のみ）
            if (phone != null && !phone.trim().isEmpty()) {
                Store duplicatePhoneStore = storeDAO.selectByPhone(phone);
                if (duplicatePhoneStore != null && duplicatePhoneStore.getStoreId() != storeId) {
                    con.close();
                    request.setAttribute("errorMessage", "この電話番号は既に使用されています。");
                    request.setAttribute("store", store);
                    request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
                    return;
                }
            }

            // 店舗情報更新
            store.setStoreName(storeName);
            store.setAddress(address);
            store.setPhone(phone);
            store.setEmail(email);

            // パスワードが入力されている場合のみ更新
            if (password != null && !password.trim().isEmpty()) {
                store.setPassword(password);
            }

            storeDAO.update(store);
            con.close();

            // セッションの店舗名も更新
            session.setAttribute("storeName", storeName);

            // 詳細画面へリダイレクト
            response.sendRedirect(request.getContextPath() + "/foodloss/StoreDetail.action");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "更新処理中にエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}