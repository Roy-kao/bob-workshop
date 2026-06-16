# TransactionAlert API 文件

## 概述

TransactionAlert API 提供交易警示的查詢與統計功能，用於監控信用卡交易中的異常行為。

## 基礎資訊

- **Base URL**: `http://localhost:8080/api/alerts`
- **Content-Type**: `application/json`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## API 端點

### 1. 查詢所有警示

**GET** `/api/alerts`

取得系統中所有的交易警示記錄。

**回應範例**:
```json
[
  {
    "alertId": 1,
    "transaction": {
      "id": 6,
      "amount": 65000.00,
      "status": "APPROVED"
    },
    "alertType": "HIGH_AMOUNT",
    "severity": "CRITICAL",
    "detectedAt": "2026-06-16T05:27:23",
    "description": "偵測到異常高額交易：NT$ 65,000，超過一般消費模式",
    "createdAt": "2026-06-16T06:27:23"
  }
]
```

---

### 2. 查詢單筆警示

**GET** `/api/alerts/{id}`

根據警示 ID 查詢特定警示的詳細資訊。

**路徑參數**:
- `id` (Long, required): 警示 ID

**回應範例**:
```json
{
  "alertId": 1,
  "transaction": {
    "id": 6,
    "amount": 65000.00
  },
  "alertType": "HIGH_AMOUNT",
  "severity": "CRITICAL",
  "detectedAt": "2026-06-16T05:27:23",
  "description": "偵測到異常高額交易：NT$ 65,000，超過一般消費模式",
  "createdAt": "2026-06-16T06:27:23"
}
```

**錯誤回應**:
- `404 Not Found`: 找不到指定的警示

---

### 3. 查詢交易的警示

**GET** `/api/alerts/transaction/{transactionId}`

取得特定交易的所有警示記錄。

**路徑參數**:
- `transactionId` (Long, required): 交易 ID

**使用範例**:
```bash
curl http://localhost:8080/api/alerts/transaction/6
```

**回應範例**:
```json
[
  {
    "alertId": 1,
    "transaction": {
      "id": 6
    },
    "alertType": "HIGH_AMOUNT",
    "severity": "CRITICAL",
    "detectedAt": "2026-06-16T05:27:23",
    "description": "偵測到異常高額交易：NT$ 65,000，超過一般消費模式",
    "createdAt": "2026-06-16T06:27:23"
  }
]
```

---

### 4. 根據嚴重程度查詢警示

**GET** `/api/alerts/severity/{severity}`

取得指定嚴重程度的所有警示記錄。

**路徑參數**:
- `severity` (AlertSeverity, required): 嚴重程度
  - 可選值: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`

**使用範例**:
```bash
curl http://localhost:8080/api/alerts/severity/CRITICAL
```

**回應範例**:
```json
[
  {
    "alertId": 1,
    "alertType": "HIGH_AMOUNT",
    "severity": "CRITICAL",
    "detectedAt": "2026-06-16T05:27:23",
    "description": "偵測到異常高額交易：NT$ 65,000，超過一般消費模式"
  },
  {
    "alertId": 7,
    "alertType": "FREQUENT_TRANSACTION",
    "severity": "CRITICAL",
    "detectedAt": "2026-06-16T05:57:23",
    "description": "嚴重異常：1小時內5筆以上交易"
  }
]
```

---

### 5. 根據警示類型查詢

**GET** `/api/alerts/type/{alertType}`

取得指定類型的所有警示記錄。

**路徑參數**:
- `alertType` (AlertType, required): 警示類型
  - 可選值: `HIGH_AMOUNT`, `FREQUENT_TRANSACTION`, `DUPLICATE_TRANSACTION`, `SUSPICIOUS_LOCATION`, `UNUSUAL_TIME`

**使用範例**:
```bash
curl http://localhost:8080/api/alerts/type/HIGH_AMOUNT
```

---

### 6. 查詢最近的警示

**GET** `/api/alerts/recent?hours={hours}`

取得指定時間範圍內的最近警示記錄。

**查詢參數**:
- `hours` (int, optional, default=24): 查詢最近 N 小時的警示

**使用範例**:
```bash
# 查詢最近 24 小時的警示
curl http://localhost:8080/api/alerts/recent

# 查詢最近 6 小時的警示
curl http://localhost:8080/api/alerts/recent?hours=6
```

**回應範例**:
```json
[
  {
    "alertId": 10,
    "alertType": "DUPLICATE_TRANSACTION",
    "severity": "HIGH",
    "detectedAt": "2026-06-16T06:19:23",
    "description": "確認重複交易：2分鐘內相同商店相同金額"
  }
]
```

---

### 7. 取得警示統計

**GET** `/api/alerts/statistics`

取得警示的統計資料，包含總警示數、各嚴重程度數量等。

**使用範例**:
```bash
curl http://localhost:8080/api/alerts/statistics
```

**回應範例**:
```json
{
  "totalAlerts": 12,
  "criticalCount": 3,
  "highCount": 4,
  "mediumCount": 4,
  "lowCount": 1
}
```

---

## 資料模型

### TransactionAlert

| 欄位 | 類型 | 說明 |
|------|------|------|
| alertId | Long | 警示 ID（主鍵） |
| transaction | Transaction | 關聯的交易 |
| alertType | AlertType | 警示類型 |
| severity | AlertSeverity | 嚴重程度 |
| detectedAt | LocalDateTime | 偵測時間 |
| description | String | 警示描述 |
| createdAt | LocalDateTime | 建立時間 |

### AlertType (警示類型)

| 值 | 說明 |
|----|------|
| HIGH_AMOUNT | 高額交易 |
| FREQUENT_TRANSACTION | 頻繁交易 |
| DUPLICATE_TRANSACTION | 重複交易 |
| SUSPICIOUS_LOCATION | 可疑地點 |
| UNUSUAL_TIME | 異常時間 |

### AlertSeverity (嚴重程度)

| 值 | 說明 |
|----|------|
| LOW | 低風險警示 |
| MEDIUM | 中風險警示 |
| HIGH | 高風險警示 |
| CRITICAL | 嚴重風險警示 |

---

## 使用範例

### 範例 1: 查詢所有高風險警示

```bash
curl http://localhost:8080/api/alerts/severity/HIGH
```

### 範例 2: 查詢特定交易的所有警示

```bash
curl http://localhost:8080/api/alerts/transaction/6
```

### 範例 3: 查詢最近 1 小時的警示

```bash
curl http://localhost:8080/api/alerts/recent?hours=1
```

### 範例 4: 查詢所有重複交易警示

```bash
curl http://localhost:8080/api/alerts/type/DUPLICATE_TRANSACTION
```

---

## 測試資料

系統預載了 12 筆測試警示資料，涵蓋：
- 2 筆高額交易警示（CRITICAL, HIGH）
- 6 筆頻繁交易警示（MEDIUM, HIGH, CRITICAL）
- 2 筆重複交易警示（MEDIUM, HIGH）
- 1 筆異常時間警示（LOW）
- 1 筆可疑地點警示（MEDIUM）

---

## 錯誤處理

### 常見錯誤碼

| 狀態碼 | 說明 |
|--------|------|
| 200 OK | 請求成功 |
| 404 Not Found | 找不到指定的資源 |
| 400 Bad Request | 請求參數錯誤 |
| 500 Internal Server Error | 伺服器內部錯誤 |

---

## 開發注意事項

1. **關聯查詢**: TransactionAlert 與 Transaction 使用 `@ManyToOne` 關聯，使用 `LAZY` 載入策略
2. **時間處理**: 所有時間欄位使用 `LocalDateTime`，自動設定為當前時間
3. **Enum 儲存**: AlertType 和 AlertSeverity 以字串形式儲存在資料庫
4. **CORS**: API 已啟用 CORS，允許跨域請求

---

## 相關 API

- [Transaction API](http://localhost:8080/swagger-ui.html) - 交易管理 API
- [Card API](http://localhost:8080/swagger-ui.html) - 信用卡管理 API
- [Merchant API](http://localhost:8080/swagger-ui.html) - 商店管理 API

---

Made with Bob