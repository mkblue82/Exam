package tool;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Action {
	public abstract void execute(
			HttpServletRequest req, HttpServletResponse res
		) throws Exception;

	protected Connection getConnection() throws Exception{
		return DBManager.getConnection();
	}

}
