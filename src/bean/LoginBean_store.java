package bean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 店舗側ログイン処理を行うサーブレット
 *
 * フロー:
 * 1. 店舗ID・パスワード入力
 * 2. 入力値バリデーション（未入力チェック、形式チェック）
 * 3. 認証処理（アカウントシステム連携）
 * 4. セッション作成
 * 5. 店舗管理画面へ遷移
 */
@WebServlet("/shop/login")
public class LoginBean_store extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // データベース接続情報
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "20060318";

    // ログイン試行制限
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * POSTリクエストの処理 - ログイン認証
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 文字エンコーディングの設定
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // フォームから送信されたデータを取得
        String shopId = request.getParameter("shopId");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // ============================================
        // ステップ1: 入力値バリデーション
        // ID・PWのどちらかが未入力の場合
        // ============================================
        if (shopId == null || shopId.trim().isEmpty()) {
            request.setAttribute("errorMessage", "店舗IDを入力してください。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "パスワードを入力してください。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
            return;
        }

        // 店舗ID形式チェック（英数字、アンダースコア、ハイフンのみ）
        if (!shopId.matches("^[a-zA-Z0-9_-]+$")) {
            request.setAttribute("errorMessage",
                "店舗IDは英数字、アンダースコア、ハイフンのみで入力してください。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
            return;
        }

        // 店舗ID長チェック（3-50文字）
        if (shopId.length() < 3 || shopId.length() > 50) {
            request.setAttribute("errorMessage",
                "店舗IDは3文字以上50文字以内で入力してください。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
            return;
        }

        // パスワード長チェック（8文字以上）
        if (password.length() < 8) {
            request.setAttribute("errorMessage",
                "パスワードは8文字以上で入力してください。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // PostgreSQLドライバのロード
            Class.forName("org.postgresql.Driver");

            // データベース接続
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // ============================================
            // ステップ2: ログイン試行回数チェック
            // ============================================
            int failedAttempts = getFailedLoginAttempts(conn, shopId);
            if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                request.setAttribute("errorMessage",
                    "ログイン試行回数が上限に達しました。15分後に再度お試しください。");
                request.setAttribute("shopId", shopId);
                request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
                return;
            }

            // ============================================
            // ステップ3: 認証処理（アカウントシステム連携）
            // ID・PWが違った場合のチェック
            // ============================================
            String sql = "SELECT T002_PK1_store, T002_FD1_store, T002_FD2_store, " +
                        "T002_FD3_store, T002_FD4_store, T002_FD5_store " +
                        "FROM T002_store " +
                        "WHERE T002_FD1_store = ? AND T002_FD2_store = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shopId);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            // 認証結果の判定
            if (rs.next()) {
                // ============================================
                // ステップ4: アカウント状態チェック
                // ============================================
                int storeId = rs.getInt("T002_PK1_store");
                String storeShopId = rs.getString("T002_FD1_store");
                String storeName = rs.getString("T002_FD3_store");
                String storeStatus = rs.getString("T002_FD4_store");
                String storeCategory = rs.getString("T002_FD5_store");

                // 店舗アカウントが無効化されている場合
                if ("inactive".equals(storeStatus)) {
                    request.setAttribute("errorMessage",
                        "この店舗アカウントは無効化されています。サポートセンターにお問い合わせください。");
                    request.setAttribute("shopId", shopId);
                    request.getRequestDispatcher("/login_store.jsp").forward(request, response);
                    return;
                }

                // 審査中の店舗の場合
                if ("pending".equals(storeStatus)) {
                    request.setAttribute("errorMessage",
                        "現在審査中です。審査完了までお待ちください。");
                    request.setAttribute("shopId", shopId);
                    request.getRequestDispatcher("/login_store.jsp").forward(request, response);
                    return;
                }

                // 出店停止中の店舗の場合
                if ("suspended".equals(storeStatus)) {
                    request.setAttribute("errorMessage",
                        "出店が一時停止されています。詳細はサポートセンターまでお問い合わせください。");
                    request.setAttribute("shopId", shopId);
                    request.getRequestDispatcher("/login_store.jsp").forward(request, response);
                    return;
                }

                // ============================================
                // ステップ5: ログイン成功 - セッション作成
                // ============================================

                // 既存セッションを無効化（セッション固定攻撃対策）
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }

                // 新しいセッション作成
                HttpSession session = request.getSession(true);

                // セッションタイムアウト設定
                if ("on".equals(rememberMe) || "true".equals(rememberMe)) {
                    session.setMaxInactiveInterval(604800); // 7日間
                } else {
                    session.setMaxInactiveInterval(1800); // 30分
                }

                // セッションに店舗情報を保存
                session.setAttribute("storeId", storeId);
                session.setAttribute("shopId", storeShopId);
                session.setAttribute("shopName", storeName);
                session.setAttribute("shopCategory", storeCategory);
                session.setAttribute("shopStatus", storeStatus);
                session.setAttribute("userType", "shop_owner");

                // 最終ログイン日時を更新
                updateLastLogin(conn, storeId);

                // ログイン失敗回数をリセット
                resetFailedLoginCount(conn, shopId);

                // ============================================
                // ステップ6: メイン画面へ遷移（店舗管理画面）
                // ============================================
                response.sendRedirect(request.getContextPath() + "/shop/dashboard.jsp");

            } else {
                // ============================================
                // ログイン失敗 - ID・PWが違った場合
                // ============================================

                // 失敗回数を記録
                recordFailedLogin(conn, shopId);
                failedAttempts++;

                // 失敗回数に応じたエラーメッセージ
                String errorMsg;
                int remainingAttempts = MAX_LOGIN_ATTEMPTS - failedAttempts;

                if (remainingAttempts <= 0) {
                    errorMsg = "ログイン試行回数が上限に達しました。15分後に再度お試しください。";
                } else if (remainingAttempts <= 2) {
                    errorMsg = "店舗IDまたはパスワードが正しくありません。\n" +
                              "残り" + remainingAttempts + "回失敗するとアカウントがロックされます。";
                } else {
                    errorMsg = "店舗IDまたはパスワードが正しくありません。";
                }

                request.setAttribute("errorMessage", errorMsg);
                request.setAttribute("shopId", shopId);
                request.getRequestDispatcher("/login_store.jsp").forward(request, response);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage",
                "データベースドライバが見つかりません。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_store.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage",
                "データベース接続エラーが発生しました。");
            request.setAttribute("shopId", shopId);
            request.getRequestDispatcher("/login_store.jsp").forward(request, response);

        } finally {
            // リソースのクローズ
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GETリクエストの処理（ログイン画面の表示）
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 既にログイン済みの店舗はダッシュボードへ
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("shopId") != null) {
            response.sendRedirect(request.getContextPath() + "/shop/dashboard.jsp");
            return;
        }

        // ログイン画面へフォワード
        request.getRequestDispatcher("/login_shop.jsp").forward(request, response);
    }

    /**
     * ログイン失敗回数を取得
     */
    private int getFailedLoginAttempts(Connection conn, String shopId) throws SQLException {
        String sql = "SELECT T003_FD2_login_fail " +
                    "FROM T003_login_attempts " +
                    "WHERE T003_FD1_shop_id = ? " +
                    "AND T003_FD3_attempt_time > NOW() - INTERVAL '15 minutes'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shopId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("T003_FD2_login_fail");
                }
            }
        }
        return 0;
    }

    /**
     * ログイン失敗を記録
     */
    private void recordFailedLogin(Connection conn, String shopId) throws SQLException {
        String sql = "INSERT INTO T003_login_attempts " +
                    "(T003_FD1_shop_id, T003_FD2_login_fail, T003_FD3_attempt_time) " +
                    "VALUES (?, 1, NOW()) " +
                    "ON CONFLICT (T003_FD1_shop_id) " +
                    "DO UPDATE SET " +
                    "T003_FD2_login_fail = T003_login_attempts.T003_FD2_login_fail + 1, " +
                    "T003_FD3_attempt_time = NOW()";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shopId);
            pstmt.executeUpdate();
        }
    }

    /**
     * ログイン失敗回数をリセット
     */
    private void resetFailedLoginCount(Connection conn, String shopId) throws SQLException {
        String sql = "DELETE FROM T003_login_attempts WHERE T003_FD1_shop_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shopId);
            pstmt.executeUpdate();
        }
    }

    /**
     * 最終ログイン日時を更新
     */
    private void updateLastLogin(Connection conn, int storeId) throws SQLException {
        String sql = "UPDATE T002_store " +
                    "SET T002_FD6_last_login = NOW() " +
                    "WHERE T002_PK1_store = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            pstmt.executeUpdate();
        }
    }
}