package foodloss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store; // 店舗用のBean
import dao.StoreDAO; // 店舗用DAO
import tool.Action;
import tool.DBManager;

public class Login_StoreExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // フォームから送信された店舗IDとパスワードを取得
        String storeIdStr = request.getParameter("storeId");
        String password = request.getParameter("password");

        if (storeIdStr == null || storeIdStr.isEmpty()) {
            request.setAttribute("error", "店舗IDを入力してください。");
            request.getRequestDispatcher("/jsp/store_login.jsp").forward(request, response);
            return;
        }

        int storeId;
        try {
            storeId = Integer.parseInt(storeIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "店舗IDは数字で入力してください。");
            request.getRequestDispatcher("/jsp/store_login.jsp").forward(request, response);
            return;
        }

        // パスワードをハッシュ化
        String hashedPassword = hashPassword(password);

        DBManager db = new DBManager();
        StoreDAO dao = new StoreDAO(db.getConnection());
        Store store = dao.login(storeId, hashedPassword); // 店舗IDとハッシュ化パスワードで認証

        if (store != null) {
            HttpSession session = request.getSession();
            session.setAttribute("store", store);
            // ログイン成功 → 店舗メニュー画面にリダイレクト
            response.sendRedirect(request.getContextPath() + "/foodloss/StoreMenu.action");
        } else {
            request.setAttribute("error", "店舗IDまたはパスワードが違います。");
            request.getRequestDispatcher("/jsp/store_login.jsp").forward(request, response);
        }
    }

    // SHA-256でハッシュ化
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
