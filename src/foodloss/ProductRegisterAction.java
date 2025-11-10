package action;

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
import dao.MerchandiseDAO;

/**
 * 商品登録処理を行うサーブレット
 * 画像の仕様に基づいた実装
 */
@WebServlet("/product_register")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 5,        // 5MB
    maxRequestSize = 1024 * 1024 * 10     // 10MB
)
public class ProductRegisterAction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploads/products";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 商品登録画面を表示
        HttpSession session = request.getSession(false);

        // セッションチェック
        if (session == null || session.getAttribute("store") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // CSRFトークン生成
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);

        request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // セッションチェック
        if (session == null || session.getAttribute("store") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // CSRFトークン検証
        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("errorMessage", "不正なリクエストです。");
            request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
            return;
        }

        // 新しいCSRFトークンを生成（ワンタイムトークン）
        String newCsrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", newCsrfToken);

        try {
            // パラメータ取得
            String productName = request.getParameter("productName");
            String quantityStr = request.getParameter("quantity");
            String expirationDateStr = request.getParameter("expirationDate");
            String tags = request.getParameter("tags");
            Part imagePart = request.getPart("productImage");

            // 基本バリデーション - 未入力チェック（画像仕様①-1）
            String validationError = validateInput(productName, quantityStr, expirationDateStr, imagePart);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }

            // 数値変換とバリデーション
            int quantity;
            Date expirationDate;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 1 || quantity > 9999) {
                    request.setAttribute("errorMessage", "個数は1～9999の範囲で入力してください。");
                    request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                    return;
                }

                expirationDate = Date.valueOf(expirationDateStr);
                Date today = new Date(System.currentTimeMillis());
                if (expirationDate.before(today)) {
                    request.setAttribute("errorMessage", "消費期限は今日以降の日付を指定してください。");
                    request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException | IllegalArgumentException e) {
                request.setAttribute("errorMessage", "入力形式が正しくありません。");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }

            // 商品名の長さチェック
            if (productName.length() > 100) {
                request.setAttribute("errorMessage", "商品名は100文字以内で入力してください。");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }

            // タグの長さチェック
            if (tags != null && tags.length() > 200) {
                request.setAttribute("errorMessage", "タグは200文字以内で入力してください。");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }

            // 店舗情報取得
            bean.Store store = (bean.Store) session.getAttribute("store");
            if (store == null) {
                request.setAttribute("errorMessage", "店舗情報が取得できませんでした。");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }
            int storeId = store.getStoreId();

            // 従業員ID取得（セッションから取得、なければ0）
            Integer employeeId = (Integer) session.getAttribute("employeeId");
            if (employeeId == null) {
                employeeId = 0; // デフォルト値
            }

            // 商品の重複チェック（画像仕様②-2）
            MerchandiseDAO merchandiseDAO = new MerchandiseDAO();
            if (merchandiseDAO.isDuplicateProduct(storeId, productName)) {
                request.setAttribute("errorMessage", "入力された店舗情報は登録済みです");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }

            // 画像ファイルのバリデーションと保存
            String imagePath = validateAndSaveImage(imagePart);
            if (imagePath == null) {
                request.setAttribute("errorMessage", "画像ファイルの保存に失敗しました。ファイル形式とサイズを確認してください。");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
                return;
            }

            // 価格はデフォルト0（必要に応じて画面から取得）
            int price = 0;

            // 商品オブジェクト作成
            Merchandise merchandise = new Merchandise();
            merchandise.setProductName(productName);
            merchandise.setStock(quantity);
            merchandise.setPrice(price);
            merchandise.setUseByDate(expirationDate);
            merchandise.setProductTag(tags != null ? tags : "");
            merchandise.setEmployeeId(employeeId);
            merchandise.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
            merchandise.setStoreId(storeId);
            merchandise.setBookingStatus(false); // デフォルトは予約なし

            // DB登録
            int result = merchandiseDAO.insert(merchandise);

            if (result > 0) {
                // 登録成功 - 商品登録完了画面へリダイレクト（画像仕様③）
                session.setAttribute("successMessage", "商品を登録しました。");
                response.sendRedirect(request.getContextPath() + "/product_register_complete");
            } else {
                // 登録失敗
                request.setAttribute("errorMessage", "商品の登録に失敗しました。もう一度お試しください。");
                request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。");
            request.getRequestDispatcher("/jsp/product_register.jsp").forward(request, response);
        }
    }

    /**
     * 入力値の基本バリデーション
     * 画像仕様①-1：未入力フィールドチェック
     * @return エラーメッセージ（エラーがない場合null）
     */
    private String validateInput(String productName, String quantity,
                                 String expirationDate, Part imagePart) {

        // 未入力チェック
        if (productName == null || productName.trim().isEmpty()) {
            return "このフィールドを入力してください"; // 商品名
        }

        if (quantity == null || quantity.trim().isEmpty()) {
            return "このフィールドを入力してください"; // 個数
        }

        if (expirationDate == null || expirationDate.trim().isEmpty()) {
            return "このフィールドを入力してください"; // 消費期限
        }

        if (imagePart == null || imagePart.getSize() == 0) {
            return "このフィールドを入力してください"; // 画像
        }

        return null; // エラーなし
    }

    /**
     * 画像ファイルのバリデーションと保存
     * @param imagePart アップロードされた画像ファイル
     * @return 保存された画像の相対パス（失敗時null）
     */
    private String validateAndSaveImage(Part imagePart) {
        try {
            // ファイルサイズチェック
            if (imagePart.getSize() > MAX_FILE_SIZE) {
                return null;
            }

            // ファイル名取得
            String fileName = getFileName(imagePart);
            if (fileName == null || fileName.isEmpty()) {
                return null;
            }

            // 拡張子チェック
            String extension = getFileExtension(fileName).toLowerCase();
            boolean validExtension = false;
            for (String allowed : ALLOWED_EXTENSIONS) {
                if (extension.equals(allowed)) {
                    validExtension = true;
                    break;
                }
            }

            if (!validExtension) {
                return null;
            }

            // ファイル保存
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // ユニークなファイル名生成
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            String uniqueFileName = timestamp + "_" + UUID.randomUUID().toString() + extension;
            String filePath = uploadPath + File.separator + uniqueFileName;

            // ファイル保存
            imagePart.write(filePath);

            // DBに保存する相対パス（現時点ではファイルシステムのみに保存）
            return UPLOAD_DIR + "/" + uniqueFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Partからファイル名を取得
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf("=") + 1).trim();
                // ダブルクォートを除去
                return fileName.replace("\"", "");
            }
        }
        return null;
    }

    /**
     * ファイル名から拡張子を取得
     */
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex > 0) {
            return fileName.substring(lastIndex);
        }
        return "";
    }
}