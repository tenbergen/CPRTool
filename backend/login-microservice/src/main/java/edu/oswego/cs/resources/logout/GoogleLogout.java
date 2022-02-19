package edu.oswego.cs.resources.logout;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.ibm.websphere.security.social.UserProfileManager;

public class GoogleLogout implements ILogout {
    @Inject
    @ConfigProperty(name="google.client-id")
    private String clientId;

    @Inject
    @ConfigProperty(name="google.client-pwd")
    private String clientSecret;

    public Response logout() {
        // final String unauthorizeUrl = "https://api.github.com/applications/{client_id}/grant";

        // google oauth endpoint
        final String unauthorizeUrl = "https://accounts.google.com/o/oauth2/v2/auth";
        // google access token endpoint
        final String unauthorizeUrl2 = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=xxxxxx";

        // Google logout endpoint
        final String unauthorizeUrl3 = "https://mail.google.com/mail/u/0/?logout&hl=en";

        // google logout endpoint
        final String unauthorizeUrl4 = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://www.example.com";

        String accessToken = UserProfileManager
                .getUserProfile()
                .getAccessToken();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        String auth = clientId + ":" + clientSecret;
        byte[] encodedAuthStream = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));

        String encodedAuth = new String(encodedAuthStream);

        return ClientBuilder
                .newClient()
                .target(unauthorizeUrl3)
                .resolveTemplate("client_id", clientId)
                .request()
                .header("Authorization", "Basic " + encodedAuth)
                .method("DELETE", Entity.json(requestBody));
    }
}
