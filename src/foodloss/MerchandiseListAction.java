package action;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import bean.Merchandise;
import dao.MerchandiseDAO;

@WebServlet("/merchandise_list")
public class MerchandiseListAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        // ログインしてない場合
        if (storeId == null) {
            response.sendRedirect("store_login.jsp");
            return;
        }

        Connection con = null;

        try {
            // ★ DB接続（JNDI）★
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/postgres");
            con = ds.getConnection();

            // DAO 呼び出し
            MerchandiseDAO dao = new MerchandiseDAO(con);
            List<Merchandise> list = dao.selectByStoreId(storeId);

            // JSP に渡す
            request.setAttribute("merchandiseList", list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }

        // 商品一覧画面へフォワード
        request.getRequestDispatcher("/Merchandise_list.jsp").forward(request, response);
    }
}
