package com.eeze.app.videoapi.controller.handlers;

import com.eeze.app.videoapi.exceptions.DataNotFound;
import com.eeze.app.videoapi.models.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DataNotFound.class)
  public ResponseEntity<ResponseDto> handleDataNotFound(DataNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDto> handleGeneralException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(ex.getMessage()));
  }
}
