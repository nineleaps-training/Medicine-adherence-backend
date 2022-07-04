package com.example.user_service.pojos.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleSsoError {
    
    private String status;
    private String message;
    
}
