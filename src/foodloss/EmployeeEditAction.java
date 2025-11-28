package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeEditAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        // GET: 編集画面表示
        if (req.getMethod().equalsIgnoreCase("GET")) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                EmployeeDAO dao = new EmployeeDAO();
                Employee emp = dao.selectById(id);
                req.setAttribute("employee", emp);
                req.getRequestDispatcher("/store_jsp/employee_edit.jsp").forward(req, res);
                return;
            }
        }

        // POST: 更新処理
        int id = Integer.parseInt(req.getParameter("employeeId"));
        String name = req.getParameter("employeeName");

        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = dao.selectById(id);  // 現在の情報取得
        emp.setEmployeeName(name);          // 氏名を更新

        dao.update(emp);  // 更新実行

        res.sendRedirect(req.getContextPath() + "/foodloss/EmployeeList.action");
    }
}

