package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Product;
import dao.ProductDAO;
import tool.Action;

public class StoreProductListAction extends Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProductDAO dao = new ProductDAO();
        List<Product> list = dao.findAll();
        request.setAttribute("productList", list);
        return "/jsp/product_list.jsp";
    }
}
