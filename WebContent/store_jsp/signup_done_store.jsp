<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="bean.Application" %>
<%
    Application app = (Application) session.getAttribute("pendingApplication");
    if (app == null) {
        // 申請情報がない場合はメッセージを表示して申請画面へ
        request.setAttribute("errorMessage", "申請情報が見つかりません。もう一度申請してください。");
        request.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>申請完了 - WASSHOI</title>
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
            max-width: 550px;
            width: 100%;
            margin: 60px auto;
            padding: 2.5rem;
            background: #fff;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
            text-align: center;
        }

        .success-icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #4CAF50, #45a049);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1.5rem;
            animation: scaleIn 0.5s ease-out;
            box-shadow: 0 5px 15px rgba(76, 175, 80, 0.3);
        }

        .success-icon::after {
            content: "✓";
            color: white;
            font-size: 50px;
            font-weight: bold;
        }

        @keyframes scaleIn {
            from {
                transform: scale(0);
                opacity: 0;
            }
            to {
                transform: scale(1);
                opacity: 1;
            }
        }

        .success-container h1 {
            color: #c07148;
            margin-bottom: 1rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
        }

        .message {
            color: #555;
            margin-bottom: 2rem;
            line-height: 1.8;
            font-size: 1.1rem;
        }

        .store-info {
            background: #f9f9f9;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            text-align: left;
            border: 1px solid #e0e0e0;
        }

        .store-info h3 {
            color: #c07148;
            font-size: 1.1rem;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
        }

        .store-info h3::before {
            content: "🏪";
            margin-right: 8px;
            font-size: 1.3rem;
        }

        .store-info p {
            margin: 0.7rem 0;
            color: #555;
            font-size: 0.95rem;
            padding-left: 1rem;
        }

        .store-info strong {
            color: #333;
            display: inline-block;
            min-width: 120px;
        }

        .notice-box {
            background: #fff3cd;
            border: 2px solid #ffc107;
            border-radius: 8px;
            padding: 1.2rem;
            margin-bottom: 2rem;
            text-align: left;
        }

        .notice-box h3 {
            color: #856404;
            font-size: 1.1rem;
            margin-bottom: 0.8rem;
            display: flex;
            align-items: center;
        }

        .notice-box h3::before {
            content: "⚠️";
            margin-right: 8px;
        }

        .notice-box p {
            color: #856404;
            font-size: 0.95rem;
            line-height: 1.7;
            margin: 0;
        }

        .steps-box {
            background: #e3f2fd;
            border: 2px solid #2196F3;
            border-radius: 8px;
            padding: 1.2rem;
            margin-bottom: 2rem;
            text-align: left;
        }

        .steps-box h3 {
            color: #1976D2;
            font-size: 1.1rem;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
        }

        .steps-box h3::before {
            content: "📋";
            margin-right: 8px;
        }

        .steps-list {
            list-style: none;
            counter-reset: step-counter;
            padding-left: 0;
        }

        .steps-list li {
            counter-increment: step-counter;
            position: relative;
            padding-left: 45px;
            margin-bottom: 1rem;
            color: #1565C0;
            line-height: 1.6;
        }

        .steps-list li:last-child {
            margin-bottom: 0;
        }

        .steps-list li::before {
            content: counter(step-counter);
            position: absolute;
            left: 0;
            top: 0;
            width: 30px;
            height: 30px;
            background: #2196F3;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 14px;
        }

        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 0.9rem 2rem;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: bold;
            text-decoration: none;
            display: inline-block;
            min-width: 180px;
        }

        .btn-primary {
            background: #c07148;
            color: #fff;
            box-shadow: 0 4px 12px rgba(192, 113, 72, 0.3);
        }

        .btn-primary:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 6px 18px rgba(192, 113, 72, 0.4);
        }

        .btn-secondary {
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
        }

        .btn-secondary:hover {
            background: #f9f9f9;
            transform: translateY(-2px);
        }

        @media screen and (max-width: 600px) {
            .success-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }

            .success-container h1 {
                font-size: 1.6rem;
            }

            .store-info strong {
                min-width: auto;
                display: block;
                margin-bottom: 0.2rem;
            }

            .button-group {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div id="container">
    <main class="column">
        <div class="main-contents">
            <div class="success-container">
                <div class="success-icon"></div>

                <h1>✨ 申請完了！</h1>

                <p class="message">
                    店舗登録の申請が正常に完了しました。<br>
                    ご登録ありがとうございます！
                </p>

                <div class="store-info">
                    <h3>申請内容</h3>
                    <p><strong>店舗名：</strong><%= app.getStoreName() %></p>
                    <p><strong>店舗住所：</strong><%= app.getStoreAddress() %></p>
                    <p><strong>電話番号：</strong><%= app.getStorePhone() %></p>
                    <p><strong>メールアドレス：</strong><%= app.getStoreEmail() %></p>
                </div>

                <div class="steps-box">
                    <h3>今後の流れ</h3>
                    <ol class="steps-list">
                        <li>管理者が営業許可書などの申請内容を確認します</li>
                        <li>審査完了後、登録メールアドレスに承認通知が届きます</li>
                        <li>承認後、ログインして商品登録が可能になります</li>
                    </ol>
                </div>

                <div class="notice-box">
                    <h3>審査について</h3>
                    <p>
                        営業許可書の審査には通常1〜3営業日かかります。<br>
                        審査完了まで、ログインすることはできませんのでご了承ください。<br>
                        承認メールが届くまでお待ちください。
                    </p>
                </div>

                <div class="button-group">
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">
                        トップページへ戻る
                    </a>
                    <a href="${pageContext.request.contextPath}/store_jsp/login_store.jsp" class="btn btn-secondary">
                        ログイン画面へ
                    </a>
                </div>
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