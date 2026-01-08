package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import tool.Action;

public class EditInfoAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // ログインチェック
        if (user == null) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // ユーザー情報をリクエストスコープに設定
        request.setAttribute("user", user);

        // 情報変更画面へ遷移
        request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
    }
}