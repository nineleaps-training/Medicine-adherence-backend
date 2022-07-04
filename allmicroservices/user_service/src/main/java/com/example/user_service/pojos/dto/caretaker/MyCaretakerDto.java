package com.example.user_service.pojos.dto.caretaker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyCaretakerDto {

    private String caretakerUsername;
    private String caretakerId;
    private String createdAt;
    private String cId;

}
