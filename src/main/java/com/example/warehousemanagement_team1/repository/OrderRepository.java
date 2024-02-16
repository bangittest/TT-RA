package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.OrderHistory;
import com.example.warehousemanagement_team1.model.Orders;
import com.example.warehousemanagement_team1.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Orders, String> {

    @Query("select o from Orders o where o.status=0 order by o.createdAt limit 100")
    List<Orders> get100OrdersWithStatus0();

    @Query("select o from Orders o where o.status=0 and o.createdUser.userId=?1 order by o.createdAt limit 100")
    List<Orders> find100OrdersWithStatus0ByUserId(Long userId);

    Orders findByOrderIdIgnoreCaseAndCreatedUser_UserId(String orderId, Long userId);

    Orders findByOrderIdIgnoreCaseAndCreatedUser_UserIdAndWarehouse_WarehouseId(String orderId, Long userId, String warehouseId);

    List<Orders> findAllByWarehouse_WarehouseIdAndStatus(String warehouseId, Integer status);

    @Query("select count (*)from Orders o where o.status=1 and LOWER(o.warehouse.warehouseId) =LOWER(:warehouseId) and date(o.storedAt) =:date")
    Integer countOrdersInDay(@Param("warehouseId") String warehouseId, @Param("date") LocalDate date);

    @Query("select count (*)from Orders o where o.status=1 and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId) and month (o.storedAt) =(:month) and year (o.storedAt)=(:year)")
    Integer countOrdersInMonthAndYear(@Param("warehouseId") String warehouseId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("select o from Orders o where LOWER(o.orderId) LIKE LOWER(concat('%', :orderId, '%')) or o.supplier.phone LIKE %:phone% or o.receiver.phone LIKE %:phoneNumber% or o.status= :status or LOWER(o.warehouse.warehouseId)= LOWER(:warehouseId)")
    Page<Orders> searchOrders(@Param("orderId") String orderId,
                              @Param("phone") String phone,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("status") Integer status,
                              @Param("warehouseId") String warehouseId,
                              Pageable pageable);
    @Query("select o from Orders o where LOWER(o.orderId) LIKE LOWER(concat('%', :orderId, '%')) or LOWER(o.supplier.phone) LIKE %:phone% or LOWER(o.receiver.phone) LIKE %:phoneNumber% or o.status= :status or LOWER(o.warehouse.warehouseId)= LOWER(:warehouseId) and o.createdUser.userId=(:userId)")
    Page<Orders> searchOrdersUser(@Param("orderId") String orderId,
                              @Param("phone") String phone,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("status") Integer status,
                              @Param("warehouseId") String warehouseId,
                              @Param("userId")Long userId,
                              Pageable pageable);


    @Query("select o from Orders o where o.createdUser.userId= :userId and LOWER(o.orderId) LIKE LOWER(concat('%', :orderId, '%')) or o.supplier.phone LIKE %:phone% or o.receiver.phone LIKE %:phoneNumber% or o.status= :status or LOWER(o.warehouse.warehouseId)= LOWER(:warehouseId)")
    Page<Orders> searchOrdersOfUser(@Param("userId") Long userId,
                                    @Param("orderId") String orderId,
                                    @Param("phone") String phone,
                                    @Param("phoneNumber") String phoneNumber,
                                    @Param("status") Integer status,
                                    @Param("warehouseId") String warehouseId,
                                    Pageable pageable);
    Page<Orders> findAllByCreatedUser_UserId(Long userId, Pageable pageable);
    @Query("SELECT o FROM Orders o " +
            "WHERE (:orderId IS NULL OR LOWER(o.orderId) LIKE %:orderId%) " +
            "AND (:phone IS NULL OR LOWER(o.supplier.phone) LIKE %:phone%) " +
            "AND (:phoneNumber IS NULL OR LOWER(o.receiver.phone) LIKE %:phoneNumber%) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:warehouseId IS NULL OR LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId))")
    Page<Orders> searchOrdersOf(@Param("orderId") String orderId,
                                @Param("phone") String phone,
                                @Param("phoneNumber") String phoneNumber,
                                @Param("status") Integer status,
                                @Param("warehouseId") String warehouseId,
                                Pageable pageable);


    Page<Orders> findAllByOrderIdOrWarehouse_WarehouseIdOrReceiver_PhoneOrSupplier_PhoneAndCreatedUser(String orderId, String warehouseId, String receiverPhone, String supplierPhone, Pageable pageable,User user);
    @Query("SELECT o FROM Orders o " +
            "WHERE (:orderId IS NULL OR LOWER(o.orderId) LIKE CONCAT('%', LOWER(:orderId), '%')) " +
            "AND (:warehouseId IS NULL OR LOWER(o.warehouse.warehouseId) LIKE CONCAT('%', LOWER(:warehouseId), '%')) " +
            "AND (:receiverPhone IS NULL OR LOWER(o.receiver.phone) LIKE CONCAT('%', LOWER(:receiverPhone), '%')) " +
            "AND (:supplierPhone IS NULL OR LOWER(o.supplier.phone) LIKE CONCAT('%', LOWER(:supplierPhone), '%')) " +
            "AND o.createdUser = :user")
    Page<Orders> findAllByCriteria(@Param("orderId") String orderId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("receiverPhone") String receiverPhone,
                                   @Param("supplierPhone") String supplierPhone,
                                   @Param("user") User user,
                                   Pageable pageable);


    List<Orders> findAllByOrderIdContainsIgnoreCaseAndCreatedUser(String orderId, User user);
    List<Orders>findAllByOrderIdContainsIgnoreCase(String orderId);

    List<Orders> findAllByReceiver_PhoneOrAndSupplier_PhoneContainsIgnoreCaseAndCreatedUser(String receiverPhone, String supplierPhone, User user);
    List<Orders>findAllByReceiver_PhoneOrAndSupplier_PhoneContainsIgnoreCase(String receiverPhone, String supplierPhone);

    @Query("SELECT o FROM Orders o WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<Orders> findByStatusOrderByCreatedAtAsc(@Param("status") Integer status);

    Page<Orders> findAllByCreatedUser(Pageable pageable, User user);

    List<Orders> findAllByCreatedUser(User user);

    @Query("SELECT o FROM Orders o WHERE o.status = :status AND (o.numberOfFailedDelivery = 1 OR o.numberOfFailedDelivery = 2) ORDER BY o.returnedAt DESC")
    List<Orders> findByStatusOrderByResetReturnedAtAsc(@Param("status") Integer status);

    @Query("SELECT o FROM Orders o WHERE o.status = :status and o.numberOfFailedDelivery=3 ORDER BY o.returnedAt ASC")
    List<Orders> findByStatusOrderByReturnedAtAsc(@Param("status") Integer status);

    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.createdUser = :createdUser ORDER BY o.returnedAt ASC")
    List<Orders> findByStatusAndCreatedUserOrderByReturnedAtAsc(@Param("status") Integer status, @Param("createdUser") Long userId);
    List<Orders> findAllByStatusAndCreatedUser(Integer status, User user);
    List<Orders> findAllByStatus(Integer status);
    List<Orders> findAllByWarehouse_WarehouseIdAndCreatedUser(String warehouseId, User user);
    List<Orders>findAllByWarehouse_WarehouseId(String warehouseId);
    @Query(value = "SELECT MAX(CAST(SUBSTRING(o.orderId, 11) AS Long)) FROM Orders o WHERE SUBSTRING(o.orderId, 4, 6) = :datePart")
    Long findMaxIdByDate(@Param("datePart") String datePart);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 2 AND DATE(o.storedAt) = :date")
    Integer findSuccessCountByDate(@Param("date") LocalDate date);
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.numberOfFailedDelivery = 3 AND DATE(o.createdAt) = :date")
    Integer findByCreatedAtBetweenAndNumberOfFailedDeliveryInteger(@Param("date") LocalDate date);
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 2 AND MONTH(o.storedAt) = MONTH(:date) AND YEAR(o.storedAt) = YEAR(:date)")
    Integer findSuccessCountByMonthYear(@Param("date") LocalDate date);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status =2  AND DATE(o.createdAt) = :date and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId)")
    Integer findByCreatedAtBetweenAndNumberOfSuccessInteger(@Param("date") LocalDate date,  @Param("warehouseId") String warehouseId);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.numberOfFailedDelivery = 3 AND DATE(o.createdAt) = :date and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId)")
    Integer findByCreatedAtBetweenAndNumberOfFailedDeliveryInteger(@Param("date") LocalDate date,  @Param("warehouseId") String warehouseId);
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 2 AND MONTH(o.storedAt) = MONTH(:date) AND YEAR(o.storedAt) = YEAR(:date)  and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId)")
    Integer findSuccessCountByMonthYear(@Param("date") LocalDate date,  @Param("warehouseId") String warehouseId);
    @Query("SELECT COUNT(o) FROM Orders o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE date(o.storedAt) = :startDate AND r.reasonId = :reason and LOWER(o.warehouse.warehouseId)=LOWER(:warehouseId)")
    Integer getFailedOrderReasonsCount(
            @Param("reason") Long reason,
            @Param("startDate") LocalDate startDate,
            @Param("warehouseId") String warehouseId
    );


    Orders findByOrderIdIgnoreCase(String orderId);

    @Query("SELECT DATE(o.createdAt) AS orderDate, " +
            "SUM(CASE WHEN o.status IN (3, 4) AND o.numberOfFailedDelivery >= 3 THEN 1 ELSE 0 END) AS numberOfFailedOrder, " +
            "SUM(CASE WHEN o.status = 2 THEN 1 ELSE 0 END) AS numberOfSuccessOrder, " +
            "r.reasonId, " +
            "COUNT(o.reason.reasonId) AS totalOrdersForReason " +
            "FROM Orders o " +
            "LEFT JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE DATE(o.createdAt) = :date " +
            "AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId) " +
            "GROUP BY DATE(o.createdAt), r.reasonId")
    List<Object[]> getOrderStatisticsByDay(@Param("date") LocalDate date, @Param("warehouseId") String warehouseId);



    @Query("SELECT COUNT(o) FROM Orders o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE FUNCTION('MONTH', o.createdAt) = :month " +
            "AND FUNCTION('YEAR', o.createdAt) = :year AND ((o.status= 3 AND o.numberOfFailedDelivery>=3) OR o.status=4) " +
            "AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId)")
    Integer getFailedOrderCountMonthYear(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("warehouseId") String warehouseId
    );
    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE MONTH(o.createdAt) = :month " +
            "AND YEAR(o.createdAt) = :year AND o.status = 2 " +
            "AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId)")
    Integer getSuccessOrderCountMonthYear(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("warehouseId") String warehouseId
    );

    @Query("SELECT COUNT(o) FROM Orders o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE FUNCTION('MONTH', o.createdAt) = :month " +
            "AND FUNCTION('YEAR', o.createdAt) = :year AND ((o.status= 3 AND o.numberOfFailedDelivery>=3) OR o.status=4) " +
            "AND r.reasonId = :reason AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId)")
    Integer getFailedOrdersCountMonthYear(
            @Param("reason") Long reason,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("warehouseId") String warehouseId
    );

    @Query("SELECT COUNT(o) FROM Orders o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE Date(o.createdAt) = :date " +
            "AND ((o.status= 3 AND o.numberOfFailedDelivery>=3) OR o.status=4) " +
            "AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId)")
    Integer getFailedOrderCountDate(
            @Param("date") LocalDate date,
            @Param("warehouseId") String warehouseId
    );
    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE Date(o.createdAt) = :date " +
            " AND o.status = 2 " +
            "AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId)")
    Integer getSuccessOrderCountDate(
            @Param("date") LocalDate date,
            @Param("warehouseId") String warehouseId
    );

    @Query("SELECT COUNT(o) FROM Orders o JOIN Reason r ON o.reason.reasonId = r.reasonId " +
            "WHERE Date(o.createdAt) = :date " +
            "AND ((o.status= 3 AND o.numberOfFailedDelivery>=3) OR o.status=4) " +
            "AND r.reasonId = :reason AND LOWER(o.warehouse.warehouseId) = LOWER(:warehouseId)")
    Integer getFailedOrderReasonCountDate(
            @Param("reason") Long reason,
            @Param("date") LocalDate date,
            @Param("warehouseId") String warehouseId
    );

    @Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM Orders o LEFT JOIN Reason r ON o.reason_id = r.reason_id WHERE DATE(o.created_at) = :orderDate AND o.status IN (3, 4) AND o.number_of_failed_delivery = 3 AND LOWER(o.warehouse_id) = LOWER(:warehouseId)) AS failed_order_count, " +
            "(SELECT COUNT(*) FROM Orders o WHERE DATE(o.created_at) = :orderDate AND o.status = 2 AND LOWER(o.warehouse_id) = LOWER(:warehouseId)) AS success_order_count, " +
            "(SELECT COUNT(*) FROM Orders o LEFT JOIN Reason r ON o.reason_id = r.reason_id WHERE DATE(o.created_at) = :orderDate AND (o.status IN (3, 4) AND o.number_of_failed_delivery = 3 AND LOWER(o.warehouse_id) = LOWER(:warehouseId) AND r.reason_id = :reasonId)) AS failed_order_reason_count " +
            "FROM Orders o " +
            "LEFT JOIN Reason r ON o.reason_id = r.reason_id " +
            "WHERE DATE(o.created_at) = :orderDate " +
            "AND LOWER(o.warehouse_id) = LOWER(:warehouseId)", nativeQuery = true)
    Object[] getOrderCountsByDateAndReason(@Param("reasonId") Long reasonId, @Param("orderDate") LocalDate orderDate, @Param("warehouseId") String warehouseId);
}
