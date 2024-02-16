package com.example.warehousemanagement_team1.service.order;

import com.example.warehousemanagement_team1.exception.OrderException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ImportFileService {
    public byte[] importExcel(ServletOutputStream outputStream, MultipartFile file, HttpServletRequest request) throws OrderException;

}
