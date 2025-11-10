package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Merchandise;
import bean.Store;
import dao.MerchandiseDAO;
import tool.Action;

public class StoreMerchandiseListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int storeId = 0;

        Object storeObj = request.getSession().getAttribute("store");
        if (storeObj != null && storeObj instanceof Store) {
            storeId = ((Store) storeObj).getStoreId();
        }

        MerchandiseDAO dao = new MerchandiseDAO();
        List<Merchandise> list;

        if (storeId != 0) {
            list = dao.selectByStoreId(storeId);
        } else {
            list = dao.selectAll();
        }

        request.setAttribute("merchandiseList", list);

        request.getRequestDispatcher("/jsp/merchandise_list.jsp").forward(request, response);
    }
}
