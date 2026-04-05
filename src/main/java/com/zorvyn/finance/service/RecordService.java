package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.RecordRequest;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final FinancialRecordRepository repository;

    public FinancialRecord create(RecordRequest req) {
        FinancialRecord record = FinancialRecord.builder()
                .amount(req.getAmount())
                .type(req.getType().toUpperCase())
                .category(req.getCategory())
                .date(req.getDate())
                .notes(req.getNotes())
                .deleted(false)
                .build();
        return repository.save(record);
    }

    public List<FinancialRecord> getAll(String type, String category, LocalDate from, LocalDate to) {
        if (type != null) return repository.findByTypeAndDeletedFalse(type.toUpperCase());
        if (category != null) return repository.findByCategoryAndDeletedFalse(category);
        if (from != null && to != null) return repository.findByDateBetweenAndDeletedFalse(from, to);
        return repository.findByDeletedFalse();
    }

    public FinancialRecord update(Long id, RecordRequest req) {
        FinancialRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        if (record.isDeleted()) throw new RuntimeException("Record not found");
        record.setAmount(req.getAmount());
        record.setType(req.getType().toUpperCase());
        record.setCategory(req.getCategory());
        record.setDate(req.getDate());
        record.setNotes(req.getNotes());
        return repository.save(record);
    }

    public String delete(Long id) {
        FinancialRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        record.setDeleted(true);
        repository.save(record);
        return "Record deleted successfully";
    }
}