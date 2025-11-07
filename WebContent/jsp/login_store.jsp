<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>店舗ログイン - Sample Online Mall</title>
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
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .login-wrapper {
            width: 100%;
            max-width: 420px;
        }

        .login-container {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 3rem 2.5rem;
            animation: fadeIn 0.5s ease;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .login-header {
            text-align: center;
            margin-bottom: 2.5rem;
        }

        .store-badge {
            display: inline-block;
            background: #c77c4a;
            color: #fff;
            padding: 0.5rem 1.5rem;
            border-radius: 50px;
            font-size: 0.85rem;
            font-weight: 600;
            margin-bottom: 1.5rem;
            letter-spacing: 0.5px;
        }

        .login-header h1 {
            color: #c77c4a;
            font-size: 1.8rem;
            font-weight: 700;
            margin: 0;
            padding-bottom: 1rem;
            border-bottom: 3px solid #c77c4a;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem 1.2rem;
            border-radius: 12px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .error-message::before {
            content: "⚠";
            font-size: 1.2rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.6rem;
            font-weight: 600;
            color: #444;
            font-size: 0.95rem;
        }

        .form-group input[type="text"],
        .form-group input[type="password"] {
            width: 100%;
            padding: 1rem;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: #fafafa;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c77c4a;
            background: #fff;
            box-shadow: 0 0 0 4px rgba(199, 124, 74, 0.1);
        }

        .form-group input::placeholder {
            color: #999;
        }

        .btn-login,
        .btn-register {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 12px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            margin-bottom: 0.8rem;
        }

        .btn-login {
            background: #c77c4a;
            color: #fff;
            box-shadow: 0 2px 8px rgba(199, 124, 74, 0.3);
        }

        .btn-login:hover {
            background: #b56c3a;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(199, 124, 74, 0.4);
        }

        .btn-login:active {
            transform: translateY(0);
        }

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
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 1px solid #e0e0e0;
        }

        .user-login-link a {
            color: #666;
            text-decoration: none;
            font-size: 0.9rem;
            transition: color 0.3s ease;
        }

        .user-login-link a:hover {
            color: #c77c4a;
        }

        @media screen and (max-width: 600px) {
            .login-container {
                padding: 2rem 1.5rem;
            }

            .login-header h1 {
                font-size: 1.5rem;
            }

            .btn-login,
            .btn-register {
                padding: 0.9rem;
            }
        }
    </style>
</head>
<body>
    <div class="login-wrapper">
        <div class="login-container">
            <div class="login-header">
                <span class="store-badge">店舗管理者</span>
                <h1>店舗ログイン</h1>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="error-message" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login_store" method="post" id="loginForm">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="form-group">
                    <label for="storeId">店舗ID</label>
                    <input type="text"
                           id="storeId"
                           name="storeId"
                           required
                           placeholder="店舗IDを入力"
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
                           placeholder="パスワードを入力"
                           autocomplete="current-password"
                           aria-required="true">
                </div>

                <button type="submit" class="btn-login">ログイン</button>
                <button type="button" class="btn-register"
                        onclick="location.href='${pageContext.request.contextPath}/jsp/signup_store.jsp'">
                    新規店舗登録
                </button>
            </form>

            <div class="user-login-link">
                <a href="${pageContext.request.contextPath}/jsp/login_user.jsp">← ユーザーログインはこちら</a>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
</body>
</html>  <!-- フッター読み込み -->
  <jsp:include page="footer.jsp" />

</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>

</body>
</html>