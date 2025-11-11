<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登録完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }

        .success-container {
            max-width: 450px;
            margin: 120px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            text-align: center;
        }

        .success-icon {
            font-size: 4rem;
            color: #4caf50;
            margin-bottom: 1rem;
        }

        h1 {
            color: #c07148;
            margin-bottom: 1rem;
        }

        .message {
            color: #666;
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .btn-login {
            display: inline-block;
            padding: 0.8rem 2rem;
            background: #c07148;
            color: #fff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: 0.3s;
        }

        .btn-login:hover {
            background: #a85d38;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <div class="success-container">
        <div class="success-icon">✓</div>
        <h1>登録完了</h1>
        <p class="message">
            ユーザー登録が完了しました。<br>
            ログインページからログインしてください。
        </p>
        <a href="${pageContext.request.contextPath}/jsp/login_user.jsp" class="btn-login">ログインページへ</a>
    </div>
</body>
</html>


