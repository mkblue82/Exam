package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class NotificationSettingAction extends Action {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.getRequestDispatcher("/jsp/notification_setting.jsp").forward(req, res);
	}
}
