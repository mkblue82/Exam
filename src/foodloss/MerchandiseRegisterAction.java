package foodloss;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import bean.Merchandise;
import bean.Store;
import dao.MerchandiseDAO;

@WebServlet("/merchandise_register")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 5,        // 5MB
    maxRequestSize = 1024 * 1024 * 10     // 10MB
)
public class MerchandiseRegisterAction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploads/merchandise";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("store") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);

        request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("store") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // CSRFチェック
        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("errorMessage", "不正なリクエストです。");
            request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
            return;
        }
        session.setAttribute("csrfToken", UUID.randomUUID().toString());

        try {
            String name = request.getParameter("productName");
            String stockStr = request.getParameter("quantity");
            String dateStr = request.getParameter("expirationDate");
            String tag = request.getParameter("tags");
            Part imagePart = request.getPart("productImage");

            // 入力チェック
            String error = validateInput(name, stockStr, dateStr, imagePart);
            if (error != null) {
                request.setAttribute("errorMessage", error);
                request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
                return;
            }

            int stock = Integer.parseInt(stockStr);
            Date useBy = Date.valueOf(dateStr);
            Date today = new Date(System.currentTimeMillis());
            if (useBy.before(today)) {
                request.setAttribute("errorMessage", "消費期限は今日以降の日付を指定してください。");
                request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
                return;
            }

            Store store = (Store) session.getAttribute("store");
            int storeId = store.getStoreId();

            MerchandiseDAO dao = new MerchandiseDAO();

            // ✅ 重複チェック（後述のDAOメソッドが必要）
            if (dao.isDuplicateProduct(storeId, name)) {
                request.setAttribute("errorMessage", "同じ商品名が既に登録されています。");
                request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
                return;
            }

            // ✅ 画像保存
            String imagePath = validateAndSaveImage(imagePart);
            if (imagePath == null) {
                request.setAttribute("errorMessage", "画像の保存に失敗しました。");
                request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
                return;
            }

            Merchandise m = new Merchandise();
            m.setProductName(name);
            m.setStock(stock);
            m.setPrice(0);
            m.setUseByDate(useBy);
            m.setProductTag(tag != null ? tag : "");
            m.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
            m.setStoreId(storeId);
            m.setBookingStatus(false);

            int result = dao.insert(m);
            if (result > 0) {
                session.setAttribute("successMessage", "商品を登録しました。");
                response.sendRedirect(request.getContextPath() + "/merchandise_register_complete");
            } else {
                request.setAttribute("errorMessage", "商品の登録に失敗しました。");
                request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "システムエラーが発生しました。");
            request.getRequestDispatcher("/jsp/merchandise_register.jsp").forward(request, response);
        }
    }

    // --- 以下はユーティリティメソッド ---
    private String validateInput(String name, String stock, String date, Part image) {
        if (name == null || name.isEmpty()) return "商品名を入力してください。";
        if (stock == null || stock.isEmpty()) return "個数を入力してください。";
        if (date == null || date.isEmpty()) return "消費期限を入力してください。";
        if (image == null || image.getSize() == 0) return "画像を選択してください。";
        return null;
    }

    private String validateAndSaveImage(Part imagePart) {
        try {
            if (imagePart.getSize() > MAX_FILE_SIZE) return null;
            String fileName = getFileName(imagePart);
            if (fileName == null || fileName.isEmpty()) return null;

            String ext = getFileExtension(fileName).toLowerCase();
            boolean ok = false;
            for (String allowed : ALLOWED_EXTENSIONS)
                if (ext.equals(allowed)) ok = true;
            if (!ok) return null;

            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            String uniqueName = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date())
                    + "_" + UUID.randomUUID() + ext;
            imagePart.write(uploadPath + File.separator + uniqueName);
            return UPLOAD_DIR + "/" + uniqueName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd == null) return null;
        for (String token : cd.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private String getFileExtension(String name) {
        int i = name.lastIndexOf(".");
        return (i > 0) ? name.substring(i) : "";
    }
}
