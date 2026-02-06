<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品登録</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .register-container {
            max-width: 600px;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
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
            background: #ffebee;
            color: #c62828;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #c62828;
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
        .form-group label span { color: red; }
        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"],
        .form-group select {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 1rem;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #c07148;
            box-shadow: 0 0 0 3px rgba(192, 113, 72, 0.1);
        }
        .image-add-area {
            border: 2px dashed #c07148;
            border-radius: 5px;
            padding: 1rem;
            background: #fef3ed;
            text-align: center;
        }
        .image-add-btn {
            background: #c07148;
            color: #fff !important;
    		opacity: 1 !important;
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1rem;
            font-family: inherit;
        }
        .image-add-btn:hover { background: #a85d38; }
        .file-count {
            margin-top: 0.8rem;
            color: #c07148;
            font-weight: bold;
            font-size: 1.1rem;
        }
        .image-preview-container {
            margin-top: 1rem;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 0.8rem;
        }
        .image-preview-item {
            position: relative;
            border: 2px solid #ddd;
            border-radius: 5px;
            overflow: hidden;
            aspect-ratio: 1;
        }
        .image-preview-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .image-preview-item .image-number {
            position: absolute;
            bottom: 3px;
            left: 3px;
            background: rgba(192, 113, 72, 0.9);
            color: white;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 0.75rem;
        }
        .btn-submit, .btn-cancel {
            width: 100%;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: block;
            text-align: center;
            font-weight: bold;
        }
        .btn-submit {
            padding: 1rem;
            font-size: 1.1rem;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            font-family: inherit;
            margin-bottom: 0.8rem;
        }
        .btn-submit:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }
        .btn-cancel {
            padding: 0.5rem;
            font-size: 1rem;
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
        }
        .btn-cancel:hover {
            background: #c07148;
            color: #fff;
        }
    </style>
</head>
<body>
<div id="container">
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="register-container">
                <h1>商品登録</h1>

                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="error-message"><%= request.getAttribute("errorMessage") %></div>
                <% } %>

                <form action="${pageContext.request.contextPath}/foodloss/MerchandiseRegisterExecute.action"
                      method="post" enctype="multipart/form-data" id="merchandiseRegisterForm">

                    <input type="hidden" name="storeId" value="${sessionScope.store.storeId}">

                    <div class="form-group">
                        <label for="merchandiseName">商品名 <span>*</span></label>
                        <input type="text" id="merchandiseName" name="merchandiseName"
                               value="<%= request.getAttribute("merchandiseName") != null ? request.getAttribute("merchandiseName") : "" %>"
                               required maxlength="100" placeholder="例: トマトジュース">
                    </div>

                    <div class="form-group">
                        <label for="price">価格（税込） <span>*</span></label>
                        <input type="number" id="price" name="price"
                               value="<%= request.getAttribute("price") != null ? request.getAttribute("price") : "" %>"
                               required min="0" max="999999" placeholder="例: 500">
                    </div>

                    <div class="form-group">
                        <label for="quantity">個数 <span>*</span></label>
                        <input type="number" id="quantity" name="quantity"
                               value="<%= request.getAttribute("quantity") != null ? request.getAttribute("quantity") : "" %>"
                               required min="1" max="9999" placeholder="例: 10">
                    </div>

                    <div class="form-group">
                        <label for="expirationDate">消費期限 <span>*</span></label>
                        <input type="date" id="expirationDate" name="expirationDate"
                               value="<%= request.getAttribute("expirationDate") != null ? request.getAttribute("expirationDate") : "" %>"
                               min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                               required>
                    </div>

                    <div class="form-group">
                        <label for="employeeNumber">社員番号 <span>*</span></label>
                        <select id="employeeNumber" name="employeeNumber" required>
                            <option value="">-- 社員番号を選択 --</option>
                            <%
                                String selectedEmployeeNumber = (String) request.getAttribute("employeeNumber");
                                java.util.List<bean.Employee> empList =
                                    (java.util.List<bean.Employee>) request.getAttribute("employeeList");
                                if (empList != null) {
                                    for (bean.Employee emp : empList) {
                                        String empNum = emp.getEmployeeNumber();
                                        boolean isSelected = (selectedEmployeeNumber != null &&
                                                            selectedEmployeeNumber.equals(empNum));
                            %>
                                        <option value="<%= empNum %>" <%= isSelected ? "selected" : "" %>>
                                            <%= empNum %>
                                        </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="tags">タグ</label>
                        <input type="text" id="tags" name="tags"
                               value="<%= request.getAttribute("tags") != null ? request.getAttribute("tags") : "" %>"
                               maxlength="200" placeholder="例: 野菜, 新鮮, セール">
                    </div>

                    <div class="form-group">
                        <label>商品画像 <span>*</span></label>

                        <div class="image-add-area">
                            <label for="imageInput" class="image-add-btn" style="display: inline-block; cursor: pointer;">
                                📷 画像を選択（複数可）
                            </label>
                            <p style="margin-top: 0.5rem; font-size: 0.85rem; color: #666;">
                                複数の画像を一度に選択できます
                            </p>
                            <div id="fileCount" class="file-count"></div>
                        </div>

                        <input type="file" id="imageInput" name="merchandiseImage" accept="image/*" multiple required onchange="previewImages(this)" style="display: none;">
                        <div id="imagePreviewContainer" class="image-preview-container"></div>
                    </div>

                    <button type="submit" class="btn-submit" onclick="return validateForm()">登録</button>
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="btn-cancel">戻る</a>
                </form>
            </div>
        </div>
    </main>

    <jsp:include page="/jsp/footer.jsp" />
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
function previewImages(input) {
    var container = document.getElementById('imagePreviewContainer');
    var fileCount = document.getElementById('fileCount');

    container.innerHTML = '';

    if (input.files && input.files.length > 0) {
        fileCount.textContent = input.files.length + '枚の画像を選択中';

        for (var i = 0; i < input.files.length; i++) {
            var file = input.files[i];
            var reader = new FileReader();

            reader.onload = (function(index) {
                return function(e) {
                    var div = document.createElement('div');
                    div.className = 'image-preview-item';
                    div.innerHTML =
                        '<img src="' + e.target.result + '" alt="プレビュー">' +
                        '<span class="image-number">' + (index + 1) + '</span>';
                    container.appendChild(div);
                };
            })(i);

            reader.readAsDataURL(file);
        }
    } else {
        fileCount.textContent = '';
    }
}

function validateForm() {
    var input = document.getElementById('imageInput');
    if (!input.files || input.files.length === 0) {
        alert('少なくとも1枚の画像を選択してください');
        return false;
    }
    return true;
}
</script>
</body>
</html>