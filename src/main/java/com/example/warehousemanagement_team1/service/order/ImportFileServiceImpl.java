package com.example.warehousemanagement_team1.service.order;

import com.example.warehousemanagement_team1.dto.ErrorDTO;
import com.example.warehousemanagement_team1.dto.SupplierDTO;
import com.example.warehousemanagement_team1.dto.request.OrderRequestDTO;
import com.example.warehousemanagement_team1.dto.request.ReceiverRequestDTO;
import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.model.User;
import com.example.warehousemanagement_team1.service.user.UserService;
import com.example.warehousemanagement_team1.utils.formatter.Formatter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ImportFileServiceImpl implements ImportFileService {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private Formatter formatter;
    @Autowired
    private MessageSource messageSource;
    @Value("${errorPath}")
    private String errorPath;

    public String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public String[] headers = {"STT", "Tên NCC", "SĐT NCC", "Email NCC", "Địa chỉ NCC", "Vĩ độ NCC", "Kinh độ NCC", "Tên BNH", "SĐT BNH", "Email BNH", "Địa chỉ BNH", "Vĩ độ BNH", "Kinh độ BNH"};


    public boolean checkExcelFormat(MultipartFile file) {
        return Objects.equals(file.getContentType(), TYPE);
    }

    @Override
    public byte[] importExcel(ServletOutputStream outputStream, MultipartFile file, HttpServletRequest request) throws OrderException {
        if (!checkExcelFormat(file)) {
            throw new OrderException("SYSS-0041",messageSource);
        }
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            List<OrderRequestDTO> ordersList = new ArrayList<>();
            List<ErrorDTO> errorDTOList = new ArrayList<>();
            Iterator<Row> rows = sheet.iterator();

            //skip header
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (rowNumber < 2) {
                    rowNumber++;
                    continue;
                }

                if (currentRow.getCell(1).getStringCellValue().isEmpty()) {
                    break;
                }

                OrderRequestDTO order = new OrderRequestDTO();

                SupplierDTO supplier = new SupplierDTO();
                supplier.setSupplierName(currentRow.getCell(1).getStringCellValue().trim());
                supplier.setPhone(currentRow.getCell(2).getStringCellValue().trim());
                supplier.setEmail(currentRow.getCell(3).getStringCellValue().trim());
                supplier.setAddress(currentRow.getCell(4).getStringCellValue().trim());

                ReceiverRequestDTO receiver = new ReceiverRequestDTO();
                receiver.setReceiverName(currentRow.getCell(7).getStringCellValue().trim());
                receiver.setPhone(currentRow.getCell(8).getStringCellValue().trim());
                receiver.setEmail(currentRow.getCell(9).getStringCellValue().trim());
                receiver.setAddress(currentRow.getCell(10).getStringCellValue().trim());
                receiver.setLatitude((currentRow.getCell(11).getNumericCellValue()));
                receiver.setLongitude((currentRow.getCell(12).getNumericCellValue()));

                order.setSupplier(supplier);
                order.setReceiver(receiver);

                ErrorDTO errorDTO = new ErrorDTO();
                String error = errorOrderRequest(order);
                if (error != null && !error.isEmpty()) {
                    errorDTO.setRow(rowNumber);
                    errorDTO.setMessage(error);
                    errorDTOList.add(errorDTO);
                }

                ordersList.add(order);
                rowNumber++;
            }

            if (errorDTOList.isEmpty()) {
                for (OrderRequestDTO orderRequestDTO : ordersList) {
                    orderService.save(orderRequestDTO, request);
                }
            } else {
                return exportFileWithErrorMessage(outputStream, errorDTOList, file);

            }
            workbook.close();
        } catch (Exception e) {
            throw new OrderException("fail to parse ImportFileService file: " + e.getMessage());
        }
        return null;
    }



    public String checkEmpty(OrderRequestDTO orderRequestDTO) {
        String error = "";

        if (orderRequestDTO.getSupplier().getSupplierName().isEmpty()
                || orderRequestDTO.getSupplier().getAddress().isEmpty()
                || orderRequestDTO.getSupplier().getPhone().isEmpty()
                || orderRequestDTO.getReceiver().getReceiverName().isEmpty()
                || orderRequestDTO.getReceiver().getAddress().isEmpty()
                || orderRequestDTO.getReceiver().getPhone().isEmpty()
                || orderRequestDTO.getReceiver().getLatitude().isNaN()
                || orderRequestDTO.getReceiver().getLongitude().isNaN()) {
            error = "Thông tin không được để trống (";
        }
        if (orderRequestDTO.getSupplier().getSupplierName().isEmpty()) {
            error += headers[1] + ", ";
        }
        if (orderRequestDTO.getSupplier().getPhone().isEmpty()) {
            error += headers[2] + ", ";
        }
        if (orderRequestDTO.getSupplier().getAddress().isEmpty()) {
            error += headers[4] + ", ";
        }
        if (orderRequestDTO.getReceiver().getReceiverName().isEmpty()) {
            error += headers[7] + ", ";
        }

        if (orderRequestDTO.getReceiver().getPhone().isEmpty()) {
            error += headers[8] + ", ";
        }
        if (orderRequestDTO.getReceiver().getAddress().isEmpty()) {
            error += headers[10] + ", ";
        }
        if (orderRequestDTO.getReceiver().getLatitude().isNaN()) {
            error += headers[11] + ", ";
        }
        if (orderRequestDTO.getReceiver().getLongitude().isNaN()) {
            error += headers[12] + ", ";
        }

        if (error.isEmpty()) {
            return "";
        } else {
            return error.substring(0, error.length() - 2) + ");";
        }
    }

    public String checkLength(OrderRequestDTO orderRequestDTO) {
        String error = "";
        if (orderRequestDTO.getReceiver().getReceiverName().length() > 50
                ||orderRequestDTO.getSupplier().getSupplierName().length() > 50
                ||orderRequestDTO.getReceiver().getAddress().length() > 200
                || orderRequestDTO.getSupplier().getAddress().length() > 200
                || orderRequestDTO.getReceiver().getEmail().length() > 50
                || orderRequestDTO.getSupplier().getEmail().length() > 50) {
            error += "Chuỗi vượt quá số ký tự (";
        }


        if (!orderRequestDTO.getReceiver().getReceiverName().isEmpty() && orderRequestDTO.getReceiver().getReceiverName().length() > 50) {
            error += headers[7] + ", ";
        }
        if (!orderRequestDTO.getSupplier().getSupplierName().isEmpty() && orderRequestDTO.getSupplier().getSupplierName().length() > 50) {
            error += headers[1] + ", ";
        }
        if (!orderRequestDTO.getReceiver().getAddress().isEmpty() && orderRequestDTO.getReceiver().getAddress().length() > 200) {
            error += headers[10] + ", ";
        }
        if (!orderRequestDTO.getSupplier().getAddress().isEmpty() && orderRequestDTO.getSupplier().getAddress().length() > 200) {
            error += headers[4] + ", ";
        }
        if (!orderRequestDTO.getReceiver().getEmail().isEmpty() && orderRequestDTO.getReceiver().getEmail().length() > 50) {
            error += headers[9] + ", ";
        }
        if (!orderRequestDTO.getSupplier().getEmail().isEmpty() && orderRequestDTO.getSupplier().getEmail().length() > 50) {
            error += headers[3] + ", ";
        }

        if (error.isEmpty()) {
            return "";
        } else {
            return error.substring(0, error.length() - 2) + ");";
        }
    }

    public String checkFormat(OrderRequestDTO orderRequestDTO) {
        String error = "";
        String regexMail = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        String regexPhone = "^(\\+?84|0)(3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9])[0-9]{7}$";

        if (!orderRequestDTO.getReceiver().getEmail().matches(regexMail)
                || !orderRequestDTO.getSupplier().getEmail().matches(regexMail)
                || !orderRequestDTO.getReceiver().getPhone().matches(regexPhone)
                || !orderRequestDTO.getSupplier().getPhone().matches(regexPhone)
                || Math.abs(orderRequestDTO.getReceiver().getLatitude()) > 90
                || Math.abs(orderRequestDTO.getReceiver().getLongitude()) > 180) {
            error += "Thông tin không đúng định dạng (";
        }

        if (!orderRequestDTO.getReceiver().getEmail().matches(regexMail)) {
            error += headers[9] + ", ";
        }
        if (!orderRequestDTO.getSupplier().getEmail().matches(regexMail)) {
            error += headers[3] + ", ";
        }
        if (!orderRequestDTO.getReceiver().getPhone().matches(regexPhone)) {
            error += headers[8] + ", ";
        }
        if (!orderRequestDTO.getSupplier().getPhone().matches(regexPhone)) {
            error += headers[2] + ", ";
        }
        if (Math.abs(orderRequestDTO.getReceiver().getLatitude()) > 90) {
            error += headers[11] + ", ";
        }
        if (Math.abs(orderRequestDTO.getReceiver().getLongitude()) > 180) {
            error += headers[12] + ", ";
        }

        if (error.isEmpty()) {
            return "";
        } else {
            return error.substring(0, error.length() - 2) + ");";
        }
    }


    public String errorOrderRequest(OrderRequestDTO orderRequestDTO) {
        String error = "";
        String emptyError = checkEmpty(orderRequestDTO);
        if (emptyError != null) {
            error += emptyError;
        }
        String lengthError = checkLength(orderRequestDTO);
        if (lengthError != null) {
            error += lengthError;
        }
        String formatError = checkFormat(orderRequestDTO);
        if (formatError != null) {
            error += formatError;
        }

        if (error.isEmpty()) {
            return "";
        } else {
            return error.trim().substring(0, error.length() - 1);
        }
    }

    //xuat ra file excel loi tra ve
    public byte[] exportFileWithErrorMessage(ServletOutputStream outputStream, List<ErrorDTO> errorDTOList, MultipartFile file) throws IOException, DataTypeException, OrderException {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Row rowHeader = sheet.getRow(1);
        if (rowHeader == null) {
            rowHeader = sheet.createRow(1);
        }
        Cell cellHeader = rowHeader.createCell(13, rowHeader.getCell(12).getCellType());
        cellHeader.setCellValue("Thông tin lỗi");
        for (ErrorDTO errorDTO : errorDTOList) {
            Cell errCell = sheet.getRow(errorDTO.getRow()).createCell(13, sheet.getRow(errorDTO.getRow()).getCell(12).getCellType());
            errCell.setCellValue(errorDTO.getMessage());
            sheet.autoSizeColumn(errorDTO.getRow());
        }
//        try {
            String filePath = errorPath + formatter.formatDate() + ".xlsx";
            FileOutputStream outStream = new FileOutputStream(filePath);
            workbook.write(outStream);
            if (!errorDTOList.isEmpty()){
                throw new OrderException("Error");
            }
            workbook.close();
            outputStream.close();
            ByteArrayOutputStream opStream = new ByteArrayOutputStream();
            workbook.write(opStream);
            workbook.close();
            return opStream.toByteArray();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
