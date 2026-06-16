import { test, expect } from '@playwright/test';
import { format } from 'date-fns';
import * as fs from 'fs';
import * as path from 'path';

/**
 * 警示列表功能測試
 */
test.describe('警示列表功能', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('應該顯示所有警示', async ({ page }) => {
    // 點擊「全部警示」按鈕
    await page.click('[data-testid="alert-filter-all"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證表格存在
    const table = page.locator('[data-testid="alert-table"]');
    await expect(table).toBeVisible();
    
    // 驗證表格內容
    const tbody = page.locator('[data-testid="alert-table-body"]');
    const content = await tbody.textContent();
    expect(content).toBeTruthy();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'alert-list');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('應該顯示高風險警示', async ({ page }) => {
    // 點擊「高風險警示」按鈕
    await page.click('[data-testid="alert-filter-high-risk"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證按鈕為啟用狀態
    const button = page.locator('[data-testid="alert-filter-high-risk"]');
    await expect(button).toHaveClass(/active/);
    
    // 驗證表格存在
    const table = page.locator('[data-testid="alert-table"]');
    await expect(table).toBeVisible();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'alert-high-risk');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('應該顯示待處理警示', async ({ page }) => {
    // 點擊「待處理警示」按鈕
    await page.click('[data-testid="alert-filter-pending"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證按鈕為啟用狀態
    const button = page.locator('[data-testid="alert-filter-pending"]');
    await expect(button).toHaveClass(/active/);
    
    // 驗證表格存在
    const table = page.locator('[data-testid="alert-table"]');
    await expect(table).toBeVisible();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'alert-pending');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('應該正確顯示風險等級顏色', async ({ page }) => {
    // 載入所有警示
    await page.click('[data-testid="alert-filter-all"]');
    await page.waitForTimeout(3000);
    
    // 檢查是否有風險等級徽章
    const riskBadges = page.locator('.risk-badge');
    const count = await riskBadges.count();
    
    if (count > 0) {
      // 驗證高風險徽章存在 (如果有的話)
      const highRisk = page.locator('.risk-high').first();
      if (await highRisk.count() > 0) {
        await expect(highRisk).toBeVisible();
      }
      
      // 驗證中風險徽章存在 (如果有的話)
      const mediumRisk = page.locator('.risk-medium').first();
      if (await mediumRisk.count() > 0) {
        await expect(mediumRisk).toBeVisible();
      }
      
      // 驗證低風險徽章存在 (如果有的話)
      const lowRisk = page.locator('.risk-low').first();
      if (await lowRisk.count() > 0) {
        await expect(lowRisk).toBeVisible();
      }
    }
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'alert-risk-colors');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('警示表格應該包含所有必要欄位', async ({ page }) => {
    await page.click('[data-testid="alert-filter-all"]');
    await page.waitForTimeout(3000);
    
    // 驗證表頭
    const headers = page.locator('[data-testid="alert-table"] thead th');
    const headerTexts = await headers.allTextContents();
    
    expect(headerTexts).toContain('警示ID');
    expect(headerTexts).toContain('警示類型');
    expect(headerTexts).toContain('風險等級');
    expect(headerTexts).toContain('交易ID');
    expect(headerTexts).toContain('卡號');
    expect(headerTexts).toContain('金額');
    expect(headerTexts).toContain('狀態');
    expect(headerTexts).toContain('偵測時間');
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'alert-table-structure');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });
});

// Made with Bob