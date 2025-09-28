package app;

import app.api.EmailProvider;

public class TheBestEmailProvider implements EmailProvider {

    @Override
    public void send(String to, String subject, String body) {
        // mails sending always succeed
    }

}
