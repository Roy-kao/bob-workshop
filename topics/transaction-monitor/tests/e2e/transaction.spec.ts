import { test, expect } from '@playwright/test';
import { format } from 'date-fns';
import * as fs from 'fs';
import * as path from 'path';

/**
 * 交易列表功能測試
 */
test.describe('交易列表功能', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
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
    const count = await rows.count();
    expect(count).toBeGreaterThan(0);
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'transaction-list');
    
    // 確保目錄存在
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
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
    
    // 驗證表格存在
    const table = page.locator('[data-testid="transaction-table"]');
    await expect(table).toBeVisible();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'transaction-recent');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('應該顯示高額交易', async ({ page }) => {
    // 點擊「高額交易」按鈕
    await page.click('[data-testid="transaction-filter-high-amount"]');
    
    // 等待 3 秒
    await page.waitForTimeout(3000);
    
    // 驗證按鈕為啟用狀態
    const button = page.locator('[data-testid="transaction-filter-high-amount"]');
    await expect(button).toHaveClass(/active/);
    
    // 驗證表格存在
    const table = page.locator('[data-testid="transaction-table"]');
    await expect(table).toBeVisible();
    
    // 驗證表格中有資料或顯示「查無資料」
    const tbody = page.locator('[data-testid="transaction-table-body"]');
    const content = await tbody.textContent();
    expect(content).toBeTruthy();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'transaction-high-amount');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('交易表格應該包含所有必要欄位', async ({ page }) => {
    await page.click('[data-testid="transaction-filter-all"]');
    await page.waitForTimeout(3000);
    
    // 驗證表頭
    const headers = page.locator('[data-testid="transaction-table"] thead th');
    const headerTexts = await headers.allTextContents();
    
    expect(headerTexts).toContain('交易ID');
    expect(headerTexts).toContain('卡號');
    expect(headerTexts).toContain('持卡人');
    expect(headerTexts).toContain('商店');
    expect(headerTexts).toContain('金額');
    expect(headerTexts).toContain('狀態');
    expect(headerTexts).toContain('交易時間');
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'transaction-table-structure');
    
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