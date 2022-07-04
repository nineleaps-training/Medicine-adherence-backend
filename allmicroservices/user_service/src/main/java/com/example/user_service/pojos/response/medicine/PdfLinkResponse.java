package com.example.user_service.pojos.response.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfLinkResponse {

    private String status;
    private String message;
    private String pdfLink;


}
