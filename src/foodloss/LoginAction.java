package foodloss;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tool.Action;

@WebServlet("/login")
public class LoginAction extends Action{
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception{
		HttpSession session = req.getSession(false);
		if (session != null){
			session.invalidate();
		}

		req.getRequestDispatcher("jsp/loginBean.jsp").forward(req, res);
	}
}