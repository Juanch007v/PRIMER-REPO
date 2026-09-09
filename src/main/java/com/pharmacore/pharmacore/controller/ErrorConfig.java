package com.pharmacore.pharmacore.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import java.util.Map;

@Configuration
public class ErrorConfig implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView("error/404", model);
        } else if (status == HttpStatus.FORBIDDEN) {
            return new ModelAndView("error/403", model);
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ModelAndView("error/500", model);
        }
        return new ModelAndView("error/error", model);
    }
}