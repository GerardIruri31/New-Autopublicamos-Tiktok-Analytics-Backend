package com.example.sbazureappdemo.dataMaintenance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StagingCleanupService {
    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dropTable(String finalTableName) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + finalTableName);
    }
}