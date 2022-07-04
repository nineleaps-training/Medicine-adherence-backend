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


//    {
//        "iss": "https://accounts.google.com",
//            "azp": "526586885579-185qa2paf0bpl5hrspretllpq2mncig4.apps.googleusercontent.com",
//            "aud": "526586885579-90t54t6rmkquqjct1819getnkstse41j.apps.googleusercontent.com",
//            "sub": "104789176208364871480",
//            "email": "abhichhimwal2901@gmail.com",
//            "email_verified": "true",
//            "name": "Abhijeet Chhimwal",
//            "picture": "https://lh3.googleusercontent.com/a-/AOh14Gi5pUC_TaDC-_iSoeaAZctPzQ4uxCkRfIQGrIazUA=s96-c",
//            "given_name": "Abhijeet",
//            "family_name": "Chhimwal",
//            "locale": "en-GB",
//            "iat": "1656395470",
//            "exp": "1656399070",
//            "alg": "RS256",
//            "kid": "2b09e744d58c9955d4f240b6a92f7b37fead2ff8",
//            "typ": "JWT"
//
//
//
//
//    }


}
