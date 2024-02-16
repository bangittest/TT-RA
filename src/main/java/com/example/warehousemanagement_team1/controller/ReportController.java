package com.example.warehousemanagement_team1.controller;

import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.exception.ReasonException;
import com.example.warehousemanagement_team1.exception.WarehouseException;
import com.example.warehousemanagement_team1.service.report.ReportService;
import com.example.warehousemanagement_team1.utils.formatter.Formatter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/reports/")
public class ReportController {
    @Autowired
    private Formatter formatter;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("report-order-by-date")
    public ResponseEntity<?> countOrdersByDate(@RequestParam(value = "startDate", required = false) String startDate,
                                               @RequestParam(value = "endDate", required = false) String endDate,
                                               @RequestParam(value = "warehouseId", required = false) List<String> warehouseIdList,
                                               ServletOutputStream outputStream,
                                               HttpServletResponse response)
            throws WarehouseException, OrderException, IOException, DataTypeException {
        checkParam(startDate, endDate, warehouseIdList);

        LocalDate parsedStartDate = formatter.convertStringToLocalDate(startDate);
        LocalDate parsedEndDate = formatter.convertStringToLocalDate(endDate);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reportService.exportCountOrdersOfWarehouseByDate(parsedStartDate, parsedEndDate, warehouseIdList, outputStream);

       return createExcelResponse(byteArrayOutputStream, "Report02_");
    }

    @GetMapping("/report-order-by-month")
    public ResponseEntity<?> countOrdersByMonth(@RequestParam(value = "startMonth", required = false) String startMonth,
                                                @RequestParam(value = "endMonth", required = false) String endMonth,
                                                @RequestParam(value = "warehouseId", required = false) List<String> warehouseIdList,
                                                ServletOutputStream outputStream,
                                                HttpServletResponse response) throws DataTypeException, WarehouseException, IOException, OrderException {
        checkParam(startMonth, endMonth, warehouseIdList);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reportService.exportCountOrdersOfWarehouseByMonth(startMonth, endMonth, warehouseIdList, outputStream);

       return createExcelResponse(byteArrayOutputStream, "Report02_");
    }


    //thống kê đơn hàng theo ngày
    @GetMapping("count-history-date")
    public ResponseEntity<?> countHistoryByDate(@RequestParam(value = "startDate", required = false) String startDate,
                                                @RequestParam(value = "endDate", required = false) String endDate,
                                                @RequestParam(value = "warehouseId", required = false) List<String> warehouseIdList,
                                                ServletOutputStream outputStream,
                                                HttpServletResponse response)
            throws IOException, DataTypeException, WarehouseException, OrderException, ReasonException {
        checkParam(startDate, endDate, warehouseIdList);

        LocalDate parsedStartDate = formatter.convertStringToLocalDate(startDate);
        LocalDate parsedEndDate = formatter.convertStringToLocalDate(endDate);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reportService.exportCountExcelReportDateByDate(parsedStartDate, parsedEndDate, warehouseIdList, outputStream);

       return createExcelResponse(byteArrayOutputStream, "Report02_");
    }

    //thống kê đơn hàng theo tháng
    @GetMapping("/count-history-month")
    public ResponseEntity<?> countHistoryByMonth(@RequestParam(value = "startMonth", required = false) String startMonth,
                                                 @RequestParam(value = "endMonth", required = false) String endMonth,
                                                 @RequestParam(value = "warehouseId", required = false) List<String> warehouseIdList,
                                                 ServletOutputStream outputStream) throws
            DataTypeException, WarehouseException, IOException, OrderException, ReasonException {
        checkParam(startMonth, endMonth, warehouseIdList);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reportService.exportCountExcelReportDateByMonth(startMonth, endMonth, warehouseIdList, outputStream);

       return createExcelResponse(byteArrayOutputStream, "Report02_");
    }

    private void checkParam(String startTime, String endTime, List<String> warehouseIdList) throws DataTypeException {
        if (startTime == null || endTime == null || startTime.isEmpty() || endTime.isEmpty()) {
            throw new DataTypeException("SYSS-0011", messageSource);
        }
        if (warehouseIdList == null || warehouseIdList.isEmpty()) {
            throw new DataTypeException("SYSS-0011", messageSource);
        }
    }


    private ResponseEntity<ByteArrayResource> createExcelResponse(ByteArrayOutputStream outputStream, String filename) throws DataTypeException {
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        String currentDate = formatter.formatDateTime(LocalDateTime.now());

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + currentDate + ".xlsx")
                .body(resource);
    }
}
