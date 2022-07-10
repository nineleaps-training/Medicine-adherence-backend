package com.example.user_service.config;

import com.example.user_service.exception.GoogleSsoException;
import com.example.user_service.pojos.authentication.GoogleOauthData;
import com.example.user_service.util.Messages;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleOauthCheck {

    public void checkForGoogleaccount(String userEmail) throws GoogleSsoException {

        try{
            String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjI2NTBhMmNlNDdiMWFiM2JhNDA5OTc5N2Y4YzA2ZWJjM2RlOTI4YWMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1MjY1ODY4ODU1NzktMTg1cWEycGFmMGJwbDVocnNwcmV0bGxwcTJtbmNpZzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1MjY1ODY4ODU1NzktOTB0NTR0NnJta3F1cWpjdDE4MTlnZXRua3N0c2U0MWouYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDQ3ODkxNzYyMDgzNjQ4NzE0ODAiLCJlbWFpbCI6ImFiaGljaGhpbXdhbDI5MDFAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJBYmhpamVldCBDaGhpbXdhbCIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaTVwVUNfVGFEQy1faVNvZWFBWmN0UHpRNHV4Q2tSZklRR3JJYXpVQT1zOTYtYyIsImdpdmVuX25hbWUiOiJBYmhpamVldCIsImZhbWlseV9uYW1lIjoiQ2hoaW13YWwiLCJsb2NhbGUiOiJlbi1HQiIsImlhdCI6MTY1NjQ3ODY5MiwiZXhwIjoxNjU2NDgyMjkyfQ.mqP1BeAnHtvHAKkQotlgd5PmwLTk39e3mo-eSwIaXGA988E7stqADDgTYCjvc6PsNqZH_L2f0oZOr-9gUknls-3slITwoj61Otym8niBdQgxXr0NrNysb1xmFRFEouMG3PeBnYiDMWfuzjWC7HESVmQ_1TjJSOJpYLR0u54-t1k84o-7iCosuvHUp9MgL1h0FsGwE35UQmmtsPqvbxI6WSSi26vaJHYszEDMsI8bec_FKL3N99TQIF27JV9OP_fG3XQkLmEO-xZ9sTDbrYyMMF-82wIQElO8cBXwucfgHlGw73Z7csAt9Eru16dz3mrC7HOnke60tPOmUoQYcgRdGA";
            RestTemplate restTemplate = new RestTemplate();
            GoogleOauthData googleOauthData = restTemplate.getForObject("https://oauth2.googleapis.com/tokeninfo?id_token="+token,GoogleOauthData.class);
            if(googleOauthData!=null && !googleOauthData.getEmail().equals(userEmail)){
                throw new GoogleSsoException(Messages.NOT_VALID);
            }
        }catch (GoogleSsoException e){
            throw new GoogleSsoException(Messages.NOT_VALID);

        }


    }


}
