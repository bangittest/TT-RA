package com.example.warehousemanagement_team1.service.order;

import com.example.warehousemanagement_team1.dto.OrderDetailDTO;
import com.example.warehousemanagement_team1.dto.OrderHistoryDTO;
import com.example.warehousemanagement_team1.dto.SupplierDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestConfirmDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestDTO;
import com.example.warehousemanagement_team1.dto.response.OrderResponseDTO;
import com.example.warehousemanagement_team1.dto.response.OrdersResponseDTO;
import com.example.warehousemanagement_team1.dto.response.ReceiverResponseDTO;
import com.example.warehousemanagement_team1.dto.response.WarehouseResponseDTO;
import com.example.warehousemanagement_team1.exception.*;
import com.example.warehousemanagement_team1.model.*;
import com.example.warehousemanagement_team1.repository.*;
import com.example.warehousemanagement_team1.service.email.EmailService;
import com.example.warehousemanagement_team1.service.user.UserService;
import com.example.warehousemanagement_team1.utils.Barchart;
import com.example.warehousemanagement_team1.utils.BarcodeAndQrcodeGenerator;
import com.example.warehousemanagement_team1.utils.formatter.Formatter;
import com.google.zxing.WriterException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Value("${label-path}")
    private String labelPath;
    @Value("${qrcode-text-file}")
    private String qrcodeTextFile;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ReceiverRepository receiverRepository;
    @Autowired
    private Formatter formatter;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private BarcodeAndQrcodeGenerator barcodeAndQrcodeGenerator;
    @Autowired
    private ReasonRepository reasonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Barchart barchart;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO save(OrderRequestDTO orderRequestDTO, HttpServletRequest request) throws DataTypeException {
        Orders order = new Orders();
        Supplier existedSupplier = supplierRepository.findByPhone(orderRequestDTO.getSupplier().getPhone());
        if (existedSupplier != null) {
            order.setSupplier(existedSupplier);
        } else {
            Supplier supplier = supplierRepository.save(new Supplier(orderRequestDTO.getSupplier()));
            order.setSupplier(supplier);
        }

        Receiver existedReceiver = receiverRepository.findByPhone(orderRequestDTO.getReceiver().getPhone());
        if (existedReceiver != null) {
            order.setReceiver(existedReceiver);
        } else {
            Receiver receiver = receiverRepository.save(new Receiver(orderRequestDTO.getReceiver()));
            order.setReceiver(receiver);
        }


        User user = userService.getAccount(request);
        order.setOrderId(getOrderId());
        order.setCreatedUser(user);
        order.setStatus(orderRequestDTO.getStatus());
        order.setNumberOfFailedDelivery(orderRequestDTO.getNumberOfFailedDelivery());
        order.setCreatedAt(orderRequestDTO.getCreatedAt());
        order.setWarehouse(warehouseRepository.findById(user.getWarehouse().getWarehouseId().toUpperCase()).orElse(null));

        Orders newOrder = orderRepository.save(order);

        //save orderHistory
        orderHistoryRepository.save(OrderHistory.builder()
                .orderedAt(newOrder.getCreatedAt())
                .order(newOrder)
                .userId(newOrder.getCreatedUser().getUserId())
                .status(newOrder.getStatus())
                .warehouse(newOrder.getWarehouse())
                .build());

        return new OrderResponseDTO(newOrder);
    }

    @Override
    public OrderDetailDTO findById(String orderId) throws OrderException {
        Orders order = orderRepository.findByOrderIdIgnoreCase(orderId);
        if (order == null) {
            throw new OrderException("SYSS-1100", messageSource);
        }
        return findOrderDetailByOrderId(order);
    }

    @Override
    public OrderDetailDTO findByIdAndUserId(String orderId, HttpServletRequest request) throws OrderException {
        User user = userService.getAccount(request);
        Orders order = orderRepository.findByOrderIdIgnoreCaseAndCreatedUser_UserId(orderId, user.getUserId());
//        Orders order = orderRepository.findByOrderIdIgnoreCaseAndCreatedUser_UserIdAndWarehouse_WarehouseId(orderId, user.getUserId(), user.getWarehouse().getWarehouseId().toUpperCase());
        if (order == null) {
            throw new OrderException("SYSS-1100", messageSource);
        }
        return findOrderDetailByOrderId(order);
    }

    public OrderDetailDTO findOrderDetailByOrderId(Orders order) {
        List<OrderHistory> list = orderHistoryRepository.getByOrderId(order.getOrderId());
        Set<OrderHistoryDTO> dtoList = new HashSet<>();
        for (OrderHistory orderHistory : list) {
            OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
            orderHistoryDTO.setOrderedAt(orderHistory.getOrderedAt());
            orderHistoryDTO.setStatus(orderHistory.getStatus());
            orderHistoryDTO.setReason(orderHistory.getReason() != null ? orderHistory.getReason().getDescription() : "");
            dtoList.add(orderHistoryDTO);
        }
        return OrderDetailDTO.builder()
                .orderHistories(dtoList)
                .returnedAt(order.getReturnedAt())
                .deliveredAt(order.getDeliveredAt())
                .numberOfFailedDelivery(order.getNumberOfFailedDelivery())
                .storedAt(order.getStoredAt())
                .createdUser(order.getCreatedUser().getUserId())
                .createdAt(order.getCreatedAt())
                .orderId(order.getOrderId())
                .receiver(new ReceiverResponseDTO(order.getReceiver()))
                .supplier(new SupplierDTO(order.getSupplier()))
                .warehouse(new WarehouseResponseDTO(order.getWarehouse()))
                .status(order.getStatus())
                .build();
    }

    @Override
    @Scheduled(fixedRate = 1200000)
    public void autoHandleOrders() throws WarehouseException {
        List<Orders> listOf100OrdersWithStatus0 = orderRepository.get100OrdersWithStatus0();
        if (!listOf100OrdersWithStatus0.isEmpty()) {
            updateOrders(listOf100OrdersWithStatus0, (long) -1);
        }
    }

    @Override
    public void handleOrders(HttpServletRequest request) throws WarehouseException, OrderException {
        User user = userService.getAccount(request);
        List<Orders> listOf100OrdersWithStatus0 = orderRepository.find100OrdersWithStatus0ByUserId(user.getUserId());
//        List<Orders> listOf100OrdersWithStatus0 = orderRepository.get100OrdersWithStatus0();
        if (listOf100OrdersWithStatus0.isEmpty()) {
            throw new OrderException("SYSS-1102", messageSource);
        }
        updateOrders(listOf100OrdersWithStatus0, user.getUserId());
    }

    @Override
    public void exportLabelOrders(List<String> orderIdList, ServletOutputStream outputStream) throws OrderException, IOException, WriterException, DataTypeException {
        if (orderIdList.size() > 10) {
            throw new OrderException("SYSS-0007", messageSource);
        }
        String currentDate = formatter.formatDate();

        File labelTempFile = new File(labelPath);
        File tempFile = File.createTempFile("LabelTempCopy", ".xlsx");

        // Tạo bản sao của file gốc
        Files.copy(labelTempFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Đổi tên bản sao thành tên mới
        String newFileName = "Labels_" + currentDate + ".xlsx";
        File newFile = new File(labelTempFile.getParent(), newFileName);
        tempFile.renameTo(newFile);

        XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(newFile);

        for (String orderId : orderIdList) {
            Orders order = orderRepository.findById(orderId.toUpperCase()).orElseThrow(() -> new OrderException("SYSS-1100", messageSource));

            // Tạo sheet mới
            XSSFSheet sheet = workbook.cloneSheet(0); // Clone từ sheet mẫu (sheet 0)
            workbook.setSheetName(workbook.getSheetIndex(sheet), orderId);
            sheet.getRow(3).getCell(12).setCellValue(order.getOrderId());

            //supplier
            sheet.getRow(7).getCell(1).setCellValue(order.getSupplier().getSupplierName());
            sheet.getRow(8).getCell(1).setCellValue(order.getSupplier().getAddress());
            sheet.getRow(10).getCell(2).setCellValue(order.getSupplier().getPhone());

            //receiver
            sheet.getRow(7).getCell(9).setCellValue(order.getReceiver().getReceiverName());
            sheet.getRow(8).getCell(9).setCellValue(order.getReceiver().getAddress());
            sheet.getRow(10).getCell(10).setCellValue(order.getReceiver().getPhone());

            sheet.getRow(17).getCell(2).setCellValue(order.getWarehouse().getWarehouseName());
            sheet.getRow(17).getCell(12).setCellValue(order.getWarehouse().getWarehouseId());

            //createdDate
            sheet.getRow(26).getCell(11).setCellValue(formatter.formatDate(order.getCreatedAt()));
            sheet.getRow(27).getCell(11).setCellValue(formatter.formatTime(order.getCreatedAt()));

            //generate barcode for order
            String barcodePath = barcodeAndQrcodeGenerator.generateCode128BarcodeImage(order.getOrderId());
            //add picture data to this workbook.
            barchart.addPicToExcel(barcodePath,workbook,sheet,9,1);

            //generate qrcode for order
            String qrcodePath = barcodeAndQrcodeGenerator.generateQRCodeImage(qrcodeTextFile + order.getOrderId());
            //add picture data to this workbook.
            barchart.addPicToExcel(qrcodePath, workbook, sheet, 11, 19);
        }
//         Xóa sheet mẫu
        workbook.removeSheetAt(0);

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateOrders(List<Orders> list, Long userId) throws WarehouseException {
        List<Warehouse> warehouses = warehouseRepository.findAvailableWarehouse();
        if (warehouses.isEmpty()) {
            throw new WarehouseException("SYSS-3004", messageSource);
        }
        List<OrderHistory> orderHistories = new ArrayList<>();
        for (Orders order : list) {
            Warehouse closetWarehouse = findClosestWarehouseToReceiver(order, warehouses);

            //update order
            order.setStatus(1);
            order.setStoredAt(LocalDateTime.now());
            order.setWarehouse(closetWarehouse);

            //update orderHistory
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrderedAt(order.getStoredAt());
            orderHistory.setOrder(order);
            orderHistory.setStatus(1);
            orderHistory.setUserId(userId);
            orderHistory.setWarehouse(closetWarehouse);
            orderHistories.add(orderHistory);

            //update warehouse
            closetWarehouse.setStock(closetWarehouse.getStock() + 1);
            warehouses.add(closetWarehouse);
        }
        orderRepository.saveAll(list);
        orderHistoryRepository.saveAll(orderHistories);
        warehouseRepository.saveAll(warehouses);
    }


    public Double distanceBetween2Points(Warehouse warehouse, Orders order) {
        double R = 6371;
        double dLat = (order.getReceiver().getLatitude() - warehouse.getLatitude()) * (Math.PI / 180);
        double dLon = (order.getReceiver().getLongitude() - warehouse.getLongitude()) * (Math.PI / 180);
        double la1ToRad = warehouse.getLatitude() * (Math.PI / 180);
        double la2ToRad = order.getReceiver().getLatitude() * (Math.PI / 180);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
                * Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public Warehouse findClosestWarehouseToReceiver(Orders order, List<Warehouse> warehouses) {
        double minDistance = distanceBetween2Points(warehouses.get(0), order);
        Warehouse closestWarehouse = warehouses.get(0);
        for (Warehouse warehouse : warehouses) {
            if (distanceBetween2Points(warehouse, order) < minDistance) {
                closestWarehouse = warehouse;
            }
        }
        return closestWarehouse;
    }


    @Override
    public List<OrdersResponseDTO> searchOrderCode(String orderCode, User user) {
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("USER"))) {
            List<Orders> list = orderRepository.findAllByOrderIdContainsIgnoreCaseAndCreatedUser(orderCode, user);
            return list.stream().map(OrdersResponseDTO::new).toList();
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {
            List<Orders> ordersList = orderRepository.findAllByOrderIdContainsIgnoreCase(orderCode);
            return ordersList.stream().map(OrdersResponseDTO::new).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<OrdersResponseDTO> searchSupplierPhoneAndReceiverPhone(String phone, User user) {
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("USER"))) {
            List<Orders> list = orderRepository.findAllByReceiver_PhoneOrAndSupplier_PhoneContainsIgnoreCaseAndCreatedUser(phone, phone, user);
            return list.stream().map(OrdersResponseDTO::new).toList();
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {
            List<Orders> list = orderRepository.findAllByReceiver_PhoneOrAndSupplier_PhoneContainsIgnoreCase(phone, phone);
            return list.stream().map(OrdersResponseDTO::new).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<OrdersResponseDTO> statusOrder(Integer status, Long userId) throws UserException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("SYSS-0002", messageSource));
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("USER"))) {
            List<Orders> list = orderRepository.findAllByStatusAndCreatedUser(status, user);
            return list.stream().map(OrdersResponseDTO::new).toList();
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {
            List<Orders> ordersList = orderRepository.findAllByStatus(status);
            return ordersList.stream().map(OrdersResponseDTO::new).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<OrdersResponseDTO> filterWareHouseCode(String warehouseCode, User user) throws WarehouseException {
        Warehouse warehouse = warehouseRepository.findById(warehouseCode.toUpperCase()).orElseThrow(() -> new WarehouseException("SYSS-0002", messageSource));

        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("USER"))) {
            List<Orders> list = orderRepository.findAllByWarehouse_WarehouseIdAndCreatedUser(warehouse.getWarehouseId(), user);
            return list.stream().map(OrdersResponseDTO::new).toList();
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {
            List<Orders> ordersList = orderRepository.findAllByWarehouse_WarehouseId(warehouse.getWarehouseId());
            return ordersList.stream().map(OrdersResponseDTO::new).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public Page<OrdersResponseDTO> page(Pageable pageable, User user) {
        if (user.getRoles().stream().allMatch(role -> "USER".equalsIgnoreCase(role.getRoleName()))) {
            return orderRepository.findAllByCreatedUser(pageable, user).map(OrdersResponseDTO::new);
        } else if (user.getRoles().stream().allMatch(role -> "ADMIN".equalsIgnoreCase(role.getRoleName()))) {
            return orderRepository.findAll(pageable).map(OrdersResponseDTO::new);
        }
        return Page.empty();
    }

    @Override
    public Page<OrderResponseDTO> getAll(Pageable pageable) {
        Page<Orders> page = orderRepository.findAll(pageable);
        return page.map(OrderResponseDTO::new);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmDelivery(Long userId, OrderRequestConfirmDTO orderRequestConfirmDTO) throws Exception {
        try {
            // Kiểm tra xem người dùng có tồn tại hay không
            User user = userRepository.findById(userId).orElseThrow(() -> new UserException("SYSS-0002", messageSource));
            Orders order = orderRepository.findById(orderRequestConfirmDTO.getOrderId().toUpperCase()).orElseThrow(() -> new OrderException("SYSS-1100", messageSource));
            if (!order.getCreatedUser().getUserId().equals(user.getUserId())) {
                throw new UserException("SYSS-3009", messageSource);
            }
            // Kiểm tra trạng thái hợp lệ cho việc xác nhận giao hàng
            if (order.getStatus() == 2) {
                throw new OrderException("SYSS-3008", messageSource);
            }
            Long reasonId = Long.valueOf(orderRequestConfirmDTO.getReasonId());
            int statusOrder = Integer.parseInt(orderRequestConfirmDTO.getStatusOrder());
            if (statusOrder == 3) {
                if (order.getNumberOfFailedDelivery() < 3) {
                    // Cập nhật thông tin khi giao hàng thất bại
                    order.setNumberOfFailedDelivery(order.getNumberOfFailedDelivery() + 1);
                    order.setReturnedAt(LocalDateTime.now());
                    order.setStatus(3);
                    // Kiểm tra lý do và cập nhật

                    Reason reason = reasonRepository.findById(reasonId)
                            .orElseThrow(() -> new ReasonException("SYSS-0014", messageSource));
                    if (reason != null) {
                        order.setReason(reason);
                    } else {
                        throw new ReasonException("SYSS-0012", messageSource);
                    }
                } else {
                    throw new OrderException("SYSS-3005", messageSource);
                }
            }
            if (statusOrder == 2) {
                if (order.getNumberOfFailedDelivery() < 3) {
                    // Cập nhật thông tin khi giao hàng thành công
                    Reason reason = reasonRepository.findById(0L).orElse(null);
                    order.setReason(reason);
                    order.setStatus(2);
                    order.setDeliveredAt(LocalDateTime.now());
                    int currentStock = order.getWarehouse().getStock();
                    int updatedStock = currentStock - 1;
                    if (currentStock < order.getWarehouse().getCapacity()) {
                        order.getWarehouse().setStock(updatedStock);
                        warehouseRepository.save(order.getWarehouse());
                    } else {
                        throw new WarehouseException("SYSS-0042", messageSource);
                    }
                } else {
                    throw new OrderException("SYSS-3005", messageSource);
                }
            }
            orderRepository.save(order);
            // Lưu lịch sử đơn hàng
            OrderHistory history = new OrderHistory();
            history.setUserId(user.getUserId());
            history.setOrderedAt(LocalDateTime.now());
            history.setOrder(order);
            Warehouse warehouse = warehouseRepository.findByWarehouseIdIgnoreCase(order.getWarehouse().getWarehouseId());

            history.setWarehouse(warehouse);
            history.setReason(order.getReason());
            history.setStatus(order.getStatus());
            orderHistoryRepository.save(history);
        } catch (NumberFormatException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new OrderException("SYSS-3006", messageSource);
        }
    }

    @Override
    public List<OrdersResponseDTO> findAllListOrder(User user) {
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equals("USER"))) {
            List<Orders> list = orderRepository.findAllByCreatedUser(user);
            return list.stream().map(OrdersResponseDTO::new).toList();
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equals("ADMIN"))) {
            List<Orders> ordersList = orderRepository.findAll();
            return ordersList.stream().map(OrdersResponseDTO::new).toList();
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleReturns() {
        try {
//            0: Đơn hàng mới; 1: Lưu kho; 2: Giao hàng thành công; 3: Giao hàng thất bại; 4: Hoàn hàng
            List<Orders> ordersList = orderRepository.findByStatusOrderByReturnedAtAsc(3);
            if (ordersList.isEmpty()) {
                System.out.println("SYSS-0037");
            }
            for (Orders order : ordersList) {
                int currentStock = order.getWarehouse().getStock();
                int updatedStock = currentStock - 1;
                if (currentStock < order.getWarehouse().getCapacity()) {
                    order.getWarehouse().setStock(updatedStock);
                    warehouseRepository.save(order.getWarehouse());
                }
                order.getWarehouse().setStock(updatedStock);
                warehouseRepository.save(order.getWarehouse());
                order.setReturnedAt(LocalDateTime.now());
                order.setStatus(4);
                orderRepository.save(order);
                emailService.sendEmailToRecipient(order);
                emailService.sendEmailToSupplier(order);
                OrderHistory history = new OrderHistory();
                history.setUserId(-1L);
                history.setOrderedAt(LocalDateTime.now());
                history.setOrder(order);
                Warehouse warehouse = warehouseRepository.findByWarehouseIdIgnoreCase(order.getWarehouse().getWarehouseId());
                history.setReason(order.getReason());
                history.setWarehouse(warehouse);
                history.setStatus(order.getStatus());
                orderHistoryRepository.save(history);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
    @Override
    public Page<OrderResponseDTO> searchOrdersWithPagination(String orderId, String phone, String phoneNumber, Integer status, String warehouseId, Pageable pageable) {
        Page<Orders> page = orderRepository.searchOrders(orderId, phone, phoneNumber, status, warehouseId, pageable);
        return page.map(OrderResponseDTO::new);
    }

    @Override
    public Page<OrderResponseDTO> searchOrdersOfUserWithPagination(HttpServletRequest request, String orderId, String phone, String phoneNumber, Integer status, String warehouseId, Pageable pageable) {
        User user = userService.getAccount(request);
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("USER"))) {
            Page<Orders> page = orderRepository.searchOrdersOfUser(user.getUserId(), orderId, phone, phoneNumber, status, warehouseId, pageable);
            return page.map(OrderResponseDTO::new);
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {
            Page<Orders> ordersPage = orderRepository.searchOrdersOf(orderId, phone, phoneNumber, status, warehouseId, pageable);
            return ordersPage.map(OrderResponseDTO::new);
        }
        return Page.empty();
    }

    @Override
    public String getOrderId() throws DataTypeException {
        String currentDate = formatter.formatDate();

        Long maxId = orderRepository.findMaxIdByDate(currentDate);
        Long nextSequencePart = (maxId != null) ? maxId + 1 : 1;
        String sequencePart = String.format("%05d", nextSequencePart);
        return "DH-" + currentDate + "-" + sequencePart;
    }

    @Override
    public Page<OrderResponseDTO> getAllByUserWithPagination(HttpServletRequest request, Pageable pageable) {
        User user = userService.getAccount(request);
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("USER"))) {
            Page<Orders> page = orderRepository.findAllByCreatedUser_UserId(user.getUserId(), pageable);
            return page.map(OrderResponseDTO::new);
        }
        if (user.getRoles().stream().allMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {
            Page<Orders> page = orderRepository.findAll(pageable);
            return page.map(OrderResponseDTO::new);
        }
        return Page.empty();
    }

    @Override
    public Page<OrderResponseDTO> getAllByWithPagination(Pageable pageable) {
        Page<Orders> page = orderRepository.findAll(pageable);
        return page.map(OrderResponseDTO::new);
    }

    @Override
    public Page<OrdersResponseDTO> searchByAllOrders(String orderId,String warehouseId, String receiverPhone, String supplierPhone, User user,Pageable pageable) {
        Page<Orders>ordersPage=orderRepository.findAllByCriteria(orderId,warehouseId,receiverPhone,supplierPhone,user,pageable);
        return ordersPage.map(OrdersResponseDTO::new);
    }
}
