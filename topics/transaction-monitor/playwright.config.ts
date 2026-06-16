import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright 測試配置
 * 信用卡交易監控系統
 */
export default defineConfig({
  testDir: './tests/e2e',
  
  // 測試超時設定 (30秒)
  timeout: 30000,
  
  // 失敗重試次數
  retries: process.env.CI ? 2 : 0,
  
  // 並行執行的 worker 數量
  workers: process.env.CI ? 1 : 3,
  
  // 報告設定
  reporter: [
    ['html', { outputFolder: 'test-results/html' }],
    ['json', { outputFile: 'test-results/results.json' }],
    ['junit', { outputFile: 'test-results/junit.xml' }],
    ['list']
  ],
  
  use: {
    // 基礎 URL
    baseURL: 'http://localhost:8080',
    
    // 截圖設定 - 失敗時截圖
    screenshot: 'only-on-failure',
    
    // 影片錄製 - 失敗時保留
    video: 'retain-on-failure',
    
    // 追蹤設定 - 第一次重試時啟用
    trace: 'on-first-retry',
    
    // 視窗大小
    viewport: { width: 1920, height: 1080 },
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

// Made with Bob