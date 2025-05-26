package com.example.demo.service;

import com.example.demo.dto.ReportRequest;
import com.example.demo.dto.ReportResponse;

public interface ReportService {
    ReportResponse reportContent(ReportRequest request);
}
