package com.example.warehousemanagement_team1.utils;

import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class Barchart {

    //tao barchart
    public JFreeChart createBarChart(DefaultCategoryDataset dataset, String title, String xLabel, String yLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,    // Tiêu đề biểu đồ
                xLabel,          // Nhãn trục x
                yLabel,    // Nhãn trục y
                dataset,          // Dataset chứa dữ liệu
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
        // Đặt margin bottom cho tiêu đề
        TextTitle titleSetting = chart.getTitle();

//        titleSetting.setMargin(new RectangleInsets(10, 0, 24, 0)); // Đặt margin bottom là 20 pixels

        titleSetting.setMargin(10, 0, 24, 0); // Đặt margin bottom


        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(true); // Hiển thị border
        plot.setOutlinePaint(java.awt.Color.BLACK); // Màu border
//        // Đặt margin top cho nhãn warehouseId
        plot.getDomainAxis().setUpperMargin(0.2); // Đặt margin top là 10%
        plot.getDomainAxis().setAxisLineVisible(false);
//        plot.setInsets(new RectangleInsets(10, 10, 10, 10)); // Thiết lập padding
        plot.getDomainAxis().setCategoryMargin(0.1); // Thay đổi giá trị tùy thuộc vào yêu cầu của bạn
        plot.getRenderer().setSeriesPaint(0, java.awt.Color.BLUE); // Thay đổi màu cho series 0
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f)); // Điều chỉnh nét vẽ
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(0, 50); // Thiết lập giá trị cao nhất cho trục Y


        return chart;
    }

    public void saveChartAsImage(JFreeChart chart, String filePath) throws IOException {
        File file = new File(filePath);
        ChartUtilities.saveChartAsPNG(file, chart, 800, 400);
    }

    public void addPicToExcel(String path, XSSFWorkbook workbook, Sheet sheet, Integer col, Integer row) throws IOException {
        // Lưu trữ độ rộng cột trước khi chèn hình ảnh
        int columnWidth = sheet.getColumnWidth(col);

        //add pic to workbook
        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] bytes = IOUtils.toByteArray(fileInputStream);
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        fileInputStream.close();

        XSSFCreationHelper helper = workbook.getCreationHelper();

        // Create the drawing patriarch.  This is the top level container for all shapes.
        Drawing drawing = sheet.createDrawingPatriarch();
        //add a picture shape
        XSSFClientAnchor anchor = helper.createClientAnchor();
        //set top-left corner of the picture,
        anchor.setCol1(col);
        anchor.setRow1(row);
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        //auto-size picture relative to its top-left corner
//        sheet.autoSizeColumn(col);
        pict.resize();
        // Khôi phục lại độ rộng cột ban đầu
        sheet.setColumnWidth(col, columnWidth);
    }
}
