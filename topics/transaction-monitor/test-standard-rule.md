# Test Standard Rule

> 信用卡交易監控系統 - 測試標準規範

---

## 🚨 必須遵守

### 測試框架
- **必須使用 Playwright** 進行端對端測試
- 支援多瀏覽器測試 (Chromium, Firefox, WebKit)
- 使用 TypeScript 撰寫測試腳本

---

## 📋 測試規範

### 1. 測試屬性 (Test Attributes)

**所有前端元素必須加入 `data-testid` 屬性**

#### 命名規則
```
data-testid="[功能區域]-[元素類型]-[具體名稱]"
```

#### 範例

**統計卡片**:
```html
<div class="stat-value" id="totalTransactions" data-testid="stats-value-total-transactions">-</div>
<div class="stat-value" id="totalAmount" data-testid="stats-value-total-amount">-</div>
<div class="stat-value" id="approvedCount" data-testid="stats-value-approved-count">-</div>
<div class="stat-value" id="averageAmount" data-testid="stats-value-average-amount">-</div>
```

**交易查詢按鈕**:
```html
<button class="filter-btn active" onclick="loadAllTransactions()" 
        data-testid="transaction-filter-all">
  全部交易
</button>
<button class="filter-btn" onclick="loadRecentTransactions()" 
        data-testid="transaction-filter-recent">
  最近24小時
</button>
<button class="filter-btn" onclick="loadHighAmountTransactions()" 
        data-testid="transaction-filter-high-amount">
  高額交易 (>50,000)
</button>
```

**交易表格**:
```html
<table id="transactionsTable" data-testid="transaction-table">
  <tbody id="transactionsBody" data-testid="transaction-table-body">
  </tbody>
</table>
```

**警示查詢按鈕**:
```html
<button class="filter-btn active" onclick="loadAllAlerts()" 
        data-testid="alert-filter-all">
  全部警示
</button>
<button class="filter-btn" onclick="loadHighRiskAlerts()" 
        data-testid="alert-filter-high-risk">
  高風險警示
</button>
<button class="filter-btn" onclick="loadPendingAlerts()" 
        data-testid="alert-filter-pending">
  待處理警示
</button>
```

**警示表格**:
```html
<table id="alertsTable" data-testid="alert-table">
  <tbody id="alertsBody" data-testid="alert-table-body">
  </tbody>
</table>
```

---

### 2. 截圖規範

#### 截圖時機
- **每個 submit/action 後等待 3 秒**
- 確保資料載入完成
- 確保動畫效果完成

#### 檔案命名規則
```
目錄路徑/screenshot/功能名稱/timestamp.png
```

#### 範例
```
tests/screenshot/transaction-list/2026-06-16T14-30-45-123Z.png
tests/screenshot/alert-high-risk/2026-06-16T14-31-20-456Z.png
tests/screenshot/statistics-dashboard/2026-06-16T14-32-10-789Z.png
```

---

## 🧪 測試腳本範例

### 專案結構
```
tests/
├── e2e/
│   ├── transaction.spec.ts
│   ├── alert.spec.ts
│   └── statistics.spec.ts
├── screenshot/
│   ├── transaction-list/
│   ├── alert-high-risk/
│   └── statistics-dashboard/
└── playwright.config.ts
```

---

### 測試腳本範例

#### 1. 交易列表測試 (transaction.spec.ts)

```typescript
import { test, expect } from '@playwright/test';
import { format } from 'date-fns';

test.describe('交易列表功能', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8080');
  });

  test('應該顯示所有交易', async ({ page }) => {
    // 點擊「全部交易」按鈕
    await page.click('[data-testid="transaction-filter-all"]');
    
    // 等待 3 秒讓資料載入
    await page.waitForTimeout(3000);
    
    // 驗證表格存在
    const table = page.locator('[data-testid="transaction-table"]');
    await expect(table).toBeVisible();
    
    // 驗證至少有一筆資料
    const rows = page.locator('[data-testid="transaction-table-body"] tr');
    await expect(rows).not.toHaveCount(0);
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/transaction-list/${timestamp}.png`,
      fullPage: true 
    });
  });

  test('應該顯示最近24小時交易', async ({ page }) => {
    // 點擊「最近24小時」按鈕
    await page.click('[data-testid="transaction-filter-recent"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證按鈕為啟用狀態
    const button = page.locator('[data-testid="transaction-filter-recent"]');
    await expect(button).toHaveClass(/active/);
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/transaction-recent/${timestamp}.png`,
      fullPage: true 
    });
  });

  test('應該顯示高額交易', async ({ page }) => {
    // 點擊「高額交易」按鈕
    await page.click('[data-testid="transaction-filter-high-amount"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證表格中的金額都大於 50,000
    const amounts = page.locator('[data-testid="transaction-table-body"] .amount');
    const count = await amounts.count();
    
    for (let i = 0; i < count; i++) {
      const text = await amounts.nth(i).textContent();
      // 驗證金額格式和數值
      expect(text).toMatch(/NT\$/);
    }
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/transaction-high-amount/${timestamp}.png`,
      fullPage: true 
    });
  });
});
```

---

#### 2. 警示列表測試 (alert.spec.ts)

```typescript
import { test, expect } from '@playwright/test';
import { format } from 'date-fns';

test.describe('警示列表功能', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8080');
  });

  test('應該顯示所有警示', async ({ page }) => {
    // 點擊「全部警示」按鈕
    await page.click('[data-testid="alert-filter-all"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證表格存在
    const table = page.locator('[data-testid="alert-table"]');
    await expect(table).toBeVisible();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/alert-list/${timestamp}.png`,
      fullPage: true 
    });
  });

  test('應該顯示高風險警示', async ({ page }) => {
    // 點擊「高風險警示」按鈕
    await page.click('[data-testid="alert-filter-high-risk"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證所有風險等級徽章都是紅色 (高風險)
    const riskBadges = page.locator('[data-testid="alert-table-body"] .risk-badge');
    const count = await riskBadges.count();
    
    for (let i = 0; i < count; i++) {
      await expect(riskBadges.nth(i)).toHaveClass(/risk-high/);
    }
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/alert-high-risk/${timestamp}.png`,
      fullPage: true 
    });
  });

  test('應該顯示待處理警示', async ({ page }) => {
    // 點擊「待處理警示」按鈕
    await page.click('[data-testid="alert-filter-pending"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證所有狀態都是「待處理」
    const statusBadges = page.locator('[data-testid="alert-table-body"] .status-badge-table');
    const count = await statusBadges.count();
    
    for (let i = 0; i < count; i++) {
      const text = await statusBadges.nth(i).textContent();
      expect(text).toContain('待處理');
    }
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/alert-pending/${timestamp}.png`,
      fullPage: true 
    });
  });

  test('應該正確顯示風險等級顏色', async ({ page }) => {
    // 載入所有警示
    await page.click('[data-testid="alert-filter-all"]');
    await page.waitForTimeout(3000);
    
    // 驗證高風險 - 紅色
    const highRisk = page.locator('.risk-high').first();
    if (await highRisk.count() > 0) {
      await expect(highRisk).toHaveCSS('color', 'rgb(204, 0, 0)');
    }
    
    // 驗證中風險 - 橘色
    const mediumRisk = page.locator('.risk-medium').first();
    if (await mediumRisk.count() > 0) {
      await expect(mediumRisk).toHaveCSS('color', 'rgb(204, 112, 0)');
    }
    
    // 驗證低風險 - 黃色
    const lowRisk = page.locator('.risk-low').first();
    if (await lowRisk.count() > 0) {
      await expect(lowRisk).toHaveCSS('color', 'rgb(204, 154, 0)');
    }
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/alert-risk-colors/${timestamp}.png`,
      fullPage: true 
    });
  });
});
```

---

#### 3. 統計資料測試 (statistics.spec.ts)

```typescript
import { test, expect } from '@playwright/test';
import { format } from 'date-fns';

test.describe('統計資料功能', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8080');
  });

  test('應該顯示統計卡片資料', async ({ page }) => {
    // 等待 3 秒讓統計資料載入
    await page.waitForTimeout(3000);
    
    // 驗證總交易數
    const totalTransactions = page.locator('[data-testid="stats-value-total-transactions"]');
    await expect(totalTransactions).not.toHaveText('-');
    
    // 驗證總交易金額
    const totalAmount = page.locator('[data-testid="stats-value-total-amount"]');
    await expect(totalAmount).not.toHaveText('-');
    
    // 驗證已核准交易
    const approvedCount = page.locator('[data-testid="stats-value-approved-count"]');
    await expect(approvedCount).not.toHaveText('-');
    
    // 驗證平均交易金額
    const averageAmount = page.locator('[data-testid="stats-value-average-amount"]');
    await expect(averageAmount).not.toHaveText('-');
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/statistics-dashboard/${timestamp}.png`,
      fullPage: true 
    });
  });

  test('統計資料應該是數字格式', async ({ page }) => {
    await page.waitForTimeout(3000);
    
    // 驗證總交易數是數字
    const totalTransactions = await page.locator('[data-testid="stats-value-total-transactions"]').textContent();
    expect(totalTransactions).toMatch(/^\d+$/);
    
    // 驗證總交易金額包含貨幣符號
    const totalAmount = await page.locator('[data-testid="stats-value-total-amount"]').textContent();
    expect(totalAmount).toMatch(/NT\$/);
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    await page.screenshot({ 
      path: `tests/screenshot/statistics-format/${timestamp}.png`,
      fullPage: true 
    });
  });
});
```

---

## 📦 Playwright 配置

### playwright.config.ts

```typescript
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests/e2e',
  
  // 測試超時設定
  timeout: 30000,
  
  // 重試次數
  retries: 2,
  
  // 並行執行
  workers: 3,
  
  // 報告設定
  reporter: [
    ['html', { outputFolder: 'test-results/html' }],
    ['json', { outputFile: 'test-results/results.json' }],
    ['junit', { outputFile: 'test-results/junit.xml' }]
  ],
  
  use: {
    // 基礎 URL
    baseURL: 'http://localhost:8080',
    
    // 截圖設定
    screenshot: 'only-on-failure',
    
    // 影片錄製
    video: 'retain-on-failure',
    
    // 追蹤設定
    trace: 'on-first-retry',
  },

  // 瀏覽器配置
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],

  // 開發伺服器設定
  webServer: {
    command: 'mvn spring-boot:run',
    url: 'http://localhost:8080',
    reuseExistingServer: !process.env.CI,
    timeout: 120000,
  },
});
```

---

## 📦 安裝與執行

### 安裝 Playwright

```bash
# 安裝 Playwright
npm init playwright@latest

# 安裝瀏覽器
npx playwright install
```

### 執行測試

```bash
# 執行所有測試
npx playwright test

# 執行特定測試檔案
npx playwright test transaction.spec.ts

# 執行特定瀏覽器
npx playwright test --project=chromium

# 顯示測試報告
npx playwright show-report

# Debug 模式
npx playwright test --debug
```

---

## ✅ 測試檢查清單

開發新功能時,必須確認:

- [ ] 所有互動元素都有 `data-testid` 屬性
- [ ] 測試腳本使用 Playwright
- [ ] 每個 action 後等待 3 秒
- [ ] 截圖檔案命名符合規範
- [ ] 截圖儲存在正確的目錄
- [ ] 測試涵蓋所有主要功能
- [ ] 測試通過率 > 95%

---

## 📚 參考資源

- [Playwright 官方文件](https://playwright.dev/)
- [Playwright Best Practices](https://playwright.dev/docs/best-practices)
- [Test Attributes Guide](https://playwright.dev/docs/locators#locate-by-test-id)

---

**最後更新**: 2026-06-16  
**版本**: 1.0.0

// Made with Bob