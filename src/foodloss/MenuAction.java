package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class MenuAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 取りあえずはmain_user.jspにフォワード先
		// ログイン出来るようになったら、ログインユーザーの属性に合わせてフォワード先変わるようにする
		req.getRequestDispatcher("/jsp/main_user.jsp").forward(req, res);
	}
}
