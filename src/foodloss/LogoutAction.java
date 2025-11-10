package foodloss;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tool.Action;

public class LogoutAction extends Action{
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception{
		HttpSession session = req.getSession(false);
		if (session != null){
			session.invalidate();
		}
		req.getRequestDispatcher("/jsp/logout_done.jsp").forward(req, res);
	}
}