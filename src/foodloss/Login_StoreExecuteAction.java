package foodloss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import dao.StoreDAO;
import tool.Action;
import tool.DBManager;

public class Login_StoreExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {


        String storeIdStr = request.getParameter("storeId");
        String password = request.getParameter("password");


        if (storeIdStr == null || storeIdStr.isEmpty()) {
            request.setAttribute("error", "店舗IDを入力してください。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp").forward(request, response);
            return;
        }


        int storeId;
        try {
            storeId = Integer.parseInt(storeIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "店舗IDは数字で入力してください。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp").forward(request, response);
            return;
        }

        // パスワードを SHA-256 に変換
        String hashedPassword = hashPassword(password);


        DBManager db = new DBManager();
        StoreDAO dao = new StoreDAO(db.getConnection());



        Store store = dao.login(storeId, hashedPassword);

        if (store != null) {

            // ログイン成功 → セッションに保存
            HttpSession session = request.getSession();
            session.setAttribute("store", store);
            session.setAttribute("storeId", store.getStoreId());
            session.setAttribute("storeName", store.getStoreName());
            session.setAttribute("storeCode", String.valueOf(store.getStoreId()));


            request.getRequestDispatcher("/store_jsp/main_store.jsp").forward(request, response);

        } else {


            request.setAttribute("error", "店舗IDまたはパスワードが違います。");
            request.getRequestDispatcher("/store_jsp/login_store.jsp").forward(request, response);
        }
    }

    // --- パスワードを SHA-256 ハッシュ化するメソッド ---
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードのハッシュ化に失敗しました", e);
        }
    }
}
