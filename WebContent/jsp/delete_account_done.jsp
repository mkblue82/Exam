<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>アカウント削除完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .delete-account-complete-container {
            max-width: 450px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
            margin-top: 150px;
        }

        .delete-account-complete-container h1 {
            color: #c07148;
            margin-bottom: 1.5rem;
            font-size: 1.8rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            text-align: center;
        }

        .complete-message {
            color: #555;
            font-size: 1rem;
            margin-bottom: 2rem;
            line-height: 1.6;
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
        }

        .btn-login:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }
    </style>
</head>
<body>
<div id="container">

    <!-- アカウント削除完了コンテンツ -->
    <main class="column">
        <div class="main-contents">
            <div class="delete-account-complete-container">
                <h1>削除完了</h1>
                <p class="complete-message">アカウント削除が完了しました。<br>ご利用ありがとうございました。</p>
                <form method="GET" action="${pageContext.request.contextPath}/foodloss/Top.action">
                    <button type="submit" class="btn-login">TOP画面へ</button>
                </form>
            </div>
        </div>
    </main>

    <!-- フッター読み込み -->
    <jsp:include page="footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="../js/slick.js"></script>
<script src="../js/main.js"></script>
</body>
</html>
