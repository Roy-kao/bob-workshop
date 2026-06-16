package com.payment.repository;

import com.payment.model.TransactionAlert;
import com.payment.model.AlertSeverity;
import com.payment.model.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易警示資料存取介面
 */
@Repository
public interface AlertRepository extends JpaRepository<TransactionAlert, Long> {
    
    /**
     * 根據交易ID查詢警示
     */
    List<TransactionAlert> findByTransactionId(Long transactionId);
    
    /**
     * 根據警示類型查詢
     */
    List<TransactionAlert> findByAlertType(AlertType alertType);
    
    /**
     * 根據嚴重程度查詢
     */
    List<TransactionAlert> findBySeverity(AlertSeverity severity);
    
    /**
     * 查詢指定時間後的警示
     */
    List<TransactionAlert> findByDetectedAtAfter(LocalDateTime since);
    
    /**
     * 根據嚴重程度和警示類型查詢
     */
    List<TransactionAlert> findBySeverityAndAlertType(AlertSeverity severity, AlertType alertType);
    
    /**
     * 查詢指定時間範圍內的警示
     */
    @Query("SELECT a FROM TransactionAlert a WHERE a.detectedAt BETWEEN :start AND :end")
    List<TransactionAlert> findByDetectedAtBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * 計算指定嚴重程度的警示數量
     */
    long countBySeverity(AlertSeverity severity);
    
    /**
     * 查詢指定交易的最新警示
     */
    @Query("SELECT a FROM TransactionAlert a WHERE a.transaction.id = :transactionId ORDER BY a.detectedAt DESC")
    List<TransactionAlert> findLatestAlertsByTransactionId(@Param("transactionId") Long transactionId);
}

// Made with Bob