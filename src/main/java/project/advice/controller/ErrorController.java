package project.advice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.token.service.TokenQueryService;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ErrorController {

    private final TokenQueryService tokenQueryService;

    @RequestMapping("/api/error")
    public void apiError(HttpServletRequest request) {
        tokenQueryService.certification(request);
    }

}