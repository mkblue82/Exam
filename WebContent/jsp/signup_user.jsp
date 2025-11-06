<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>新規ユーザー登録 - Sample Online Mall</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="login-container">
    <h1>新規ユーザー登録</h1>

    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("errorMessage") %>
        </div>
    <% } %>

    <form action="register" method="post" id="registerForm">
        <div class="form-group">
            <label for="name">氏名</label>
            <input type="text" id="name" name="name" required placeholder="山田 太郎">
        </div>

        <div class="form-group">
            <label for="email">メールアドレス</label>
            <input type="email" id="email" name="email" required placeholder="example@mail.com">
        </div>

        <div class="form-group">
            <label for="password">パスワード</label>
            <input type="password" id="password" name="password" required minlength="8">
        </div>

        <button type="submit" class="btn-register">新規登録</button>
        <button type="button" class="btn-login" onclick="location.href='login.jsp'">ログインに戻る</button>
    </form>
</div>
</body>
</html>
