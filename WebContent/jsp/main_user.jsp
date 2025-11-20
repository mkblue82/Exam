<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, bean.Store, bean.Merchandise" %>

<jsp:include page="/jsp/header_user.jsp" />

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>メニュー</title>
    <style>
        body { font-family: sans-serif; }
        .store-box {
            border: 1px solid #ccc;
            padding: 10px;
            margin-bottom: 20px;
            background: #f8f8f8;
        }
        .merch {
            margin-left: 20px;
            padding: 5px;
            border-left: 3px solid #0099CC;
        }
    </style>
</head>
<body>

<h2>出店店舗と商品一覧</h2>

<%
    Map<Store, List<Merchandise>> shopMerchMap =
        (Map<Store, List<Merchandise>>) request.getAttribute("shopMerchMap");
%>

<% if (shopMerchMap != null) { %>

    <% for (Map.Entry<Store, List<Merchandise>> entry : shopMerchMap.entrySet()) {
        Store store = entry.getKey();
        List<Merchandise> merchList = entry.getValue();
    %>

        <div class="store-box">
            <h3>店舗名：<%= store.getStoreName() %></h3>
            <p>店舗ID：<%= store.getStoreId() %></p>

            <% if (merchList != null && !merchList.isEmpty()) { %>

                <% for (Merchandise m : merchList) { %>
                    <div class="merch">
                        <strong>商品名：</strong> <%= m.getMerchandiseName() %><br>
                        <strong>値段：</strong> <%= m.getPrice() %> 円<br>
                        <strong>在庫：</strong> <%= m.getStock() %><br>
                        <strong>消費期限：</strong> <%= m.getUseByDate() %>
                    </div>
                <% } %>

            <% } else { %>
                <p>（この店舗の商品はありません）</p>
            <% } %>

        </div>

    <% } %>

<% } else { %>
    <p>商品情報が取得できませんでした。</p>
<% } %>

</body>
</html>

<jsp:include page="/jsp/footer.jsp" />
