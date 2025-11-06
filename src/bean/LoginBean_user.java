package bean;

import java.io.Serializable;

/**
 * ログイン情報を保持するBeanクラス
 * ログインフォームからの入力値を受け取る用
 */
public class LoginBean_user implements Serializable {
    private static final long serialVersionUID = 1L;

    // フィールド
    private String email;           // メールアドレス
    private String password;        // パスワード
    private boolean rememberMe;     // ログイン状態を保持するか
    private String errorMessage;    // エラーメッセージ

    // =====================================
    // コンストラクタ
    // =====================================

    /**
     * デフォルトコンストラクタ
     */
    public LoginBean_user() {
    }

    /**
     * メールアドレスとパスワードを設定するコンストラクタ
     */
    public LoginBean_user(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // =====================================
    // Getter / Setter
    // =====================================

    /**
     * メールアドレスを取得
     */
    public String getEmail() {
        return email;
    }

    /**
     * メールアドレスを設定
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * パスワードを取得
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * ログイン状態保持フラグを取得
     */
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * ログイン状態保持フラグを設定
     */
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    /**
     * エラーメッセージを取得
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * エラーメッセージを設定
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // =====================================
    // バリデーションメソッド
    // =====================================

    /**
     * メールアドレスが入力されているかチェック
     */
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    /**
     * パスワードが入力されているかチェック
     */
    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }

    /**
     * 入力値の基本チェック
     * @return エラーメッセージ（問題なければnull）
     */
    public String validate() {
        if (!hasEmail()) {
            return "メールアドレスを入力してください。";
        }

        if (!hasPassword()) {
            return "パスワードを入力してください。";
        }

        // メールアドレスの形式チェック
        if (!isValidEmail(email)) {
            return "メールアドレスの形式が正しくありません。";
        }

        // パスワードの長さチェック
        if (password.length() < 8) {
            return "パスワードは8文字以上で入力してください。";
        }

        return null; // エラーなし
    }

    /**
     * メールアドレスの形式チェック
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * データをクリア（セキュリティ対策）
     */
    public void clear() {
        this.email = null;
        this.password = null;
        this.rememberMe = false;
        this.errorMessage = null;
    }

    // =====================================
    // その他のメソッド
    // =====================================

    /**
     * オブジェクトの文字列表現（パスワードはマスク）
     */
    @Override
    public String toString() {
        return "LoginBean{" +
                "email='" + email + '\'' +
                ", password='***'" +  // パスワードはマスク
                ", rememberMe=" + rememberMe +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}