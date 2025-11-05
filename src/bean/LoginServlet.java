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
 * ログイン処理を行うサーブレット
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // データベース接続情報
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "20060318";

    /**
     * POSTリクエストの処理
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 文字エンコーディングの設定
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // フォームから送信されたデータを取得
        String emailAddress = request.getParameter("email_address");
        String password = request.getParameter("password");

        // 入力チェック
        if (emailAddress == null || emailAddress.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "メールアドレスとパスワードを入力してください。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
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

            // ユーザー認証のSQLクエリ
            String sql = "SELECT T001_PK1_user, T001_FD1_user, T001_FD2_user " +
                        "FROM T001_user " +
                        "WHERE T001_FD1_user = ? AND T001_FD2_user = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emailAddress);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            // 認証結果の判定
            if (rs.next()) {
                // ログイン成功
                int userId = rs.getInt("T001_PK1_user");
                String userEmail = rs.getString("T001_FD1_user");

                // セッションにユーザー情報を保存
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("userEmail", userEmail);
                session.setAttribute("userName", userEmail); // メールアドレスをユーザー名として使用

                // メインメニュー画面へリダイレクト
                response.sendRedirect(request.getContextPath() + "/mainMenu.jsp");

            } else {
                // ログイン失敗
                request.setAttribute("errorMessage",
                    "メールアドレスまたはパスワードが正しくありません。");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage",
                "データベースドライバが見つかりません。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage",
                "データベース接続エラーが発生しました。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);

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
        // ログイン画面へフォワード
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}