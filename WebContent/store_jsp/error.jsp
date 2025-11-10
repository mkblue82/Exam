<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>エラーページ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }
        .error-container {
            max-width: 500px;
            margin: 100px auto;
            padding: 3rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }

        .error-container h1 {
            font-size: 1.8rem;
            color: #d32f2f;
            margin-bottom: 1rem;
        }
        .error-message {
            font-size: 1rem;
            color: #666;
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        .error-detail {
            font-size: 0.9rem;
            color: #999;
            margin-bottom: 2rem;
            padding: 1rem;
            background: #f9f9f9;
            border-left: 4px solid #d32f2f;
            text-align: left;
            border-radius: 4px;
            word-break: break-word;
        }
        .button-group {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
            flex-direction: row-reverse;
        }
        .btn {
            padding: 0.8rem 2rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            text-decoration: none;
            transition: 0.3s;
            font-weight: bold;
        }
        .btn-home {
            background-color: #c07148;
            color: #fff;
        }
        .btn-home:hover {
            background-color: #a85d38;
        }
        .btn-back {
            background-color: #ccc;
            color: #333;
        }
        .btn-back:hover {
            background-color: #bbb;
        }
        @media screen and (max-width: 600px) {
            .error-container {
                margin: 40px 20px;
                padding: 1.5rem;
            }
            .error-container h1 {
                font-size: 1.5rem;
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
    <!-- ヘッダー -->
    <jsp:include page="/jsp/header.jsp" />

    <!-- エラーコンテンツ -->
    <main class="column">
        <div class="error-container">

            <h1>エラーが発生しました。</h1>
            <p class="error-message">ご不便をおかけして申し訳ございません。</p>

            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isEmpty()) {
            %>
                <div class="error-detail">
                    <%= errorMessage %>
                </div>
            <%
                }
            %>

            <div class="button-group">
            <!-- 後でセッション確認してmain_user.jspかmain_store.jspに振り分けるかの処理にする -->
            <!-- とりあえず仮でmain_user.jspに飛ぶ -->
                <a href="${pageContext.request.contextPath}/jsp/main_user.jsp" class="btn btn-home">ホームへ戻る</a>
                <button onclick="history.back()" class="btn btn-back">前のページに戻る</button>
            </div>
        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
