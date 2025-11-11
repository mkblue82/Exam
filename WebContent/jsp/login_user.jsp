<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ログイン</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
/* 必要に応じて以前のCSSをここに貼る */
* { margin:0; padding:0; box-sizing:border-box; }
body { font-family:Arial,sans-serif; background:#f5f5f5; min-height:100vh; padding:20px; }
.login-wrapper { width:100%; max-width:440px; margin:3rem auto; }
.login-container { background:#fff; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.1); padding:3rem 2.5rem; }
.login-header { text-align:center; margin-bottom:2rem; }
.login-header h1 { color:#c77c4a; font-size:2rem; font-weight:700; margin:0 0 1rem 0; }
.error-message { background:#ffebee; color:#c62828; padding:1rem; border-radius:4px; margin-bottom:1.5rem; border-left:4px solid #c62828; font-size:0.9rem; }
.form-group { margin-bottom:1.5rem; }
.form-group label { display:block; margin-bottom:0.5rem; font-weight:600; color:#333; font-size:1rem; }
.form-group input[type="email"], .form-group input[type="password"] { width:100%; padding:0.8rem; border:2px solid #ddd; border-radius:4px; font-size:1rem; }
.form-group input:focus { outline:none; border-color:#c07148; }
.btn-login, .btn-register { width:100%; padding:1rem; border:none; border-radius:4px; font-size:1rem; cursor:pointer; margin-bottom:1rem; font-weight:600; }
.btn-login { background:#c07148; color:#fff; }
.btn-login:hover { background:#a85d38; }
.btn-register { background:#fff; color:#c77c4a; border:2px solid #c77c4a; }
.btn-register:hover { background:#c77c4a; color:#fff; }
</style>
</head>
<body>
<div class="login-wrapper">
    <div class="login-container">
        <div class="login-header">
            <h1>ログイン</h1>
        </div>

        <!-- エラー表示 -->
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/LoginExecute.Action" method="post">

            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email" id="email" name="email" value="${param.email != null ? param.email : ''}" required>
            </div>
            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn-login">ログイン</button>
            <button type="button" class="btn-register" onclick="location.href='${pageContext.request.contextPath}/jsp/signup_user.jsp'">新規登録</button>
        </form>
    </div>
</div>
</body>
</html>
