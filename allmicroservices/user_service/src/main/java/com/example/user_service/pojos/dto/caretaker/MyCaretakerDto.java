package com.example.user_service.pojos.dto.caretaker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyCaretakerDto implements Serializable {

    @NotNull(message = "CareTaker username should not be null")
    @NotBlank(message = "CareTaker username should not be Empty")
    private String caretakerUsername;

    @NotBlank(message = "CareTaker id should not be null")
    @NotNull(message = "CareTaker id should not be Empty")
    private String caretakerId;

    private String createdAt;

    @NotBlank(message = "id should not be null")
    @NotNull(message = "id should not be Empty")
    private String cId;

}
