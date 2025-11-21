package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import bean.Store;
import dao.EmployeeDAO;
import tool.Action;

public class MerchandiseRegisterAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HttpSession session = request.getSession();
        Store store = (Store) session.getAttribute("store");

        if (store == null) {
            response.sendRedirect(request.getContextPath() + "/foodloss/Login_Store.action");
            return;
        }

        // 店舗に所属する社員リストを取得
        EmployeeDAO employeeDAO = new EmployeeDAO();
        List<Employee> employeeList = employeeDAO.selectByStoreCode(String.valueOf(store.getStoreId()));

        // 社員が存在すれば最初の社員を自動セット
        if (!employeeList.isEmpty()) {
            request.setAttribute("defaultEmployee", employeeList.get(0));
        }

        request.setAttribute("employeeList", employeeList);
        request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp")
               .forward(request, response);
    }
}