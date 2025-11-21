<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.Store" %>
<%
    Store store = (Store) request.getAttribute("store");
    if (store == null) {
        response.sendRedirect(request.getContextPath() + "/foodloss/Menu.action");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>店舗詳細</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
    .store-detail-container {
        max-width: 900px;
        margin: 40px auto;
        padding: 2rem;
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .store-detail-container h2 {
        color: #c07148;
        text-align: center;
        font-size: 1.8rem;
        margin-bottom: 2rem;
    }

    .detail-section {
        margin-bottom: 2rem;
    }

    .detail-section h3 {
        font-size: 1.3rem;
        color: #333;
        margin-bottom: 1rem;
        padding-bottom: 0.5rem;
        border-bottom: 1px solid #e0e0e0;
    }

    .detail-row {
        display: flex;
        padding: 1rem 0 1rem 50px;
        border-bottom: 1px solid #f0f0f0;
    }

    .detail-row:last-child {
        border-bottom: none;
    }

    .detail-label {
        width: 200px;
        font-weight: bold;
        color: #666;
        flex-shrink: 0;
    }

    .detail-value {
        flex: 1;
        color: #333;
    }

    .button-container {
        display: flex;
        gap: 15px;
        justify-content: center;
        margin-top: 30px;
    }

    .btn {
        padding: 12px 40px;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        cursor: pointer;
        transition: background-color 0.3s, transform 0.1s;
        font-weight: bold;
        text-decoration: none;
        display: inline-block;
    }

    .btn:active {
        transform: scale(0.98);
    }

    .btn-primary {
        background: #c07148;
        color: #fff;
        box-shadow: 0 3px 10px rgba(192, 113, 72, 0.3);
    }

    .btn-primary:hover {
        background: #a85d38;
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(192, 113, 72, 0.4);
    }

    .btn-secondary {
        background-color: #ccc;
        color: #333;
        transition: all 0.3s;
    }

    .btn-secondary:hover {
        background-color: #c07148;
        color: #fff;
        transform: translateY(-3px);
    }

    .back-button {
        margin-top: 30px;
        text-align: center;
    }

    .back-button a {
        display: inline-block;
        padding: 12px 40px;
        background-color: #ccc;
        text-decoration: none;
        border-radius: 5px;
        font-weight: bold;
        color: #333;
        transition: all 0.3s;
    }

    .back-button a:hover {
        background-color: #c07148;
        color: #fff;
        transform: translateY(-3px);
    }

    .no-data {
        color: #999;
        font-style: italic;
    }

    @media screen and (max-width: 600px) {
        .store-detail-container {
            margin: 20px;
            padding: 1.5rem;
        }

        .detail-row {
            flex-direction: column;
        }

        .detail-label {
            width: 100%;
            margin-bottom: 0.5rem;
        }
    }
</style>
</head>
<body>
<div id="container">
    <!-- ヘッダー読み込み -->
    <jsp:include page="/store_jsp/header_store.jsp" />

    <!-- メインコンテンツ -->
    <main class="column">
        <div class="main-contents">
            <div class="store-detail-container">
                <h2>店舗詳細情報</h2>

                <!-- 基本情報 -->
                <div class="detail-section">
                    <h3>基本情報</h3>

                    <div class="detail-row">
                        <div class="detail-label">店舗ID</div>
                        <div class="detail-value"><%= store.getStoreId() %></div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">店舗名</div>
                        <div class="detail-value"><%= store.getStoreName() %></div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">住所</div>
                        <div class="detail-value"><%= store.getAddress() != null ? store.getAddress() : "未設定" %></div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">電話番号</div>
                        <div class="detail-value"><%= store.getPhone() != null ? store.getPhone() : "未設定" %></div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">メールアドレス</div>
                        <div class="detail-value"><%= store.getEmail() != null ? store.getEmail() : "未設定" %></div>
                    </div>
                </div>

                <!-- 営業許可証 -->
                <div class="detail-section">
                    <h3>営業許可証</h3>

                    <div class="detail-row">
                        <div class="detail-label">ステータス</div>
                        <div class="detail-value">
                            <% if (store.getLicense() != null) { %>
                                登録済み
                            <% } else { %>
                                <span class="no-data">未登録</span>
                            <% } %>
                        </div>
                    </div>

                    <% if (store.getLicense() != null) { %>
                    <div class="detail-row">
                        <div class="detail-label">ダウンロード</div>
                        <div class="detail-value">
                            <a href="${pageContext.request.contextPath}/foodloss/DownloadLicense.action?storeId=<%= store.getStoreId() %>"
                               class="btn btn-secondary" style="padding: 8px 20px; font-size: 14px;">
                                営業許可証をダウンロード
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>

                <!-- ホームに戻るボタン -->
                <div class="back-button">
                    <a href="${pageContext.request.contextPath}/foodloss/Menu.action">ホームに戻る</a>
                </div>

            </div>
        </div>
    </main>

    <!-- フッター読み込み -->
    <jsp:include page="/jsp/footer.jsp" />
</div>

<!-- JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<script src="${pageContext.request.contextPath}/js/slick.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
