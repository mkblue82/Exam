package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tool.Action;

public class MerchandiseRegisterAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        // ★ セッションから storeId を取得
        HttpSession session = req.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        // ★ ログインしていない場合 → ログイン画面へ
        if (storeId == null) {
            res.sendRedirect(req.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        // ★ ログインしていれば商品登録画面へ
        req.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp")
           .forward(req, res);
    }
}
