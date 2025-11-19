<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Merchandise" %>

<%
    // サーブレットから受け取った商品一覧
    List<Merchandise> products = (List<Merchandise>) request.getAttribute("merchandiseList");

    // セッション情報
    Integer storeId = (session.getAttribute("storeId") != null)
                        ? (Integer) session.getAttribute("storeId")
                        : null;

    String storeName = (session.getAttribute("storeName") != null)
                        ? (String) session.getAttribute("storeName")
                        : null;
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>商品一覧 - フードロス削減システム</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .main-content {
            max-width: 1000px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        h2 {
            font-size: 1.8rem;
            text-align: center;
            color: #333;
            margin-bottom: 30px;
        }
        .store-info {
            text-align: center;
            font-weight: bold;
            color: #a65d36;
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th {
            background-color: #a65d36;
            color: #fff;
            padding: 12px;
        }
        td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .btn {
            display: inline-block;
            padding: 8px 25px;
            background-color: #c07148;
            color: white;
            border-radius: 8px;
            text-decoration: none;
            transition: 0.3s;
        }
        .btn:hover {
            background-color: #c77c4a;
        }
        .back-button {
            margin-top: 40px;
            text-align: center;
        }
        .back-button a {
            display: inline-block;
            padding: 12px 40px;
            background-color: #ccc;
            text-decoration: none;
            border-radius: 8px;
            font-weight: bold;
            color: #333;
            margin: 0 10px;
        }
        .back-button a:hover {
            background-color: #a65d36;
            color: #fff;
        }
    </style>
</head>

<body>

<div id="container">

    <!-- ▼ 共通ヘッダー -->
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="main-content">

                <h2>登録商品一覧</h2>

                <!-- ▼ ログイン店舗の表示 -->
                <div class="store-info">
                    <% if (storeName != null) { %>
                        表示中の店舗：<%= storeName %>（ID：<%= storeId %>）
                    <% } else { %>
                        店舗ID：<%= storeId != null ? storeId : "不明" %>
                    <% } %>
                </div>

                <!-- ▼ 商品一覧 -->
                <% if (products != null && !products.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>商品名</th>
                                <th>価格</th>
                                <th>在庫</th>
                                <th>消費期限</th>
                                <th>登録日時</th>
                                <th>編集</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Merchandise m : products) { %>
                                <tr>
                                    <td><%= m.getMerchandiseId() %></td>
                                    <td><%= m.getMerchandiseName() %></td>
                                    <td>￥<%= m.getPrice() %></td>
                                    <td><%= m.getStock() %></td>
                                    <td><%= m.getUseByDate() %></td>
                                    <td><%= m.getRegistrationTime() %></td>
                                    <td>
                                        <a class="btn"
										   href="${pageContext.request.contextPath}/foodloss/MerchandiseEdit.action?id=<%= m.getMerchandiseId() %>">
										   編集
										</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>

                <% } else { %>
                    <p style="text-align:center; padding:20px; color:#999;">
                        登録された商品はありません。
                    </p>
                <% } %>
<!-- ん」 --> -->>
               <!-- ▼ 戻る・商品登録・割引設定ボタン -->
					<div class="back-button">
					    <a href="${pageContext.request.contextPath}/store_jsp/main_store.jsp">メインメニューへ戻る</a>
					    <a href="${pageContext.request.contextPath}/store_jsp/merchandise_register_store.jsp">商品登録</a>
					    <a href="${pageContext.request.contextPath}/store_jsp/discount_setting.jsp">割引設定</a>
					</div>

            </div>
        </div>
    </main>

    <!-- ▼ 共通フッター -->
    <jsp:include page="/jsp/footer.jsp" />

</div>

<!-- 共通JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>
