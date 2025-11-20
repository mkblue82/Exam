package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import tool.Action;

public class MerchandiseRegisterAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        HttpSession session = req.getSession();

        // セッションから店舗情報を取得
        Store store = (Store) session.getAttribute("store");

        // ログインチェック（店舗として）
        if (store == null) {
            // 店舗としてログインしていない場合、ログイン画面へリダイレクト
            System.out.println("⚠️ 店舗ログインが必要です。ログイン画面へリダイレクト");
            res.sendRedirect(req.getContextPath() + "/foodloss/Login_Store.action");
            return;
        }

        System.out.println("✅ 店舗ログイン確認: storeId=" + store.getStoreId()
                         + ", storeName=" + store.getStoreName());

        // 商品登録画面へフォワード
        req.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp")
           .forward(req, res);
    }
}
