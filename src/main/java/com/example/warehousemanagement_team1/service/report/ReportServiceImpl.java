package com.example.warehousemanagement_team1.service.report;

import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.exception.ReasonException;
import com.example.warehousemanagement_team1.exception.WarehouseException;

import com.example.warehousemanagement_team1.model.Reason;

import com.example.warehousemanagement_team1.model.Warehouse;
import com.example.warehousemanagement_team1.repository.OrderRepository;
import com.example.warehousemanagement_team1.repository.ReasonRepository;
import com.example.warehousemanagement_team1.service.warehouse.WarehouseService;
import com.example.warehousemanagement_team1.utils.Barchart;
import com.example.warehousemanagement_team1.utils.common.Common;
import com.example.warehousemanagement_team1.utils.formatter.Formatter;

import jakarta.servlet.ServletOutputStream;
import org.apache.poi.ss.usermodel.*;


import com.example.warehousemanagement_team1.utils.validation.Validator;

import org.apache.poi.xssf.usermodel.*;

import org.jfree.chart.JFreeChart;

import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.*;

import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

@Service
public class ReportServiceImpl implements ReportService {
    @Value("${report01-path}")
    private String report01Path;
    @Value("${pathLabelPath}")
    private String report02Path;
    @Value(("${chart-path}"))
    private String chartPath;
    @Value(("${title-report01}"))
    private String titleReport01;
    @Value("${filePath}")
    private String filePath1;
    @Autowired
    private Formatter formatter;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Common common;
    @Autowired
    private Barchart barchart;
    @Autowired
    private ReasonRepository reasonRepository;
    @Autowired
    private Validator validator;

    @Autowired
    private ExcelBarChart excelBarChart;

    @Override
    public void exportCountOrdersOfWarehouseByDate(LocalDate startDate, LocalDate endDate, List<String> warehouseIdList, ServletOutputStream outputStream) throws OrderException, WarehouseException, IOException, DataTypeException {
        if (warehouseIdList.size() > 10) {
            throw new OrderException("SYSS-0007", messageSource);
        }
        if (!validator.checkDayBetween(startDate, endDate)) {
            throw new DataTypeException("SYSS-0010", messageSource);
        }
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new DataTypeException("SYSS-0042", messageSource);
        }
        //tao workbook
        XSSFWorkbook workbook = createWorkbook(report01Path, "Report01_");
        XSSFSheet sheet = workbook.getSheetAt(0); // Clone từ sheet mẫu (sheet 0)

        LocalDate currentDate = startDate;
        int colDate = 3;
        int row = 3;
        int cell = 1;
        String fromDateToDate = "Từ ngày " + formatter.formatDate(startDate.atStartOfDay()) + " - " + formatter.formatDate(endDate.atStartOfDay());
        setStringValueToCell(sheet, 1, 4, fromDateToDate);

        //set thong tin tung kho theo tung ngay
        setWarehouseIdToCell(warehouseIdList, sheet, row, cell);

        while (!currentDate.isAfter(endDate)) {
            //set ngay vao tung cot
            setStringValueToCell(sheet, 2, colDate, formatter.formatDate(currentDate.atStartOfDay()));

            int rowIdx = 3;

            for (String warehouseId : warehouseIdList) {
                Warehouse warehouse = warehouseService.findById(warehouseId);

                int orderNumber = orderRepository.countOrdersInDay(warehouseId, currentDate);

                //set so don hang vao tung cell kho hang - theo ngay
                setValueToCell(sheet, rowIdx, colDate, orderNumber);
                rowIdx++;
            }
            colDate++;
            currentDate = currentDate.plusDays(1);
        }

        //tao barchart
//        addBarchart(orderNumberList,timeList,warehouseList, workbook, sheet, titleReport01, "", "Số lượng");
//            excelBarChart.createBarChart(sheet, warehouseIdList.size(), "Biểu đồ tổng số lượng đơn hàng", "C", "D", "BaoCao");

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void exportCountOrdersOfWarehouseByMonth(String startMonth, String endMonth, List<String> warehouseIdList, ServletOutputStream outputStream) throws OrderException, WarehouseException, IOException, DataTypeException {
        Map<Integer, Integer> mapStartMonth = validator.validateMonthAndYear(startMonth);
        Map<Integer, Integer> mapEndMonth = validator.validateMonthAndYear(endMonth);

        int monthStart = 0;
        int yearStart = 0;
        for (Map.Entry<Integer, Integer> key : mapStartMonth.entrySet()) {
            monthStart = key.getKey();
            yearStart = key.getValue();
        }
        int monthEnd = 0;
        int yearEnd = 0;
        for (Map.Entry<Integer, Integer> key : mapEndMonth.entrySet()) {
            monthEnd = key.getKey();
            yearEnd = key.getValue();
        }
        int monthBetween;
        if (yearStart != yearEnd) {
            if (yearEnd - yearStart > 1) {
                throw new DataTypeException("SYSS-0010", messageSource);
            }
            monthBetween = 12 - monthStart + monthEnd;
        } else {
            monthBetween = monthEnd - monthStart;
        }

        if (monthBetween >= 12) {
            throw new DataTypeException("SYSS-0010", messageSource);
        }
        if (monthEnd > LocalDateTime.now().getMonthValue() && yearEnd == LocalDateTime.now().getYear()
                || monthStart > LocalDateTime.now().getMonthValue() && yearStart == LocalDateTime.now().getYear()) {
            throw new DataTypeException("SYSS-0042", messageSource);
        }
        if (warehouseIdList.size() > 10) {
            throw new OrderException("SYSS-0007", messageSource);
        }

        //tao workbook
        XSSFWorkbook workbook = createWorkbook(report01Path, "Report01_");
        XSSFSheet sheet = workbook.getSheetAt(0);

        int colDate = 3;
        int row = 3;
        int cell = 1;
        String fromMonthToMonth = "Từ tháng " + startMonth + " - " + endMonth;
        setStringValueToCell(sheet, 1, 4, fromMonthToMonth);

        //set thong tin tung kho theo tung ngay
        setWarehouseIdToCell(warehouseIdList, sheet, row, cell);

        LocalDate currentMonth = formatter.convertToLocalDate(startMonth);
        while (!currentMonth.isAfter(formatter.convertToLocalDate(endMonth))) {
            setStringValueToCell(sheet, 2, colDate, currentMonth.format(DateTimeFormatter.ofPattern("MM/yyyy")));

            int rowIdx = 3;
            for (String warehouseId : warehouseIdList) {
                Warehouse warehouse = warehouseService.findById(warehouseId);

                //tinh so orders trong thang cua tung kho
                int orderNumber = orderRepository.countOrdersInMonthAndYear(warehouseId, monthStart, yearStart);

                //set so don hang vao tung cell kho hang - theo ngay
                setValueToCell(sheet, rowIdx, colDate, orderNumber);
                rowIdx++;
            }
            colDate++;
            currentMonth = currentMonth.plusMonths(1);
            monthStart++;
            if (monthStart > 12) {
                monthStart = monthStart - 12;
                yearStart = yearStart + 1;
            }
        }

        //tao barchart
//        addBarchart(orderNumberList,timeList,warehouseList, workbook, sheet, titleReport01, "", "Số lượng");
//        excelBarChart.createBarChart(sheet, warehouseIdList.size(), "Biểu đồ tổng số lượng đơn hàng", "C", "D", "BaoCao");

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Reason> findAll() {
        return reasonRepository.findAll();
    }

    //set gia tri so luong don hang vao tung cell tuong ung tung kho - tung ngay
    private void setValueToCell(XSSFSheet sheet, int rowIdx, int colDate, int orderNumber) {

        XSSFRow existedRow = sheet.getRow(rowIdx);
        if (existedRow == null) {
            existedRow = sheet.createRow(rowIdx);
        }
        XSSFCell existedCell = existedRow.getCell(colDate);
        if (existedCell == null) {
            existedCell = existedRow.createCell(colDate);
        }
        existedCell.setCellValue(orderNumber);
    }

    private void setStringValueToCell(XSSFSheet sheet, int rowIdx, int colDate, String string) {

        XSSFRow existedRow = sheet.getRow(rowIdx);
        if (existedRow == null) {
            existedRow = sheet.createRow(rowIdx);
        }
        XSSFCell existedCell = existedRow.getCell(colDate);
        if (existedCell == null) {
            existedCell = existedRow.createCell(colDate);
        }
        existedCell.setCellValue(string);
    }

    //set thong tin tung kho hang vao report
    public void setWarehouseIdToCell(List<String> warehouseIdList, XSSFSheet sheet, int row, int cell) throws WarehouseException {
        for (String warehouseId : warehouseIdList) {
            Warehouse warehouse = warehouseService.findById(warehouseId);
            sheet.getRow(row).getCell(cell).setCellValue(warehouse.getWarehouseId());
            sheet.getRow(row).getCell(cell + 1).setCellValue(warehouse.getWarehouseName());
            row++;
        }
    }

    public void addchart(List<Integer> orderList, List<String> timeList, List<String> warehouseList, XSSFWorkbook workbook, XSSFSheet sheet, String title, String xLabel, String yLabel) throws IOException {
        // Tạo dataset cho biểu đồ
        DefaultCategoryDataset dataset = createDataset(orderList, timeList, warehouseList);

        // Tạo biểu đồ cột
        JFreeChart chart = barchart.createBarChart(dataset, title, xLabel, yLabel);

        // Lưu biểu đồ vào file
        barchart.saveChartAsImage(chart, chartPath);

        //add bieu do vao excel
        //add picture data to this workbook.
        barchart.addPicToExcel(chartPath, workbook, sheet, 2, 15);
    }

    //tao data cho barchart
    public DefaultCategoryDataset createDataset(List<Integer> orderList, List<String> timeList, List<String> warehouseList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < orderList.size(); i++) {
            dataset.addValue(orderList.get(i), warehouseList.get(i), timeList.get(i));
        }
        return dataset;
    }

    //xuất theo ngày
    @Override
    public void exportCountExcelReportDateByDate(LocalDate startDate, LocalDate endDate, List<String> wareHouseIdList, ServletOutputStream outputStream) throws DataTypeException, IOException, OrderException, WarehouseException, ReasonException {
        if (!validator.checkDayBetween(startDate, endDate)) {
            throw new DataTypeException("SYSS-0010", messageSource);
        }
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new DataTypeException("SYSS-0042", messageSource);
        }
        if (wareHouseIdList.size() > 10) {
            throw new OrderException("SYSS-0007", messageSource);
        }

        XSSFWorkbook workbook = createWorkbook(report02Path, "Report02_");

        for (String warehouseId : wareHouseIdList) {
            Warehouse warehouse = warehouseService.findById(warehouseId);

            XSSFSheet sheet = workbook.cloneSheet(0); // Clone từ sheet mẫu (sheet 0)
            workbook.setSheetName(workbook.getSheetIndex(sheet), warehouse.getWarehouseId());
//            workbook.setSheetName(workbook.getSheetIndex(sheet), warehouse.getWarehouseId().replace("-", ""));
            LocalDate currentDate = startDate;
            int colDate = 2;
            int cell = 1;
            String titleSheet = "Bảng thống kê hiệu suất giao đơn hàng kho " + warehouse.getWarehouseName();
            setStringValueToCell(sheet, 0, 2, titleSheet);
            String fromDateToDate = "Từ ngày " + formatter.formatDate(startDate.atStartOfDay()) + " - " + formatter.formatDate(endDate.atStartOfDay());
            setStringValueToCell(sheet, 1, 3, fromDateToDate);

            List<Reason> reasons = reasonRepository.findAll();
            //set thông tin từng ly do theo từng tháng
            setWarehouseIdToCell1(reasons, sheet, 5, cell);
            int cellSuccess = 2;
            while (!currentDate.isAfter(endDate)) {
                setStringValueToCell(sheet, 2, colDate, formatter.formatDate(currentDate.atStartOfDay()));

                //set so don thanh cong + that bai vao cell
                int rowIdx = 5;
                int numberOfSuccessOrder = orderRepository.getSuccessOrderCountDate(currentDate, warehouse.getWarehouseId());
                setValueToCell(sheet, 3, cellSuccess, numberOfSuccessOrder);

                int numberOfFailedOrder = orderRepository.getFailedOrderCountDate(currentDate, warehouse.getWarehouseId());
                setValueToCell(sheet, 4, cellSuccess, numberOfFailedOrder);

                for (Reason reason : reasons) {
                    int orderNumber = orderRepository.getFailedOrderReasonCountDate(reason.getReasonId(), currentDate, warehouseId);
                    setValueToCell(sheet, rowIdx, colDate, orderNumber);
                    rowIdx++;
                }
                cellSuccess++;
                colDate++;
                currentDate = currentDate.plusDays(1);
            }
//            while (!currentDate.isAfter(endDate)) {
//                setStringValueToCell(sheet, 2, colDate, formatter.formatDate(currentDate.atStartOfDay()));
//
//                // Lặp qua từng lý do trong danh sách reasons
//                for (Reason reason : reasons) {
//                    Long reasonId = reason.getReasonId();
//                    List<Object[]> orderStatisticsList = orderRepository.getOrderStatisticsByDay(currentDate, warehouse.getWarehouseId());
//
//                    // Khởi tạo biến để lưu trữ số lượng đơn hàng thành công, không thành công và số đơn hàng theo lý do
//                    int numberOfSuccessOrder = 0;
//                    int numberOfFailedOrder = 0;
//                    int numberOfFailedOrderForReason = 0;
//
//                    // Lặp qua danh sách kết quả thống kê để tìm kết quả tương ứng cho lý do hiện tại
//                    for (Object[] orderStatistics : orderStatisticsList) {
//                        java.sql.Date sqlDate = (java.sql.Date) orderStatistics[0];
//                        LocalDate orderDate = sqlDate.toLocalDate();
//                        Long currentReasonId = (Long) orderStatistics[3];
//                        if (orderDate.equals(currentDate) && Objects.equals(currentReasonId, reasonId)) {
//                            // Ép kiểu các giá trị phù hợp
//                             numberOfSuccessOrder = ((Number) orderStatistics[1]).intValue();
//                             numberOfFailedOrder = ((Number) orderStatistics[2]).intValue();
//                            numberOfFailedOrderForReason = ((Number) orderStatistics[4]).intValue();
//                            break; // Đã tìm thấy kết quả cho lý do hiện tại, nên thoát khỏi vòng lặp
//                        }
//                    }
//                    // Đặt các giá trị vào cell
//                    setValueToCell(sheet, 3, colDate, numberOfSuccessOrder);
//                    setValueToCell(sheet, 4, colDate, numberOfFailedOrder);
//
//                    int rowIdx = 5 + reasons.indexOf(reason);
//                    setValueToCell(sheet, rowIdx, colDate, numberOfFailedOrderForReason);
//                }
//
//                cellSuccess++;
//                colDate++;
//                currentDate = currentDate.plusDays(1);
//            }


            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();

            if (!drawing.getCharts().isEmpty()) {
                XSSFChart chart = drawing.getCharts().get(0);
                chart.setTitleText(titleSheet);
            }
//            excelBarChart.createBarChart(sheet, reasons.size() + 2, titleSheet, "B", "C", warehouse.getWarehouseId().replace("-", ""));
        }

        workbook.removeSheetAt(0);

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWarehouseIdToCell1(List<Reason> reasonList, XSSFSheet sheet, int startRow, int startCell) throws WarehouseException {
        int currentRow = startRow;
        int index = 3;

        for (Reason reason : reasonList) {
            XSSFRow row = sheet.getRow(currentRow);
            if (row == null) {
                row = sheet.createRow(currentRow);
            }

            XSSFCell cellId = row.getCell(startCell - 1);
            if (cellId == null) {
                cellId = row.createCell(startCell - 1);
            }
            cellId.setCellValue((index));

            XSSFCell cellDescription = row.getCell(startCell); // Cột B
            if (cellDescription == null) {
                cellDescription = row.createCell(startCell);
            }
            cellDescription.setCellValue(reason.getDescription());

            currentRow++;
            index++;

        }
    }

    //xuất theo tháng
    @Override
    public void exportCountExcelReportDateByMonth(String startMonth, String endMonth, List<String> wareHouseIdList, ServletOutputStream outputStream) throws OrderException, WarehouseException, IOException, DataTypeException, ReasonException {
        Map<Integer, Integer> mapStartMonth = validator.validateMonthAndYear(startMonth);
        Map<Integer, Integer> mapEndMonth = validator.validateMonthAndYear(endMonth);

        int monthStart = 0;
        int yearStart = 0;
        for (Map.Entry<Integer, Integer> entry : mapStartMonth.entrySet()) {
            monthStart = entry.getKey();
            yearStart = entry.getValue();
        }

        int monthEnd = 0;
        int yearEnd = 0;
        for (Map.Entry<Integer, Integer> entry : mapEndMonth.entrySet()) {
            monthEnd = entry.getKey();
            yearEnd = entry.getValue();
        }

        int monthBetween;
        if (yearStart != yearEnd) {
            if (yearEnd - yearStart > 1) {
                throw new DataTypeException("SYSS-0010", messageSource);
            }
            monthBetween = 12 - monthStart + monthEnd;
        } else {
            monthBetween = monthEnd - monthStart;
        }

        if (monthBetween >= 12) {
            throw new DataTypeException("SYSS-0010", messageSource);
        }
        if (monthEnd > LocalDateTime.now().getMonthValue() && yearEnd == LocalDateTime.now().getYear()
                || monthStart > LocalDateTime.now().getMonthValue() && yearStart == LocalDateTime.now().getYear()) {
            throw new DataTypeException("SYSS-0042", messageSource);
        }
        if (wareHouseIdList.size() > 10) {
            throw new DataTypeException("SYSS-0010", messageSource);
        }
        XSSFWorkbook workbook = createWorkbook(report02Path, "Report02_");
        for (String wareHouseId : wareHouseIdList) {
            Warehouse warehouse = warehouseService.findById(wareHouseId);
            // Tạo sheet mới
            XSSFSheet sheet = workbook.cloneSheet(0); // Clone từ sheet mẫu (sheet 0)
            workbook.setSheetName(workbook.getSheetIndex(sheet), warehouse.getWarehouseId());
//            workbook.setSheetName(workbook.getSheetIndex(sheet), warehouse.getWarehouseId().replace("-", ""));

            int colDate = 2;
            int row = 5;
            int cell = 1;
            String titleSheet = "Bảng thống kê hiệu suất giao đơn hàng kho " + warehouse.getWarehouseName();
            setStringValueToCell(sheet, 0, 2, titleSheet);
            String fromMonthToMonth = "Từ tháng " + startMonth + " - " + endMonth;
            setStringValueToCell(sheet, 1, 3, fromMonthToMonth);

            List<Reason> reasonList = reasonRepository.findAll();
            //set thông tin từng kho hàng theo từng tháng
            setWarehouseIdToCell1(reasonList, sheet, row, cell);

            LocalDate currentMonth = formatter.convertToLocalDate(startMonth);
            int cellSuccess = 2;
            while (!currentMonth.isAfter(formatter.convertToLocalDate(endMonth))) {
                setStringValueToCell(sheet, 2, colDate, currentMonth.format(DateTimeFormatter.ofPattern("MM/yyyy")));

                //set so don thanh cong + that bai vao cell
                int rowIdx = 5;
                int numberOfSuccessOrder = orderRepository.getSuccessOrderCountMonthYear(monthStart, yearStart, warehouse.getWarehouseId());
                setValueToCell(sheet, 3, cellSuccess, numberOfSuccessOrder);

                int numberOfFailedOrder = orderRepository.getFailedOrderCountMonthYear(monthStart, yearStart, warehouse.getWarehouseId());
                setValueToCell(sheet, 4, cellSuccess, numberOfFailedOrder);

                for (Reason reason : reasonList) {
                    Reason fetchedReason = reasonRepository.findById(reason.getReasonId()).orElseThrow(() -> new ReasonException("SYSS-3000", messageSource));
                    int orderNumber = orderRepository.getFailedOrdersCountMonthYear(fetchedReason.getReasonId(), monthStart, yearStart, warehouse.getWarehouseId());

                    //set số đơn hàng vào từng ô của kho hàng - theo ngày
                    setValueToCell(sheet, rowIdx, colDate, orderNumber);
                    rowIdx++;
                }
                colDate++;
                currentMonth = currentMonth.plusMonths(1);
                monthStart++;
                if (monthStart > 12) {
                    monthStart = monthStart - 12;
                    yearStart = yearStart + 1;
                }
                cellSuccess++;
            }

//            LocalDate currentMonthYear = formatter.convertToLocalDate(startMonth);
//            while (!currentMonthYear.isAfter(formatter.convertToLocalDate(endMonth))) {
//                setStringValueToCell(sheet, 2, colDate, currentMonthYear.format(DateTimeFormatter.ofPattern("MM/yyyy")));
//
//                int rowIdx = 5; // Bắt đầu từ dòng thứ 5
//
//                // Lặp qua từng lý do trong danh sách reasonList
//                for (Reason reason : reasonList) {
//                    Long reasonId = reason.getReasonId();
//
//                    // Gọi phương thức để lấy mảng đối tượng từ repository
//                    Object[] orderCountsObject = orderRepository.getOrderCountsByDateAndReason(reasonId, currentMonthYear, warehouse.getWarehouseId());
//
//                    // Chuyển đổi mảng đối tượng thành mảng số nguyên
//                    Integer[] orderCounts = new Integer[orderCountsObject.length];
//                    for (int i = 0; i < orderCountsObject.length; i++) {
//                        orderCounts[i] = (Integer) orderCountsObject[i];
//                    }
//
//                    // Kiểm tra nếu mảng orderCounts đủ phần tử
//                    if (orderCounts.length >= 3) {
//                        // Lấy các giá trị từ mảng orderCounts
//                        int numberOfSuccessOrder = orderCounts[1];
//                        int numberOfFailedOrder = orderCounts[0];
//                        int numberOfFailedOrderForReason = orderCounts[2];
//
//                        // Đặt các giá trị vào cell
//                        setValueToCell(sheet, 3, colDate, numberOfSuccessOrder);
//                        setValueToCell(sheet, 4, colDate, numberOfFailedOrder);
//                        setValueToCell(sheet, rowIdx, colDate, numberOfFailedOrderForReason);
//                    } else {
//                        // Nếu mảng không đủ phần tử, đặt các giá trị mặc định vào cell
//                        setValueToCell(sheet, 3, colDate, 0);
//                        setValueToCell(sheet, 4, colDate, 0);
//                        setValueToCell(sheet, rowIdx, colDate, 0);
//                    }
//
//                    // Tăng chỉ số dòng lên cho lần lặp tiếp theo
//                    rowIdx++;
//                }
//
//                colDate++;
//                currentMonthYear = currentMonthYear.plusMonths(1);
//            }

            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();

            if (!drawing.getCharts().isEmpty()) {
                XSSFChart chart = drawing.getCharts().get(0);
                chart.setTitleText(titleSheet);
            }

//            excelBarChart.createBarChart(sheet, reasonList.size() + 2, titleSheet, "B", "C", warehouse.getWarehouseId().replace("-", ""));
        }

        // Xóa sheet mẫu
        workbook.removeSheetAt(0);

        try {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public XSSFWorkbook createWorkbook(String reportPath, String reportName) throws DataTypeException, IOException {
        String currentDateTime = formatter.formatDateTime(LocalDateTime.now());
        File labelTempFile = new File(reportPath);
        File tempFile = File.createTempFile("Report01Copy", ".xlsx");
        // Tạo bản sao của file gốc
        Files.copy(labelTempFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Đổi tên bản sao thành tên mới
        String newFileName = reportName + currentDateTime + ".xlsx";
        File newFile = new File(labelTempFile.getParent(), newFileName);
        tempFile.renameTo(newFile);

        return (XSSFWorkbook) WorkbookFactory.create(newFile);
    }

}

