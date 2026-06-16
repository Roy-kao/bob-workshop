# Frontend Integration Plan

> 警示功能前端整合計劃

---

## 📋 整合目標

在現有的信用卡交易監控系統前端加入警示管理功能,包含:
1. 警示列表顯示區塊
2. 警示 API 呼叫功能
3. 風險等級樣式 (高/中/低)

---

## 🎯 實作步驟

### 步驟 1: 修改 index.html

**位置**: 在交易查詢區塊後 (第 93 行之後)

**新增內容**:
```html
<!-- 警示列表區塊 -->
<div class="section">
  <h2>🚨 異常警示</h2>
  
  <div class="filter-bar">
    <button class="filter-btn active" onclick="loadAllAlerts()">
      全部警示
    </button>
    <button class="filter-btn" onclick="loadHighRiskAlerts()">
      高風險警示
    </button>
    <button class="filter-btn" onclick="loadPendingAlerts()">
      待處理警示
    </button>
  </div>

  <div class="table-container">
    <table id="alertsTable">
      <thead>
        <tr>
          <th>警示ID</th>
          <th>警示類型</th>
          <th>風險等級</th>
          <th>交易ID</th>
          <th>卡號</th>
          <th>金額</th>
          <th>狀態</th>
          <th>偵測時間</th>
        </tr>
      </thead>
      <tbody id="alertsBody">
        <tr>
          <td colspan="8" class="loading">載入中...</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
```

---

### 步驟 2: 修改 app.js

**位置**: 在現有程式碼後新增

**新增內容**:

#### 2.1 API 基礎設定
```javascript
// 警示 API 基礎 URL
const ALERT_API_BASE = '/api/alerts';
```

#### 2.2 載入警示函數
```javascript
// 載入所有警示
async function loadAllAlerts() {
  setActiveAlertButton(0);
  try {
    const response = await fetch(ALERT_API_BASE);
    const alerts = await response.json();
    displayAlerts(alerts);
  } catch (error) {
    console.error('載入警示失敗:', error);
    showAlertError('無法載入警示資料');
  }
}

// 載入高風險警示
async function loadHighRiskAlerts() {
  setActiveAlertButton(1);
  try {
    const response = await fetch(`${ALERT_API_BASE}/high-risk`);
    const alerts = await response.json();
    displayAlerts(alerts);
  } catch (error) {
    console.error('載入高風險警示失敗:', error);
    showAlertError('無法載入高風險警示');
  }
}

// 載入待處理警示
async function loadPendingAlerts() {
  setActiveAlertButton(2);
  try {
    const response = await fetch(`${ALERT_API_BASE}?status=PENDING`);
    const alerts = await response.json();
    displayAlerts(alerts);
  } catch (error) {
    console.error('載入待處理警示失敗:', error);
    showAlertError('無法載入待處理警示');
  }
}
```

#### 2.3 顯示警示函數
```javascript
// 顯示警示列表
function displayAlerts(alerts) {
  const tbody = document.getElementById('alertsBody');
  
  if (alerts.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="loading">查無警示資料</td></tr>';
    return;
  }
  
  tbody.innerHTML = alerts.map(alert => `
    <tr>
      <td>${alert.id}</td>
      <td>${getAlertTypeText(alert.alertType)}</td>
      <td>
        <span class="risk-badge risk-${alert.riskLevel.toLowerCase()}">
          ${getRiskLevelText(alert.riskLevel)}
        </span>
      </td>
      <td>${alert.transaction.id}</td>
      <td><code>${alert.transaction.card.maskedCardNumber}</code></td>
      <td class="amount">${formatCurrency(alert.transaction.amount)}</td>
      <td>
        <span class="status-badge-table status-${alert.status}">
          ${getAlertStatusText(alert.status)}
        </span>
      </td>
      <td>${formatDateTime(alert.detectedAt)}</td>
    </tr>
  `).join('');
}
```

#### 2.4 輔助函數
```javascript
// 設定警示按鈕啟用狀態
function setActiveAlertButton(index) {
  const buttons = document.querySelectorAll('.filter-bar')[1].querySelectorAll('.filter-btn');
  buttons.forEach((btn, i) => {
    if (i === index) {
      btn.classList.add('active');
    } else {
      btn.classList.remove('active');
    }
  });
}

// 取得警示類型文字
function getAlertTypeText(type) {
  const typeMap = {
    'HIGH_AMOUNT': '高額交易',
    'FREQUENT_TRANSACTIONS': '頻繁交易',
    'DUPLICATE_TRANSACTION': '重複交易',
    'UNUSUAL_MERCHANT': '異常商店',
    'SUSPICIOUS_PATTERN': '可疑模式'
  };
  return typeMap[type] || type;
}

// 取得風險等級文字
function getRiskLevelText(level) {
  const levelMap = {
    'HIGH': '高風險',
    'MEDIUM': '中風險',
    'LOW': '低風險'
  };
  return levelMap[level] || level;
}

// 取得警示狀態文字
function getAlertStatusText(status) {
  const statusMap = {
    'PENDING': '待處理',
    'INVESTIGATING': '調查中',
    'RESOLVED': '已解決',
    'FALSE_POSITIVE': '誤報'
  };
  return statusMap[status] || status;
}

// 顯示警示錯誤訊息
function showAlertError(message) {
  const tbody = document.getElementById('alertsBody');
  tbody.innerHTML = `
    <tr>
      <td colspan="8" style="color: var(--red); text-align: center;">
        ⚠️ ${message}
      </td>
    </tr>
  `;
}
```

#### 2.5 頁面載入時初始化 (修改現有的 DOMContentLoaded)
```javascript
// 修改現有的 DOMContentLoaded
document.addEventListener('DOMContentLoaded', () => {
  loadStatistics();
  loadAllTransactions();
  loadAllAlerts();  // 新增這行
});
```

---

### 步驟 3: 修改 styles.css

**位置**: 在現有樣式後新增

**新增內容**:

```css
/* 警示風險等級樣式 */
.risk-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  font-family: 'IBM Plex Mono', monospace;
}

/* 高風險 - 紅色 */
.risk-high {
  background: rgba(255, 85, 85, 0.2);
  color: #cc0000;
  border: 1px solid rgba(255, 85, 85, 0.4);
}

/* 中風險 - 橘色 */
.risk-medium {
  background: rgba(255, 140, 0, 0.2);
  color: #cc7000;
  border: 1px solid rgba(255, 140, 0, 0.4);
}

/* 低風險 - 黃色 */
.risk-low {
  background: rgba(255, 193, 7, 0.2);
  color: #cc9a00;
  border: 1px solid rgba(255, 193, 7, 0.4);
}

/* 警示狀態樣式 */
.status-PENDING {
  background: rgba(255, 140, 0, 0.2);
  color: #cc7000;
}

.status-INVESTIGATING {
  background: rgba(0, 102, 204, 0.2);
  color: #0066cc;
}

.status-RESOLVED {
  background: rgba(80, 250, 123, 0.2);
  color: #2d7a4d;
}

.status-FALSE_POSITIVE {
  background: rgba(108, 117, 125, 0.2);
  color: #495057;
}
```

---

## 🔍 API 端點假設

根據 Workshop 提示,假設以下 API 端點將被實作:

| 端點 | 方法 | 說明 |
|------|------|------|
| `/api/alerts` | GET | 查詢所有警示 |
| `/api/alerts/high-risk` | GET | 查詢高風險警示 |
| `/api/alerts?status=PENDING` | GET | 查詢待處理警示 |
| `/api/alerts/statistics` | GET | 警示統計資料 |

---

## 📊 資料結構假設

### Alert 物件結構
```json
{
  "id": 1,
  "alertType": "HIGH_AMOUNT",
  "riskLevel": "HIGH",
  "status": "PENDING",
  "detectedAt": "2026-06-16T14:30:00",
  "transaction": {
    "id": 123,
    "amount": 75000,
    "card": {
      "maskedCardNumber": "**** **** **** 1234"
    }
  }
}
```

---

## ✅ 完成檢查清單

實作完成後,請確認:

- [ ] index.html 已加入警示列表區塊
- [ ] app.js 已加入所有警示相關函數
- [ ] styles.css 已加入風險等級樣式
- [ ] 頁面載入時自動載入警示
- [ ] 三個篩選按鈕功能正常
- [ ] 風險等級顏色正確顯示 (高/紅、中/橘、低/黃)
- [ ] 錯誤處理機制正常運作

---

## 🚀 下一步

完成前端整合後:
1. 測試前端功能 (在後端 API 實作前會顯示錯誤,這是正常的)
2. 切換到 Code 模式實作後端 API
3. 整合測試前後端功能

---

**建立日期**: 2026-06-16  
**版本**: 1.0.0

// Made with Bob