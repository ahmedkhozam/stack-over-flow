package com.example.demo.service;

import com.example.demo.dto.ReportRequest;
import com.example.demo.dto.ReportResponse;

import java.util.List;

public interface ReportService {
    ReportResponse reportContent(ReportRequest request);
    List<ReportResponse> getAllReports();

}
