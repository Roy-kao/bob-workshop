# 信用卡交易監控系統

> **用途**：IBM Bob Workshop 實戰演練
> **難度**：⭐⭐⭐ 中等

## 📋 專案簡介

這是一個信用卡交易監控系統的 Sample Application，展示了基本的交易查詢功能。在 Workshop 中，學員將使用 **IBM Bob** 新增交易監控與警示功能。

## 🎯 Workshop 目標

完成本 Workshop 後，你將能夠：
- ✅ 使用 Bob 從現有程式碼快速新增功能
- ✅ 建立自訂 Slash Command 提升開發效率
- ✅ 撰寫專案 Rules 確保程式碼品質
- ✅ 建立 AGENTS.md 指導 AI 助手
- ✅ 建立 SKILL.md 記錄技術知識
- ✅ 實作金融系統的安全最佳實踐

## 🚀 快速開始

### 系統需求

- Java 17 或以上
- Maven 3.6 或以上
- IBM Bob IDE

## 🔧 技術棧

- **框架**: Spring Boot 3.2.0
- **資料庫**: H2 (記憶體資料庫)
- **ORM**: JPA/Hibernate
- **前端**: HTML + CSS + JavaScript
- **字型**: IBM Plex Sans & IBM Plex Mono

### 啟動應用程式

```bash
# 使用 Maven Wrapper
./mvnw spring-boot:run

# 或使用已安裝的 Maven
mvn spring-boot:run
```

應用程式將在 `http://localhost:8080` 啟動。

### 存取介面

- **Web UI**: http://localhost:8080
- **H2 資料庫控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:payment_db`
  - Username: `sa`
  - Password: (留空)
- **API 端點**: http://localhost:8080/api/transactions

## 📊 現有功能

### API 端點

| 方法 | 端點 | 說明 |
|------|------|------|
| GET | `/api/transactions` | 查詢所有交易 |
| GET | `/api/transactions/{id}` | 查詢單筆交易 |
| GET | `/api/transactions/card/{cardId}` | 查詢指定卡片的交易 |
| GET | `/api/transactions/recent?hours=24` | 查詢最近的交易 |
| GET | `/api/transactions/high-amount?threshold=50000` | 查詢高額交易 |
| GET | `/api/transactions/statistics` | 取得交易統計 |

### 測試資料

系統已預載以下測試資料：
- 5 張信用卡
- 8 家特約商店
- 20 筆交易（包含異常交易範例）

## 📁 專案結構

```
payment-transaction-monitor/
├── src/main/java/com/payment/
│   ├── TransactionMonitorApplication.java  # 主應用程式
│   ├── config/                             # 配置層
│   │   └── OpenApiConfig.java              # ✅ Swagger API 文件配置
│   ├── model/                              # 資料模型
│   │   ├── Card.java                       # ✅ 信用卡實體
│   │   ├── CardStatus.java                 # ✅ 信用卡狀態列舉
│   │   ├── Merchant.java                   # ✅ 商店實體
│   │   ├── MerchantCategory.java           # ✅ 商店類別列舉
│   │   ├── Transaction.java                # ✅ 交易實體
│   │   ├── TransactionStatus.java          # ✅ 交易狀態列舉
│   │   └── TransactionAlert.java           # ❌ 待實作
│   ├── repository/                         # 資料存取層
│   │   ├── CardRepository.java             # ✅ 已提供
│   │   ├── MerchantRepository.java         # ✅ 已提供
│   │   ├── TransactionRepository.java      # ✅ 已提供
│   │   └── AlertRepository.java            # ❌ 待實作
│   ├── service/                            # 業務邏輯層
│   │   ├── TransactionService.java         # ✅ 已提供
│   │   └── AlertDetectionService.java      # ❌ 待實作
│   └── controller/                         # API 控制層
│       ├── TransactionController.java      # ✅ 已提供
│       └── AlertController.java            # ❌ 待實作
├── src/main/resources/
│   ├── application.yml                     # ✅ 應用程式配置
│   ├── data.sql                            # ✅ 測試資料
│   └── static/                             # 前端資源
│       ├── index.html                      # ✅ Web UI
│       ├── styles.css                      # ✅ 樣式
│       └── app.js                          # ✅ JavaScript
└── .bob/                                   # ✅ Bob 配置目錄
    ├── slash-commands/                     # ✅ Slash Commands
    │   └── analyze-security.md             # ✅ 安全檢查指令
    ├── rules/                              # ❌ 待建立
    │   ├── coding-standards.md             # ❌ 待建立
    │   └── security-rules.md               # ❌ 待建立
    ├── rules-code/                         # ❌ 待建立（Code Mode 專用規則）
    │   └── AGENTS.md                       # ❌ 待建立
    └── skills/                             # ❌ 待建立
        └── frontend-slides/                # ❌ 待建立（簡報製作 SKILL 範例）
            └── SKILL.md                    # ❌ 待建立
```

---

## 🎯 Workshop 任務

### 👥 團隊分工建議（3+1 模式）

本 Workshop 採用 **3 人開發 + 1 人簡報** 的協作模式，充分展示 Bob 在程式開發與文件製作的多元應用。

---

### **開發組（3 人）**

#### **👤 Person A：基礎建設（難度 ⭐⭐）**
**任務：TransactionAlert Entity + Repository + 基礎 API**

**具體工作：**
1. 建立 `TransactionAlert.java` Entity
   - 參考 `Transaction.java` 結構
   - 欄位：alertId, transactionId, alertType, severity, detectedAt, description
2. 建立 `AlertRepository.java` 介面
3. 建立 `AlertController.java` 基礎 CRUD API
   - GET `/api/alerts` - 查詢所有警示
   - GET `/api/alerts/{id}` - 查詢單筆警示
   - GET `/api/alerts/transaction/{transactionId}` - 查詢交易的警示

**Bob 使用重點：**
```
使用 Bob Ask Mode 分析 Transaction.java 結構
使用 Bob Code Mode 產生 Entity 與 Repository
```

**時間：25 分鐘**

---

#### **👤 Person B：核心邏輯（難度 ⭐⭐⭐）**
**任務：AlertDetectionService - 3 種偵測規則**

**具體工作：**
1. 建立 `AlertDetectionService.java`
2. 實作 3 種偵測規則：
   - **高額交易**：單筆超過 50,000 元
   - **頻繁交易**：1 小時內超過 5 筆（需查詢 TransactionRepository）
   - **重複交易**：5 分鐘內相同金額相同商店（複雜查詢）
3. 整合 TransactionRepository 與 AlertRepository
4. 實作自動偵測邏輯（新交易觸發檢查）

**Bob 使用重點：**
```
使用 Bob 實作複雜查詢邏輯
使用 Bob 優化效能（避免 N+1 查詢）
使用 Bob 產生單元測試
```

**時間：30 分鐘**

---

#### **👤 Person C：整合與進階功能（難度 ⭐⭐⭐⭐）**
**任務：前端整合 + Bob 進階功能（最複雜）**

**具體工作：**

**Phase 1: 前端整合（30-70分鐘）**
1. 在 `index.html` 加入警示列表區塊
2. 在 `app.js` 加入 API 呼叫功能
3. 在 `styles.css` 加入警示樣式（高/中/低風險）

**Phase 2: Bob 進階功能（70-100分鐘）**

**2.1 建立 AGENTS.md（範例）**
```markdown
# 專案特性
- H2 資料庫名稱：payment_db
- 包名：com.payment
- Constructor Injection Only（無 @Autowired）

# 非顯而易見的決策
- TransactionAlert 使用 alertType 字串（非 enum）- 因為規則會擴充
- AlertDetectionService 不自動觸發 - 需手動呼叫（效能考量）
- 前端使用原生 JS（非 React）- 簡化 Workshop 複雜度
```

**2.2 建立 coding-standards.md（範例）**
```markdown
# 必須遵守
- Constructor Injection（參考 TransactionService.java）
- Lombok @RequiredArgsConstructor（所有 Service）
- BigDecimal 處理金額（參考 Transaction.java）
```

**2.3 建立 security-rules.md（範例）**
```markdown
# 必須檢查
1. 卡號遮罩：只顯示後 4 碼
2. SQL Injection：使用 JPA Query Methods
3. 金額精度：BigDecimal.setScale(2, HALF_UP)
```

**2.4 建立 Slash Command: /analyze-security（範例）**
```markdown
# 功能
檢查程式碼是否符合金融系統安全規範

# 檢查項目
1. 搜尋 "cardNumber" 確認有 mask
2. 搜尋 "float" 或 "double" 處理金額
```

**提示**：以上僅為範例，學員需根據實際開發經驗補充完整內容。

**Bob 使用重點：**
```
使用 Bob 產生前端程式碼
使用 Bob 建立 Slash Command
使用 Bob 撰寫 Rules 與 AGENTS.md
```

**時間：30 分鐘**

---

### **🔄 Bob 進階功能建立策略：先實作後建立**

#### **為什麼採用「先實作後建立」？**

本 Workshop 刻意採用 **先開發程式碼，再建立 Rules/AGENTS.md/SKILL.md** 的順序，原因如下：

**📚 教學目的：**
- 讓學員體驗「發現問題 → 制定規則」的真實開發流程
- 理解 Rules 的價值來自實際痛點，而非憑空想像
- 學習如何從程式碼中提煉最佳實踐

**🎯 實務模擬：**
- 模擬新專案初期的探索階段
- 團隊需要先累積經驗，才能制定合理規範
- 避免過早制定不切實際的規則

#### **建立時機與分工**

**Phase 1-2 (0-70分鐘)：自由開發階段**
```
Person A/B：專注實作功能
Person C：觀察開發過程，記錄以下內容：
  - 常見的程式碼模式
  - 遇到的技術問題
  - 安全性考量
  - 最佳實踐
```

**Phase 3 (70-100分鐘)：統一建立 Bob 進階功能**
```
Person C 根據實作經驗建立：

1. AGENTS.md (70-80分鐘)
   - 詢問 Person A：Entity 設計的非顯而易見決策
   - 詢問 Person B：查詢邏輯的注意事項
   - 記錄專案特性（H2 資料庫名稱、包名等）

2. Rules (80-90分鐘)
   - coding-standards.md：從實際程式碼提取編碼風格
   - security-rules.md：從 Person B 的安全考量整理規則
   - 使用 Bob 分析現有程式碼產生規則範例

3. SKILL.md + Slash Command (90-100分鐘)
   - 整理全專案的技術知識與架構決策
   - 建立 /analyze-security 指令
```

**Phase 4 (100分鐘)：全員 Review**
```
關鍵同步點：確保 Rules 反映實際開發經驗
- Person A/B/C 一起檢視內容
- 確認規則符合實際做法
- 調整不合理的規範
```

#### **內容來源對照表**

| Bob 功能 | 內容來源 | 範例 |
|---------|---------|------|
| **AGENTS.md** | Person A 的 Entity 設計 | H2 資料庫名稱、包名結構 |
| | Person B 的查詢邏輯 | Repository 查詢方法命名規則 |
| | Person C 的整合經驗 | 前後端 API 對接注意事項 |
| **coding-standards.md** | 實際程式碼風格 | 從 Transaction.java 提取範例 |
| | Constructor Injection | 從 Service 類別提取模式 |
| **security-rules.md** | Person B 的安全實作 | BigDecimal 使用、卡號遮罩 |
| | AlertDetectionService | SQL Injection 防護範例 |
| **SKILL.md** | 全員技術決策 | 交易監控系統架構知識 |
| | 偵測規則設計 | 異常交易偵測演算法 |

#### **企業實務對比**

**Workshop 模式（本專案）：**
```
先實作 → 累積經驗 → 建立規則 → 應用規則
✅ 適合學習與探索
✅ 規則基於實際經驗
```

**企業專案模式：**
```
建立規則 → 開發時套用 → 持續優化
✅ 適合成熟專案
✅ 確保一致性
```

**混合策略（最佳實踐）：**
```
基礎 AGENTS.md（專案特性）→ 自由開發 → 補充 Rules（最佳實踐）
✅ 兼顧效率與品質
```

---

### **📊 簡報組（1 人）**

#### **👤 Person D：技術簡報製作**
**任務：使用 Bob + Frontend-slides SKILL 製作成果簡報**

**簡報大綱（8-10 slides）：**

1. **封面** - 信用卡交易監控系統開發成果
2. **專案背景** - 客戶需求與技術挑戰
3. **系統架構** - 現有系統 + 新增模組架構圖
4. **開發成果 - Entity 設計** - TransactionAlert 資料模型
5. **開發成果 - 偵測規則** - 3 種規則實作邏輯
6. **開發成果 - API 端點** - REST API 設計與 Swagger UI
7. **Bob 使用技巧** - Ask/Code Mode、Slash Commands、Rules
8. **安全最佳實踐** - 卡號遮罩、SQL Injection 防護
9. **Demo 展示** - 系統操作截圖與警示觸發範例
10. **總結與心得** - 開發效率提升與學習收穫

**Bob 使用方式：**
```
使用 Frontend-slides SKILL 製作技術簡報，主題：信用卡交易監控系統開發成果

要求：
- IBM 企業風格設計
- 包含程式碼範例與架構圖
- 加入 Demo 截圖
- 專業技術呈現
- 10 slides 以內
```

**時間分配：**
- 0-20分鐘：與開發組討論大綱
- 20-60分鐘：使用 Bob 產生簡報初稿
- 60-100分鐘：根據開發進度更新內容、加入截圖
- 100-120分鐘：最終潤飾與排練

---

### 🎯 依賴關係

```
Person A (Entity + Repository)
    ↓
Person B (Service - 偵測規則) ← 依賴 Person A
    ↓
Person C (Controller + 前端) ← 依賴 Person A & B
    ↓
Person D (簡報) ← 依賴全員成果
```

---

### 💡 協作建議

**關鍵同步點：**
1. **30分鐘**：Person A 完成後，Person B/C 開始整合
2. **70分鐘**：開發組提供截圖給 Person D
3. **100分鐘**：全員 Review 簡報內容

**避免衝突：**
- Person A/B/C 各自負責不同檔案
- 使用 Git 分支開發（可選）
- 定期同步進度

**彈性調整：**
- 如果 Person B 提前完成，協助 Person C 前端開發
- 如果 Person A 提前完成，協助撰寫測試或文件

---

## 💡 開發提示

### 使用 Bob 的最佳實踐

1. **分析現有程式碼**
   ```
   請分析 Transaction.java 的結構，我需要建立類似的 TransactionAlert Entity
   ```

2. **產生程式碼**
   ```
   根據設計文件，幫我建立 AlertDetectionService，實作高額交易偵測規則
   ```

3. **安全檢查**
   ```
   /analyze-security src/main/java/com/payment/controller/AlertController.java
   ```

### 金融系統開發重點

- ✅ 使用 `BigDecimal` 處理金額
- ✅ 卡號必須遮罩顯示
- ✅ 使用 Prepared Statement 防止 SQL Injection
- ✅ 記錄審計日誌
- ✅ 適當的錯誤處理

## 🔧 Slash Commands 使用指南

### 什麼是 Slash Command?

Slash Command 是 IBM Bob 的自訂指令功能,讓你可以建立專案特定的快捷指令。

### 可用指令

#### `/analyze-security` - 安全檢查

執行完整的安全規範檢查:

```
/analyze-security
```

**檢查項目**:
- ✅ 卡號遮罩實作
- ✅ BigDecimal 金額處理
- ✅ SQL 注入防護
- ✅ 日誌安全

**使用範例**:
```
# 檢查整個專案
/analyze-security

# 檢查特定檔案
/analyze-security src/main/java/com/payment/model/Card.java

# 檢查特定目錄
/analyze-security src/main/java/com/payment/controller
```

#### `/test-runner` - 測試執行與錯誤分析

執行完整的端對端測試並產生錯誤分析報告:

```
/test-runner
```

**測試套件**:
- ✅ 交易測試 (transaction.spec.ts)
- ✅ 警示測試 (alert.spec.ts)
- ✅ 統計測試 (statistics.spec.ts)

**分析項目**:
- 🔍 失敗測試統計
- 🔍 錯誤類型分類
- 🔍 截圖與影片證據
- 🔍 修正建議

**使用範例**:
```
# 執行完整測試
/test-runner

# 執行特定測試套件
/test-runner transaction
/test-runner alert

# 除錯模式
/test-runner --debug
```

### 建立自己的 Slash Command

詳細指南請參考: [slash-commands-guide.md](slash-commands-guide.md)

**快速開始**:
1. 在 `.bob/slash-commands/` 建立 Markdown 檔案
2. 定義指令的功能和執行步驟
3. 在 Bob 中使用 `/指令名稱` 執行

**範例結構**:
```markdown
# /my-command

> 指令描述

## 功能說明
說明這個指令的用途

## 執行步驟
1. 步驟一
2. 步驟二

## 使用範例
/my-command [參數]
```

## 📚 參考資源

- [Spring Boot 文件](https://spring.io/projects/spring-boot)
- [IBM Bob 官方文件](https://bob.ibm.com/docs)
- [Workshop 設計文件](../design-docs/)

## 🆘 常見問題

### Q: 如何重新載入測試資料？

A: 重新啟動應用程式即可，H2 資料庫會自動重建。

### Q: 如何查看資料庫內容？

A: 存取 http://localhost:8080/h2-console

### Q: API 回傳 JSON 循環參考錯誤？

A: 在 Entity 的關聯欄位上加上 `@JsonIgnore` 或使用 DTO。

## 📝 授權

本專案僅供 IBM Bob Workshop 教學使用。

---

**準備好開始挑戰了嗎？啟動應用程式，開啟 Bob，讓我們開始吧！** 🚀