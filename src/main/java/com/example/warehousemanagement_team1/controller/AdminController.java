package com.example.warehousemanagement_team1.controller;

import com.example.warehousemanagement_team1.dto.OrderDetailDTO;
import com.example.warehousemanagement_team1.dto.response.OrderResponseDTO;
import com.example.warehousemanagement_team1.dto.response.OrdersResponseDTO;
import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.model.User;
import com.example.warehousemanagement_team1.service.order.OrderService;
import com.example.warehousemanagement_team1.utils.validation.Validator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/all")
public class AdminController {
    @Autowired
    private Validator validator;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MessageSource messageSource;

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", defaultValue = "0") String page,
                                    @RequestParam(value = "limit", defaultValue = "10") String limit) throws DataTypeException {
        Integer parsedPage = validator.validateInteger(page);
        Integer parsedLimit = validator.validateInteger(limit);
        Pageable pageable=PageRequest.of(parsedPage,parsedLimit);
        Page<OrderResponseDTO>orderResponseDTOS=orderService.getAll(pageable);
        return new ResponseEntity<>(orderResponseDTOS, HttpStatus.OK);
    }
    @GetMapping("search-sort-pagination")
    public ResponseEntity<?> searchWithPagination(@RequestParam(value = "orderId", required = false) String orderId,
                                                  @RequestParam(value = "phone", required = false) String phone,
                                                  @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                                  @RequestParam(value = "status", required = false) Integer status,
                                                  @RequestParam(value = "warehouseId", required = false) String warehouseId,
                                                  @RequestParam(value = "page", defaultValue = "0") String page,
                                                  @RequestParam(value = "limit", defaultValue = "10") String limit,
                                                  @RequestParam(value = "sort", defaultValue = "orderId") String sort,
                                                  @RequestParam(value = "order", defaultValue = "asc") String order) throws DataTypeException, OrderException {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Integer parsedPage = validator.validateInteger(page);
        Integer parsedLimit = validator.validateInteger(limit);

        Pageable pageable = PageRequest.of(parsedPage, parsedLimit, Sort.by(direction, sort));
        Page<OrderResponseDTO> orderPage;
        if ((orderId == null || orderId.isEmpty())
                && (phone == null || phone.isEmpty())
                && (phoneNumber == null || phoneNumber.isEmpty())
                && status == null
                && (warehouseId == null || warehouseId.isEmpty())) {
            orderPage = orderService.getAllByWithPagination(pageable);
        } else {
            orderPage = orderService.searchOrdersWithPagination(orderId, phone, phoneNumber, status, warehouseId, pageable);
        }
        if (orderPage.isEmpty()) {
            throw new OrderException("SYSS-1103", messageSource);
        }
        return new ResponseEntity<>(orderPage, HttpStatus.OK);
    }

    @GetMapping("{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) throws OrderException, DataTypeException {
        String parsedOrderId = validator.validateString(orderId);
        OrderDetailDTO orderDetailDTO = orderService.findById(parsedOrderId);
        if (orderDetailDTO == null) {
            throw new OrderException("SYSS-1100", messageSource);
        }
        return new ResponseEntity<>(orderDetailDTO, HttpStatus.OK);
    }

}
