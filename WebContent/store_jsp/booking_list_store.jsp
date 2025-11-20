<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Booking" %>

<%
    List<Booking> bookingList = (List<Booking>) request.getAttribute("bookingList");

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
    <title>予約一覧</title>
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

        .pickup-btn {
            display: inline-block;
            padding: 8px 25px;
            background-color: #c07148;
            color: white;
            border-radius: 5px;
            text-decoration: none;
            transition: 0.3s;
            font-weight: bold;
        }

        .pickup-btn:hover {
            background-color: #a85d38;
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

        @media screen and (max-width: 1000px) {
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
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="main-content">

                <h2>予約一覧</h2>

                <div class="store-info">
                    <% if (storeName != null) { %>
                        表示中の店舗：<%= storeName %>（ID：<%= storeId %>）
                    <% } else { %>
                        店舗ID：<%= storeId != null ? storeId : "不明" %>
                    <% } %>
                </div>

                <% if (bookingList != null && !bookingList.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>予約ID</th>
                                <th>商品名</th>
                                <th>予約ユーザーID</th>
                                <th>数量</th>
                                <th>受取予定時刻</th>
                                <th>予約日時</th>
                                <th>受取状態</th>
                                <th>受け取り</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% for (Booking b : bookingList) { %>
                                <tr>
                                    <td><%= b.getBookingId() %></td>
                                    <td><%= b.getMerchandiseName() %></td>
                                    <td><%= b.getUserId() %></td>
                                    <td><%= b.getCount() %></td>
                                    <td><%= b.getPickupTime() %></td>
                                    <td><%= b.getBookingTime() %></td>
                                    <td><%= b.getPickupStatus() ? "受取済" : "未受取" %></td>
                                    <td>
                                        <% if (!b.getPickupStatus()) { %>
                                            <a class="pickup-btn"
                                               href="${pageContext.request.contextPath}/foodloss/PickupBooking.action?bookingId=<%= b.getBookingId() %>">
                                                受け取り
                                            </a>
                                        <% } else { %>
                                            ー
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