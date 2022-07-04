package com.example.user_service.pojos.response.image;

import com.example.user_service.model.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagesResponse {
    private String status;
    private String message;
    private List<Image> images;
}
