<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>マイページ - フードロス削減システム</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .main-content {
            max-width: 600px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .point-section h2 {
            font-size: 1.6rem;
            color: #333;
            margin-bottom: 10px;
            text-align: center;
        }

        .point-section p {
            font-size: 2.4rem;
            font-weight: bold;
            color: #c07148;
            margin-bottom: 60px;
            text-align: center;
        }

        .button-group {
            display: flex;
            justify-content: center;
            gap: 40px;
            flex-wrap: wrap;
            margin-top: 40px;
        }

        .button-group a {
            display: block;
            width: 220px;
            padding: 15px;
            text-align: center;
            background-color: #ccc;
            border-radius: 8px;
            text-decoration: none;
            color: #333;
            font-weight: bold;
            transition: background 0.3s;
        }

        .button-group a:hover {
            background-color: #c07148;
            color: #fff;
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ✅ 共通ヘッダー -->
    <jsp:include page="header.jsp" />

    <!-- ✅ メインエリア -->
    <main class="column">
        <div class="main-contents">
            <div class="main-content">
                <div class="point-section">
                    <h2>所有ポイント</h2>
                    <p>${user.point} P</p>
                </div>
                <div class="button-group">
                    <a href="${pageContext.request.contextPath}/foodloss/DeleteAccount.action">アカウント削除</a>
                    <a href="${pageContext.request.contextPath}/jsp/edit_info.jsp">情報変更</a>
                    <a href="${pageContext.request.contextPath}/foodloss/Logout.action">ログアウト</a>
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
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
