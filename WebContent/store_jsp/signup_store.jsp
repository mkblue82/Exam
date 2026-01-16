<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // CSRFトークンをセッションに生成
    if (session.getAttribute("csrfToken") == null) {
        session.setAttribute("csrfToken", java.util.UUID.randomUUID().toString());
    }
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新規店舗登録</title>
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

    .main-contents {
        width: 100%;
        padding: 20px;
    }

    .register-container {
        max-width: 450px;
        width: 100%;
        margin: 80px auto;
        padding: 2rem;
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
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

    .form-group {
        margin-bottom: 1.5rem;
    }

    .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: bold;
        color: #555;
    }

    .form-group input[type="text"],
    .form-group input[type="email"],
    .form-group input[type="tel"],
    .form-group input[type="password"],
    .form-group input[type="file"] {
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

    .show-password input {
        margin-right: 5px;
    }

    .password-match {
        font-size: 0.85rem;
        margin-top: 5px;
        font-weight: bold;
    }

    .mismatch {
        color: #c62828;
    }

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
        font-family: inherit;
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
        font-family: inherit;
    }

    .btn-cancel:hover {
        background: #c07148;
        color: #fff;
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
    <main>
        <div class="main-contents">
            <div class="register-container">
                <h1>新規店舗登録</h1>

                <form action="${pageContext.request.contextPath}/foodloss/SignupStore.action"
                      method="post" enctype="multipart/form-data" onsubmit="return checkPasswordMatch()">
                    <!-- CSRFトークン -->
                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>">

                    <!-- ★エラーメッセージを店舗名の上に表示★ -->
                    <% if (request.getAttribute("formError") != null) { %>
                        <div class="error-message">
                            <%= request.getAttribute("formError") %>
                        </div>
                    <% } %>

                    <div class="form-group">
                        <label for="storeName">店舗名</label>
                        <input type="text" id="storeName" name="storeName" required placeholder="例:Sample Bakery" value="${storeName}">
                    </div>

                    <div class="form-group">
                        <label for="address">住所</label>
                        <input type="text" id="address" name="address" required placeholder="例:東京都新宿区〇〇1-2-3" value="${address}">
                    </div>

                    <div class="form-group">
                        <label for="phone">電話番号</label>
                        <input type="tel" id="phone" name="phone" required pattern="[0-9]{10,11}" placeholder="0312345678" value="${phone}">
                    </div>

                    <div class="form-group">
                        <label for="email">メールアドレス</label>
                        <input type="email" id="email" name="email" required placeholder="store@mail.com" value="${email}">
                    </div>

                    <div class="form-group">
                        <label for="password">パスワード</label>
                        <input type="password" id="password" name="password" required minlength="8" placeholder="8文字以上で入力してください">
                        <div class="show-password">
                            <input type="checkbox" id="showPassword">
                            <label for="showPassword">パスワードを表示する</label>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="passwordConfirm">パスワード（確認）</label>
                        <input type="password" id="passwordConfirm" name="passwordConfirm" required minlength="8" placeholder="もう一度入力してください">
                        <p id="passwordMessage" class="password-match"></p>
                    </div>

                    <div class="form-group">
                        <label for="permitFile">営業許可書(画像またはPDF)</label>
                        <input type="file" id="permitFile" name="permitFile" accept=".jpg,.jpeg,.png,.pdf" required>
                    </div>

                    <button type="submit" class="btn-submit">申請する</button>
                    <button type="button" class="btn-cancel"
                            onclick="location.href='${pageContext.request.contextPath}/store_jsp/login_store.jsp'">ログインに戻る</button>

                     <!--トップページに戻るボタン -->
                	<button type="button" class="btn-back"
                        	onclick="location.href='${pageContext.request.contextPath}/jsp/index.jsp'">
                    	トップページに戻る
                	</button>
                </form>
            </div>
        </div>
    </main>

    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script>
    const passwordInput = document.getElementById("password");
    const confirmInput = document.getElementById("passwordConfirm");
    const message = document.getElementById("passwordMessage");
    const showCheckbox = document.getElementById("showPassword");

    showCheckbox.addEventListener("change", function() {
        passwordInput.type = this.checked ? "text" : "password";
    });

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