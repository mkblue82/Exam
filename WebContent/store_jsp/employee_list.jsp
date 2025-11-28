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
    <title>社員一覧</title>
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

        .btn {
            display: inline-block;
            padding: 8px 25px;
            background-color: #c07148;
            color: white;
            border-radius: 5px;
            text-decoration: none;
            transition: 0.3s;
            font-weight: bold;
            border: none;
            cursor: pointer;
        }

        .btn:hover {
            background-color: #a85d38;
            transform: translateY(-2px);
        }

        .btn-delete {
            background-color: #dc3545;
        }

        .btn-delete:hover {
            background-color: #c82333;
        }

        .no-data {
            text-align: center;
            padding: 40px 20px;
            color: #999;
            font-size: 1rem;
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
            transition: all 0.3s;
        }

        .back-button a:hover {
            background-color: #c07148;
            color: #fff;
            transform: translateY(-3px);
        }

        /* 削除確認ダイアログのスタイル */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.active {
            display: flex;
        }

        .modal-content {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
            max-width: 500px;
            width: 90%;
            text-align: center;
        }

        .modal-content h3 {
            color: #dc3545;
            margin-bottom: 20px;
            font-size: 1.5rem;
        }

        .modal-content p {
            margin-bottom: 30px;
            color: #333;
            font-size: 1rem;
            line-height: 1.6;
        }

        .modal-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
        }

        .modal-buttons button {
            padding: 10px 30px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            transition: all 0.3s;
        }

        .btn-confirm {
            background-color: #dc3545;
            color: white;
        }

        .btn-confirm:hover {
            background-color: #c82333;
        }

        .btn-cancel {
            background-color: #6c757d;
            color: white;
        }

        .btn-cancel:hover {
            background-color: #5a6268;
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

            .btn {
                padding: 6px 15px;
                font-size: 0.85rem;
            }

            .back-button a {
                display: block;
                margin: 10px auto;
                max-width: 300px;
            }

            .modal-content {
                padding: 20px;
            }

        }

        /* 商品一覧と同じ定義に統一 */
		.btn.table-button {
		    width: 100px;
		    padding: 8px 0;
		    text-align: center;
		    box-sizing: border-box;
		}

		.btn-delete {
		    background-color: #d9534f;  /* 商品一覧と同じ */
		}

		.btn-delete:hover {
		    background-color: #c9302c;  /* 商品一覧と同じ */
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
                                <th>社員番号</th>
                                <th>氏名</th>
                                <th>編集</th>
                                <th>削除</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Employee emp : employeeList) { %>
                                <tr>
                                    <td><%= emp.getId() %></td>
                                    <td><%= emp.getEmployeeCode() %></td>
                                    <td><%= emp.getEmployeeName() %></td>
                                    <td>
									  <a class="btn table-button"
									     href="${pageContext.request.contextPath}/foodloss/EmployeeEdit.action?id=<%= emp.getId() %>">
									     編集
									  </a>
									</td>
									<td>
									  <a class="btn btn-delete table-button"
									     href="#"
									     onclick="showDeleteConfirm(<%= emp.getId() %>, '<%= emp.getEmployeeName() %>'); return false;">
									     削除
									  </a>
									</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">
                        社員情報が登録されていません。
                    </p>
                <% } %>

                <div class="back-button">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホームに戻る</a>
                    <a href="${pageContext.request.contextPath}/foodloss/EmployeeRegister.action">社員登録</a>
                </div>

            </div>
        </div>
    </main>

    <!-- 共通フッター -->
    <jsp:include page="/jsp/footer.jsp" />

</div>

<!-- 削除確認モーダル -->
<div id="deleteModal" class="modal-overlay">
    <div class="modal-content">
        <h3>社員の削除確認</h3>
        <p id="deleteMessage"></p>
        <p style="color: #999; font-size: 0.9rem;">この操作は取り消せません。</p>
        <div class="modal-buttons">
            <button class="btn-cancel" onclick="hideDeleteConfirm()">キャンセル</button>
            <button class="btn-confirm" onclick="deleteEmployee()">削除する</button>
        </div>
    </div>
</div>

<!-- 削除用のフォーム（非表示） -->
<form id="deleteForm" method="post" action="${pageContext.request.contextPath}/foodloss/EmployeeDelete.action" style="display: none;">
    <input type="hidden" id="deleteEmployeeId" name="id">
</form>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
    let deleteEmployeeIdValue = null;

    function showDeleteConfirm(employeeId, employeeName) {
        deleteEmployeeIdValue = employeeId;
        document.getElementById('deleteMessage').innerHTML =
            '<strong>' + employeeName + '</strong> さんを削除してもよろしいですか?';
        document.getElementById('deleteModal').classList.add('active');
    }

    function hideDeleteConfirm() {
        deleteEmployeeIdValue = null;
        document.getElementById('deleteModal').classList.remove('active');
    }

    function deleteEmployee() {
        if (deleteEmployeeIdValue !== null) {
            document.getElementById('deleteEmployeeId').value = deleteEmployeeIdValue;
            document.getElementById('deleteForm').submit();
        }
    }

    // モーダル外をクリックした時に閉じる
    document.getElementById('deleteModal').addEventListener('click', function(e) {
        if (e.target === this) {
            hideDeleteConfirm();
        }
    });
</script>

</body>
</html>