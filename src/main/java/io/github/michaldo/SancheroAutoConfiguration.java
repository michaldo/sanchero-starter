package io.github.michaldo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@AutoConfiguration(before = ErrorMvcAutoConfiguration.class)
public class SancheroAutoConfiguration {

    @Component
    static class SancheroErrorAttributes extends DefaultErrorAttributes {
        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

            Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
            if (options.isIncluded(ErrorAttributeOptions.Include.BINDING_ERRORS) && webRequest.getAttribute("sanchero-errors", RequestAttributes.SCOPE_REQUEST) != null) {
                errorAttributes.put("errors", webRequest.getAttribute("sanchero-errors", RequestAttributes.SCOPE_REQUEST));
            }
            if (options.isIncluded(ErrorAttributeOptions.Include.MESSAGE) && webRequest.getAttribute("sanchero-message", RequestAttributes.SCOPE_REQUEST)  != null) {
                errorAttributes.put("message", webRequest.getAttribute("sanchero-message", RequestAttributes.SCOPE_REQUEST));
            }
            return errorAttributes;
        }
    }

    @ControllerAdvice
    static class SancheroAdvice {

        @ExceptionHandler(ConstraintViolationException.class)
        void handle(ConstraintViolationException constraintViolationException, HttpServletRequest request, HttpServletResponse response) throws IOException {

            List<SancheroError> sancheroErrors = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(constraintViolation -> new SancheroError(leaf(constraintViolation.getPropertyPath()), constraintViolation.getMessage()))
                    .sorted(Comparator.comparing(SancheroError::field))
                    .toList();
            request.setAttribute("sanchero-errors", sancheroErrors);
            request.setAttribute("sanchero-message", "Validation failed");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        private String leaf(Path path) {
            Path.Node leaf = null;
            for (Iterator<Path.Node> it = path.iterator(); it.hasNext();) {
                leaf = it.next();
            }
            return leaf == null ? null : leaf.getName();
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        void handle(MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request, HttpServletResponse response) throws IOException {

            List<SancheroError> sancheroErrors = methodArgumentNotValidException.getAllErrors()
                    .stream()
                    .filter(objectError -> objectError instanceof FieldError)
                    .map(fieldError -> new SancheroError(((FieldError) fieldError).getField(), fieldError.getDefaultMessage()))
                    .sorted(Comparator.comparing(SancheroError::field))
                    .toList();
            request.setAttribute("sanchero-errors", sancheroErrors);
            request.setAttribute("sanchero-message", "Validation failed");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    record SancheroError(String field, String description) { }
}
