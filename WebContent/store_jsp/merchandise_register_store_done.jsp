<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="bean.Merchandise" %>
<%
    Merchandise merchandise = (Merchandise) session.getAttribute("registeredMerchandise");
    if (merchandise == null) {
        response.sendRedirect(request.getContextPath() + "/foodloss/Menu.action");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品登録完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
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

        .merchandise-info {
            background: #f9f9f9;
            padding: 1.5rem;
            border-radius: 5px;
            margin-bottom: 2rem;
            text-align: left;
        }

        .merchandise-info p {
            margin: 0.5rem 0;
            color: #555;
        }

        .merchandise-info strong {
            color: #333;
        }

        .btn-primary,
        .btn-secondary {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            text-decoration: none;
            display: inline-block;
            margin-bottom: 0.8rem;
        }

        .btn-primary {
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
        }

        .btn-primary:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-secondary {
            background: #fff;
            color: #c07148;
            border: 1px solid #c07148;
        }

        .btn-secondary:hover {
            background: #c07148;
            color: #fff;
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
    <%-- ヘッダー読み込み --%>
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="success-container">
                <h1>商品登録完了</h1>

                <p class="message">
                    商品登録が完了しました！
                </p>

                <div class="merchandise-info">
                    <p><strong>商品名：</strong><%= merchandise.getMerchandiseName() %></p>
                    <p><strong>価格(税込)：</strong><%= merchandise.getPrice() %>円</p>
                    <p><strong>在庫数：</strong><%= merchandise.getStock() %>個</p>
                    <p><strong>消費期限：</strong><%= merchandise.getUseByDate() %></p>
                </div>

                <a href="${pageContext.request.contextPath}/foodloss/MerchandiseRegister.action" class="btn-primary">
                    続けて商品を登録する
                </a>
                <a href="${pageContext.request.contextPath}/foodloss/MerchandiseList.action" class="btn-secondary">
                    登録商品一覧を見る
                </a>
                <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="btn-secondary">
                    ホームに戻る
                </a>
            </div>
        </div>
    </main>

    <%-- フッター読み込み --%>
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>