<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.Merchandise" %>
<%@ page import="java.text.SimpleDateFormat" %>


<%
    // サーブレットから受け取った商品一覧
    List<Merchandise> products = (List<Merchandise>) request.getAttribute("merchandiseList");

    // 割引情報
    Boolean isDiscountApplied = (Boolean) request.getAttribute("isDiscountApplied");
    Integer discountRate = (Integer) request.getAttribute("discountRate");

    // セッション情報
    Integer storeId = (session.getAttribute("storeId") != null)
                        ? (Integer) session.getAttribute("storeId")
                        : null;

    String storeName = (session.getAttribute("storeName") != null)
                        ? (String) session.getAttribute("storeName")
                        : null;

    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登録商品一覧</title>

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

        .discount-notice {
            text-align: center;
            background-color: #fff3cd;
            color: #856404;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 20px;
            font-weight: bold;
        }

        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th {
            background-color: #c07148; color: #fff; padding: 12px;
            font-weight: bold; border: 1px solid #c07148;
        }
        td { padding: 12px; border: 1px solid #ddd; text-align: center; color: #333; }
        tbody tr:hover { background-color: #f5f5f5; }

        .price-display {
            display: flex; flex-direction: column; align-items: center; gap: 3px;
        }
        .discounted-price { font-size: 1.1rem; font-weight: bold; color: #d9534f; }
        .original-price { font-size: 0.85rem; color: #999; text-decoration: line-through; }

        .btn {
            display: inline-block; padding: 8px 25px; background-color: #c07148;
            color: #fff; border-radius: 5px; text-decoration: none; transition: .3s; font-weight: bold;
        }
        .btn:hover { background-color: #a85d38; transform: translateY(-2px); color: #fff; }

        .no-data { text-align: center; padding: 40px 20px; color: #999; font-size: 1rem; }

        .back-button { margin-top: 40px; text-align: center; }
        .back-button a {
            display: inline-block; padding: 12px 40px; background-color: #ccc; text-decoration: none;
            border-radius: 8px; font-weight: bold; color: #333; margin: 0 10px; transition: .3s;
        }
        .back-button a:hover { background-color: #c07148; color: #fff; transform: translateY(-3px); }

        @media screen and (max-width: 1000px) {
            .main-content { margin: 20px; padding: 1.5rem; }
            table { font-size: 0.9rem; }
            th, td { padding: 8px; }
            .back-button a { display: block; margin: 10px auto; max-width: 300px; }
        }

        /* 編集・削除ボタン（社員一覧と統一） */
        .table-button { display: inline-block; width: 100px; padding: 8px 0; text-align: center; box-sizing: border-box; }
        .btn-delete { background-color: #d9534f; color: #fff; }
        .btn-delete:hover { background-color: #c9302c; color:#fff; }

        /* --- 削除確認モーダル（社員一覧と同じ） --- */
        .modal-overlay {
            display: none; position: fixed; inset: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,.5); z-index: 1000; justify-content: center; align-items: center;
        }
        .modal-overlay.active { display: flex; }
        .modal-content {
            background: #fff; padding: 30px; border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0,0,0,.3); max-width: 500px; width: 90%; text-align: center;
        }
        .modal-content h3 { color: #dc3545; margin-bottom: 20px; font-size: 1.5rem; }
        .modal-content p { margin-bottom: 30px; color: #333; font-size: 1rem; line-height: 1.6; }
        .modal-buttons { display: flex; justify-content: center; gap: 15px; }
        .modal-buttons button {
            padding: 10px 30px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; transition: .3s;
        }
        .btn-confirm { background: #d9534f; color: #fff; }
        .btn-confirm:hover { background: #c9302c; }
        .btn-cancel { background: #6c757d; color: #fff; }
        .btn-cancel:hover { background: #5a6268; }
    </style>
</head>

<body>
<div id="container">

    <!-- 共通ヘッダー -->
    <jsp:include page="/store_jsp/header_store.jsp" />

    <main class="column">
        <div class="main-contents">
            <div class="main-content">

                <h2>登録商品一覧</h2>

                <%
                    String deleteError = (String) session.getAttribute("deleteError");
                    if (deleteError != null) {
                %>
                    <div style="color:#d9534f;font-weight:bold;text-align:center;margin-bottom:20px;">
                        <%= deleteError %>
                    </div>
                <%
                        session.removeAttribute("deleteError"); // 1回で消えるように
                    }
                %>

                <!-- ログイン店舗の表示 -->
                <div class="store-info">
                    <% if (storeName != null) { %>
                        表示中の店舗：<%= storeName %>（ID：<%= storeId %>）
                    <% } else { %>
                        店舗ID：<%= storeId != null ? storeId : "不明" %>
                    <% } %>
                </div>

                <!-- 割引適用中の通知 -->
                <% if (isDiscountApplied != null && isDiscountApplied && discountRate != null) { %>
                    <div class="discount-notice">
                        現在、全商品に<%= discountRate %>%OFFの割引が適用されています
                    </div>
                <% } %>

                <!-- 商品一覧 -->
                <% if (products != null && !products.isEmpty()) { %>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>商品名</th>
                            <th>価格(税込)</th>
                            <th>在庫</th>
                            <th>消費期限</th>
                            <th>登録日時</th>
                            <th style="width:120px;">編集</th>
                            <th style="width:120px;">削除</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Merchandise m : products) { %>
                            <tr>
                                <td><%= m.getMerchandiseId() %></td>
                                <td><%= m.getMerchandiseName() %></td>
                                <td>
                                    <%
                                        int originalPrice = m.getPrice();
                                        if (isDiscountApplied != null && isDiscountApplied && discountRate != null) {
                                            int discountedPrice = (int)(originalPrice * (100 - discountRate) / 100.0);
                                    %>
                                        <div class="price-display">
                                            <span class="discounted-price">￥<%= discountedPrice %></span>
                                            <span class="original-price">(元価格: ￥<%= originalPrice %>)</span>
                                        </div>
                                    <% } else { %>
                                        ￥<%= originalPrice %>
                                    <% } %>
                                </td>
                                <td><%= m.getStock() %></td>
                                <td><%= m.getUseByDate() %></td>
                                <td><%= datetimeFormat.format(m.getRegistrationTime()) %></td>
                                <td>
                                    <a class="btn table-button"
                                       href="${pageContext.request.contextPath}/foodloss/MerchandiseEdit.action?id=<%= m.getMerchandiseId() %>">
                                        編集
                                    </a>
                                </td>
                                <td>
                                    <a class="btn btn-delete table-button"
                                       href="#"
                                       onclick="showDeleteConfirm(<%= m.getMerchandiseId() %>, '<%= m.getMerchandiseName() %>'); return false;">
                                        削除
                                    </a>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">登録された商品はありません。</p>
                <% } %>

                <!-- 戻る・商品登録・割引設定ボタン -->
                <div class="back-button">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホームに戻る</a>
                    <a href="${pageContext.request.contextPath}/foodloss/MerchandiseRegister.action">商品登録</a>
                    <a href="${pageContext.request.contextPath}/foodloss/DiscountSetting.action">割引設定</a>
                </div>

            </div>
        </div>
    </main>

    <!-- 共通フッター -->
    <jsp:include page="/jsp/footer.jsp" />

</div>

<!-- 削除確認モーダル（社員一覧と統一） -->
<div id="deleteModal" class="modal-overlay">
    <div class="modal-content">
        <h3>商品の削除確認</h3>
        <p id="deleteMessage"></p>
        <p style="color:#999;font-size:.9rem;">この操作は取り消せません。</p>
        <div class="modal-buttons">
            <button class="btn-cancel" onclick="hideDeleteConfirm()">キャンセル</button>
            <button class="btn-confirm" onclick="deleteMerchandise()">削除する</button>
        </div>
    </div>
</div>

<!-- 削除用フォーム（POST推奨／CSRFトークンがある場合はhiddenで追加） -->
<form id="deleteForm" method="post"
      action="${pageContext.request.contextPath}/foodloss/MerchandiseDelete.action"
      style="display:none;">
    <input type="hidden" id="deleteMerchandiseId" name="id">
</form>

<!-- 共通JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
    let deleteMerchandiseIdValue = null;

    function showDeleteConfirm(merchandiseId, merchandiseName) {
        deleteMerchandiseIdValue = merchandiseId;
        const msg = document.getElementById('deleteMessage');
        msg.innerHTML = ''; // XSS回避のためDOMで構築
        msg.append('「');
        const strong = document.createElement('strong');
        strong.textContent = merchandiseName || '';
        msg.append(strong);
        msg.append('」を削除してもよろしいですか？');
        document.getElementById('deleteModal').classList.add('active');
    }

    function hideDeleteConfirm() {
        deleteMerchandiseIdValue = null;
        document.getElementById('deleteModal').classList.remove('active');
    }

    function deleteMerchandise() {
        if (deleteMerchandiseIdValue !== null) {
            document.getElementById('deleteMerchandiseId').value = deleteMerchandiseIdValue;
            document.getElementById('deleteForm').submit();
        }
    }

    // モーダル外クリックで閉じる
    document.getElementById('deleteModal').addEventListener('click', function(e) {
        if (e.target === this) hideDeleteConfirm();
    });
</script>

</body>
</html>
