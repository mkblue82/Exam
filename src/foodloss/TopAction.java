package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class TopAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

		req.getRequestDispatcher("/jsp/index.jsp").forward(req, res);
	}
}
