<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="bean.Store" %>
<%@ page import="java.sql.Time" %>
<%
    Store store = (Store) session.getAttribute("store");

    // 割引設定情報を取得
    Time discountStartTime = store.getDiscountTime();
    Integer discountRate = store.getDiscountRate();

    // 時間を表示用に変換（例: 18:00:00 → 18時）
    String displayTime = "";
    if (discountStartTime != null) {
        displayTime = String.valueOf(discountStartTime.toLocalTime().getHour()) + "時";
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>割引設定完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: #fff;
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

        .discount-info {
            background: #f9f9f9;
            padding: 1.5rem;
            border-radius: 5px;
            margin-bottom: 2rem;
            text-align: left;
        }

        .discount-info p {
            margin: 0.5rem 0;
            color: #555;
        }

        .discount-info strong {
            color: #333;
        }

        .btn-back {
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

        .btn-back:hover {
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
    <!-- ヘッダー -->
    <jsp:include page="header_store.jsp" />

    <!-- 割引設定完了 -->
    <main class="column">
        <div class="main-contents">
            <div class="success-container">
                <h1>割引設定完了</h1>

                <p class="message">
                    割引設定が完了しました！<br>
                    以下の内容で設定されています。
                </p>

                <div class="discount-info">
                    <p><strong>開始時間：</strong><%= displayTime %></p>
                    <p><strong>割引率：</strong><%= discountRate %>%OFF</p>
                </div>

                <a href="${pageContext.request.contextPath}/foodloss/MerchandiseList.action" class="btn-back">
                    商品一覧を確認
                </a>
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
