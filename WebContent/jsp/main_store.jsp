<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>店舗メインメニュー - フードロス削減システム</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .main-content {
            max-width: 700px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }

        h2 {
            font-size: 1.8rem;
            color: #333;
            margin-bottom: 50px;
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
            padding: 15px 0;
            text-align: center;
            background-color: #ccc;
            border-radius: 8px;
            text-decoration: none;
            color: #333;
            font-weight: bold;
            transition: background 0.3s;
        }

        .button-group a:hover {
            background-color: #a65d36;
            transform: translateY(-3px);
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
                <h2>店舗メインメニュー</h2>
                <div class="button-group">
                    <a href="${pageContext.request.contextPath}/foodloss/StoreMerchandiseList.action">登録商品一覧</a>
					<a href="${pageContext.request.contextPath}/foodloss/StoreProductList.action">登録商品一覧</a>
					<a href="${pageContext.request.contextPath}/foodloss/StoreReservationList.action">予約リスト</a>
					<a href="${pageContext.request.contextPath}/foodloss/StoreEmployeeList.action">社員管理</a>
                </div>
            </div>
        </div>
    </main>

    <!-- ✅ 共通フッター -->
    <jsp:include page="footer.jsp" />
</div>

<!-- JS（共通） -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
