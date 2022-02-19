package edu.oswego.cs.resources.logout;

import com.ibm.websphere.security.social.UserProfileManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class LogoutHandler {
    @Inject
    private GoogleLogout googleLogout;

    private static final String GOOGLE_LOGIN = "googleLogin";

    public ILogout getLogout() {
        String socialMediaName = UserProfileManager.getUserProfile().getSocialMediaName();

        switch (socialMediaName) {
            case GOOGLE_LOGIN:
                return googleLogout;
            default:
                throw new UnsupportedOperationException("Cannot find the right logout service for social media name " + socialMediaName);
        }
    }
}
