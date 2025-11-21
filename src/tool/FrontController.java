package tool;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = { "*.action" })
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,   // 1MB
	    maxFileSize = 5 * 1024 * 1024,     // 5MB
	    maxRequestSize = 10 * 1024 * 1024  // 10MB
	)
public class FrontController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			System.out.println("FrontController called: " + req.getServletPath());

			// パスを取得s
			String path = req.getServletPath().substring(1);
			// ファイル名を取得しクラス名に変換
			String name = path.replace(".action", "Action").replace('/', '.');
			// アクションクラスのインスタンスを返却

			String fullClassName = "foodloss." + name;

			Action action = (Action) Class.forName(name).getDeclaredConstructor().newInstance();

			// 遷移先URLを取得
			action.execute(req, res);


		} catch (Exception e) {
			e.printStackTrace();
			// エラーページへリダイレクト
			req.getRequestDispatcher("/error.jsp").forward(req, res);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		doGet(req,res);

	}
}
