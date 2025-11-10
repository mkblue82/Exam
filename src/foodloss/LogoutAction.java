package foodloss;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tool.Action;

public class LogoutAction extends Action{
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception{

		// 現在のセッションを取得
		HttpSession session = req.getSession(false);

		// セッションが存在する場合、セッションを無効化する
		if (session != null){
			session.invalidate();
		}

		// ログアウト完了ページにフォワード
		req.getRequestDispatcher("/jsp/logout_done.jsp").forward(req, res);
	}
}