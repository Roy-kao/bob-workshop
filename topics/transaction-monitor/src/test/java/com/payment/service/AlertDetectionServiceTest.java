package com.payment.service;

import com.payment.model.*;
import com.payment.repository.AlertRepository;
import com.payment.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AlertDetectionService 單元測試
 * 測試三種偵測規則的正確性和效能優化
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("警示偵測服務測試")
class AlertDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertDetectionService alertDetectionService;

    private Card testCard;
    private Merchant testMerchant;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        // 建立測試用卡片
        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("1234567890123456");
        testCard.setCardholderName("測試持卡人");
        testCard.setStatus(CardStatus.ACTIVE);

        // 建立測試用商店
        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setMerchantCode("M001");
        testMerchant.setMerchantName("測試商店");
        testMerchant.setCategory(MerchantCategory.RETAIL);

        // 建立測試用交易
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setCard(testCard);
        testTransaction.setMerchant(testMerchant);
        testTransaction.setAmount(new BigDecimal("1000"));
        testTransaction.setStatus(TransactionStatus.APPROVED);
        testTransaction.setTransactionTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("規則1: 應偵測到高額交易 - 金額超過 50,000 元")
    void shouldDetectHighAmountTransaction() {
        // Given: 交易金額為 60,000 元
        testTransaction.setAmount(new BigDecimal("60000"));
        
        TransactionAlert expectedAlert = new TransactionAlert();
        expectedAlert.setAlertId(1L);
        expectedAlert.setTransaction(testTransaction);
        expectedAlert.setAlertType(AlertType.HIGH_AMOUNT);
        expectedAlert.setSeverity(AlertSeverity.MEDIUM);
        
        when(alertRepository.save(any(TransactionAlert.class))).thenReturn(expectedAlert);

        // When: 執行高額交易偵測
        TransactionAlert result = alertDetectionService.detectHighAmountTransaction(testTransaction);

        // Then: 應該產生警示
        assertThat(result).isNotNull();
        assertThat(result.getAlertType()).isEqualTo(AlertType.HIGH_AMOUNT);
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.MEDIUM);
        
        verify(alertRepository, times(1)).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("規則1: 不應偵測到高額交易 - 金額低於閾值")
    void shouldNotDetectHighAmountTransaction_WhenBelowThreshold() {
        // Given: 交易金額為 30,000 元（低於 50,000 閾值）
        testTransaction.setAmount(new BigDecimal("30000"));

        // When: 執行高額交易偵測
        TransactionAlert result = alertDetectionService.detectHighAmountTransaction(testTransaction);

        // Then: 不應該產生警示
        assertThat(result).isNull();
        verify(alertRepository, never()).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("規則1: 應根據金額設定正確的嚴重程度 - CRITICAL")
    void shouldSetCriticalSeverity_ForVeryHighAmount() {
        // Given: 交易金額為 250,000 元（超過 200,000 臨界值）
        testTransaction.setAmount(new BigDecimal("250000"));
        
        TransactionAlert expectedAlert = new TransactionAlert();
        expectedAlert.setAlertId(1L);
        expectedAlert.setSeverity(AlertSeverity.CRITICAL);
        
        when(alertRepository.save(any(TransactionAlert.class))).thenReturn(expectedAlert);

        // When: 執行高額交易偵測
        TransactionAlert result = alertDetectionService.detectHighAmountTransaction(testTransaction);

        // Then: 嚴重程度應為 CRITICAL
        assertThat(result).isNotNull();
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.CRITICAL);
    }

    @Test
    @DisplayName("規則2: 應偵測到頻繁交易 - 1小時內超過5筆")
    void shouldDetectFrequentTransactions() {
        // Given: 1小時內已有 6 筆交易
        when(transactionRepository.countByCardIdAndTransactionTimeAfter(
            eq(testCard.getId()), 
            any(LocalDateTime.class)
        )).thenReturn(6L);
        
        TransactionAlert expectedAlert = new TransactionAlert();
        expectedAlert.setAlertId(2L);
        expectedAlert.setTransaction(testTransaction);
        expectedAlert.setAlertType(AlertType.FREQUENT_TRANSACTION);
        expectedAlert.setSeverity(AlertSeverity.MEDIUM);
        
        when(alertRepository.save(any(TransactionAlert.class))).thenReturn(expectedAlert);

        // When: 執行頻繁交易偵測
        TransactionAlert result = alertDetectionService.detectFrequentTransactions(testTransaction);

        // Then: 應該產生警示
        assertThat(result).isNotNull();
        assertThat(result.getAlertType()).isEqualTo(AlertType.FREQUENT_TRANSACTION);
        
        // 驗證只執行一次查詢（避免 N+1 問題）
        verify(transactionRepository, times(1))
            .countByCardIdAndTransactionTimeAfter(anyLong(), any(LocalDateTime.class));
        verify(alertRepository, times(1)).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("規則2: 不應偵測到頻繁交易 - 交易數量未超過閾值")
    void shouldNotDetectFrequentTransactions_WhenBelowThreshold() {
        // Given: 1小時內只有 3 筆交易
        when(transactionRepository.countByCardIdAndTransactionTimeAfter(
            eq(testCard.getId()), 
            any(LocalDateTime.class)
        )).thenReturn(3L);

        // When: 執行頻繁交易偵測
        TransactionAlert result = alertDetectionService.detectFrequentTransactions(testTransaction);

        // Then: 不應該產生警示
        assertThat(result).isNull();
        verify(alertRepository, never()).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("規則2: 應根據頻率設定正確的嚴重程度 - HIGH")
    void shouldSetHighSeverity_ForVeryFrequentTransactions() {
        // Given: 1小時內已有 9 筆交易
        when(transactionRepository.countByCardIdAndTransactionTimeAfter(
            eq(testCard.getId()), 
            any(LocalDateTime.class)
        )).thenReturn(9L);
        
        TransactionAlert expectedAlert = new TransactionAlert();
        expectedAlert.setAlertId(2L);
        expectedAlert.setSeverity(AlertSeverity.HIGH);
        
        when(alertRepository.save(any(TransactionAlert.class))).thenReturn(expectedAlert);

        // When: 執行頻繁交易偵測
        TransactionAlert result = alertDetectionService.detectFrequentTransactions(testTransaction);

        // Then: 嚴重程度應為 HIGH
        assertThat(result).isNotNull();
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.HIGH);
    }

    @Test
    @DisplayName("規則3: 應偵測到重複交易 - 5分鐘內相同金額相同商店")
    void shouldDetectDuplicateTransaction() {
        // Given: 5分鐘內有相同的交易
        Transaction duplicateTransaction = new Transaction();
        duplicateTransaction.setId(2L);
        duplicateTransaction.setCard(testCard);
        duplicateTransaction.setMerchant(testMerchant);
        duplicateTransaction.setAmount(testTransaction.getAmount());
        duplicateTransaction.setTransactionTime(LocalDateTime.now().minusMinutes(3));
        
        when(transactionRepository.findPotentialDuplicates(
            eq(testCard.getId()),
            eq(testMerchant.getId()),
            eq(testTransaction.getAmount()),
            any(LocalDateTime.class),
            any(LocalDateTime.class)
        )).thenReturn(Arrays.asList(testTransaction, duplicateTransaction));
        
        TransactionAlert expectedAlert = new TransactionAlert();
        expectedAlert.setAlertId(3L);
        expectedAlert.setTransaction(testTransaction);
        expectedAlert.setAlertType(AlertType.DUPLICATE_TRANSACTION);
        expectedAlert.setSeverity(AlertSeverity.HIGH);
        
        when(alertRepository.save(any(TransactionAlert.class))).thenReturn(expectedAlert);

        // When: 執行重複交易偵測
        TransactionAlert result = alertDetectionService.detectDuplicateTransaction(testTransaction);

        // Then: 應該產生警示
        assertThat(result).isNotNull();
        assertThat(result.getAlertType()).isEqualTo(AlertType.DUPLICATE_TRANSACTION);
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.HIGH);
        
        // 驗證使用優化的查詢（一次性取得所有重複交易）
        verify(transactionRepository, times(1))
            .findPotentialDuplicates(anyLong(), anyLong(), any(BigDecimal.class), 
                any(LocalDateTime.class), any(LocalDateTime.class));
        verify(alertRepository, times(1)).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("規則3: 不應偵測到重複交易 - 沒有相同交易")
    void shouldNotDetectDuplicateTransaction_WhenNoDuplicates() {
        // Given: 5分鐘內沒有相同的交易
        when(transactionRepository.findPotentialDuplicates(
            eq(testCard.getId()),
            eq(testMerchant.getId()),
            eq(testTransaction.getAmount()),
            any(LocalDateTime.class),
            any(LocalDateTime.class)
        )).thenReturn(Collections.singletonList(testTransaction));

        // When: 執行重複交易偵測
        TransactionAlert result = alertDetectionService.detectDuplicateTransaction(testTransaction);

        // Then: 不應該產生警示
        assertThat(result).isNull();
        verify(alertRepository, never()).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("整合測試: 應執行所有偵測規則並返回所有警示")
    void shouldDetectAllAlerts() {
        // Given: 設定一個會觸發多個規則的交易
        testTransaction.setAmount(new BigDecimal("60000")); // 觸發高額交易
        
        // Mock 頻繁交易偵測
        when(transactionRepository.countByCardIdAndTransactionTimeAfter(
            anyLong(), any(LocalDateTime.class)
        )).thenReturn(6L);
        
        // Mock 重複交易偵測
        Transaction duplicateTransaction = new Transaction();
        duplicateTransaction.setId(2L);
        when(transactionRepository.findPotentialDuplicates(
            anyLong(), anyLong(), any(BigDecimal.class),
            any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(Arrays.asList(testTransaction, duplicateTransaction));
        
        // Mock 警示儲存
        when(alertRepository.save(any(TransactionAlert.class)))
            .thenAnswer(invocation -> {
                TransactionAlert alert = invocation.getArgument(0);
                alert.setAlertId(System.currentTimeMillis());
                return alert;
            });

        // When: 執行所有偵測規則
        List<TransactionAlert> results = alertDetectionService.detectAllAlerts(testTransaction);

        // Then: 應該產生 3 個警示
        assertThat(results).hasSize(3);
        assertThat(results).extracting(TransactionAlert::getAlertType)
            .containsExactlyInAnyOrder(
                AlertType.HIGH_AMOUNT,
                AlertType.FREQUENT_TRANSACTION,
                AlertType.DUPLICATE_TRANSACTION
            );
        
        // 驗證所有偵測方法都被呼叫
        verify(alertRepository, times(3)).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("整合測試: 正常交易不應觸發任何警示")
    void shouldNotDetectAnyAlerts_ForNormalTransaction() {
        // Given: 正常交易（金額低、無頻繁、無重複）
        testTransaction.setAmount(new BigDecimal("1000"));
        
        when(transactionRepository.countByCardIdAndTransactionTimeAfter(
            anyLong(), any(LocalDateTime.class)
        )).thenReturn(2L);
        
        when(transactionRepository.findPotentialDuplicates(
            anyLong(), anyLong(), any(BigDecimal.class),
            any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(Collections.singletonList(testTransaction));

        // When: 執行所有偵測規則
        List<TransactionAlert> results = alertDetectionService.detectAllAlerts(testTransaction);

        // Then: 不應該產生任何警示
        assertThat(results).isEmpty();
        verify(alertRepository, never()).save(any(TransactionAlert.class));
    }

    @Test
    @DisplayName("查詢測試: 應正確查詢所有警示")
    void shouldGetAllAlerts() {
        // Given
        List<TransactionAlert> expectedAlerts = Arrays.asList(
            new TransactionAlert(),
            new TransactionAlert()
        );
        when(alertRepository.findAll()).thenReturn(expectedAlerts);

        // When
        List<TransactionAlert> results = alertDetectionService.getAllAlerts();

        // Then
        assertThat(results).hasSize(2);
        verify(alertRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查詢測試: 應根據交易ID查詢警示")
    void shouldGetAlertsByTransactionId() {
        // Given
        Long transactionId = 1L;
        List<TransactionAlert> expectedAlerts = Collections.singletonList(new TransactionAlert());
        when(alertRepository.findByTransactionId(transactionId)).thenReturn(expectedAlerts);

        // When
        List<TransactionAlert> results = alertDetectionService.getAlertsByTransactionId(transactionId);

        // Then
        assertThat(results).hasSize(1);
        verify(alertRepository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    @DisplayName("查詢測試: 應根據警示類型查詢")
    void shouldGetAlertsByType() {
        // Given
        AlertType alertType = AlertType.HIGH_AMOUNT;
        List<TransactionAlert> expectedAlerts = Collections.singletonList(new TransactionAlert());
        when(alertRepository.findByAlertType(alertType)).thenReturn(expectedAlerts);

        // When
        List<TransactionAlert> results = alertDetectionService.getAlertsByType(alertType);

        // Then
        assertThat(results).hasSize(1);
        verify(alertRepository, times(1)).findByAlertType(alertType);
    }

    @Test
    @DisplayName("查詢測試: 應根據嚴重程度查詢")
    void shouldGetAlertsBySeverity() {
        // Given
        AlertSeverity severity = AlertSeverity.HIGH;
        List<TransactionAlert> expectedAlerts = Collections.singletonList(new TransactionAlert());
        when(alertRepository.findBySeverity(severity)).thenReturn(expectedAlerts);

        // When
        List<TransactionAlert> results = alertDetectionService.getAlertsBySeverity(severity);

        // Then
        assertThat(results).hasSize(1);
        verify(alertRepository, times(1)).findBySeverity(severity);
    }
}

// Made with Bob