package com.example.user_service.pojos.dto.caretaker;

import com.example.user_service.validators.image.ImageValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class SendImageDto {

    @NotNull(message = "Caretaker name should not be null")
    @NotBlank(message = "Caretaker name should not be empty")
    private String name;

    @NotNull(message = "Medicine name should not be null")
    @NotBlank(message = "Medicine name should not be empty")
    private String medName;

    @NotNull(message = "Caretaker id should not be null")
    @NotBlank(message = "Caretaker id should not be empty")
    private String id;

    @NotNull(message = "Medicine id should not be null")
    private Integer medId;

    @NotNull(message = "Image is required")
    @ImageValidator
    private MultipartFile image;


}
