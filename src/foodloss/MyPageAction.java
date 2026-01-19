package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;

public class MyPageAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // セッションからユーザー情報を取得
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            // セッションがない場合はエラーページへ
            req.setAttribute("errorMessage", "セッションが切れています。");
            req.getRequestDispatcher("/error.jsp").forward(req, res);
            return;
        }

        User sessionUser = (User) session.getAttribute("user");

        // データベース接続を取得（Actionクラスの機能を利用）
        Connection con = getConnection();

        try {
            // データベースから最新のユーザー情報を取得
            UserDAO userDAO = new UserDAO(con);
            User latestUser = userDAO.findById(sessionUser.getUserId());

            if (latestUser != null) {
                // セッションを最新情報で更新
                session.setAttribute("user", latestUser);
            } else {
                // ユーザーが見つからない場合
                req.setAttribute("errorMessage", "ユーザー情報が見つかりません。");
                req.getRequestDispatcher("/error.jsp").forward(req, res);
                return;
            }
        } finally {
            // 接続を閉じる（必要に応じて）
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }

        // マイページへフォワード
        req.getRequestDispatcher("/jsp/mypage.jsp").forward(req, res);
    }
}