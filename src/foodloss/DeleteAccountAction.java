package foodloss;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;

public class DeleteAccountAction extends Action {
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String action = req.getParameter("action");
		// アカウント削除画面を表示
		if (action == null || action.isEmpty()) {
			req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
		}
		// パスワード検証
		else if ("verify".equals(action)) {
			String password = req.getParameter("password");
			HttpSession session = req.getSession(false);
			// パスワード検証ロジック
			// DBからユーザー情報を取得してパスワードを確認
			boolean isPasswordCorrect = verifyPassword(session, password);
			if (isPasswordCorrect) {
				// パスワードが正しい場合、最終確認画面へ
				req.setAttribute("verified", true);
				req.getRequestDispatcher("/jsp/delete_account_confirm.jsp").forward(req, res);
			} else {
				// パスワードが間違っている場合
				req.setAttribute("error", "パスワードが正しくありません");
				req.setAttribute("password", password); // 入力されたパスワードを初期値として渡す
				req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
			}
		}
		// アカウント削除実行
		else if ("execute".equals(action)) {
			HttpSession session = req.getSession(false);
			String userId = (String) session.getAttribute("userId");
			try {
				// DBからユーザーを削除
				deleteUserFromDB(userId);
				// セッションを無効化
				if (session != null) {
					session.invalidate();
				}
				// 削除完了画面へ遷移
				req.getRequestDispatcher("/jsp/delete_account_done.jsp").forward(req, res);
			} catch (Exception e) {
				req.setAttribute("error", "アカウント削除に失敗しました");
				req.getRequestDispatcher("/jsp/delete_account_confirm.jsp").forward(req, res);
			}
		}
	}

	// パスワードを検証するメソッド
	private boolean verifyPassword(HttpSession session, String password) {
		Connection con = null;
		try {
			// セッションからユーザーIDを取得
			String userIdStr = (String) session.getAttribute("userId");
			if (userIdStr == null || password == null) {
				return false;
			}

			int userId = Integer.parseInt(userIdStr);

			// DB接続を取得
			con = DBUtil.getConnection();
			UserDAO userDAO = new UserDAO(con);

			// DBからユーザー情報を取得
			User user = userDAO.findById(userId);
			if (user == null) {
				return false;
			}

			// 入力されたパスワードとDBのパスワードを比較
			boolean isCorrect = password.equals(user.getPassword());

			return isCorrect;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// DBからユーザーを削除するメソッド
	private void deleteUserFromDB(String userId) throws Exception {
		Connection con = null;
		try {
			int id = Integer.parseInt(userId);

			// DB接続を取得
			con = DBUtil.getConnection();
			UserDAO userDAO = new UserDAO(con);

			// 削除前にユーザー情報を取得
			User user = userDAO.findById(id);
			String userName = (user != null) ? user.getName() : "Unknown";

			// ユーザーを削除
			userDAO.delete(id);

			System.out.println("User [" + userName + "] (ID: " + userId + ") deleted at " + new java.util.Date());
		} catch (Exception e) {
			System.err.println("Delete failed: " + e.getMessage());
			throw e;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}