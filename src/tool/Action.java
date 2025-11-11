package tool;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Action {
	public abstract void execute(
			HttpServletRequest req, HttpServletResponse res
		) throws Exception;

	protected Connection getConnection() throws SQLException {
		return new DBManager().getConnection();
	}
}