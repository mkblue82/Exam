<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>店舗ログイン - Sample Online Mall</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* ログインページ専用CSS */
	        body {
	            background: #f5f5f5;
	        }

        .login-container {
            max-width: 450px;
            width: 90%;
            padding: 2.5rem;
            background: #fff;
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }

        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .login-header .store-badge {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            padding: 0.5rem 1.5rem;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: bold;
            margin-bottom: 1rem;
        }

        .login-container h1 {
            color: #333;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 2rem;
            font-weight: bold;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
            font-size: 0.9rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #555;
        }

        .form-group input[type="email"],
        .form-group input[type="password"],
        .form-group input[type="text"] {
            width: 100%;
            padding: 0.9rem;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 1rem;
            transition: 0.3s;
            box-sizing: border-box;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .btn-login,
        .btn-register {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            margin-bottom: 0.8rem;
        }

        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
        }

        .btn-register {
            background: #fff;
            color: #667eea;
            border: 2px solid #667eea;
        }

        .btn-register:hover {
            background: #667eea;
            color: #fff;
        }

        .additional-links {
            text-align: center;
            margin-top: 1.5rem;
        }

        .additional-links a {
            color: #667eea;
            text-decoration: none;
            font-size: 0.9rem;
        }

        .additional-links a:hover {
            text-decoration: underline;
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
        }

        .user-login-link a:hover {
            color: #667eea;
        }

        @media screen and (max-width: 600px) {
            .login-container {
                padding: 2rem;
            }

            .login-container h1 {
                font-size: 1.6rem;
            }
        }
    </style>
</head>
<body>
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
                       placeholder="店舗IDを入力してください"
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
                    onclick="location.href='${pageContext.request.contextPath}/jsp/signup_store.jsp'">
                新規店舗登録
            </button>
        </form>


        <div class="user-login-link">
            <a href="${pageContext.request.contextPath}/jsp/login_user.jsp">← ユーザーログインはこちら</a>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
</body>
</html>
 <jsp:include page="footer.jsp" />

</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>

</body>
</html>
