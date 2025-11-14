package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class MerchandiseRegisterAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        // ここに処理を書く
        // 例: データベースから商品情報を取得

        // JSPに遷移（フォワード）
        req.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp").forward(req, res);

        // またはリダイレクト
        // res.sendRedirect("merchandise_register.jsp");
    }
}