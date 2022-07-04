package com.example.user_service.pojos.response.caretaker;


import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.pojos.dto.caretaker.MyCaretakerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaretakerListResponse {
    private String status;
    private String message;
    private List<MyCaretakerDto> userCaretakerList;
}
