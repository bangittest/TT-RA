package com.example.warehousemanagement_team1.service.excel;

import com.example.warehousemanagement_team1.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    void imPortOrders(MultipartFile file, User user) throws Exception;

}
