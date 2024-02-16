package com.example.warehousemanagement_team1.service.excel;

import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.model.*;
import com.example.warehousemanagement_team1.repository.*;
import com.example.warehousemanagement_team1.utils.validation.Validator;
import io.micrometer.common.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService{
    @Value("${errorPath}")
    String errorPath;
    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReceiverRepository receiverRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private MessageSource messageSource;
    @Transactional(rollbackFor = Exception.class)
    public void imPortOrders(MultipartFile file, User user) throws Exception {
        try {
            try (InputStream fileInputStream = file.getInputStream();
                 Workbook workbook = new XSSFWorkbook(fileInputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                List<String> errorMessages = new ArrayList<>();

                for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);

                    try {
                        processRow(row, user, workbook, errorMessages, i);
                    } catch (Exception e) {
                        log.error("Hàng xử lý lỗi " + (i + 1), e);
                        errorMessages.add("Hàng " + (i + 1) + ": " + e.getMessage());
                    }
                }
                if (!errorMessages.isEmpty()) {
                    log.error("SYSS-0035",messageSource);
                    errorMessages.forEach(log::error);

                    Sheet errorSheet = workbook.getSheet("ErrorSheet");

                    if (errorSheet == null) {
                        errorSheet = workbook.createSheet("ErrorSheet");
                        Row headerRow = errorSheet.createRow(0);
                        headerRow.createCell(0).setCellValue("Error Date/Time");
                        headerRow.createCell(1).setCellValue("Error Information");
                    }

                    int rowNum = errorSheet.getLastRowNum() + 1;

//                    for (String errorMessage : errorMessages) {
//                        Row errorRow = errorSheet.createRow(rowNum++);
//                        LocalDateTime now = LocalDateTime.now();
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//                        String formattedDateTime = now.format(formatter);
//                        errorRow.createCell(0).setCellValue(formattedDateTime);
//                        errorRow.createCell(1).setCellValue(errorMessage);
//                    }

                    try (FileOutputStream errorFileOut = new FileOutputStream(errorPath)) {
                        workbook.write(errorFileOut);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi ghi dữ liệu mới vào tệp ImportFileService", e);
                    } finally {
                        try {
                            workbook.close();
                        } catch (IOException e) {
                            throw new RuntimeException("Lỗi đóng sổ làm việc", e);
                        }
                    }
                    throw new OrderException("SYSS-0035",messageSource);
                }
            } catch (IOException e) {
                throw new OrderException("Lỗi đọc file");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error", e);
        }
    }
    private void processRow(Row row, User user, Workbook workbook, List<String> errorMessages, int rowIndex) {
        try {
            if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                return;
            }
            Supplier supplier = new Supplier();
            Receiver receiver = new Receiver();
            Orders orders = new Orders();
            OrderHistory orderHistory = new OrderHistory();
            String PREFIX = "DH";
            DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
            LocalDate today = LocalDate.now();
            String datePart = today.format(DATE_FORMATTER);
            Long maxId = orderRepository.findMaxIdByDate(datePart);
            Long nextSequencePart = (maxId != null) ? maxId + 1 : 1;
            String sequencePart = String.format("%05d", nextSequencePart);
            String orderCode = PREFIX + "-" + datePart + "-" + sequencePart;

            String excelId = getStringCellValue(row.getCell(0));
            orders.setOrderId(orderCode);
            supplier.setSupplierName(getStringCellValue(row.getCell(1)));
            supplier.setPhone(getStringCellValue(row.getCell(2)));
            supplier.setEmail(getStringCellValue(row.getCell(3)));
            supplier.setAddress(getStringCellValue(row.getCell(4)));

            receiver.setReceiverName(getStringCellValue(row.getCell(5)));
            receiver.setPhone(getStringCellValue(row.getCell(6)));
            receiver.setEmail(getStringCellValue(row.getCell(7)));
            receiver.setAddress(getStringCellValue(row.getCell(8)));
//            Cell latitudeCell = row.getCell(9);
//
//            if (latitudeCell == null || latitudeCell.getCellType() == CellType.BLANK) {
//                writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
//            } else {
//                try {
//                    if (latitudeCell.getCellType() == CellType.NUMERIC) {
//                        try {
//                            double numericValue = latitudeCell.getNumericCellValue();
//                            receiver.setLatitude(numericValue);
//                        } catch (Exception e) {
//                            writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
//                        }
//                    } else if (latitudeCell.getCellType() == CellType.STRING) {
//                        try {
//                            String latitudeValue = latitudeCell.getStringCellValue();
//                            if (latitudeValue.length() > 19 || latitudeValue.length() < 3){
//                                writeErrorToExcel("Giá trị tọa độ ít nhất 3 kí tự nhiều nhất 18 ký tự ", row.getRowNum(), rowIndex-2, workbook, errorMessages);
//                            }
//                            double numericValue = Double.parseDouble((latitudeValue));
//                            receiver.setLongitude(numericValue);
//                        }catch (Exception e) {
//                            writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
//                        }
//                    } else {
//                        writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
//                    }
//                } catch (Exception e) {
//                    writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
//                }
//            }
            //vĩ độ
            Cell latitudeCell = row.getCell(9);

            if (latitudeCell == null || latitudeCell.getCellType() == CellType.BLANK) {
                writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
            } else {
                try {
                    switch (latitudeCell.getCellType()) {
                        case NUMERIC:
                            double numericValue = latitudeCell.getNumericCellValue();

                            if (numericValue >= -90.0 && numericValue <= 90.0) {
                                receiver.setLatitude(numericValue);
                            } else {
                                writeErrorToExcel("Giá trị tọa độ không hợp lệ", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                            }
                            break;

                        case STRING:
                            String latitudeValue = latitudeCell.getStringCellValue();

                            try {
                                double numericValueFromString = Double.parseDouble(latitudeValue);
                                if (numericValueFromString >= -90.0 && numericValueFromString <= 90.0) {
                                    receiver.setLatitude(numericValueFromString);
                                } else {
                                    writeErrorToExcel("Giá trị tọa độ không hợp lệ", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                                }
                            } catch (NumberFormatException e) {
                                writeErrorToExcel("Lỗi chuyển đổi dữ liệu sang số", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                            }
                            break;
                        default:
                            writeErrorToExcel("Giá trị tọa độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                    }
                } catch (Exception e) {
                    writeErrorToExcel("Lỗi xử lý giá trị tọa độ: " + e.getMessage(), row.getRowNum(), rowIndex-2, workbook, errorMessages);
                }
            }
//            Cell longitudeCell = row.getCell(10);
////
////            if (longitudeCell == null || longitudeCell.getCellType() == CellType.BLANK) {
////                writeErrorToExcel("Giá trị kinh độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
////            } else {
////                try {
////                    if (longitudeCell.getCellType() == CellType.NUMERIC) {
////                        try {
////                            double numericValue = longitudeCell.getNumericCellValue();
////                            receiver.setLongitude(numericValue);
////                        } catch (Exception e) {
////                            writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự", row.getRowNum(), rowIndex-2, workbook, errorMessages);
////                        }
////                    } else if (longitudeCell.getCellType() == CellType.STRING) {
////                       try {
////                           String latitudeValue = longitudeCell.getStringCellValue();
////                           if (latitudeValue.length() > 19 || latitudeValue.length() < 3){
////                               writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự nhiều nhất 18 ký tự ", row.getRowNum(), rowIndex-2, workbook, errorMessages);
////                           }
////                           Double numericValue = Double.parseDouble(latitudeValue);
////                           receiver.setLatitude(numericValue);
////                       }catch (Exception e) {
////                           writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự", row.getRowNum(), rowIndex-2, workbook, errorMessages);
////                       }
////                    } else {
////                        writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự", row.getRowNum(), rowIndex-2, workbook, errorMessages);
////                    }
////                } catch (Exception e) {
////                    writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự", row.getRowNum(), rowIndex-2, workbook, errorMessages);
////                }
////            }

            Cell longitudeCell = row.getCell(10);

            if (longitudeCell == null || longitudeCell.getCellType() == CellType.BLANK) {
                writeErrorToExcel("Giá trị kinh độ không được bỏ trống", row.getRowNum(), rowIndex-2, workbook, errorMessages);
            } else {
                try {
                    switch (longitudeCell.getCellType()) {
                        case NUMERIC:
                            double numericValue = longitudeCell.getNumericCellValue();
                            if (numericValue >= -180.0 && numericValue <= 180.0) {
                                receiver.setLongitude(numericValue);
                            } else {
                                writeErrorToExcel("Giá trị kinh độ không hợp lệ", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                            }
                            break;

                        case STRING:
                            String longitudeValue = longitudeCell.getStringCellValue();
                            if (longitudeValue.length() > 19 || longitudeValue.length() < 3) {
                                writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự nhiều nhất 18 ký tự", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                            } else {
                                try {
                                    // Convert the string value to Double
                                    Double numericValueFromString = Double.parseDouble(longitudeValue);
                                    if (numericValueFromString >= -180.0 && numericValueFromString <= 180.0) {
                                        receiver.setLongitude(numericValueFromString);
                                    } else {
                                        writeErrorToExcel("Giá trị kinh độ không hợp lệ", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                                    }
                                } catch (NumberFormatException e) {
                                    writeErrorToExcel("Lỗi chuyển đổi dữ liệu sang số", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                                }
                            }
                            break;

                        default:
                            writeErrorToExcel("Giá trị kinh độ phải có ít nhất 3 kí tự", row.getRowNum(), rowIndex-2, workbook, errorMessages);
                    }
                } catch (Exception e) {
                    writeErrorToExcel("Lỗi xử lý giá trị kinh độ: " + e.getMessage(), row.getRowNum(), rowIndex-2, workbook, errorMessages);
                }
            }


            orders.setCreatedAt(LocalDateTime.now());
            orders.setNumberOfFailedDelivery(0);
            orders.setSupplier(supplier);
            orders.setReceiver(receiver);
            orders.setCreatedUser(user);

            // Validate
            List<String> validationErrors = validateOrders(orders, rowIndex);

            if (!validationErrors.isEmpty()) {
                int columnIndex = 1;
                for (String validationError : validationErrors) {
                    writeErrorToExcel(validationError, rowIndex, columnIndex, workbook, errorMessages);
                    columnIndex++;
                }
            } else {
                receiverRepository.save(receiver);
                supplierRepository.save(supplier);
                orderRepository.save(orders);

                orderHistory.setOrder(orders);
                orderHistory.setOrderedAt(LocalDateTime.now());
                orderHistory.setUserId(orders.getCreatedUser().getUserId());
                orderHistory.setWarehouse(orders.getWarehouse());
                orderHistory.setStatus(orders.getStatus());
                orderHistoryRepository.save(orderHistory);
            }
        } catch (Exception e) {
            log.error("Hàng xử lý lỗi " + (rowIndex + 1), e);
            errorMessages.add("Hàng " + (rowIndex + 1) + ": " + e.getMessage());
        }
    }

    private void writeErrorToExcel(String errorMessage, int rowIndex, int columnIndex, Workbook errorWorkbook, List<String> errorMessages) {
        Sheet errorSheet = errorWorkbook.getSheet("ErrorSheet");

        if (errorSheet == null) {
            errorSheet = errorWorkbook.createSheet("ErrorSheet");
            Row headerRow = errorSheet.createRow(0);
            headerRow.createCell(0).setCellValue("Error Date/Time");
            headerRow.createCell(1).setCellValue("Error Information");
        }

        int rowNum = errorSheet.getLastRowNum() + 1;
        Row errorRow = errorSheet.createRow(rowNum);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        Cell dateCell = errorRow.createCell(0);
        dateCell.setCellValue(formattedDateTime);

        Cell errorMessageCell = errorRow.createCell(1);
//        Cột " + (columnIndex + 1) + ":
//        Cột " + (columnIndex + 1) + ":
        errorMessageCell.setCellValue("Hàng " + (rowIndex + 1) + ", " + errorMessage);

        errorMessages.add(" - Hàng " + (rowIndex + 1) + "," + errorMessage);
    }

    private String getStringCellValue(Cell cell) {
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue() : null;
    }

    private List<String> validateOrders(Orders orders, int rowIndex) {
        List<String> validationErrors = new ArrayList<>();
        if (orders.getSupplier() == null || orders.getReceiver() == null) {
            validationErrors.add("Nhà cung cấp hoặc người nhận không được bỏ trống");
        } else {
            try {
                validateStringField(orders.getSupplier().getSupplierName(), "Tên nhà cung cấp", validationErrors);
                validator.validatePhoneNumber(orders.getSupplier().getPhone(), "Số điện thoại nhà cung cấp", validationErrors);
                validateStringField(orders.getSupplier().getEmail(), "Email nhà cung cấp", validationErrors);
                validateStringField(orders.getSupplier().getAddress(), "Địa chỉ nhà cung cấp", validationErrors);
                validateStringField(orders.getReceiver().getReceiverName(), "Tên người nhận", validationErrors);
                validator.validatePhoneNumber(orders.getReceiver().getPhone(), "Số điện thoại người nhận", validationErrors);
                validateStringField(orders.getReceiver().getEmail(), "Email người nhận", validationErrors);
                validateStringField(orders.getReceiver().getAddress(), "Địa chỉ người nhận", validationErrors);
            }catch (Exception e){
                validationErrors.add(String.valueOf(e.getMessage()));
            }

            try {
//                if (supplierRepository.existsByEmail(orders.getSupplier().getEmail())) {
//                    validationErrors.add("Email nhà cung cấp không được trùng");
//                }

                if (receiverRepository.existsReceiverByEmail(orders.getReceiver().getEmail())) {
                    validationErrors.add("Email người nhận không được trùng");
                }
            } catch (Exception e) {
                validationErrors.add(String.format("Dòng %d: Có lỗi xảy ra trong quá trình kiểm tra email"));
            }
            try {
                if (receiverRepository.existsReceiverByPhone(orders.getReceiver().getPhone())) {
                    validationErrors.add(String.format("Số điện thoại người nhận %s không được trùng", orders.getReceiver().getPhone()));
                    validateStringField(false, "Số điện thoại người nhận không được trùng", validationErrors);
                }
            } catch (Exception e) {
                validationErrors.add(String.format("Dòng %d: Có lỗi xảy ra trong quá trình kiểm tra số điện thoại", rowIndex + 1));
            }

            try {
                validateEmail(orders.getSupplier().getEmail(), "Email nhà cung cấp", rowIndex, validationErrors);
                validateEmail(orders.getReceiver().getEmail(), "Email người nhận", rowIndex, validationErrors);
            } catch (Exception e) {
                validationErrors.add(String.format("Dòng %d: Có lỗi xảy ra trong quá trình kiểm tra email", rowIndex + 1));
            }
        }
        return validationErrors;
    }
    private void validateStringField(boolean condition, String errorMessage, List<String> validationErrors) {
        if (!condition) {
            validationErrors.add(errorMessage);
        }
    }
    private void validateStringField(String fieldValue, String fieldName, List<String> validationErrors) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            validationErrors.add(fieldName + " không thể trống");
        }
    }

    private void validateEmail(String email, String fieldName, int rowIndex, List<String> validationErrors) {
        try {
            if (email == null || email.isEmpty()) {
                validationErrors.add(String.format("Row %d, %s: Email không được để trống", rowIndex + 1, fieldName));
                return;
            }

            if (!validator.isValidEmail(email)) {
                validationErrors.add(String.format("Row %d, %s: Email không hợp lệ ", fieldName));
            }
        } catch (Exception e) {
            validationErrors.add(String.format("Row %d, %s:  Email Lỗi định dạng", fieldName));
        }
    }
}





