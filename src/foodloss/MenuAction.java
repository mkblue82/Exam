package foodloss;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tool.Action;

public class MenuAction extends Action {
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HttpSession session = req.getSession();

		// セッションから店舗情報とユーザー情報を取得
		Object store = session.getAttribute("store");
		Object user = session.getAttribute("user");

		// 店舗としてログインしている場合
		if (store != null) {
			req.getRequestDispatcher("/jsp/main_store.jsp").forward(req, res);
		}
		// 一般ユーザーとしてログインしている場合
		else if (user != null) {
			req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
		}
		// ログインしていない場合（念のため）
		else {
			res.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
		}
	}
}