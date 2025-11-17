<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新規店舗登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        * { margin:0; padding:0; box-sizing:border-box; }
        body { font-family:-apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif; background:#f5f5f5; min-height:100vh; padding:20px; }
        .login-wrapper { width:100%; max-width:440px; margin:3rem auto; }
        .login-container { background:#fff; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.1); padding:3rem 2.5rem; }
        .login-header { text-align:center; margin-bottom:2rem; }
        .login-header h1 { color:#c77c4a; font-size:2rem; font-weight:700; margin:0 0 1rem 0; }
        .login-header::after { content:''; display:block; width:70%; height:3px; background:#c77c4a; margin:0 auto; }
        .error-message { background:#ffebee; color:#c62828; padding:1rem; border-radius:4px; margin-bottom:1.5rem; border-left:4px solid #c62828; font-size:0.9rem; word-wrap:break-word; }
        .form-group { margin-bottom:1.5rem; }
        .form-group label { display:block; margin-bottom:0.5rem; font-weight:600; color:#333; font-size:1rem; }
        .form-group input[type="text"], .form-group input[type="email"], .form-group input[type="tel"], .form-group input[type="password"], .form-group input[type="file"] {
            width:100%; padding:0.8rem; border:2px solid #ddd; border-radius:4px; font-size:1rem; transition:border-color 0.3s ease; background:#fff;
        }
        .form-group input:focus { outline:none; border-color:#c77c4a; }
        .btn-login, .btn-register { width:100%; padding:1rem; border:none; border-radius:4px; font-size:1rem; cursor:pointer; transition:all 0.3s ease; font-weight:600; margin-bottom:1rem; }
        .btn-login { background:#c77c4a; color:#fff; }
        .btn-login:hover { background:#b56c3a; }
        .btn-register { background:#fff; color:#c77c4a; border:2px solid #c77c4a; }
        .btn-register:hover { background:#c77c4a; color:#fff; }
        .user-login-link { text-align:center; margin-top:1.5rem; padding-top:1.5rem; border-top:1px solid #ddd; }
        .user-login-link a { color:#666; text-decoration:none; font-size:0.9rem; }
        .user-login-link a:hover { color:#c77c4a; }
        .password-match { font-size:0.85rem; margin-top:5px; font-weight:bold; }
        .mismatch { color:#c62828; }
        .show-password { display:flex; align-items:center; margin-top:5px; font-size:0.9rem; color:#555; }
        .show-password input { margin-right:5px; }
    </style>
</head>
<body>
<div class="login-wrapper">
    <div class="login-container">
        <div class="login-header">
            <h1>新規店舗登録</h1>
        </div>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/foodloss/SignupStore.action" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label for="storeName">店舗名</label>
                <input type="text" id="storeName" name="storeName" required placeholder="例：Sample Bakery"
                       value="${param.storeName != null ? param.storeName : ''}">
            </div>

            <div class="form-group">
                <label for="address">店舗住所</label>
                <input type="text" id="address" name="address" required placeholder="例：東京都新宿区〇〇1-2-3"
                       value="${param.address != null ? param.address : ''}">
            </div>

            <div class="form-group">
                <label for="phone">店舗電話番号</label>
                <input type="tel" id="phone" name="phone" required placeholder="例：0312345678"
                       pattern="[0-9]{10,11}" value="${param.phone != null ? param.phone : ''}">
            </div>

            <div class="form-group">
                <label for="email">店舗メールアドレス</label>
                <input type="email" id="email" name="email" required placeholder="例：store@mail.com"
                       value="${param.email != null ? param.email : ''}">
            </div>

            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" required minlength="8" placeholder="8文字以上で入力してください">
                <div class="show-password">
                    <input type="checkbox" id="togglePassword">
                    <label for="togglePassword">パスワードを表示する</label>
                </div>
            </div>

            <div class="form-group">
                <label for="passwordConfirm">パスワード（確認）</label>
                <input type="password" id="passwordConfirm" name="passwordConfirm" required minlength="8" placeholder="もう一度入力してください">
                <p id="passwordMessage" class="password-match"></p>
            </div>

            <div class="form-group">
                <label for="permitFile">営業許可書（画像またはPDF）</label>
                <input type="file" id="permitFile" name="permitFile" accept=".jpg,.jpeg,.png,.pdf" required>
            </div>

            <button type="submit" class="btn-login">申請する</button>
            <button type="button" class="btn-register"
                    onclick="location.href='${pageContext.request.contextPath}/foodloss/Login_Store.action'">
                ログインに戻る
            </button>
        </form>

        <div class="user-login-link">
            <a href="${pageContext.request.contextPath}/jsp/login_user.jsp">← ユーザーログインはこちら</a>
        </div>
    </div>
</div>

<jsp:include page="../jsp/footer.jsp" />

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>

<script>
    const passwordInput = document.getElementById("password");
    const confirmInput = document.getElementById("passwordConfirm");
    const message = document.getElementById("passwordMessage");
    const toggle = document.getElementById("togglePassword");

    toggle.addEventListener("change", function() {
        const type = this.checked ? "text" : "password";
        passwordInput.type = type;
        confirmInput.type = type;
    });

    function checkPasswords() {
        if (passwordInput.value.length === 0 || confirmInput.value.length === 0) {
            message.textContent = "";
            return;
        }
        if (passwordInput.value !== confirmInput.value) {
            message.textContent = "⚠ パスワードが一致しません";
            message.className = "password-match mismatch";
        } else {
            message.textContent = "";
        }
    }

    passwordInput.addEventListener("input", checkPasswords);
    confirmInput.addEventListener("input", checkPasswords);
</script>

</body>
</html>
