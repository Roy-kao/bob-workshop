package com.payment.controller;

import com.payment.model.TransactionAlert;
import com.payment.model.AlertSeverity;
import com.payment.model.AlertType;
import com.payment.repository.AlertRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 交易警示控制器
 *
 * @author IBM Bob Workshop
 * @version 1.0.0
 */
@Tag(name = "警示管理", description = "交易警示查詢與統計 API")
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {
    
    private final AlertRepository alertRepository;
    
    @Operation(
        summary = "查詢所有警示",
        description = "取得系統中所有的交易警示記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得警示列表",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<TransactionAlert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
    
    @Operation(
        summary = "查詢單筆警示",
        description = "根據警示 ID 查詢特定警示的詳細資訊"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得警示資料",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        ),
        @ApiResponse(responseCode = "404", description = "找不到指定的警示")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionAlert> getAlertById(
            @Parameter(description = "警示 ID", required = true, example = "1")
            @PathVariable Long id) {
        return alertRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "查詢交易的警示",
        description = "取得特定交易的所有警示記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得該交易的警示列表",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        )
    })
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<TransactionAlert>> getAlertsByTransaction(
            @Parameter(description = "交易 ID", required = true, example = "1")
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(alertRepository.findByTransactionId(transactionId));
    }
    
    @Operation(
        summary = "根據嚴重程度查詢警示",
        description = "取得指定嚴重程度的所有警示記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得警示列表",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        )
    })
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<TransactionAlert>> getAlertsBySeverity(
            @Parameter(description = "嚴重程度 (LOW, MEDIUM, HIGH, CRITICAL)", required = true, example = "HIGH")
            @PathVariable AlertSeverity severity) {
        return ResponseEntity.ok(alertRepository.findBySeverity(severity));
    }
    
    @Operation(
        summary = "根據警示類型查詢",
        description = "取得指定類型的所有警示記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得警示列表",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        )
    })
    @GetMapping("/type/{alertType}")
    public ResponseEntity<List<TransactionAlert>> getAlertsByType(
            @Parameter(description = "警示類型", required = true, example = "HIGH_AMOUNT")
            @PathVariable AlertType alertType) {
        return ResponseEntity.ok(alertRepository.findByAlertType(alertType));
    }
    
    @Operation(
        summary = "查詢最近的警示",
        description = "取得指定時間範圍內的最近警示記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得最近警示列表",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        )
    })
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionAlert>> getRecentAlerts(
            @Parameter(description = "查詢最近 N 小時的警示", example = "24")
            @RequestParam(defaultValue = "24") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return ResponseEntity.ok(alertRepository.findByDetectedAtAfter(since));
    }
    
    @Operation(
        summary = "查詢高風險警示",
        description = "取得高風險和極高風險的警示記錄"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得高風險警示列表",
            content = @Content(schema = @Schema(implementation = TransactionAlert.class))
        )
    })
    @GetMapping("/high-risk")
    public ResponseEntity<List<TransactionAlert>> getHighRiskAlerts() {
        List<TransactionAlert> highAlerts = alertRepository.findBySeverity(AlertSeverity.HIGH);
        List<TransactionAlert> criticalAlerts = alertRepository.findBySeverity(AlertSeverity.CRITICAL);
        
        List<TransactionAlert> allHighRisk = new java.util.ArrayList<>(criticalAlerts);
        allHighRisk.addAll(highAlerts);
        
        return ResponseEntity.ok(allHighRisk);
    }
    
    @Operation(
        summary = "取得警示統計",
        description = "取得警示的統計資料，包含總警示數、各嚴重程度數量等"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "成功取得統計資料"
        )
    })
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<TransactionAlert> allAlerts = alertRepository.findAll();
        
        long criticalCount = alertRepository.countBySeverity(AlertSeverity.CRITICAL);
        long highCount = alertRepository.countBySeverity(AlertSeverity.HIGH);
        long mediumCount = alertRepository.countBySeverity(AlertSeverity.MEDIUM);
        long lowCount = alertRepository.countBySeverity(AlertSeverity.LOW);
        
        return ResponseEntity.ok(Map.of(
            "totalAlerts", allAlerts.size(),
            "criticalCount", criticalCount,
            "highCount", highCount,
            "mediumCount", mediumCount,
            "lowCount", lowCount
        ));
    }
}

// Made with Bob