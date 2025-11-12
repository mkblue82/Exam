package foodloss;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;

public class EditInfoExecuteAction extends Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // セッションからログイン中のユーザー情報を取得
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return "login.jsp"; // 未ログイン時はログイン画面へ
        }

        // フォーム入力の取得（対象：氏名・メールアドレス・パスワード）
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 入力チェック
        if (name == null || email == null || password == null ||
            name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "すべての項目を入力してください。");
            return "/jsp/edit_info.jsp";
        }

        Connection con = null;
        try {
            con = DBManager.getConnection();
            UserDAO dao = new UserDAO(con);

            // DB登録済みのユーザー情報を取得（更新のため最新状態に）
            User dbUser = dao.findById(loginUser.getUserId());
            if (dbUser == null) {
                request.setAttribute("error", "ユーザー情報が見つかりません。");
                return "/jsp/edit_info.jsp";
            }

            // 更新する項目だけ上書き
            dbUser.setName(name);
            dbUser.setEmail(email);
            dbUser.setPassword(password);

            dao.update(dbUser); // DB更新実行

            // セッションのユーザー情報も更新
            session.setAttribute("user", dbUser);

            // 完了メッセージを設定して結果画面へ
            request.setAttribute("message", "ユーザー情報を更新しました。");
            return "/jsp/edit_info_result.jsp";

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "更新中にエラーが発生しました。");
            return "/jsp/edit_info.jsp";
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ignore) {}
            }
        }
    }
}
