<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>検索結果</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <style>
    .search-container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .search-header h2 { color: #c17f59; font-size: 1.6rem; margin-bottom: 10px; }
    .search-keyword { font-weight: bold; color: #e67e22; }
    .result-count { font-size: 0.95rem; color: #777; margin-bottom: 20px; }
    .store-section {
      background: #fff; padding: 20px; border-radius: 10px;
      margin-bottom: 40px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    .store-name {
      color: #c17f59; font-size: 1.3rem; margin-bottom: 15px;
      border-bottom: 2px solid #e67e22; padding-bottom: 5px;
    }
    .product-grid {
      display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 20px;
    }
    .product-card {
      background: #fff; padding: 15px; border-radius: 8px;
      border: 1px solid #eee; text-align: center; cursor: pointer;
      transition: .3s;
    }
    .product-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 5px 20px rgba(0, 0, 0, .15);
    }
    .product-card img {
      width: 100%; height: 120px; object-fit: cover;
      border-radius: 6px; margin-bottom: 10px;
    }
    .product-name { color: #333; margin-bottom: 5px; }
    .product-price { color: #e67e22; font-size: 1.1rem; font-weight: bold; }
    .no-results { text-align: center; color: #888; padding: 50px 15px; }
    .no-results i { font-size: 4rem; margin-bottom: 20px; color: #ccc; }
    .back-link {
      display: inline-block; padding: 10px 30px; background: #c17f59; color: #fff;
      text-decoration: none; border-radius: 5px; transition: .3s;
    }
    .back-link:hover { background: #a66b4a; }
  </style>
</head>
<body>

  <jsp:include page="header_user.jsp" />

  <main class="search-container">

    <div class="search-header">
      <h2><i class="fa fa-search"></i> 検索結果</h2>
      <c:if test="${not empty keyword}">
        <p>「<span class="search-keyword">${fn:escapeXml(keyword)}</span>」の検索結果</p>
      </c:if>
    </div>

    <c:choose>

      <!-- 検索結果がある -->
      <c:when test="${not empty searchResults}">
        <p class="result-count">${resultCount} 件見つかりました</p>

        <c:forEach var="store" items="${searchResults}">
          <div class="store-section">
            <h3 class="store-name"><i class="fa fa-store"></i> ${fn:escapeXml(store.storeName)}</h3>

            <div class="product-grid">
              <c:forEach var="product" items="${store.products}">
                <div class="product-card"
                     onclick="location.href='${pageContext.request.contextPath}/foodloss/ProductDetail.action?id=${product.id}'">
                  <img src="${pageContext.request.contextPath}/images/${product.image}"
                       onerror="this.src='${pageContext.request.contextPath}/images/no-image.png'">
                  <p class="product-name">${fn:escapeXml(product.name)}</p>
                  <p class="product-price">¥ ${product.price}</p>
                </div>
              </c:forEach>
            </div>
          </div>
        </c:forEach>
      </c:when>

      <!-- 検索ワードなし -->
      <c:when test="${empty keyword}">
        <div class="no-results">
          <i class="fa fa-search"></i>
          <p>検索キーワードを入力してください</p>
          <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="back-link">
            <i class="fa fa-home"></i> ホームに戻る
          </a>
        </div>
      </c:when>

      <!-- 検索結果ロ件 -->
      <c:otherwise>
        <div class="no-results">
          <i class="fa fa-frown"></i>
          <p>「${fn:escapeXml(keyword)}」に一致する商品・店舗が見つかりませんでした</p>
          <p style="font-size: 0.9rem; color: #aaa;">別のキーワードで再検索してみてください</p>
          <a href="${pageContext.request.contextPath}/foodloss/Menu.action" class="back-link">
            <i class="fa fa-home"></i> ホームに戻る
          </a>
        </div>
      </c:otherwise>

    </c:choose>

  </main>

</body>
</html>
