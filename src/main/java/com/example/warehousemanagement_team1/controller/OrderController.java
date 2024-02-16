package com.example.warehousemanagement_team1.controller;

import com.example.warehousemanagement_team1.dto.OrderDetailDTO;
import com.example.warehousemanagement_team1.dto.OrderHistoryDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestConfirmDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestDTO;
import com.example.warehousemanagement_team1.dto.response.OrderResponseDTO;
import com.example.warehousemanagement_team1.dto.response.OrdersResponseDTO;
import com.example.warehousemanagement_team1.dto.response.WarehouseResponseDTO;
import com.example.warehousemanagement_team1.exception.*;
import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.model.User;
import com.example.warehousemanagement_team1.service.excel.ExcelService;
import com.example.warehousemanagement_team1.service.order.ImportFileService;
import com.example.warehousemanagement_team1.service.order.OrderService;
import com.example.warehousemanagement_team1.service.user.UserService;
import com.example.warehousemanagement_team1.utils.formatter.Formatter;
import com.example.warehousemanagement_team1.utils.validation.Validator;
import com.google.zxing.WriterException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders/")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private Formatter formatter;
    @Autowired
    private UserService userService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private Validator validator;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ImportFileService importFileService;

    @PostMapping("add")
    public ResponseEntity<?> addOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO, HttpServletRequest request) throws DataTypeException {
        OrderResponseDTO orderResponseDTO = orderService.save(orderRequestDTO, request);
        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId, HttpServletRequest request) throws OrderException, DataTypeException {
        String parsedOrderId = validator.validateString(orderId);
        //        OrderDetailDTO orderDetailDTO = orderService.findById(orderId);
        OrderDetailDTO orderDetailDTO = orderService.findByIdAndUserId(parsedOrderId, request);
        if (orderDetailDTO == null) {
            throw new OrderException("SYSS-1100", messageSource);
        }
        return new ResponseEntity<>(orderDetailDTO, HttpStatus.OK);
    }

    @PatchMapping("handle-orders")
    public ResponseEntity<?> handleOrders(HttpServletRequest request) throws WarehouseException, OrderException {
        orderService.handleOrders(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("export-labels")
    public ResponseEntity<?> exportLabelForOrders(@RequestParam(value = "orderId", required = false) List<String> orderIdList,
                                                  ServletOutputStream outputStream)
            throws IOException, OrderException, WriterException, DataTypeException {

        if (orderIdList == null || orderIdList.isEmpty()) {
            throw new DataTypeException("SYSS-0011", messageSource, "orderId");
        }
        String currentDate = formatter.formatDate();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        orderService.exportLabelOrders(orderIdList, outputStream);

        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + currentDate + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("search-pagination")
    public ResponseEntity<?> searchWithPagination(@RequestParam(value = "orderId", required = false) String orderId,
                                                  @RequestParam(value = "phone", required = false) String phone,
                                                  @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                                  @RequestParam(value = "status", required = false) Integer status,
                                                  @RequestParam(value = "warehouseId", required = false) String warehouseId,
                                                  @RequestParam(value = "page", defaultValue = "0") String page,
                                                  @RequestParam(value = "limit", defaultValue = "10") String limit,
                                                  @RequestParam(value = "sort", defaultValue = "orderId") String sort,
                                                  @RequestParam(value = "order", defaultValue = "asc") String order,
                                                  HttpServletRequest request) throws DataTypeException, OrderException {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Integer parsedPage = validator.validateInteger(page);
        Integer parsedLimit = validator.validateInteger(limit);
//        Integer parsedStatus = validator.validateInteger(status);

        Pageable pageable = PageRequest.of(parsedPage, parsedLimit, Sort.by(direction, sort));
        Page<OrderResponseDTO> orderPage;
        if ((orderId == null || orderId.isEmpty())
                && (phone == null || phone.isEmpty())
                && (phoneNumber == null || phoneNumber.isEmpty())
                && status == null
                && (warehouseId == null || warehouseId.isEmpty())) {
            orderPage = orderService.getAllByUserWithPagination(request, pageable);
        } else {
            orderPage = orderService.searchOrdersOfUserWithPagination(request, orderId, phone, phoneNumber, status, warehouseId, pageable);
        }
        if (orderPage.isEmpty()) {
            throw new OrderException("SYSS-1103", messageSource);
        }
        return new ResponseEntity<>(orderPage, HttpStatus.OK);
    }


    //list orders
    @GetMapping("list")
    public ResponseEntity<?> listOrderUser(HttpServletRequest request) {
        User user = userService.getAccount(request);
        List<OrdersResponseDTO> list = orderService.findAllListOrder(user);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //search theo IdOrder
    @GetMapping("searchCode")
    public ResponseEntity<?> searchOrderCode(@RequestParam(name = "searchCode", defaultValue = "", required = false) String searchCode, HttpServletRequest request) {
        User user = userService.getAccount(request);
        List<OrdersResponseDTO> list = orderService.searchOrderCode(searchCode, user);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //search số điện thoại
    @GetMapping("searchPhone")
    public ResponseEntity<?> searchSupplierPhoneAndReceiverPhone(@RequestParam(name = "searchPhone", defaultValue = "", required = false) String searchPhone, HttpServletRequest request) {
        User user = userService.getAccount(request);
        List<OrdersResponseDTO> list = orderService.searchSupplierPhoneAndReceiverPhone(searchPhone, user);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //phân trang
    @GetMapping("page")
    public ResponseEntity<?> page(@RequestParam(name = "page", defaultValue = "0") String page,
                                  @RequestParam(name = "limit", defaultValue = "10") String limit, HttpServletRequest request) throws OrderException {
        try {
            User user = userService.getAccount(request);
            Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit));
            Page<OrdersResponseDTO> list = orderService.page(pageable, user);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NumberFormatException e) {
            throw new OrderException("SYSS-0002", messageSource);
        }
    }

    //lọc theo trangj thái
    @GetMapping("/status-orders")
    public ResponseEntity<?> statusOrder(@RequestParam String status, HttpServletRequest request) throws UserException, OrderException {
        try {
            User user = userService.getAccount(request);
            int statusValue = Integer.parseInt(status);

            if (statusValue < 0 || statusValue > 4) {
                throw new OrderException("SYSS-0039", messageSource);
            }
            List<OrdersResponseDTO> list = orderService.statusOrder(statusValue, user.getUserId());
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NumberFormatException e) {
            throw new OrderException("SYSS-0013", messageSource);
        }
    }

    //lọc mã kho hàng ra danh sách order
    @GetMapping("order-warehouse")
    public ResponseEntity<?> filterWareHouseCode(@RequestParam(name = "warehouseId", required = false, defaultValue = "") String warehouseId, HttpServletRequest request) throws WarehouseException, ResponseException {
        User user = userService.getAccount(request);
        List<OrdersResponseDTO> list = orderService.filterWareHouseCode(warehouseId, user);
        if (list.isEmpty()) {
            throw new ResponseException("SYSS-0040", messageSource);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //hoàn đơn hủy
    @Scheduled(cron = "0 0/1 10-22 * * ?") //Chạy mỗi 1 tiếng 1 lần từ 10h đến 22h
    public void handleReturns() {
        orderService.handleReturns();
    }

    //    @Scheduled(cron = "0 0/1 10-22 * * ?") // Chạy mỗi 1 phút từ 10h đến 22h
    //hoàn trả đơn hàng
    @PutMapping("confirm-status")
    public ResponseEntity<?> confirmDelivery(@Valid @RequestBody OrderRequestConfirmDTO orderRequestConfirmDTO, HttpServletRequest request) throws Exception {
        try {
            User user = userService.getAccount(request);
            int status = Integer.parseInt(orderRequestConfirmDTO.getStatusOrder());
            if (status < 2 || status > 3) {
                throw new OrderException("SYSS-0015", messageSource);
            }
            orderService.confirmDelivery(user.getUserId(), orderRequestConfirmDTO);
            return new ResponseEntity<>("Thành công", HttpStatus.OK);
        } catch (NumberFormatException e) {
            throw new OrderException("SYSS-0002", messageSource);
        }
    }


    @PostMapping("/export")
    public ResponseEntity<String> export(@RequestParam(name = "file") MultipartFile file, HttpServletRequest request) {
        try {
            User user = userService.getAccount(request);
            String expectedFileName = "INB_ImportData.xlsx";

            if (!Objects.equals(file.getOriginalFilename(), expectedFileName)) {
                return new ResponseEntity<>("Tên tệp không đúng. Chỉ chấp nhận tệp có tên là " + expectedFileName, HttpStatus.BAD_REQUEST);
            }
            excelService.imPortOrders(file, user);
            return new ResponseEntity<>("Thành công", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi đang xử lý import đơn hàng", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("add-by-file")
    public ResponseEntity<?> addByFile(@RequestParam(name = "file") MultipartFile file, HttpServletResponse response, HttpServletRequest request) throws OrderException, DataTypeException {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] ops = importFileService.importExcel(outputStream, file, request);

            if (ops != null && ops.length > 0) {
                ByteArrayResource resource = new ByteArrayResource(ops);
                String currentDate = formatter.formatDate();
                return ResponseEntity.ok()
                        .contentLength(resource.contentLength())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"INB_ImportError_" + currentDate + ".xlsx\"")
                        .body(resource);
            } else {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while processing the file.", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while processing the file.", e);
        }
    }
    @GetMapping("all-by-orders")
    public ResponseEntity<?> searchAllByOrder(@RequestParam(name = "orderId", defaultValue = "") String orderId,
                                              @RequestParam(name = "phone", defaultValue = "") String phone,
                                              @RequestParam(name = "phoneNumber", defaultValue = "") String phoneNumber,
                                              @RequestParam(name = "warehouseId", defaultValue = "") String warehouseId,
                                              @RequestParam(name = "page", defaultValue = "0") String page,
                                              @RequestParam(name = "limit", defaultValue = "10") String limit,
                                              @RequestParam(name = "sort", defaultValue = "orderId") String sort,
                                              @RequestParam(name = "order", defaultValue = "asc") String order,
                                              HttpServletRequest request) throws DataTypeException, OrderException {
        try {
            User user = userService.getAccount(request);
            Pageable pageable;
            if (order.equals("DESC")) {
                pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), Sort.by(sort).descending());
            } else {
                pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), Sort.by(sort).ascending());
            }
            Page<OrdersResponseDTO> list = orderService.searchByAllOrders(orderId, warehouseId, phone, phoneNumber, user, pageable);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NumberFormatException exception) {
            throw new OrderException("SYSS-0013", messageSource);
        }
    }
//    public ResponseEntity<?> searchOrders(@RequestParam(name = "orderId", defaultValue = "") String orderId,
//                                                     @RequestParam(name = "warehouseId", defaultValue = "") String warehouseId,
//                                                     @RequestParam(name = "receiverPhone", defaultValue = "") String receiverPhone,
//                                                     @RequestParam(name = "supplierPhone", defaultValue = "") String supplierPhone,
//                                                     @RequestParam(name = "page", defaultValue = "0") int page,
//                                                     @RequestParam(name = "size", defaultValue = "10") int size,
//                                                     @RequestParam(name = "sort", defaultValue = "orderId,asc") String sort,
//                                                     HttpServletRequest request) {
//        User user = userService.getAccount(request);
//        String[] sortParams = sort.split(",");
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParams[0]).ascending());
//        if (sortParams.length == 2 && sortParams[1].equalsIgnoreCase("desc")) {
//            pageable = PageRequest.of(page, size, Sort.by(sortParams[0]).descending());
//        }
//        Page<OrdersResponseDTO> orders = orderService.searchByAllOrders(orderId, warehouseId, receiverPhone, supplierPhone, user, pageable);
//        return new ResponseEntity<>(orders, HttpStatus.OK);
//    }

}


