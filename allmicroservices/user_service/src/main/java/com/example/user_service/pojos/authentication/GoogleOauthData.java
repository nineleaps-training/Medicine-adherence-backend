package com.example.user_service.pojos.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleOauthData {

    private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String email;
    private String email_verified;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private String locale;
    private String iat;
    private String exp;
    private String alg;
    private String kid;
    private String typ;

}
