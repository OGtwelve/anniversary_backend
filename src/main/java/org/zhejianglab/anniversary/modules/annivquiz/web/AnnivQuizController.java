package org.zhejianglab.anniversary.modules.annivquiz.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zhejianglab.anniversary.common.response.SuccessResponse;
import org.zhejianglab.anniversary.common.response.ErrorResponse;
import org.zhejianglab.anniversary.modules.annivquiz.dto.QuizValidateRequest;
import org.zhejianglab.anniversary.modules.annivquiz.service.AnnivQuizService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/anniv/quiz")
public class AnnivQuizController {

    private final AnnivQuizService quizSvc;

    public AnnivQuizController(AnnivQuizService quizSvc) {
        this.quizSvc = quizSvc;
    }

    @GetMapping
    public ResponseEntity<Object> getQuiz() {
        return ResponseEntity.ok(new SuccessResponse("ok", quizSvc.getActiveQuiz()));
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validate(@RequestBody @Valid QuizValidateRequest req, HttpServletRequest http) {
        return ResponseEntity.ok(
                new SuccessResponse("ok",
                        quizSvc.validate(req.getQuizCode(), req.getAnswers(),
                                http.getRemoteAddr(), http.getHeader("User-Agent")))
        );
    }
}
