package foodloss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Employee;
import bean.Store;
import dao.EmployeeDAO;
import tool.Action;

public class EmployeeRegisterAction extends Action {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {

	    // ★ 1. 先に GET リクエストかどうかチェックする
	    if (request.getMethod().equalsIgnoreCase("GET")) {
	        request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
	               .forward(request, response);
	        return;
	    }

	    // ★ 2. POSTリクエスト時だけ以下を実行する
	    request.setCharacterEncoding("UTF-8");

	    String employeeCodeStr = request.getParameter("employeeCode");
	    String employeeName = request.getParameter("employeeName");

	    if (employeeCodeStr == null || employeeCodeStr.isEmpty()) {
	        request.setAttribute("error", "社員コードを入力してください。");
	        request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
	               .forward(request, response);
	        return;
	    }

	    if (employeeName == null || employeeName.isEmpty()) {
	        request.setAttribute("error", "社員名を入力してください。");
	        request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
	               .forward(request, response);
	        return;
	    }

	    // ★ employeeCodeStr は null/空でないと保証されてから parseInt する
	    int employeeCode = Integer.parseInt(employeeCodeStr);

	    HttpSession session = request.getSession();
	    Store store = (Store) session.getAttribute("store");

	    if (store == null) {
	        request.setAttribute("error", "ログイン情報がありません。再ログインしてください。");
	        request.getRequestDispatcher("/store_jsp/login_store.jsp")
	               .forward(request, response);
	        return;
	    }

	    Employee emp = new Employee();
	    emp.setId(employeeCode);
	    emp.setEmployeeCode(employeeCodeStr);
	    emp.setEmployeeName(employeeName);
	    emp.setStoreCode(String.valueOf(store.getStoreId()));

	    EmployeeDAO dao = new EmployeeDAO();
	    int result = dao.insert(emp);

	    if (result > 0) {
	        response.sendRedirect(request.getContextPath() + "/foodloss/EmployeeList.action");
	    } else {
	        request.setAttribute("error", "登録に失敗しました。");
	        request.getRequestDispatcher("/store_jsp/employee_register_store.jsp")
	               .forward(request, response);
	    }
	}

}
