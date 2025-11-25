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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品編集</title>
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

        .edit-container {
            max-width: 600px;
            width: 100%;
            margin: 40px auto;
            padding: 2rem;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }

        .edit-container h1 {
            color: #c07148;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 2rem;
            border-bottom: 2px solid #c07148;
            padding-bottom: 1rem;
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

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"],
        .form-group input[type="file"] {
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

        .image-display {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 0.5rem;
        }

        .image-display img {
            max-width: 120px;
            max-height: 120px;
            border: 2px solid #ddd;
            border-radius: 5px;
            object-fit: cover;
        }

        .image-display p {
            color: #999;
            font-style: italic;
        }

        #preview {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 1rem;
            margin-bottom: 1.5rem;
        }

        #preview img {
            max-width: 120px;
            max-height: 120px;
            border: 2px solid #c07148;
            border-radius: 5px;
            object-fit: cover;
        }

        .btn-update,
        .btn-back {
            width: 100%;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.3s;
            font-weight: bold;
            text-decoration: none;
            display: block;
            text-align: center;
        }

        .btn-update {
            padding: 1.2rem;
            font-size: 1.1rem;
            background: #c07148;
            color: #fff;
            box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
            margin-bottom: 0.8rem;
        }

        .btn-update:hover {
            background: #a85d38;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
        }

        .btn-back {
            padding: 0.6rem;
            background: #fff;
            color: #c07148;
            border: 2px solid #c07148;
        }

        .btn-back:hover {
            background: #c07148;
            color: #fff;
        }

        @media screen and (max-width: 600px) {
            .edit-container {
                margin: 20px;
                padding: 1.5rem;
            }

            .edit-container h1 {
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
            <div class="edit-container">
                <h1>商品情報編集</h1>

                <form action="${pageContext.request.contextPath}/foodloss/MerchandiseEdit.action" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="merchandiseId" value="<%= m.getMerchandiseId() %>">

                    <div class="form-group">
                        <label for="merchandiseName">商品名</label>
                        <input type="text" id="merchandiseName" name="merchandiseName" value="<%= m.getMerchandiseName() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="price">価格</label>
                        <input type="number" id="price" name="price" value="<%= m.getPrice() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="stock">在庫数</label>
                        <input type="number" id="stock" name="stock" value="<%= m.getStock() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="useByDate">消費期限</label>
                        <input type="date" id="useByDate" name="useByDate" value="<%= useByDateStr %>" required>
                    </div>

                    <div class="form-group">
                        <label for="employeeId">担当社員番号</label>
                        <input type="number" id="employeeId" name="employeeId" value="<%= m.getEmployeeId() %>" required>
                    </div>

                    <div class="form-group">
                        <label for="merchandiseTag">タグ</label>
                        <input type="text" id="merchandiseTag" name="merchandiseTag" value="<%= m.getMerchandiseTag() %>">
                    </div>

                    <div class="form-group">
                        <label>現在の画像</label>
                        <div class="image-display">
                        <%
                            if (images != null && !images.isEmpty()) {
                                for (MerchandiseImage img : images) {
                                    byte[] data = img.getImageData();
                                    if (data != null) {
                                        String base64 = Base64.getEncoder().encodeToString(data);
                        %>
                                        <img src="data:image/jpeg;base64,<%= base64 %>" alt="商品画像">
                        <%
                                    }
                                }
                            } else {
                        %>
                            <p>画像なし</p>
                        <% } %>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="imageFile">画像を変更（複数可）</label>
                        <input type="file" id="imageFile" name="imageFile" multiple>
                    </div>

                    <div id="preview"></div>

                    <button type="submit" class="btn-update">更新する</button>
                </form>

                <!-- 戻るボタン（フォームの外） -->
                <a href="${pageContext.request.contextPath}/foodloss/MerchandiseList.action" class="btn-back">戻る</a>
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

<script>
    document.getElementById('imageFile').addEventListener('change', function (event) {
        const preview = document.getElementById('preview');
        preview.innerHTML = ''; //  既存のプレビューをクリア

        const files = event.target.files;

        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const reader = new FileReader();

            reader.onload = function (e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                preview.appendChild(img);
            };

            reader.readAsDataURL(file);
        }
    });
</script>

</body>
</html>