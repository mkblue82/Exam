package foodloss;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import dao.StoreDAO;

@WebServlet("/signup_store")
public class SignupStoreAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (sessionToken == null || !sessionToken.equals(token)) {
            req.setAttribute("errorMessage", "不正なアクセスです。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        String storeName = req.getParameter("storeName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = hashPassword(req.getParameter("password"));

        Store store = new Store();
        store.setStoreName(storeName);
        store.setEmail(email);
        store.setPhone(phone);
        store.setPassword(password);

        try (Connection conn = DBManager.getConnection()) {
            StoreDAO dao = new StoreDAO(conn);
            dao.insert(store);

            session.removeAttribute("csrfToken");
            req.getRequestDispatcher("/jsp/signupsuccess_store.jsp").forward(req, res);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
        }
    }

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
