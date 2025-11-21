package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Employee;
import dao.EmployeeDAO;
import tool.Action;

public class MerchandiseRegisterAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        System.out.println("===== 商品登録画面 開始 =====");

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            System.out.println("✅ DAO生成OK");

            List<Employee> employeeList = employeeDAO.selectAll();
            System.out.println("✅ selectAll実行完了");
            System.out.println("✅ 取得件数: " + employeeList.size());

            for (Employee emp : employeeList) {
                System.out.println("  → ID:" + emp.getId() + " 名前:" + emp.getEmployeeName());
            }

            request.setAttribute("employeeList", employeeList);

        } catch (Exception e) {
            System.out.println("❌ エラー発生: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("===== JSPへフォワード =====");
        request.getRequestDispatcher("/store_jsp/merchandise_register_store.jsp")
               .forward(request, response);
    }
}