package foodloss;

import java.security.MessageDigest;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.DAO;
import dao.UserDAO;
import tool.Action;

public class EditInfoExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("user");

        if (loginUser == null) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String passwordConfirm = request.getParameter("passwordConfirm");


        // 未入力チェック（必須項目）
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("user", loginUser);
            request.setAttribute("error", "ユーザー名を入力してください。");
            request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("user", loginUser);
            request.setAttribute("error", "メールアドレスを入力してください。");
            request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
            return;
        }

        Connection con = null;

        try {
            // DAO経由でConnection取得
            DAO db = new DAO();
            con = db.getConnection();

            UserDAO dao = new UserDAO(con);
            User dbUser = dao.findById(loginUser.getUserId());

            if (dbUser == null) {
                request.setAttribute("user", loginUser);
                request.setAttribute("error", "ユーザー情報が見つかりません。");
                request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
                return;
            }

            boolean hasChanges = false;

            // --- 名前更新 ---
            if (name != null && !name.trim().isEmpty()) {
                if (!name.equals(dbUser.getName())) {
                    hasChanges = true;
                }
                dbUser.setName(name);
            }

            // --- メール重複チェック ---
            if (email != null && !email.trim().isEmpty()) {
                if (!email.equals(dbUser.getEmail())) {
                    User exist = dao.findByEmail(email);
                    if (exist != null && exist.getUserId() != loginUser.getUserId()) {
                        // 他ユーザーが使っている場合はエラー
                        request.setAttribute("user", dbUser);
                        request.setAttribute("error", "このメールアドレスは既に使用されています。");
                        request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
                        return;
                    }
                    hasChanges = true;
                }
                dbUser.setEmail(email);
            }

         // --- パスワード確認チェック ---
            if (password != null && !password.trim().isEmpty()) {

                if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
                    request.setAttribute("user", dbUser);
                    request.setAttribute("error", "パスワード確認を入力してください。");
                    request.getRequestDispatcher("/jsp/edit_info_user.jsp")
                           .forward(request, response);
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    request.setAttribute("user", dbUser);
                    request.setAttribute("error", "パスワードが一致しません。");
                    request.getRequestDispatcher("/jsp/edit_info_user.jsp")
                           .forward(request, response);
                    return;
                }
            }


         // --- 電話番号 ---
            if (phone != null && !phone.trim().isEmpty()) {
                if (!phone.equals(dbUser.getPhone())) {
                    hasChanges = true;
                }
                dbUser.setPhone(phone);
            }

         // ===== 電話番号重複チェック =====
            if (phone != null && !phone.isEmpty()) {
                if (!phone.equals(dbUser.getPhone())) {
                    User existPhone = dao.findByPhone(phone);
                    if (existPhone != null && existPhone.getUserId() != loginUser.getUserId()) {
                        request.setAttribute("user", dbUser);
                        request.setAttribute("error", "この電話番号は既に使用されています。");
                        request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
                        return;
                    }
                    dbUser.setPhone(phone);
                }
            }




            // --- パスワードハッシュ化して更新 ---
            if (password != null && !password.trim().isEmpty()) {
                dbUser.setPassword(hashPassword(password));
                hasChanges = true;
            }

            // DB更新
            dao.update(dbUser);

            // セッション更新
            session.setAttribute("user", dbUser);

            // 完了画面へ
            request.setAttribute("message", "ユーザー情報を更新しました。");
            request.getRequestDispatcher("/jsp/edit_info_result_user.jsp").forward(request, response);

        } catch (Exception e) {
        	e.printStackTrace();

            String message = e.getMessage();

            // 電話番号 UNIQUE 制約
            if (message != null && message.contains("uk_t004_user_phone")) {
                request.setAttribute("user", loginUser);
                request.setAttribute("error", "この電話番号は既に使用されています。");
                request.getRequestDispatcher("/jsp/edit_info_user.jsp")
                       .forward(request, response);
                return;
            }

            // メール UNIQUE 制約（あれば）
            if (message != null && message.contains("uk_t004_user_email")) {
                request.setAttribute("user", loginUser);
                request.setAttribute("error", "このメールアドレスは既に使用されています。");
                request.getRequestDispatcher("/jsp/edit_info_user.jsp")
                       .forward(request, response);
                return;
            }

            // その他のDBエラー
            request.setAttribute("user", loginUser);
            request.setAttribute("error", "データベースエラーが発生しました。");
            request.getRequestDispatcher("/jsp/edit_info_user.jsp")
                   .forward(request, response);



            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // SHA-256 ハッシュ化
    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}