<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String errorMessage = (String) request.getAttribute("error");
    String employeeNumber = (String) request.getAttribute("employeeNumber");
    String employeeName = (String) request.getAttribute("employeeName");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>社員登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #fff;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        #container {
            flex: 1;
            display: flex;
            flex-direction: column;
            width: 100%;
        }

        .column {
            flex: 1;
            width: 100%;
        }

        .main-contents {
            width: 100%;
            padding: 20px;
        }

        .register-container {
            max-width: 450px;
            width: 100%;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .register-container h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
        }

        .error-message {
            background-color: #fee;
            color: #c00;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c00;
            font-weight: bold;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #555;
        }

        .form-group label .required {
            color: red;
            margin-left: 0.3rem;
        }

        .form-group input[type="text"] {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
            box-sizing: border-box;
            transition: 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
        }

        .form-group input.error {
            border-color: #c00;
            background-color: #fff5f5;
        }

        .btn-register,
        .btn-back {
            width: 100%;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
        }

        .btn-register {
            padding: 1rem;
            font-size: 1.1rem;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            margin-bottom: 0.8rem;
            font-family: inherit;
        }

        .btn-register:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-back {
            padding: 0.6rem;
            font-size: 1rem;
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
            font-family: inherit;
        }

        .btn-back:hover {
            background: #c07148;
            color: #fff;
        }

        @media screen and (max-width: 600px) {
            .register-container {
                margin: 20px;
                padding: 1.5rem;
            }

            .register-container h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
<div id="container">
    <!-- ヘッダー -->
    <jsp:include page="/store_jsp/header_store.jsp" />

    <!-- メインコンテンツ -->
    <main class="column">
        <div class="main-contents">
            <div class="register-container">
                <h1>社員登録</h1>

                <!-- エラーメッセージ表示 -->
                <% if (errorMessage != null) { %>
                    <div class="error-message">
                        <%= errorMessage %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/foodloss/EmployeeRegister.action" method="post">
                    <div class="form-group">
                        <label for="employeeNumber">
                            社員番号
                            <span class="required">*</span>
                        </label>
                        <input type="text"
                               id="employeeNumber"
                               name="employeeNumber"
                               value="<%= employeeNumber != null ? employeeNumber : "" %>"
                               class="<%= (errorMessage != null && errorMessage.contains("社員番号")) ? "error" : "" %>"
                               required
                               placeholder="例: 001"
                               maxlength="20">
                    </div>

                    <div class="form-group">
                        <label for="employeeName">
                            社員名
                            <span class="required">*</span>
                        </label>
                        <input type="text"
                               id="employeeName"
                               name="employeeName"
                               value="<%= employeeName != null ? employeeName : "" %>"
                               class="<%= (errorMessage != null && errorMessage.contains("社員名")) ? "error" : "" %>"
                               required
                               placeholder="例: 山田太郎"
                               maxlength="50">
                    </div>

                    <button type="submit" class="btn-register">登録</button>
                </form>

                <!-- 戻るボタン（フォームの外） -->
                <button type="button" class="btn-back"
                        onclick="location.href='${pageContext.request.contextPath}/foodloss/EmployeeList.action'">
                    戻る
                </button>
            </div>
        </div>
    </main>

    <!-- フッター -->
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

</body>
</html>