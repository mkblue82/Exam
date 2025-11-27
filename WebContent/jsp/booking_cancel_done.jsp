<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Booking" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    Integer bookingId = (Integer) request.getAttribute("bookingId");
    Booking booking = (Booking) request.getAttribute("booking");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>予約取消完了</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .main-content {
            max-width: 800px;
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

        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 30px;
            border: 1px solid #c3e6cb;
            text-align: center;
            font-size: 1.2rem;
            font-weight: bold;
        }


        .booking-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 30px;
        }

        .booking-info h3 {
            color: #c07148;
            margin-bottom: 15px;
            font-size: 1.2rem;
        }

        .booking-info table {
            width: 100%;
            border-collapse: collapse;
        }

        .booking-info th {
            background-color: #c07148;
            color: white;
            padding: 12px;
            text-align: left;
            width: 40%;
        }

        .booking-info td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
        }

        .button-group {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 30px;
        }

        .btn {
            display: inline-block;
            padding: 12px 40px;
            border-radius: 5px;
            font-weight: bold;
            text-decoration: none;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 1rem;
        }

        .btn-primary {
            background-color: #c07148;
            color: white;
        }

        .btn-primary:hover {
            background-color: #a85d38;
            transform: translateY(-2px);
        }

        @media screen and (max-width: 800px) {
            .main-content {
                margin: 20px;
                padding: 1.5rem;
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

    <!-- 共通ヘッダー -->
    <jsp:include page="/jsp/header_user.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="main-content">

                <h2>予約取消完了</h2>


                <div class="success-message">
                    予約を取り消しました
                </div>

                <% if (booking != null) { %>
                    <div class="booking-info">
                        <h3>取り消した予約情報</h3>
                        <table>
                            <tr>
                                <th>予約ID</th>
                                <td><%= bookingId != null ? bookingId : booking.getBookingId() %></td>
                            </tr>
                            <tr>
                                <th>商品名</th>
                                <td><%= booking.getMerchandiseName() != null ? booking.getMerchandiseName() : "−" %></td>
                            </tr>
                            <tr>
                                <th>数量</th>
                                <td><%= booking.getCount() %>個</td>
                            </tr>
                            <tr>
                                <th>受取予定時刻</th>
                                <td><%= booking.getPickupTime() != null ? sdf.format(booking.getPickupTime()) : "−" %></td>
                            </tr>
                        </table>
                    </div>
                <% } %>

                <div class="button-group">
                    <a href="${pageContext.request.contextPath}/foodloss/BookingUserList.action"
                       class="btn btn-primary">
                        予約リストに戻る
                    </a>
                </div>

            </div>
        </div>
    </main>

    <!-- 共通フッター -->
    <jsp:include page="/jsp/footer.jsp" />

</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>