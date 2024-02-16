package com.example.warehousemanagement_team1.controller;

import com.example.warehousemanagement_team1.dto.request.WarehouseRequestDTO;
import com.example.warehousemanagement_team1.dto.response.WarehouseResponseDTO;
import com.example.warehousemanagement_team1.exception.Exceptions;
import com.example.warehousemanagement_team1.exception.WarehouseException;
import com.example.warehousemanagement_team1.model.Warehouse;
import com.example.warehousemanagement_team1.repository.WarehouseRepository;
import com.example.warehousemanagement_team1.service.warehouse.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses/")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WarehouseRepository warehouseRepository;

    @PostMapping("add")
    public ResponseEntity<?> addWarehouses(@RequestBody @Valid List<WarehouseRequestDTO> list) throws WarehouseException {
        List<WarehouseResponseDTO> warehouseResponseDTOS = warehouseService.save(list);
        if(warehouseResponseDTOS.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(warehouseResponseDTOS,HttpStatus.CREATED);
    }

    @PostMapping("")
    public ResponseEntity<?>add(@RequestBody @Valid WarehouseRequestDTO warehouseRequestDTO) throws WarehouseException {
       WarehouseResponseDTO warehouseResponseDTO= warehouseService.save(warehouseRequestDTO);
        return new ResponseEntity<>(warehouseResponseDTO,HttpStatus.CREATED);
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<?>edit(@PathVariable String id,@RequestBody @Valid WarehouseRequestDTO warehouseRequestDTO) throws WarehouseException {
        Warehouse warehouse=warehouseService.findById(id);
        warehouseRequestDTO.setWarehouseId(warehouse.getWarehouseId());
        Warehouse editedWarehouse=warehouseService.update(warehouseRequestDTO);
        return new ResponseEntity<>(editedWarehouse,HttpStatus.OK);
    }

    @GetMapping("search-sort")
    public ResponseEntity<?>list(@RequestParam(name = "limit", defaultValue = "10")String limit,
                                 @RequestParam(name = "page",defaultValue = "0")String noPage ,
                                 @RequestParam(name = "sort",defaultValue = "warehouseId")String sort,
                                 @RequestParam(name = "order",defaultValue = "ASC")String order,
                                 @RequestParam(name = "search",defaultValue = "",required = false) String search) throws Exceptions {
        try {
            Pageable pageable;
            if (order.equals("DESC")){
                pageable= PageRequest.of(Integer.parseInt(noPage),Integer.parseInt(limit), Sort.by(sort).descending());
            }else {
                pageable=PageRequest.of(Integer.parseInt(noPage),Integer.parseInt(limit),Sort.by(sort).ascending());
            }
            Page<WarehouseResponseDTO> list= warehouseService.searchAllAndSortAndPage(pageable,search);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }catch (NumberFormatException exception){
            throw  new Exceptions("SYSS-0013",messageSource);
        }
    }
}
