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
            color: white;
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1rem;
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
        .image-preview-item .remove-btn {
            position: absolute;
            top: 3px;
            right: 3px;
            background: red;
            color: white;
            border: none;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            cursor: pointer;
            font-size: 14px;
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
                        <input type="text" id="merchandiseName" name="merchandiseName" required maxlength="100" placeholder="例: トマトジュース">
                    </div>

                    <div class="form-group">
                        <label for="price">価格（税込） <span>*</span></label>
                        <input type="number" id="price" name="price" required min="0" max="999999" placeholder="例: 500">
                    </div>

                    <div class="form-group">
                        <label for="quantity">個数 <span>*</span></label>
                        <input type="number" id="quantity" name="quantity" required min="1" max="9999" placeholder="例: 10">
                    </div>

                    <div class="form-group">
                        <label for="expirationDate">消費期限 <span>*</span></label>
                        <input type="date" id="expirationDate" name="expirationDate" required>
                    </div>

                    <div class="form-group">
					    <label for="employeeNumber">社員番号 <span>*</span></label>
					    <select id="employeeNumber" name="employeeNumber" required>
					        <option value="">-- 社員番号を選択 --</option>
					        <%
					            java.util.List<bean.Employee> empList =
					                (java.util.List<bean.Employee>) request.getAttribute("employeeList");
					            if (empList != null) {
					                for (bean.Employee emp : empList) {
					        %>
					                    <option value="<%= emp.getEmployeeNumber() %>"><%= emp.getEmployeeNumber() %></option>
					        <%
					                }
					            }
					        %>
					    </select>
					</div>

                    <div class="form-group">
                        <label for="tags">タグ</label>
                        <input type="text" id="tags" name="tags" maxlength="200" placeholder="例: 野菜, 新鮮, セール">
                    </div>

                    <div class="form-group">
                        <label>商品画像 <span>*</span></label>

                        <div class="image-add-area">
                            <button type="button" class="image-add-btn" onclick="document.getElementById('imageInput').click();">
                                📷 画像を追加
                            </button>
                            <p style="margin-top: 0.5rem; font-size: 0.85rem; color: #666;">
                                ボタンをクリックして画像を追加（何度でも追加可能）
                            </p>
                            <div id="fileCount" class="file-count"></div>
                        </div>

                        <input type="file" id="imageInput" accept="image/*" style="display:none;" onchange="addImages(this)">
                        <div id="fileInputsContainer"></div>
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
var imageDataList = [];
var imageCounter = 0;

function addImages(input) {
    if (!input.files || input.files.length === 0) return;

    for (var i = 0; i < input.files.length; i++) {
        var file = input.files[i];
        var id = 'img_' + imageCounter++;

        imageDataList.push({ id: id, file: file, name: file.name });
        createFileInput(id, file);
        createPreview(id, file);
    }

    updateFileCount();
    input.value = '';
}

function createFileInput(id, file) {
    var container = document.getElementById('fileInputsContainer');
    var input = document.createElement('input');
    input.type = 'file';
    input.name = 'merchandiseImage';
    input.id = 'file_' + id;
    input.style.display = 'none';

    var dt = new DataTransfer();
    dt.items.add(file);
    input.files = dt.files;

    container.appendChild(input);
}

function createPreview(id, file) {
    var container = document.getElementById('imagePreviewContainer');
    var div = document.createElement('div');
    div.className = 'image-preview-item';
    div.id = 'preview_' + id;

    var reader = new FileReader();
    reader.onload = function(e) {
        var index = getImageIndex(id);
        div.innerHTML =
            '<img src="' + e.target.result + '" alt="プレビュー">' +
            '<button type="button" class="remove-btn" onclick="removeImage(\'' + id + '\')">×</button>' +
            '<span class="image-number">' + (index + 1) + '</span>';
    };
    reader.readAsDataURL(file);
    container.appendChild(div);
}

function removeImage(id) {
    imageDataList = imageDataList.filter(function(item) { return item.id !== id; });

    var fileInput = document.getElementById('file_' + id);
    if (fileInput) fileInput.remove();

    var preview = document.getElementById('preview_' + id);
    if (preview) preview.remove();

    updateImageNumbers();
    updateFileCount();
}

function getImageIndex(id) {
    for (var i = 0; i < imageDataList.length; i++) {
        if (imageDataList[i].id === id) return i;
    }
    return 0;
}

function updateImageNumbers() {
    for (var i = 0; i < imageDataList.length; i++) {
        var preview = document.getElementById('preview_' + imageDataList[i].id);
        if (preview) {
            var numSpan = preview.querySelector('.image-number');
            if (numSpan) numSpan.textContent = (i + 1);
        }
    }
}

function updateFileCount() {
    var count = imageDataList.length;
    var elem = document.getElementById('fileCount');
    elem.textContent = count > 0 ? count + '枚の画像を選択中' : '';
}

function validateForm() {
    if (imageDataList.length === 0) {
        alert('少なくとも1枚の画像を選択してください');
        return false;
    }
    return true;
}
</script>
</body>
</html>