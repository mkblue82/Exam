<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Employee" %>

<%
    List<Employee> employeeList = (List<Employee>) request.getAttribute("employeeList");

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
    <title>社員一覧 - フードロス削減システム</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 0;
        }
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

    <!-- 共通ヘッダー -->
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="main-content">

                <h2>社員一覧</h2>

                <div class="store-info">
                    <% if (storeName != null) { %>
                        表示中の店舗：<%= storeName %>（ID：<%= storeId %>）
                    <% } else { %>
                        店舗ID：<%= storeId != null ? storeId : "不明" %>
                    <% } %>
                </div>

                <% if (employeeList != null && !employeeList.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>社員コード</th>
                                <th>氏名</th>
                                <th>編集</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Employee emp : employeeList) { %>
                                <tr>
                                    <td><%= emp.getId() %></td>
                                    <td><%= emp.getEmployeeCode() %></td>
                                    <td><%= emp.getEmployeeName() %></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/foodloss/EmployeeEdit.action?id=<%= emp.getId() %>">編集</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p style="text-align:center; padding:20px; color:#999;">
                        社員情報が登録されていません。
                    </p>
                <% } %>

                <div class="back-button">
                    <a href="${pageContext.request.contextPath}/store_jsp/main_store.jsp">メインメニューへ戻る</a>
                    <a href="${pageContext.request.contextPath}/foodloss/EmployeeRegister.action">社員登録</a>
                </div>

            </div>
        </div>
    </main>

    <!-- 共通フッター -->
    <jsp:include page="/jsp/footer.jsp" />

</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
