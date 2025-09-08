package org.zhejianglab.dxjh.modules.annivquiz.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhejianglab.dxjh.modules.annivquiz.dto.SurveyStatsDto;
import org.zhejianglab.dxjh.modules.annivquiz.service.SurveyStatsService;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@RestController
@RequestMapping("/api/admin")
public class AdminSurveyStatsController {

    private final SurveyStatsService service;

    public AdminSurveyStatsController(SurveyStatsService service) {
        this.service = service;
    }

    @GetMapping("/survey-stats")
    public ResponseEntity<SurveyStatsDto> surveyStats() {
        return ResponseEntity.ok(service.load());
    }
}
