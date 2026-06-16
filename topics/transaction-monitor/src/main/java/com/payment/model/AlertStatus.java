package com.payment.model;

/**
 * 警示狀態列舉
 */
public enum AlertStatus {
    /**
     * 待處理
     */
    PENDING,
    
    /**
     * 調查中
     */
    INVESTIGATING,
    
    /**
     * 已解決
     */
    RESOLVED,
    
    /**
     * 誤報
     */
    FALSE_POSITIVE
}

// Made with Bob