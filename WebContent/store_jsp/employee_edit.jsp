<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="bean.Employee" %>
<%
    Employee employee = (Employee) request.getAttribute("employee");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>社員情報編集 - フードロス削減システム</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif;
            background: #f5f5f5;
            min-height: 100vh;
            padding: 20px;
        }

        .edit-wrapper {
            width: 100%;
            max-width: 440px;
            margin: 3rem auto;
        }

        .edit-container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 3rem 2.5rem;
        }

        .edit-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .edit-header h1 {
            color: #c77c4a;
            font-size: 2rem;
            font-weight: 700;
            margin: 0 0 1rem 0;
        }

        .edit-header::after {
            content: '';
            display: block;
            width: 70%;
            height: 3px;
            background: #c77c4a;
            margin: 0 auto;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
            font-size: 1rem;
        }

        .form-group input[type="text"] {
            width: 100%;
            padding: 0.8rem;
            border: 2px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
            background: #fff;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c77c4a;
        }

        .btn-update {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            background: #c77c4a;
            color: #fff;
        }

        .btn-update:hover {
            background: #b56c3a;
        }

        .back-link {
            text-align: center;
            margin-top: 1.5rem;
        }

        .back-link a {
            color: #666;
            text-decoration: none;
            font-size: 0.9rem;
        }

        .back-link a:hover {
            color: #c77c4a;
        }
    </style>
</head>
<body>

<!-- ✅ 共通ヘッダー -->
<jsp:include page="/store_jsp/header_store.jsp" />

<main class="column">
    <div class="edit-wrapper">
        <div class="edit-container">
            <div class="edit-header">
                <h1>社員情報編集</h1>
            </div>

            <form action="${pageContext.request.contextPath}/foodloss/EmployeeUpdate.action" method="post">
                <input type="hidden" name="id" value="<%= employee.getId() %>">

                <div class="form-group">
                    <label for="employeeCode">社員コード</label>
                    <input type="text" id="employeeCode" name="employeeCode" value="<%= employee.getEmployeeCode() %>" required>
                </div>

                <div class="form-group">
                    <label for="employeeName">氏名</label>
                    <input type="text" id="employeeName" name="employeeName" value="<%= employee.getEmployeeName() %>" required>
                </div>

                <button type="submit" class="btn-update">更新</button>
            </form>

            <div class="back-link">
                <a href="${pageContext.request.contextPath}/foodloss/EmployeeList.action">← 戻る</a>
            </div>
        </div>
    </div>
</main>

<!-- ✅ 共通フッター -->
<jsp:include page="/jsp/footer.jsp" />

</body>
</html>