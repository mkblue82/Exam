<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>店舗ログイン</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* リセット */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif;
            background: #f5f5f5;
            min-height: 100vh;
            padding: 20px;
        }

        .login-wrapper {
            width: 100%;
            max-width: 440px;
            margin: 3rem auto;
        }

        .login-container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 3rem 2.5rem;
        }

        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .login-header h1 {
            color: #c77c4a;
            font-size: 2rem;
            font-weight: 700;
            margin: 0 0 1rem 0;
        }

        .login-header::after {
            content: '';
            display: block;
            width: 70%;
            height: 3px;
            background: #c77c4a;
            margin: 0 auto;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
            font-size: 0.9rem;
            word-wrap: break-word;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
            font-size: 1rem;
        }

        .form-group input[type="text"],
        .form-group input[type="password"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
            background: #fff;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c77c4a;
        }

        .btn-login,
        .btn-register {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            margin-bottom: 1rem;
        }

        .btn-login {
            background: #c77c4a;
            color: #fff;
        }

        .btn-login:hover {
            background: #b56c3a;
        }

        ssss

        .btn-register {
            background: #fff;
            color: #c77c4a;
            border: 2px solid #c77c4a;
        }

        .btn-register:hover {
            background: #c77c4a;
            color: #fff;
        }

        .user-login-link {
            text-align: center;
            margin-top: 1.5rem;
            padding-top: 1.5rem;
            border-top: 1px solid #ddd;
        }

        .user-login-link a {
            color: #666;
            text-decoration: none;
            font-size: 0.9rem;
        }

        .user-login-link a:hover {
            color: #c77c4a;
        }
    </style>
</head>
<body>
    <div class="login-wrapper">
        <div class="login-container">
            <div class="login-header">
                <h1>店舗ログイン</h1>
            </div>


            <form action="${pageContext.request.contextPath}/foodloss/Login_StoreExecute.action" method="post" id="loginForm">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="form-group">
                    <label for="storeId">店舗ID</label>
                    <input type="text"
                           id="storeId"
                           name="storeId"
                           required
                           aria-required="true"
                           value="${storeId}">
                </div>

                <div class="form-group">
                    <label for="password">パスワード</label>
                    <input type="password"
                           id="password"
                           name="password"
                           required
                           minlength="8"
                           autocomplete="current-password"
                           aria-required="true">
                </div>

                <button type="submit" class="btn-login">ログイン</button>
                <button type="button" class="btn-register"
                        onclick="location.href='${pageContext.request.contextPath}/store_jsp/signup_store.jsp'">
                    新規登録
                </button>
            </form>

            <div class="user-login-link">
                <a href="${pageContext.request.contextPath}/jsp/login_user.jsp">← ユーザーログインはこちら</a>
            </div>
        </div>
    </div>


    <jsp:include page="../jsp/footer.jsp" />

 <!-- JS -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
    <script src="../js/slick.js"></script>
    <script src="../js/main.js"></script>
    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
</body>
</html>