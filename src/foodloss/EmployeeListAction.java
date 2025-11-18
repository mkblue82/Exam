package foodloss;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeListAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        HttpSession session = req.getSession(false);
        String storeCode = null;

        if (session != null) {
            storeCode = (String) session.getAttribute("storeCode");
        }

        String employeeCode = req.getParameter("employeeCode");

        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> list = new ArrayList<>();

        try {
            if (employeeCode != null && !employeeCode.isEmpty()) {
                Employee e = dao.selectByCode(employeeCode);
                if (e != null && e.getStoreCode().equals(storeCode)) {
                    list.add(e);
                }
            } else if (storeCode != null && !storeCode.isEmpty()) {
                list = dao.selectByStoreCode(storeCode);
            }

            req.setAttribute("employeeList", list);
            req.setAttribute("employeeCode", employeeCode);
            req.setAttribute("storeCode", storeCode);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "社員情報の取得中にエラーが発生しました。");
        }

        req.getRequestDispatcher("/store_jsp/employee_list.jsp")
           .forward(req, res);
    }
}
