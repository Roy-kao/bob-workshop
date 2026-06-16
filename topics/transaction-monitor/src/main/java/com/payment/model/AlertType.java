package com.payment.model;

/**
 * 警示類型
 */
public enum AlertType {
    HIGH_AMOUNT,           // 高額交易
    FREQUENT_TRANSACTION,  // 頻繁交易
    DUPLICATE_TRANSACTION, // 重複交易
    SUSPICIOUS_LOCATION,   // 可疑地點
    UNUSUAL_TIME          // 異常時間
}

// Made with Bob