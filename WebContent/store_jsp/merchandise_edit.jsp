<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Merchandise" %>
<%@ page import="bean.MerchandiseImage" %>
<%@ page import="java.util.List" %>

<%
    Merchandise m = (Merchandise) request.getAttribute("merchandise");
    List<MerchandiseImage> images = (List<MerchandiseImage>) request.getAttribute("images");

    String useByDateStr = (m.getUseByDate() != null) ? m.getUseByDate().toString() : "";
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>商品編集 - フードロス削減システム</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        body {
            font-family: sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }

        .edit-wrapper {
            width: 100%;
            max-width: 450px;   /* ★前の細めデザイン */
            margin: 3rem auto;
        }

        .edit-container {
            background: #fff;
            border-radius: 8px;
            padding: 2rem 2rem;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        .edit-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .edit-header h1 {
            color: #c77c4a;
            font-size: 1.8rem;
        }

        .form-group {
            margin-bottom: 1.3rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.4rem;
            font-weight: bold;
            color: #444;
        }

        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group input[type="date"] {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        /* 画像ボックス（前の小さめレイアウト） */
        .image-box {
            text-align: center;
            margin-right: 10px;
            margin-bottom: 10px;
        }

        /* 削除ボタン */
        .btn-delete {
            background: red;
            color: #fff;
            padding: 5px 10px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 4px;
        }

        /* 更新ボタン */
        .btn-update {
            width: 100%;
            background: #c77c4a;
            color: #fff;
            padding: 0.9rem;
            font-size: 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 1rem;
        }

        .btn-update:hover {
            background: #b66c3a;
        }

        /* 戻るリンク（中央寄せ） */
        .back-link {
            text-align: center;
            margin-top: 1.5rem;
        }

        .back-link a {
            color: #666;
            text-decoration: none;
        }

        .back-link a:hover {
            color: #c77c4a;
        }
    </style>
</head>
<body>

<jsp:include page="/store_jsp/header_store.jsp" />

<main class="column">
    <div class="edit-wrapper">
        <div class="edit-container">

            <div class="edit-header">
                <h1>商品情報編集</h1>
            </div>

            <form action="${pageContext.request.contextPath}/foodloss/MerchandiseEdit.action" method="post" enctype="multipart/form-data">
                <input type="hidden" name="merchandiseId" value="<%= m.getMerchandiseId() %>">

                <!-- 商品名 -->
                <div class="form-group">
                    <label>商品名</label>
                    <input type="text" name="merchandiseName" value="<%= m.getMerchandiseName() %>" required>
                </div>

                <!-- 価格 -->
                <div class="form-group">
                    <label>価格</label>
                    <input type="number" name="price" value="<%= m.getPrice() %>" required>
                </div>

                <!-- 在庫 -->
                <div class="form-group">
                    <label>在庫数</label>
                    <input type="number" name="stock" value="<%= m.getStock() %>" required>
                </div>

                <!-- 消費期限 -->
                <div class="form-group">
                    <label>消費期限</label>
                    <input type="date" name="useByDate" value="<%= useByDateStr %>" required>
                </div>

                <!-- 社員番号 -->
                <div class="form-group">
                    <label>担当社員番号</label>
                    <input type="number" name="employeeId" value="<%= m.getEmployeeId() %>" required>
                </div>

                <!-- タグ -->
                <div class="form-group">
                    <label>タグ</label>
                    <input type="text" name="merchandiseTag" value="<%= m.getMerchandiseTag() %>">
                </div>

                <!-- ★ 既存画像表示 -->
                <div class="form-group">
                    <label>登録済み画像</label>

                    <div style="display:flex; flex-wrap:wrap;">
                    <%
                        if (images != null && !images.isEmpty()) {
                            int index = 0;
                            for (MerchandiseImage img : images) {
                                boolean isMain = (index == 0);
                    %>

                        <div class="image-box">
                            <img src="<%= request.getContextPath() %>/foodloss/ImageDisplay.action?id=<%= img.getImageId() %>"
                                 style="width:120px; height:120px; object-fit:cover; border:<%= isMain ? "3px solid red" : "1px solid #ccc" %>;">
                            <% if (isMain) { %>
                                <div style="color:red; font-weight:bold; margin-top:4px;">メイン</div>
                            <% } %>

                            <form action="${pageContext.request.contextPath}/foodloss/DeleteImage.action" method="post">
                                <input type="hidden" name="imageId" value="<%= img.getImageId() %>">
                                <input type="hidden" name="merchandiseId" value="<%= m.getMerchandiseId() %>">
                                <button class="btn-delete">削除</button>
                            </form>
                        </div>

                    <%
                                index++;
                            }
                        } else {
                    %>
                        <p>画像なし</p>
                    <% } %>
                    </div>
                </div>

                <!-- ★ 削除用フォームを JS で作成して送信 -->
				<script>
				function deleteImage(imageId, merchandiseId) {
				    if (!confirm("画像を削除しますか？")) return;

				    const form = document.createElement("form");
				    form.method = "post";
				    form.action = "<%= request.getContextPath() %>/foodloss/DeleteImage.action";

				    const i1 = document.createElement("input");
				    i1.type = "hidden";
				    i1.name = "imageId";
				    i1.value = imageId;
				    form.appendChild(i1);

				    const i2 = document.createElement("input");
				    i2.type = "hidden";
				    i2.name = "merchandiseId";
				    i2.value = merchandiseId;
				    form.appendChild(i2);

				    document.body.appendChild(form);
				    form.submit();
				}
				</script>

                <!-- 新しい画像アップロード -->
                <div class="form-group">
                    <label>画像を追加（複数可）</label>
                    <input type="file" name="imageFile" id="imageFile" multiple>
                </div>

                <!-- プレビュー -->
                <div id="preview" style="margin-top:10px;"></div>

                <script>
                    document.getElementById('imageFile').addEventListener('change', function (event) {
                        const preview = document.getElementById('preview');
                        preview.innerHTML = "";

                        for (const file of event.target.files) {
                            const reader = new FileReader();
                            reader.onload = e => {
                                const img = document.createElement("img");
                                img.src = e.target.result;
                                img.style.width = "120px";
                                img.style.height = "120px";
                                img.style.objectFit = "cover";
                                img.style.marginRight = "10px";
                                img.style.marginBottom = "10px";
                                preview.appendChild(img);
                            };
                            reader.readAsDataURL(file);
                        }
                    });
                </script>

                <button type="submit" class="btn-update">更新する</button>
            </form>

            <div class="back-link">
                <a href="${pageContext.request.contextPath}/foodloss/MerchandiseList.action">← 戻る</a>
            </div>

        </div>
    </div>
</main>

<jsp:include page="/jsp/footer.jsp" />

</body>
</html>
