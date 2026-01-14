<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
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

    // ========== 予約リストを「未受取」と「受取済」に分ける ==========
    List<Booking> activeBookings = new ArrayList<>();    // 未受取
    List<Booking> completedBookings = new ArrayList<>(); // 受取済

    if (bookingList != null && !bookingList.isEmpty()) {
        for (Booking b : bookingList) {
            if (b.getPickupStatus()) {
                completedBookings.add(b);  // 受取済
            } else {
                activeBookings.add(b);      // 未受取
            }
        }
    }
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
            margin-bottom: 30px;
            font-size: 1rem;
        }

        /* セクション分け */
        .booking-section {
            margin-bottom: 50px;
        }

        .section-title {
            font-size: 1.4rem;
            font-weight: bold;
            color: #333;
            padding: 10px 15px;
            margin-bottom: 20px;
            border-left: 5px solid #c07148;
            background: #f9f9f9;
        }

        .section-title.active {
            border-left-color: #4a90e2;
            background: #f0f8ff;
            color: #4a90e2;
        }

        .section-title.completed {
            border-left-color: #999;
            background: #f5f5f5;
            color: #666;
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

        /* 受取済テーブルのスタイル */
        .completed-section th {
            background-color: #999;
            border-color: #999;
        }

        .completed-section td {
            color: #666;
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
            padding: 30px 20px;
            color: #999;
            font-size: 1rem;
            background: #f9f9f9;
            border-radius: 5px;
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

        /* 折りたたみ機能 */
        .toggle-btn {
            background: none;
            border: none;
            color: #666;
            cursor: pointer;
            font-size: 0.9rem;
            margin-left: 10px;
            text-decoration: underline;
        }

        .toggle-btn:hover {
            color: #c07148;
        }

        .collapsible-content {
            display: block;
        }

        .collapsible-content.hidden {
            display: none;
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

                <!-- ========== 未受取の予約 ========== -->
                <div class="booking-section">
                    <h3 class="section-title active">
                        📋 未受取の予約 (<%= activeBookings.size() %>件)
                    </h3>

                    <% if (!activeBookings.isEmpty()) { %>
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
                                    <th>予約取消</th>
                                </tr>
                            </thead>

                            <tbody>
                                <% for (Booking b : activeBookings) { %>
                                    <%
                                        Integer price = (Integer) request.getAttribute("price_" + b.getBookingId());
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
                                        <td>
                                            <a class="cancel-btn"
                                               href="${pageContext.request.contextPath}/foodloss/BookingCancel.action?bookingId=<%= b.getBookingId() %>">
                                                予約取消
                                            </a>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    <% } else { %>
                        <p class="no-data">未受取の予約はありません。</p>
                    <% } %>
                </div>

                <!-- ========== 受取済の予約 ========== -->
                <div class="booking-section">
                    <h3 class="section-title completed">
                        ✓ 受取済の予約 (<%= completedBookings.size() %>件)
                        <button class="toggle-btn" onclick="toggleCompleted()">
                            <span id="toggleText">非表示</span>
                        </button>
                    </h3>

                    <div id="completedContent" class="collapsible-content">
                        <% if (!completedBookings.isEmpty()) { %>
                            <table class="completed-section">
                                <thead>
                                    <tr>
                                        <th>予約ID</th>
                                        <th>店舗名</th>
                                        <th>商品名</th>
                                        <th>合計金額</th>
                                        <th>数量</th>
                                        <th>受取予定時刻</th>
                                        <th>予約日時</th>
                                        <th>状態</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <% for (Booking b : completedBookings) { %>
                                        <%
                                            Integer price = (Integer) request.getAttribute("price_" + b.getBookingId());
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
                                            <td>受取済</td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        <% } else { %>
                            <p class="no-data">受取済の予約はありません。</p>
                        <% } %>
                    </div>
                </div>

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
<script>
    // 受取済セクションの表示/非表示切り替え
    function toggleCompleted() {
        const content = document.getElementById('completedContent');
        const toggleText = document.getElementById('toggleText');

        if (content.classList.contains('hidden')) {
            content.classList.remove('hidden');
            toggleText.textContent = '非表示';
        } else {
            content.classList.add('hidden');
            toggleText.textContent = '表示';
        }
    }
</script>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>