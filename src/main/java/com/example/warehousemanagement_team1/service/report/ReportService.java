package com.example.warehousemanagement_team1.service.report;

import com.example.warehousemanagement_team1.exception.DataTypeException;
import com.example.warehousemanagement_team1.exception.OrderException;
import com.example.warehousemanagement_team1.exception.ReasonException;
import com.example.warehousemanagement_team1.exception.WarehouseException;
import com.example.warehousemanagement_team1.model.Reason;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {

    void exportCountOrdersOfWarehouseByDate(LocalDate startDate, LocalDate endDate, List<String> warehouseIdList, ServletOutputStream outputStream) throws OrderException, WarehouseException, IOException, DataTypeException;

    void exportCountOrdersOfWarehouseByMonth(String startMonth, String endMonth, List<String> warehouseIdList, ServletOutputStream outputStream) throws OrderException, WarehouseException, IOException, DataTypeException;

    List<Reason> findAll();

    void exportCountExcelReportDateByMonth(String startMonth, String endMonth, List<String> wareHouseIdList, ServletOutputStream outputStream) throws OrderException, WarehouseException, IOException, DataTypeException, ReasonException;

    void exportCountExcelReportDateByDate(LocalDate startDate, LocalDate endDate, List<String> wareHouseId, ServletOutputStream outputStream) throws DataTypeException, IOException, OrderException, WarehouseException, ReasonException;


}
