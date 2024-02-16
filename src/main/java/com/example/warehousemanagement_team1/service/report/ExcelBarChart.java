package com.example.warehousemanagement_team1.service.report;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExcelBarChart {
    public void createBarChart( XSSFSheet sheet, int lastRow,String title,
                               String columnSeries,String columnCategory,String sheetName ) throws IOException {
        // Tạo đối tượng drawing để vẽ biểu đồ trên sheet
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        // Tạo anchor để xác định vị trí và kích thước của biểu đồ trên sheet
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 15, 16, 35);
        // Tạo đối tượng chart để lưu trữ biểu đồ
        XSSFChart chart = drawing.createChart(anchor);

        // Lấy đối tượng CTChart từ chart
        CTChart ctChart = ((XSSFChart) chart).getCTChart();
        // Lấy đối tượng CTPlotArea từ CTChart để thêm biểu đồ cột
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();

        // Thiết lập thuộc tính VaryColors của biểu đồ cột để màu sắc của các cột thay đổi
        CTBoolean ctBoolean = ctBarChart.addNewVaryColors();
        ctBoolean.setVal(true);
        // Thiết lập hướng của biểu đồ cột (theo cột)
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);

        // Duyệt qua các dòng từ 2 đến 6 (ví dụ: dữ liệu nằm từ dòng 2 đến dòng 6)
        for (int r = 3; r < 3+lastRow; r++) {
            // Thêm series mới cho biểu đồ cột
            CTBarSer ctBarSer = ctBarChart.addNewSer();

            // Thiết lập giá trị của series
            CTSerTx ctSerTx = ctBarSer.addNewTx();
            CTStrRef ctStrRef = ctSerTx.addNewStrRef();
            ctStrRef.setF(sheetName+"!$" + columnSeries + "$" + (r+1)); // Đường dẫn tới ô chứa tên của series

            // Thiết lập chỉ số (index) của series
            ctBarSer.addNewIdx().setVal(r - 2);

            // Thiết lập dữ liệu trục x (category) của biểu đồ
            CTAxDataSource cttAxDataSource = ctBarSer.addNewCat();
            ctStrRef = cttAxDataSource.addNewStrRef();
            ctStrRef.setF(sheetName+"!$" + columnCategory +"$3:$R$3"); // Đường dẫn tới các ô chứa tên của các category

            // Thiết lập dữ liệu trục y (value) của biểu đồ
            CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
            CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
            ctNumRef.setF(sheetName+"!$" +columnCategory+"$" + (r+1) + ":$R$" + (r+1)); // Đường dẫn tới các ô chứa giá trị của các category
            System.out.println(sheetName+"!$" +columnCategory+"$" + (r+1) + ":$R$" + (r+1));

            // Thiết lập các thuộc tính vẽ cột (đường viền)
            ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{0, 0, 0});

        }

        // Thiết lập thông tin về trục x (category axis)
        ctBarChart.addNewAxId().setVal(123456);
        ctBarChart.addNewAxId().setVal(123457);
        CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
        ctCatAx.addNewAxId().setVal(123456);
        CTScaling ctScaling = ctCatAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctCatAx.addNewDelete().setVal(false);
        ctCatAx.addNewAxPos().setVal(STAxPos.B);
        ctCatAx.addNewCrossAx().setVal(123457);
        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        // Thiết lập thông tin về trục y (value axis)
        CTValAx ctValAx = ctPlotArea.addNewValAx();
        ctValAx.addNewAxId().setVal(123457);
        ctScaling = ctValAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctValAx.addNewDelete().setVal(false);
        ctValAx.addNewAxPos().setVal(STAxPos.L);
        ctValAx.addNewCrossAx().setVal(123456);
        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        // Thiết lập thông tin về hình chú thích (legend)
        CTLegend ctLegend = ctChart.addNewLegend();
        ctLegend.addNewLegendPos().setVal(STLegendPos.B);
        ctLegend.addNewOverlay().setVal(false);

        // Tạo đối tượng title
        chart.setTitleText(title);
    }

}
