<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ログイン - Sample Online Mall</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="login-container">
        <h1>ログイン</h1>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form action="login" method="post" id="loginForm">
            <div class="form-group">
                <label for="email">メールアドレス</label>
                <input type="email" id="email" name="email" required
                       placeholder="example@mail.com">
            </div>

            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password"
                       required minlength="8">
            </div>

            <button type="submit" class="btn-login">ログイン</button>
            <button type="button" class="btn-register"
                    onclick="location.href='register.jsp'">新規作成</button>
        </form>
    </div>

    <script src="js/validation.js"></script>
</body>
</html>