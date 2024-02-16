package com.example.warehousemanagement_team1.utils.common;

import org.apache.poi.ss.usermodel.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.Color;

@Component
public class Common {
    public void customizeChartAppearance(JFreeChart barChart) {
        CategoryPlot plot = (CategoryPlot) barChart.getPlot();

        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, Color.GREEN);
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(true);
        plot.setOutlinePaint(java.awt.Color.BLACK);
        plot.setInsets(new RectangleInsets(10, 10, 10, 10)); // Thiết lập padding
        plot.getDomainAxis().setCategoryMargin(0.1); // Thay đổi giá trị tùy thuộc vào yêu cầu của bạn
        plot.getRenderer().setSeriesPaint(0, java.awt.Color.BLUE); // Thay đổi màu cho series 0
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f)); // Điều chỉnh nét vẽ
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(0, 100); // Thiết lập giá trị cao nhất cho trục Y
        renderer.setMaximumBarWidth(0.1);
        plot.getDomainAxis().setCategoryMargin(0.1); // Điều chỉnh khoảng cách giữa các cột
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Góc 45 độ để tránh chồng lên nhau
        domainAxis.setCategoryMargin(0.1); // Khoảng cách giữa các cột
        renderer.setShadowVisible(true);
        renderer.setDefaultEntityRadius(10);
    }


    public void CellStyleHeaderStyle(Sheet sheet, Row row){
        // Định dạng cho font và đường viền
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            row.getCell(i).setCellStyle(headerStyle);
        }
    }

    public void CellStyleBorders(Sheet sheet,Row row){
        // Định dạng cho font và đường viền cho dòng dữ liệu
        CellStyle dataStyle = sheet.getWorkbook().createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            row.getCell(i).setCellStyle(dataStyle);
        }
    }
}
