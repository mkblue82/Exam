<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Booking" %>
<%@ page import="bean.User" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");
    String message = (String) request.getAttribute("message");

    User user = (session.getAttribute("user") != null)
                ? (User) session.getAttribute("user")
                : null;

    Integer userId = (user != null) ? user.getUserId() : null;
    String userName = (user != null) ? user.getName() : null;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>予約一覧</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .main-content {
            max-width: 1200px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        h2 {
            font-size: 1.8rem;
            text-align: center;
            color: #c07148;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            margin-bottom: 2rem;
        }

        .store-info {
            text-align: center;
            font-weight: bold;
            color: #c07148;
            margin-bottom: 20px;
            font-size: 1rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th {
            background-color: #c07148;
            color: #fff;
            padding: 12px;
            font-weight: bold;
            border: 1px solid #c07148;
        }

        td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
            color: #333;
        }

        tbody tr:hover {
            background-color: #f5f5f5;
        }


        .cancel-btn {
            display: inline-block;
            padding: 8px 25px;
            background-color: #999;
            color: white;
            border-radius: 5px;
            text-decoration: none;
            transition: 0.3s;
            font-weight: bold;
            border: none;
            cursor: pointer;
        }

        .cancel-btn:hover {
            background-color: #dc3545;
            color: white;
            transform: translateY(-2px);
        }

        .no-data {
            text-align: center;
            padding: 40px 20px;
            color: #999;
            font-size: 1rem;
        }

        .back-button {
            margin-top: 30px;
            text-align: center;
        }

        .back-button a {
            display: inline-block;
            padding: 12px 40px;
            background-color: #ccc;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            color: #333;
            transition: all 0.3s;
        }

        .back-button a:hover {
            background-color: #c07148;
            color: #fff;
            transform: translateY(-3px);
        }

        @media screen and (max-width: 1200px) {
            .main-content {
                margin: 20px;
                padding: 1.5rem;
            }

            table {
                font-size: 0.9rem;
            }

            th, td {
                padding: 8px;
            }
        }
    </style>
</head>

<body>
<div id="container">

    <!-- 共通ヘッダー -->
    <jsp:include page="/jsp/header_user.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="main-content">

                <h2>予約一覧</h2>

                <div class="store-info">
                    <% if (userName != null) { %>
                        <%= userName %>さんの予約（ユーザーID：<%= userId %>）
                    <% } else { %>
                        ユーザーID：<%= userId != null ? userId : "不明" %>
                    <% } %>
                </div>

                <% if (bookingList != null && !bookingList.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>予約ID</th>
                                <th>店舗名</th>
                                <th>商品名</th>
                                <th>合計金額</th>
                                <th>数量</th>
                                <th>受取予定時刻</th>
                                <th>予約日時</th>
                                <th>受取状態</th>
                                <th>予約取消</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% for (Booking b : bookingList) { %>
                                <%
                                    // 価格情報を取得
                                    Integer price = (Integer) request.getAttribute("price_" + b.getBookingId());
                                    // 合計金額を計算（価格×数量）
                                    int total = (price != null) ? price * b.getCount() : 0;
                                %>
                                <tr>
                                    <td><%= b.getBookingId() %></td>
                                    <td><%= request.getAttribute("store_" + b.getBookingId()) != null ? request.getAttribute("store_" + b.getBookingId()) : "−" %></td>
                                    <td><%= b.getMerchandiseName() != null ? b.getMerchandiseName() : "−" %></td>
                                    <td class="price-cell">
                                        <%= total > 0 ? "¥" + String.format("%,d", total) : "−" %>
                                    </td>
                                    <td><%= b.getCount() %></td>
                                    <td><%= b.getPickupTime() != null ? sdf.format(b.getPickupTime()) : "−" %></td>
                                    <td><%= b.getBookingTime() != null ? sdf.format(b.getBookingTime()) : "−" %></td>
                                    <td><%= b.getPickupStatus() ? "受取済" : "未受取" %></td>
                                    <td>
                                        <% if (!b.getPickupStatus()) { %>
                                            <a class="cancel-btn"
                                               href="${pageContext.request.contextPath}/foodloss/BookingCancel.action?bookingId=<%= b.getBookingId() %>">
                                                予約取消
                                            </a>
                                        <% } else { %>
                                            −
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>

                <% } else { %>
                    <p class="no-data">
                        現在、予約は登録されていません。
                    </p>
                <% } %>

                <div class="back-button">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホームに戻る</a>
                </div>

            </div>
        </div>
    </main>

    <!-- 共通フッター -->
    <jsp:include page="/jsp/footer.jsp" />

</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>
