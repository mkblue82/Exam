package foodloss;

import java.sql.Connection;
import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import bean.Merchandise;
import dao.MerchandiseDAO;
import tool.Action;

public class MerchandiseListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        // ログインしてない場合
        if (storeId == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        Connection con = null;

        try {
            // JNDIでDB接続
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/postgres");
            con = ds.getConnection();

            // DAO
            MerchandiseDAO dao = new MerchandiseDAO(con);
            List<Merchandise> list = dao.selectByStoreId(storeId);

            // JSPに渡す
            request.setAttribute("merchandiseList", list);

        } finally {
            if (con != null) con.close();
        }

        // 遷移先 JSP
        request.getRequestDispatcher("/store_jsp/merchandise_list.jsp")
               .forward(request, response);
    }
}
