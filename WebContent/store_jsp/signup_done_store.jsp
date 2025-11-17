<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="bean.Store" %>
<%
    Store store = (Store) session.getAttribute("registeredStore");
    if (store == null) {
        response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登録完了</title>
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

        .success-container {
            max-width: 450px;
            width: 100%;
            margin: 80px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            text-align: center;
        }

        .success-container h1 {
            color: #c07148;
            margin-bottom: 1.5rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
        }

        .message {
            color: #555;
            margin-bottom: 2rem;
            line-height: 1.6;
            font-size: 1rem;
        }

        .store-info {
            background: #f9f9f9;
            padding: 1.5rem;
            border-radius: 5px;
            margin-bottom: 2rem;
            text-align: left;
        }

        .store-info p {
            margin: 0.5rem 0;
            color: #555;
        }

        .store-info strong {
            color: #333;
        }

        .notice-box {
            background: #fff3cd;
            border: 1px solid #ffc107;
            border-radius: 5px;
            padding: 1rem;
            margin-bottom: 2rem;
            text-align: left;
        }

        .notice-box h3 {
            color: #856404;
            font-size: 1rem;
            margin-bottom: 0.5rem;
        }

        .notice-box p {
            color: #856404;
            font-size: 0.9rem;
            line-height: 1.5;
            margin: 0;
        }

        .btn-login {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            text-decoration: none;
            display: inline-block;
        }

        .btn-login:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        @media screen and (max-width: 600px) {
            .success-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }

            .success-container h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
<div id="container">
    <!-- 登録完了 -->
    <main class="column">
        <div class="main-contents">
            <div class="success-container">
                <h1>申請完了</h1>

                <p class="message">
                    店舗登録の申請が完了しました！<br>
                    ご登録ありがとうございます。
                </p>

                <div class="store-info">
                    <p><strong>店舗名：</strong><%= store.getStoreName() %></p>
                    <p><strong>店舗住所：</strong><%= store.getAddress() %></p>
                    <p><strong>電話番号：</strong><%= store.getPhone() %></p>
                    <p><strong>メールアドレス：</strong><%= store.getEmail() %></p>
                </div>

                <div class="notice-box">
                    <h3>⚠ 審査について</h3>
                    <p>
                        営業許可書の審査には数日かかる場合があります。<br>
                        審査完了後、登録されたメールアドレスに通知をお送りいたします。<br>
                        審査が完了するまで、ログインはできません。
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/store_jsp/login_store.jsp" class="btn-login">
                    ログイン画面へ
                </a>
            </div>
        </div>
    </main>

    <!-- フッター読み込み -->
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>