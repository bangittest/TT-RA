package com.example.warehousemanagement_team1.service.warehouse;

import com.example.warehousemanagement_team1.dto.request.WarehouseRequestDTO;
import com.example.warehousemanagement_team1.dto.response.WarehouseResponseDTO;
import com.example.warehousemanagement_team1.exception.WarehouseException;
import com.example.warehousemanagement_team1.model.Warehouse;
import com.example.warehousemanagement_team1.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WarehouseResponseDTO> save(List<WarehouseRequestDTO> list) throws WarehouseException {
        List<Warehouse> warehouses = new ArrayList<>();

        for (WarehouseRequestDTO warehouseRequestDTO : list) {
            if (warehouseRepository.findByWarehouseName(warehouseRequestDTO.getWarehouseName()) != null) {
                throw new WarehouseException("SYSS-3003", messageSource);
            }

            warehouseRequestDTO.setWarehouseId(getWarehouseId(warehouseRequestDTO.getWarehouseName()));
            Warehouse warehouse = warehouseRepository.save(new Warehouse(warehouseRequestDTO));
            warehouses.add(warehouse);
        }
        return warehouses.stream().map(WarehouseResponseDTO::new).toList();
    }

    @Override
    public Warehouse findById(String warehouseId) throws WarehouseException {
        Warehouse warehouse=warehouseRepository.findByWarehouseIdIgnoreCase(warehouseId);
        if (warehouse==null){
            throw new WarehouseException("SYSS-3000",messageSource);
        }
        return warehouse;
    }

    @Override
    public Warehouse update(WarehouseRequestDTO warehouseRequestDTO) throws WarehouseException {
        Warehouse warehouse = findById(warehouseRequestDTO.getWarehouseId());

        //check duplicate warehouseName
        boolean existedName = warehouseRepository.findAll().stream().anyMatch(item ->
                !warehouseRequestDTO.getWarehouseName().equalsIgnoreCase(warehouse.getWarehouseName())
                        && warehouseRequestDTO.getWarehouseName().equalsIgnoreCase(item.getWarehouseName()));
        if (existedName) {
            throw new WarehouseException("SYSS-3003", messageSource);
        }

        return warehouseRepository.save(new Warehouse(warehouseRequestDTO));
    }

    @Override
    public WarehouseResponseDTO save(WarehouseRequestDTO warehouseRequestDTO) throws WarehouseException {
        if (warehouseRepository.findByWarehouseName(warehouseRequestDTO.getWarehouseName()) != null) {
            throw new WarehouseException("SYSS-3003", messageSource);
        }
        warehouseRequestDTO.setWarehouseId(getWarehouseId(warehouseRequestDTO.getWarehouseName()));
        Warehouse warehouse = warehouseRepository.save(new Warehouse(warehouseRequestDTO));
        return new WarehouseResponseDTO(warehouse);
    }

    @Override
    public Page<WarehouseResponseDTO> searchAllAndSortAndPage(Pageable pageable, String search) {
        Page<Warehouse>warehouses=warehouseRepository.findAllByWarehouseIdContainsIgnoreCaseOrWarehouseNameContainsIgnoreCase(pageable,search,search);
        return warehouses.map(WarehouseResponseDTO::new);
    }



    public String getWarehouseId(String warehouseName) {
        StringBuilder warehouseId = new StringBuilder();
        String[] words = warehouseName.trim().split(" ");
        for (String word : words) {
            if (!word.isEmpty() && !word.equalsIgnoreCase("Kho")) {
                char firstLetter = word.charAt(0);

                // Thay thế ký tự "Đ" bằng "D"
                if (Character.toUpperCase(firstLetter) == 'Đ') {
                    firstLetter = 'D';
                }

                // Loại bỏ dấu tiếng Việt
                String normalized = Normalizer.normalize(Character.toString(firstLetter), Normalizer.Form.NFD);
                Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
                String sanitizedLetter = pattern.matcher(normalized).replaceAll("");

                warehouseId.append(sanitizedLetter);
            }
        }

        return "K-" + warehouseId.toString();
    }
}
