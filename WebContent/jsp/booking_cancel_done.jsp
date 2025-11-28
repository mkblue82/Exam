<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Booking" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    Integer bookingId = (Integer) request.getAttribute("bookingId");
    Booking booking = (Booking) request.getAttribute("booking");
    Integer price = (Integer) request.getAttribute("price");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    // 合計金額を計算
    int total = (price != null && booking != null) ? price * booking.getCount() : 0;
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>予約取消完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .store-detail-container {
            max-width: 900px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .store-detail-container h2 {
            color: #c07148;
            text-align: center;
            font-size: 1.8rem;
            margin-bottom: 2rem;
        }

        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 30px;
            border: 1px solid #c3e6cb;
            text-align: center;
            font-size: 1.1rem;
            font-weight: bold;
        }

        .detail-section {
            margin-bottom: 2rem;
        }

        .detail-section h3 {
            font-size: 1.3rem;
            color: #333;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid #e0e0e0;
        }

        .detail-row {
            display: flex;
            padding: 1rem 0 1rem 50px;
            border-bottom: 1px solid #f0f0f0;
        }

        .detail-row:last-child {
            border-bottom: none;
        }

        .detail-label {
            width: 200px;
            font-weight: bold;
            color: #666;
            flex-shrink: 0;
        }

        .detail-value {
            flex: 1;
            color: #333;
        }


        .button-container {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 40px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s, transform 0.1s;
            font-weight: bold;
            text-decoration: none;
            display: inline-block;
        }

        .btn:active {
            transform: scale(0.98);
        }

        .btn-primary {
            background-color: #c07148;
            color: white;
        }

        .btn-primary:hover {
            background-color: #a85d38;
            transform: translateY(-2px);
            color: white;
        }

        .error-message {
            text-align: center;
            color: #dc3545;
            padding: 20px;
            font-size: 1.1rem;
        }

        @media screen and (max-width: 600px) {
            .store-detail-container {
                margin: 20px;
                padding: 1.5rem;
            }

            .detail-row {
                flex-direction: column;
                padding-left: 0;
            }

            .detail-label {
                width: 100%;
                margin-bottom: 0.5rem;
            }

            .button-container {
                flex-direction: column;
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
            <div class="store-detail-container">

                <h2>予約取消完了</h2>

                <div class="success-message">
                    予約を取り消しました
                </div>

                <% if (booking != null) { %>
                    <div class="detail-section">
                        <h3>取り消した予約情報</h3>

                        <div class="detail-row">
                            <div class="detail-label">予約ID</div>
                            <div class="detail-value"><%= bookingId != null ? bookingId : booking.getBookingId() %></div>
                        </div>

                        <div class="detail-row">
                            <div class="detail-label">商品名</div>
                            <div class="detail-value"><%= booking.getMerchandiseName() != null ? booking.getMerchandiseName() : "−" %></div>
                        </div>

                        <div class="detail-row">
                            <div class="detail-label">合計金額</div>
                            <div class="detail-value price-value">
                                <%= total > 0 ? "¥" + String.format("%,d", total) : "−" %>
                            </div>
                        </div>

                        <div class="detail-row">
                            <div class="detail-label">数量</div>
                            <div class="detail-value"><%= booking.getCount() %>個</div>
                        </div>

                        <div class="detail-row">
                            <div class="detail-label">受取予定時刻</div>
                            <div class="detail-value"><%= booking.getPickupTime() != null ? sdf.format(booking.getPickupTime()) : "−" %></div>
                        </div>

                        <div class="detail-row">
                            <div class="detail-label">予約日時</div>
                            <div class="detail-value"><%= booking.getBookingTime() != null ? sdf.format(booking.getBookingTime()) : "−" %></div>
                        </div>
                    </div>

                    <div class="button-container">
                        <a href="${pageContext.request.contextPath}/foodloss/BookingUserList.action"
                           class="btn btn-primary">
                            予約リストに戻る
                        </a>
                    </div>
                <% } else { %>
                    <p class="error-message">予約情報が取得できませんでした</p>
                    <div class="button-container">
                        <a href="${pageContext.request.contextPath}/foodloss/BookingUserList.action"
                           class="btn btn-primary">
                            予約リストに戻る
                        </a>
                    </div>
                <% } %>

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
