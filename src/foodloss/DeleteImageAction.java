package foodloss;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

public class DeleteImageAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            int imageId = Integer.parseInt(request.getParameter("imageId"));
            int merchandiseId = Integer.parseInt(request.getParameter("merchandiseId"));

            try (Connection con = new DBManager().getConnection()) {
                MerchandiseImageDAO dao = new MerchandiseImageDAO(con);
                dao.delete(imageId);
            }

            // 更新画面へ戻る
            response.sendRedirect(
                request.getContextPath() + "/foodloss/MerchandiseEdit.action?id=" + merchandiseId
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
