package com.example.warehousemanagement_team1.service.order;

import com.example.warehousemanagement_team1.dto.OrderDetailDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestConfirmDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestDTO;
import com.example.warehousemanagement_team1.dto.response.OrderResponseDTO;
import com.example.warehousemanagement_team1.dto.response.OrdersResponseDTO;
import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.exception.UserException;
import com.example.warehousemanagement_team1.exception.WarehouseException;
import com.example.warehousemanagement_team1.model.Orders;
import com.example.warehousemanagement_team1.model.User;
import com.google.zxing.WriterException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface OrderService {
    OrderResponseDTO save(OrderRequestDTO orderRequestDTO, HttpServletRequest request) throws DataTypeException;

    OrderDetailDTO findById(String orderId) throws OrderException;

    OrderDetailDTO findByIdAndUserId(String orderId, HttpServletRequest request) throws OrderException;

    void autoHandleOrders() throws WarehouseException;

    void handleOrders(HttpServletRequest request) throws WarehouseException, OrderException;

    void exportLabelOrders(List<String> orderIdList, ServletOutputStream outputStream) throws OrderException, IOException, WriterException, DataTypeException;

    List<OrdersResponseDTO> searchOrderCode(String orderCode, User user);

    List<OrdersResponseDTO> searchSupplierPhoneAndReceiverPhone(String phone, User user);

    List<OrdersResponseDTO> statusOrder(Integer status, Long userId) throws UserException;

    List<OrdersResponseDTO> filterWareHouseCode(String WarehouseCode, User user) throws WarehouseException;

    Page<OrdersResponseDTO> page(Pageable pageable, User user);
    Page<OrderResponseDTO> getAll(Pageable pageable);

    void confirmDelivery(Long userId, OrderRequestConfirmDTO orderRequestConfirmDTO) throws Exception;

    List<OrdersResponseDTO> findAllListOrder(User user);

    void handleReturns();

    Page<OrderResponseDTO> searchOrdersWithPagination(String orderId,
                                                      String phone,
                                                      String phoneNumber,
                                                      Integer status,
                                                      String warehouseId,
                                                      Pageable pageable);

    Page<OrderResponseDTO> searchOrdersOfUserWithPagination(HttpServletRequest request,
                                                            String orderId,
                                                            String phone,
                                                            String phoneNumber,
                                                            Integer status,
                                                            String warehouseId,
                                                            Pageable pageable);

    String getOrderId() throws DataTypeException;
    Page<OrderResponseDTO> getAllByUserWithPagination(HttpServletRequest request,Pageable pageable);
    Page<OrderResponseDTO> getAllByWithPagination(Pageable pageable);
    Page<OrdersResponseDTO>searchByAllOrders(String orderId, String warehouseId, String receiverPhone, String supplierPhone,User user, Pageable pageable);

}
