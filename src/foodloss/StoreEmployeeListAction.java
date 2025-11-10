package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class StoreEmployeeListAction extends Action {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> list = dao.findAll();
        request.setAttribute("employeeList", list);
        return "/jsp/employee_list.jsp";
    }
}
