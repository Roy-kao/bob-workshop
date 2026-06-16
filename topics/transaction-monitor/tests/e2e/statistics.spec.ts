import { test, expect } from '@playwright/test';
import { format } from 'date-fns';
import * as fs from 'fs';
import * as path from 'path';

/**
 * 統計資料功能測試
 */
test.describe('統計資料功能', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('應該顯示統計卡片資料', async ({ page }) => {
    // 等待 3 秒讓統計資料載入
    await page.waitForTimeout(3000);
    
    // 驗證總交易數
    const totalTransactions = page.locator('[data-testid="stats-value-total-transactions"]');
    await expect(totalTransactions).toBeVisible();
    await expect(totalTransactions).not.toHaveText('-');
    
    // 驗證總交易金額
    const totalAmount = page.locator('[data-testid="stats-value-total-amount"]');
    await expect(totalAmount).toBeVisible();
    await expect(totalAmount).not.toHaveText('-');
    
    // 驗證已核准交易
    const approvedCount = page.locator('[data-testid="stats-value-approved-count"]');
    await expect(approvedCount).toBeVisible();
    await expect(approvedCount).not.toHaveText('-');
    
    // 驗證平均交易金額
    const averageAmount = page.locator('[data-testid="stats-value-average-amount"]');
    await expect(averageAmount).toBeVisible();
    await expect(averageAmount).not.toHaveText('-');
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'statistics-dashboard');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('統計資料應該是正確格式', async ({ page }) => {
    await page.waitForTimeout(3000);
    
    // 驗證總交易數是數字
    const totalTransactions = await page.locator('[data-testid="stats-value-total-transactions"]').textContent();
    expect(totalTransactions).toMatch(/^\d+$/);
    
    // 驗證總交易金額包含貨幣符號
    const totalAmount = await page.locator('[data-testid="stats-value-total-amount"]').textContent();
    expect(totalAmount).toMatch(/NT\$/);
    
    // 驗證已核准交易是數字
    const approvedCount = await page.locator('[data-testid="stats-value-approved-count"]').textContent();
    expect(approvedCount).toMatch(/^\d+$/);
    
    // 驗證平均交易金額包含貨幣符號
    const averageAmount = await page.locator('[data-testid="stats-value-average-amount"]').textContent();
    expect(averageAmount).toMatch(/NT\$/);
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'statistics-format');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('統計卡片應該有正確的圖示', async ({ page }) => {
    await page.waitForTimeout(3000);
    
    // 驗證統計卡片存在
    const statCards = page.locator('.stat-card');
    const count = await statCards.count();
    expect(count).toBe(4);
    
    // 驗證每個卡片都有圖示
    for (let i = 0; i < count; i++) {
      const icon = statCards.nth(i).locator('.stat-icon');
      await expect(icon).toBeVisible();
    }
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'statistics-cards');
    
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    
    await page.screenshot({ 
      path: path.join(screenshotDir, `${timestamp}.png`),
      fullPage: true 
    });
  });

  test('系統狀態應該顯示為運行中', async ({ page }) => {
    await page.waitForTimeout(3000);
    
    // 驗證系統狀態徽章
    const statusBadge = page.locator('#systemStatus');
    await expect(statusBadge).toBeVisible();
    await expect(statusBadge).toContainText('系統運行中');
    
    // 驗證狀態指示燈存在
    const statusDot = page.locator('.status-dot');
    await expect(statusDot).toBeVisible();
    
    // 截圖
    const timestamp = format(new Date(), "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'");
    const screenshotDir = path.join('tests', 'screenshot', 'system-status');
    
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