<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // CSRFトークンがセッションにない場合は生成
    if (session.getAttribute("csrfToken") == null) {
        String csrfToken = java.util.UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新規ユーザー登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

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

        .main-contents { width: 100%; padding: 20px; }

        .register-container {
            max-width: 450px;
            width: 100%;
            margin: 80px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .register-container h1 {
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

        .form-group { margin-bottom: 1.5rem; }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #555;
        }

        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="tel"],
        .form-group input[type="password"] {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
            transition: 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
        }

        .show-password {
            display: flex;
            align-items: center;
            margin-top: 5px;
            font-size: 0.9rem;
            color: #555;
        }

        .show-password input { margin-right: 5px; }

        .password-match {
            font-size: 0.85rem;
            margin-top: 5px;
            font-weight: bold;
        }

        .mismatch { color: #c62828; }

        .btn-submit,
        .btn-cancel {
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

        .btn-submit {
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
        }

        .btn-submit:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-cancel {
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
        }

        .btn-cancel:hover {
            background: #c07148;
            color: #fff;
        }
    </style>
</head>
<body>
<div id="container">
    <main>
        <div class="main-contents">
            <div class="register-container">
                <h1>新規ユーザー登録</h1>

                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="error-message">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/foodloss/SignupUser.action"
                      method="post" onsubmit="return checkPasswordMatch()">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="form-group">
                        <label for="name">氏名</label>
                        <input type="text" id="name" name="name" required
                               placeholder="名前を入力してください"
                               value="${param.name}">
                    </div>

                    <div class="form-group">
                        <label for="email">メールアドレス</label>
                        <input type="email" id="email" name="email" required
                               placeholder="example@mail.com"
                               value="${param.email}">
                    </div>

                    <div class="form-group">
                        <label for="phone">電話番号</label>
                        <input type="tel" id="phone" name="phone" required
                               placeholder="09012345678"
                               pattern="[0-9]{10,11}"
                               value="${param.phone}">
                    </div>

                    <div class="form-group">
                        <label for="password">パスワード</label>
                        <input type="password" id="password" name="password" required minlength="8"
                               placeholder="8文字以上で入力してください">
                        <div class="show-password">
                            <input type="checkbox" id="showPassword">
                            <label for="showPassword">パスワードを表示する</label>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">パスワード（確認）</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required minlength="8"
                               placeholder="もう一度入力してください">
                        <p id="passwordMessage" class="password-match"></p>
                    </div>

                    <button type="submit" class="btn-submit">新規登録</button>
                    <button type="button" class="btn-cancel"
                            onclick="location.href='${pageContext.request.contextPath}/foodloss/Login.action'">ログインに戻る</button>
                </form>
            </div>
        </div>
    </main>

    <jsp:include page="footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
    const passwordInput = document.getElementById("password");
    const confirmInput = document.getElementById("confirmPassword");
    const message = document.getElementById("passwordMessage");
    const showPasswordCheckbox = document.getElementById("showPassword");

    // パスワード表示切替
    showPasswordCheckbox.addEventListener("change", function() {
        const type = this.checked ? "text" : "password";
        passwordInput.type = type;
    });

    // リアルタイム一致チェック
    function checkPasswords() {
        if (confirmInput.value.length === 0) {
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

    // 最終送信チェック
    function checkPasswordMatch() {
        if (passwordInput.value !== confirmInput.value) {
            alert("パスワードが一致していません。");
            return false;
        }
        return true;
    }
</script>

</body>
</html>
