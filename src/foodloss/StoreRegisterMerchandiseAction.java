package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tool.Action;

public class StoreRegisterMerchandiseAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 単純に登録ページへ遷移
        return "/jsp/register_merchandise.jsp";
    }
}
