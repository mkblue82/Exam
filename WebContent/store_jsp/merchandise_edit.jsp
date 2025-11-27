<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Merchandise" %>
<%@ page import="bean.MerchandiseImage" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Base64" %>

<%
    Merchandise m = (Merchandise) request.getAttribute("merchandise");
    List<MerchandiseImage> images = (List<MerchandiseImage>) request.getAttribute("images");
    String useByDateStr = (m.getUseByDate() != null) ? m.getUseByDate().toString() : "";
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>商品編集</title>
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
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
            font-size: 2rem;
        }
        .form-group { margin-bottom: 1.5rem; }
        .form-group label { font-weight: bold; margin-bottom: .5rem; display: block; color: #555; }

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"],
        .form-group select {
            width: 100%;
            padding: .8rem;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        /* 既存画像 ------------------- */
        .old-image-container {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 1rem;
        }
        .old-image-item {
            position: relative;
            width: 120px;
            height: 120px;
            border: 2px solid #ddd;
            border-radius: 5px;
            overflow: hidden;
        }
        .old-image-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .remove-old-btn {
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
        }

        /* 新規画像追加 UI -------------- */
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
            padding: .8rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .image-add-btn:hover { background: #a85d38; }

        .image-preview-container {
            margin-top: 1rem;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: .8rem;
        }
        .image-preview-item {
            position: relative;
            border: 2px solid #ddd;
            border-radius: 5px;
            object-fit: cover;
        }

        /* ★ ボタン完全統一：見た目もサイズも100%一致 ★ */
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

		/* 更新ボタン（背景色が茶色） */
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

                <h1>商品編集</h1>

                <form action="${pageContext.request.contextPath}/foodloss/MerchandiseEdit.action"
                      method="post" enctype="multipart/form-data">

                    <input type="hidden" name="merchandiseId" value="<%= m.getMerchandiseId() %>">
                    <input type="hidden" id="deletedImageIds" name="deletedImageIds">

                    <!-- ① 商品情報入力 ------------------------------------------------------------ -->
                    <div class="form-group">
                        <label>商品名 <span>*</span></label>
                        <input type="text" name="merchandiseName" value="<%= m.getMerchandiseName() %>" required>
                    </div>

                    <div class="form-group">
                        <label>価格（円） <span>*</span></label>
                        <input type="number" name="price" value="<%= m.getPrice() %>" required>
                    </div>

                    <div class="form-group">
                        <label>在庫数 <span>*</span></label>
                        <input type="number" name="stock" value="<%= m.getStock() %>" required>
                    </div>

                    <div class="form-group">
                        <label>消費期限 <span>*</span></label>
                        <input type="date" name="useByDate" value="<%= useByDateStr %>" required>
                    </div>

                    <div class="form-group">
                        <label>社員番号 <span>*</span></label>
                        <input type="number" name="employeeId" value="<%= m.getEmployeeId() %>" required>
                    </div>

                    <div class="form-group">
                        <label>タグ</label>
                        <input type="text" name="merchandiseTag" value="<%= m.getMerchandiseTag() %>">
                    </div>

                    <!-- ② 既存画像（削除ボタン付き） ------------------------------------------ -->
                    <div class="form-group">
                        <label>現在の画像（削除可能）</label>
                        <div class="old-image-container">
                        <% if (images != null && !images.isEmpty()) {
                               for (MerchandiseImage img : images) {
                                   byte[] data = img.getImageData();
                                   if (data != null) {
                                       String base64 = Base64.getEncoder().encodeToString(data);
                        %>

                            <div class="old-image-item" id="old_<%= img.getImageId() %>">
                                <img src="data:image/jpeg;base64,<%= base64 %>">
                                <button type="button" class="remove-old-btn"
                                        onclick="removeOldImage('<%= img.getImageId() %>')">×</button>
                            </div>

                        <%         }
                               }
                           } else { %>
                            <p>画像なし</p>
                        <% } %>
                        </div>
                    </div>

                    <!-- ③ 新規画像追加エリア --------------------------------------------------- -->
                    <div class="form-group">
                        <label>商品画像（追加）</label>

                        <div class="image-add-area">
                            <button type="button" class="image-add-btn"
                                    onclick="document.getElementById('imageInput').click();">📷 画像を追加</button>
                            <p style="margin-top:.5rem;font-size:.85rem;color:#666;">
                                ボタンをクリックして画像を追加（何度でも追加可能）
                            </p>
                            <div id="fileCount" class="file-count"></div>
                        </div>

                        <input type="file" id="imageInput" accept="image/*"
                               style="display:none;" onchange="addImages(this)">
                        <div id="fileInputsContainer"></div>

                        <div id="imagePreviewContainer" class="image-preview-container"></div>
                    </div>

                    <!-- ④ 更新ボタン ----------------------------------------------------------- -->
                    <button type="submit" class="btn-submit">更新</button>
                    <a href="${pageContext.request.contextPath}/foodloss/MerchandiseList.action"
                       class="btn-cancel">戻る</a>

                </form>
            </div>
        </div>
    </main>

    <jsp:include page="/jsp/footer.jsp" />
</div>

<script>
/* --- 既存画像削除 --- */
var deletedOldList = [];
function removeOldImage(imageId) {
    deletedOldList.push(imageId);
    document.getElementById("deletedImageIds").value = deletedOldList.join(",");

    const target = document.getElementById("old_" + imageId);
    if (target) target.remove();
}

/* --- 新規画像追加（商品登録と同じ） --- */
var imageDataList = [];
var imageCounter = 0;

function addImages(input) {
    if (!input.files.length) return;

    for (let file of input.files) {
        let id = "img_" + imageCounter++;
        imageDataList.push({ id, file });

        createFileInput(id, file);
        createPreview(id, file);
    }

    updateFileCount();
    input.value = "";
}

function createFileInput(id, file) {
    const container = document.getElementById("fileInputsContainer");
    let input = document.createElement("input");
    input.type = "file";
    input.name = "imageFile";
    input.style.display = "none";
    input.id = "file_" + id;

    let dt = new DataTransfer();
    dt.items.add(file);
    input.files = dt.files;

    container.appendChild(input);
}

function createPreview(id, file) {
    const container = document.getElementById("imagePreviewContainer");
    let div = document.createElement("div");
    div.className = "image-preview-item";
    div.id = "preview_" + id;

    let reader = new FileReader();
    reader.onload = function(e) {
        let index = getImageIndex(id);
        div.innerHTML =
            '<img src="' + e.target.result + '">' +
            '<button type="button" class="remove-btn" onclick="removeImage(\'' + id + '\')">×</button>' +
            '<span class="image-number">' + (index + 1) + '</span>';
    };
    reader.readAsDataURL(file);

    container.appendChild(div);
}

function removeImage(id) {
    imageDataList = imageDataList.filter(i => i.id !== id);

    const fileInput = document.getElementById("file_" + id);
    if (fileInput) fileInput.remove();

    const preview = document.getElementById("preview_" + id);
    if (preview) preview.remove();

    updateImageNumbers();
    updateFileCount();
}

function getImageIndex(id) {
    return imageDataList.findIndex(i => i.id === id);
}

function updateImageNumbers() {
    imageDataList.forEach((item, index) => {
        const div = document.getElementById("preview_" + item.id);
        if (div) {
            div.querySelector(".image-number").textContent =
                index + 1;
        }
    });
}

function updateFileCount() {
    const count = imageDataList.length;
    document.getElementById("fileCount").textContent =
        count > 0 ? count + "枚の画像を選択中" : "";
}
</script>

</body>
</html>
