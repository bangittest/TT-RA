package com.example.warehousemanagement_team1.service.recevier;

import com.example.warehousemanagement_team1.dto.request.ReceiverRequestDTO;
import com.example.warehousemanagement_team1.model.Receiver;
import com.example.warehousemanagement_team1.repository.ReceiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiverServiceImpl implements ReceiverService{
    @Autowired
    private ReceiverRepository receiverRepository;
    @Override
    public ReceiverRequestDTO save(ReceiverRequestDTO receiverDTO) {
        Receiver receiver=receiverRepository.save(Receiver.builder()
                        .receiverName(receiverDTO.getReceiverName())
                        .phone(receiverDTO.getPhone())
                        .email(receiverDTO.getEmail())
                        .address(receiverDTO.getAddress())
                        .latitude(receiverDTO.getLatitude())
                        .longitude(receiverDTO.getLongitude())
                .build());
        return new ReceiverRequestDTO(receiver);
    }
}
