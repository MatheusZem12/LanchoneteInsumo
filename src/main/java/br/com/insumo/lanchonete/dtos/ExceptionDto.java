package br.com.insumo.lanchonete.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionDto {

    @JsonProperty(value = "status")
	private final int status;

    @JsonProperty(value = "message")
	private final String message;

    @JsonProperty(value = "timestamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime timestamp;

    @JsonProperty(value = "path")
	private final String path;
    
    @JsonProperty(value = "exception")
	private final String exceptionName;

}
