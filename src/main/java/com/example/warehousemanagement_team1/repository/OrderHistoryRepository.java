package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface OrderHistoryRepository extends JpaRepository<OrderHistory,String> {
    @Query("select o from OrderHistory o where LOWER(o.order.orderId) =lower(:orderId) order by o.orderedAt desc ")
    List<OrderHistory>getByOrderId(@Param("orderId") String orderId);

    @Query("SELECT COUNT(o) FROM OrderHistory o JOIN o.reason r " +
            "WHERE date(o.orderedAt) = :startDate AND r.reasonId = :reason and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId)")
    Integer getFailedOrderReasonsCount(
            @Param("reason") Long reason,
            @Param("startDate") LocalDate startDate,
            @Param("warehouseId") String warehouseId
    );

    @Query("SELECT COUNT(distinct o.order.orderId) FROM OrderHistory o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE FUNCTION('MONTH', o.orderedAt) = :month " +
            "AND FUNCTION('YEAR', o.orderedAt) = :year " +
            "AND r.reasonId = :reason and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId)")
    Integer getFailedOrderReasonsCountMonthYear(
            @Param("reason") Long reason,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("warehouseId") String warehouseId
    );

    @Query("SELECT COUNT(o) FROM OrderHistory o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE FUNCTION('MONTH', o.orderedAt) = :month " +
            "AND FUNCTION('YEAR', o.orderedAt) = :year " +
            "AND r.reasonId = :reason AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId) group by o.status")
    Integer getFailedOrderCountMonthYear(
            @Param("reason") Long reason,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("warehouseId") String warehouseId
    );
}
