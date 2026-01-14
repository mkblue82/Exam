<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ログイン－ユーザー</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: #f5f5f5;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        #container {
            flex: 1;
            display: flex;
            flex-direction: column;
            width: 100%;
        }

        .column {
            flex: 1;
            width: 100%;
        }

        .main-contents {
            width: 100%;
            padding: 20px;
        }

        .login-container {
            max-width: 450px;
            width: 100%;
            margin: 80px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .login-container h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 5px;
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
        .form-group input[type="password"] {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
            box-sizing: border-box;
            transition: 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
        }

        .btn-login,
        .btn-register {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            margin-bottom: 0.8rem;
        }

        .btn-login {
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            font-family: inherit;
        }

        .btn-login:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-register {
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
            font-family: inherit;
        }

        .btn-register:hover {
            background: #c07148;
            color: #fff;
        }

        @media screen and (max-width: 600px) {
            .login-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }

            .login-container h1 {
                font-size: 1.5rem;
            }
        }

        .btn-back {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 0.9rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: normal;
            background: #f5f5f5;
            color: #666;
            border: 1px solid #ddd;
            font-family: inherit;
        }

        .btn-back:hover {
            background: #e0e0e0;
            color: #333;
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ログインコンテンツ -->
    <main class="column">
        <div class="main-contents">
            <div class="login-container">
                <h1>ログイン</h1>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="error-message">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/foodloss/LoginExecute.action" method="post">
                    <div class="form-group">
                        <label for="email">メールアドレス</label>
                        <input type="email" id="email" name="email" required
                               placeholder="example@mail.com"
                               value="${param.email != null ? param.email : ''}">
                    </div>

                    <div class="form-group">
                        <label for="password">パスワード</label>
                        <input type="password" id="password" name="password" required
                               placeholder="パスワードを入力してください">
                    </div>

                    <button type="submit" class="btn-login">ログイン</button>
                </form>

                <!-- フォームの外に配置 -->
                <button type="button" class="btn-register"
                        onclick="location.href='${pageContext.request.contextPath}/foodloss/SignupUser.action'">
                    新規登録
                </button>


                <!--トップページに戻ボタン -->
                <button type="button" class="btn-back"
                        onclick="location.href='${pageContext.request.contextPath}/jsp/index.jsp'">
                    トップページに戻る
                </button>
            </div>
        </div>
    </main>

    <!-- フッター読み込み -->
    <jsp:include page="footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>