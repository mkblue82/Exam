package foodloss;

import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.FileInfo;
import dao.StoreDAO;
import tool.Action;
import tool.DBManager;


public class DownloadLicenseAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        DBManager db = new DBManager();

        try {
            String storeIdStr = request.getParameter("storeId");
            if (storeIdStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int storeId = Integer.parseInt(storeIdStr);

            try (Connection conn = db.getConnection()) {

                StoreDAO dao = new StoreDAO(conn);
                // 改良: ライセンスデータとファイルタイプを取得する
                FileInfo fileInfo = dao.getLicenseFile(storeId); // byte[] と拡張子（例: pdf, png）

                if (fileInfo == null || fileInfo.getData() == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                byte[] fileData = fileInfo.getData();
                String fileType = fileInfo.getType(); // "pdf" または "png"

                String contentType;
                switch (fileType.toLowerCase()) {
                    case "png":
                        contentType = "image/png";
                        break;
                    case "pdf":
                        contentType = "application/pdf";
                        break;
                    default:
                        contentType = "application/octet-stream";
                        break;
                }


                response.setContentType(contentType);
                response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"license_store_" + storeId + "." + fileType + "\""
                );

                OutputStream out = response.getOutputStream();
                out.write(fileData);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {}
        }
    }
}
