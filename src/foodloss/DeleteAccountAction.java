package foodloss;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;
import tool.DBManager;


public class DeleteAccountAction extends Action {
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String action = req.getParameter("action");

		// アカウント削除画面を表示
		if (action == null || action.isEmpty()) {
			req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
		}
		// パスワード検証
		else if ("verify".equals(action)) {
			try {
				String password = req.getParameter("password");
				HttpSession session = req.getSession();
				User user = (User) session.getAttribute("user");

				if (user == null) {
					res.sendRedirect("Login.action");
					return;
				}

				// パスワードが正しいか確認
				Connection con = DBManager.getConnection();
				UserDAO userDAO = new UserDAO(con);
				User loginUser = userDAO.findById(user.getUserId());
				con.close();

				if (loginUser == null || !password.equals(loginUser.getPassword())) {
					req.setAttribute("error", "パスワードが正しくありません");
					req.setAttribute("password", password);
					req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
					return;
				}

				// パスワードが正しい場合、最終確認画面へ
				req.setAttribute("verified", true);
				req.getRequestDispatcher("/jsp/delete_account_confirm.jsp").forward(req, res);

			} catch (Exception e) {
				e.printStackTrace();
				req.getRequestDispatcher("error.jsp").forward(req, res);
			}
		}
		// アカウント削除実行
		else if ("execute".equals(action)) {
			try {
				HttpSession session = req.getSession();
				User user = (User) session.getAttribute("user");

				if (user == null) {
					res.sendRedirect("Login.action");
					return;
				}

				// ユーザーを削除
				Connection con = DBManager.getConnection();
				UserDAO userDAO = new UserDAO(con);

				User deleteUser = userDAO.findById(user.getUserId());
				String userName = (deleteUser != null) ? deleteUser.getName() : "Unknown";

				userDAO.delete(user.getUserId());
				System.out.println("User [" + userName + "] (ID: " + user.getUserId() + ") deleted at " + new java.util.Date());

				con.close();

				// セッションを無効化
				session.invalidate();

				// 削除完了画面へリダイレクト
				res.sendRedirect("delete_account_done.jsp");

			} catch (Exception e) {
				e.printStackTrace();
				req.getRequestDispatcher("error.jsp").forward(req, res);
			}
		}
	}
}