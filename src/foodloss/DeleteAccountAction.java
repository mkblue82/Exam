package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tool.Action;

public class DeleteAccountAction extends Action {
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String action = req.getParameter("action");

		// アカウント削除画面を表示
		if (action == null || action.isEmpty()) {
			req.getRequestDispatcher("/scoremanager/main/delete_account.jsp").forward(req, res);
		}
		// パスワード検証
		else if ("verify".equals(action)) {
			String password = req.getParameter("password");
			HttpSession session = req.getSession(false);

			// パスワード検証ロジック（実装例）
			// DBからユーザー情報を取得してパスワードを確認
			boolean isPasswordCorrect = verifyPassword(session, password);

			if (isPasswordCorrect) {
				// パスワードが正しい場合、最終確認画面へ
				req.setAttribute("verified", true);
				req.getRequestDispatcher("/scoremanager/main/delete_account_confirm.jsp").forward(req, res);
			} else {
				// パスワードが間違っている場合
				req.setAttribute("error", "パスワードが正しくありません");
				req.getRequestDispatcher("/scoremanager/main/delete_account.jsp").forward(req, res);
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
				req.getRequestDispatcher("/scoremanager/main/delete_account_complete.jsp").forward(req, res);
			} catch (Exception e) {
				req.setAttribute("error", "アカウント削除に失敗しました");
				req.getRequestDispatcher("/scoremanager/main/delete_account_confirm.jsp").forward(req, res);
			}
		}
	}


	private boolean verifyPassword(HttpSession session, String password) {
		return true;
	}

	/**
	 * DBからユーザーを削除するメソッド
	 */
	private void deleteUserFromDB(String userId) {
		System.out.println("User " + userId + " deleted at " + new java.util.Date());
	}
}