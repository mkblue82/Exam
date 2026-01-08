<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>情報変更</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        /* ======= 情報変更ページ専用 ======= */
        .main-content {
            max-width: 600px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .edit-section h2 {
            font-size: 1.8rem;
            color: #c07148;
            text-align: center;
            margin-bottom: 30px;
        }

        form label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
            color: #333;
        }

        form input[type="text"],
        form input[type="email"],
        form input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
            transition: border-color 0.3s;
        }

        form input:focus {
            border-color: #c07148;
            outline: none;
        }

        .button-group {
            display: flex;
            justify-content: center;
            gap: 15px;
            flex-wrap: wrap;
            margin-top: 30px;
        }

        .button-group button,
        .button-group a {
            padding: 12px 40px;
            background-color: #ccc;
            border-radius: 5px;
            text-decoration: none;
            color: #333;
            font-weight: bold;
            font-size: 16px;
            border: none;
            cursor: pointer;
            transition: all 0.3s;
            font-family: inherit;
        }

        .button-group button:hover,
        .button-group a:hover {
            background-color: #c07148;
            color: #fff;
            transform: translateY(-3px);
        }
    </style>
</head>
<body>
<div id="container">

    <!-- ✅ 共通ヘッダー -->
    <jsp:include page="header_user.jsp" />


    <!-- ✅ メインエリア -->
    <main class="column">
        <div class="main-contents">
            <div class="main-content">
                <div class="edit-section">
                    <h2>登録情報の変更</h2>

                    <form action="${pageContext.request.contextPath}/foodloss/EditInfoExecute.action" method="post" id="editForm">
                        <label for="username">ユーザー名</label>
                        <input type="text" id="name" name="name" value="${user.name}">

                        <label for="email">メールアドレス</label>
                        <input type="email" id="email" name="email" value="${user.email}">

                        <label for="password">新しいパスワード</label>
                        <input type="password" id="password" name="password" placeholder="変更しない場合は空欄">

                        <div class="button-group">
                            <button type="submit">変更を保存</button>
                            <a href="${pageContext.request.contextPath}/jsp/foods.jsp">戻る</a>
                        </div>
                    </form>

                    <%
                    String error = (String) request.getAttribute("error");
                    if (error != null) {
                    %>
                    <script>
                        // エラーメッセージを表示（サーバー側からエラーが返された場合のみ）
                        document.addEventListener('DOMContentLoaded', function() {
                            var errorMsg = "<%= error %>";

                            // エラー内容に応じて該当フィールドにエラーを設定
                            if (errorMsg.indexOf("ユーザー名") !== -1) {
                                var nameField = document.getElementById("name");
                                nameField.setCustomValidity(errorMsg);
                                nameField.reportValidity();
                            } else if (errorMsg.indexOf("メールアドレス") !== -1 && errorMsg.indexOf("使用されています") !== -1) {
                                // メール重複エラー
                                var emailField = document.getElementById("email");
                                emailField.setCustomValidity(errorMsg);
                                emailField.reportValidity();
                            } else if (errorMsg.indexOf("メールアドレス") !== -1) {
                                // メール未入力エラー
                                var emailField = document.getElementById("email");
                                emailField.setCustomValidity(errorMsg);
                                emailField.reportValidity();
                            } else {
                                alert(errorMsg);
                            }

                            // 入力時にエラーをクリア
                            var inputs = document.querySelectorAll('input');
                            inputs.forEach(function(input) {
                                input.addEventListener('input', function() {
                                    this.setCustomValidity('');
                                });
                            });
                        });
                    </script>
                    <% } %>
                </div>
            </div>
        </div>
    </main>

    <!-- ✅ 共通フッター -->
    <jsp:include page="footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>

</body>
</html>
