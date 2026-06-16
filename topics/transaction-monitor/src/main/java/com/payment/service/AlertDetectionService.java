package com.payment.service;

import com.payment.model.*;
import com.payment.repository.AlertRepository;
import com.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 警示偵測服務
 * 負責偵測異常交易並產生警示
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlertDetectionService {
    
    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    
    // 偵測閾值常數
    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("50000");
    private static final int FREQUENT_TRANSACTION_HOURS = 1;
    private static final int FREQUENT_TRANSACTION_COUNT = 5;
    private static final int DUPLICATE_TRANSACTION_MINUTES = 5;
    
    /**
     * 對新交易執行所有偵測規則
     * 這是主要的入口方法，會自動執行所有偵測邏輯
     * 
     * @param transaction 要檢查的交易
     * @return 偵測到的警示列表
     */
    public List<TransactionAlert> detectAllAlerts(Transaction transaction) {
        log.info("開始偵測交易警示: 交易ID={}, 金額={}", 
            transaction.getId(), transaction.getAmount());
        
        List<TransactionAlert> alerts = new ArrayList<>();
        
        // 執行三種偵測規則
        TransactionAlert highAmountAlert = detectHighAmountTransaction(transaction);
        if (highAmountAlert != null) {
            alerts.add(highAmountAlert);
        }
        
        TransactionAlert frequentAlert = detectFrequentTransactions(transaction);
        if (frequentAlert != null) {
            alerts.add(frequentAlert);
        }
        
        TransactionAlert duplicateAlert = detectDuplicateTransaction(transaction);
        if (duplicateAlert != null) {
            alerts.add(duplicateAlert);
        }
        
        log.info("偵測完成: 交易ID={}, 發現 {} 個警示", 
            transaction.getId(), alerts.size());
        
        return alerts;
    }
    
    /**
     * 規則 1: 偵測高額交易
     * 單筆交易金額超過 50,000 元
     * 
     * @param transaction 要檢查的交易
     * @return 如果偵測到異常則返回警示，否則返回 null
     */
    public TransactionAlert detectHighAmountTransaction(Transaction transaction) {
        if (transaction.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            log.warn("偵測到高額交易: 交易ID={}, 金額={}", 
                transaction.getId(), transaction.getAmount());
            
            TransactionAlert alert = new TransactionAlert();
            alert.setTransaction(transaction);
            alert.setAlertType(AlertType.HIGH_AMOUNT);
            alert.setSeverity(determineSeverityByAmount(transaction.getAmount()));
            alert.setDescription(String.format(
                "高額交易警示: 交易金額 %s 元超過閾值 %s 元",
                transaction.getAmount(), HIGH_AMOUNT_THRESHOLD
            ));
            alert.setDetectedAt(LocalDateTime.now());
            
            return alertRepository.save(alert);
        }
        
        return null;
    }
    
    /**
     * 規則 2: 偵測頻繁交易
     * 同一張卡在 1 小時內超過 5 筆交易
     * 使用優化的查詢避免 N+1 問題
     * 
     * @param transaction 要檢查的交易
     * @return 如果偵測到異常則返回警示，否則返回 null
     */
    public TransactionAlert detectFrequentTransactions(Transaction transaction) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(FREQUENT_TRANSACTION_HOURS);
        
        // 使用單一查詢計算交易數量，避免 N+1 查詢問題
        long recentTransactionCount = transactionRepository
            .countByCardIdAndTransactionTimeAfter(
                transaction.getCard().getId(), 
                oneHourAgo
            );
        
        if (recentTransactionCount > FREQUENT_TRANSACTION_COUNT) {
            log.warn("偵測到頻繁交易: 卡片ID={}, {} 小時內交易 {} 筆", 
                transaction.getCard().getId(), 
                FREQUENT_TRANSACTION_HOURS, 
                recentTransactionCount);
            
            TransactionAlert alert = new TransactionAlert();
            alert.setTransaction(transaction);
            alert.setAlertType(AlertType.FREQUENT_TRANSACTION);
            alert.setSeverity(determineSeverityByFrequency(recentTransactionCount));
            alert.setDescription(String.format(
                "頻繁交易警示: 卡片 %s 在 %d 小時內已有 %d 筆交易（閾值: %d 筆）",
                transaction.getCard().getMaskedCardNumber(),
                FREQUENT_TRANSACTION_HOURS,
                recentTransactionCount,
                FREQUENT_TRANSACTION_COUNT
            ));
            alert.setDetectedAt(LocalDateTime.now());
            
            return alertRepository.save(alert);
        }
        
        return null;
    }
    
    /**
     * 規則 3: 偵測重複交易
     * 5 分鐘內相同卡片、相同商店、相同金額的交易
     * 使用複雜查詢一次性取得所有可能的重複交易
     * 
     * @param transaction 要檢查的交易
     * @return 如果偵測到異常則返回警示，否則返回 null
     */
    public TransactionAlert detectDuplicateTransaction(Transaction transaction) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now()
            .minusMinutes(DUPLICATE_TRANSACTION_MINUTES);
        LocalDateTime now = LocalDateTime.now();
        
        // 使用優化的查詢一次性找出所有潛在重複交易
        List<Transaction> potentialDuplicates = transactionRepository
            .findPotentialDuplicates(
                transaction.getCard().getId(),
                transaction.getMerchant().getId(),
                transaction.getAmount(),
                fiveMinutesAgo,
                now
            );
        
        // 排除當前交易本身
        long duplicateCount = potentialDuplicates.stream()
            .filter(t -> !t.getId().equals(transaction.getId()))
            .count();
        
        if (duplicateCount > 0) {
            log.warn("偵測到重複交易: 交易ID={}, 在 {} 分鐘內發現 {} 筆相同交易", 
                transaction.getId(), 
                DUPLICATE_TRANSACTION_MINUTES, 
                duplicateCount);
            
            TransactionAlert alert = new TransactionAlert();
            alert.setTransaction(transaction);
            alert.setAlertType(AlertType.DUPLICATE_TRANSACTION);
            alert.setSeverity(AlertSeverity.HIGH);
            alert.setDescription(String.format(
                "重複交易警示: 在 %d 分鐘內偵測到 %d 筆相同的交易（卡片: %s, 商店: %s, 金額: %s 元）",
                DUPLICATE_TRANSACTION_MINUTES,
                duplicateCount,
                transaction.getCard().getMaskedCardNumber(),
                transaction.getMerchant().getMerchantName(),
                transaction.getAmount()
            ));
            alert.setDetectedAt(LocalDateTime.now());
            
            return alertRepository.save(alert);
        }
        
        return null;
    }
    
    /**
     * 根據交易金額決定警示嚴重程度
     */
    private AlertSeverity determineSeverityByAmount(BigDecimal amount) {
        BigDecimal criticalThreshold = new BigDecimal("200000");
        BigDecimal highThreshold = new BigDecimal("100000");
        
        if (amount.compareTo(criticalThreshold) >= 0) {
            return AlertSeverity.CRITICAL;
        } else if (amount.compareTo(highThreshold) >= 0) {
            return AlertSeverity.HIGH;
        } else {
            return AlertSeverity.MEDIUM;
        }
    }
    
    /**
     * 根據交易頻率決定警示嚴重程度
     */
    private AlertSeverity determineSeverityByFrequency(long transactionCount) {
        if (transactionCount >= 10) {
            return AlertSeverity.CRITICAL;
        } else if (transactionCount >= 8) {
            return AlertSeverity.HIGH;
        } else {
            return AlertSeverity.MEDIUM;
        }
    }
    
    /**
     * 查詢所有警示
     */
    @Transactional(readOnly = true)
    public List<TransactionAlert> getAllAlerts() {
        return alertRepository.findAll();
    }
    
    /**
     * 根據交易ID查詢警示
     */
    @Transactional(readOnly = true)
    public List<TransactionAlert> getAlertsByTransactionId(Long transactionId) {
        return alertRepository.findByTransactionId(transactionId);
    }
    
    /**
     * 根據警示類型查詢
     */
    @Transactional(readOnly = true)
    public List<TransactionAlert> getAlertsByType(AlertType alertType) {
        return alertRepository.findByAlertType(alertType);
    }
    
    /**
     * 根據嚴重程度查詢
     */
    @Transactional(readOnly = true)
    public List<TransactionAlert> getAlertsBySeverity(AlertSeverity severity) {
        return alertRepository.findBySeverity(severity);
    }
    
    /**
     * 查詢最近的警示
     */
    @Transactional(readOnly = true)
    public List<TransactionAlert> getRecentAlerts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return alertRepository.findByDetectedAtAfter(since);
    }
}

// Made with Bob