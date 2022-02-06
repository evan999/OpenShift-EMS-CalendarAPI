package com.ems.CalendarClient.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import org.springframework.web.servlet.view.RedirectView;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
public class CalendarController {
    private final static Log logger = LogFactory.getLog(CalendarController.class);
    private static final String APPLICATION_NAME = "CalendarService";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static com.google.api.services.calendar.Calendar client;

//    URL url = getClass().getResource("credentials.json");
//    File keyFile = new File(url.getPath());
    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;

    @Value("${google.client.client-id")
    private String clientId;
    @Value("${google.client.client-secret")
    private String clientSecret;

    // TODO: Get Redirect URI
    @Value("${google.client.redirectUri}")
    private String redirectURI;


    HttpTransport TRANSPORT;
    private String SERVICE_ACCOUNT="calendarservice@beaming-edition-337001.iam.gserviceaccount.com";

    private Set<Event> events = new HashSet<>();

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    final DateTime date1 = new DateTime("2017-05-05T16:30:00.000+05:30");
    final DateTime date2 = new DateTime(new Date());

//    @RequestMapping(value = "/Callback", method = RequestMethod.GET)
//    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
//        return new RedirectView(authorize());
//    }

    @RequestMapping(value = "/Callback", method = RequestMethod.GET)
    public ResponseEntity<String> oAuth2Callback(@RequestParam(value = "code") ) throws IOException, GeneralSecurityException {
        com.google.api.services.calendar.model.Events eventList;
        String message;

//        Preconditions.checkArgument(!Strings.isNullOrEmpty(APPLICATION_NAME),
//        "applicationName cannot be null or empty!");
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(SERVICE_ACCOUNT)
                    .setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
//                    .setServiceAccountPrivateKeyFrom(keyFile)
                    .build();
            credential.refreshToken();
            client = new com.google.api.services.calendar.Calendar.Builder(TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
            System.out.println(client);
            Events events = client.events();
            eventList = events.list("primary").setTimeMin(date1).setTimeMax(date2).execute();
            message = eventList.getItems().toString();
            System.out.println("My:" + eventList.getItems());

            final DateTime date1 = new DateTime("2017-05-05T16:30:00.000+05:30");
            final DateTime date2 = new DateTime(new Date());

//            Event event = new Event()
//                    .setSummary("Google I/O 2015")
//                    .setLocation("800 Howard St., San Francisco, CA 94103")
//                    .setDescription("A chance to hear more about Google's developer products.");
//
//            DateTime startDateTime = new DateTime("2021-07-31T09:00:00-07:00");
//            EventDateTime start = new EventDateTime()
//                    .setDateTime(startDateTime)
//                    .setTimeZone("America/Los_Angeles");
//            event.setStart(start);

//            DateTime endDateTime = new DateTime("")
        } catch (Exception exception) {
            logger.warn("Exception while handling OAuth2 callback (" + exception.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            message = "Exception while handling OAuth2 callback (" + exception.getMessage() + ")."
                    + " Redirecting to google connection status page.";
        }

        System.out.println("cal message:" + message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    public Set<Event> getEvents() throws IOException {
        return this.events;
    }

    private String authorize() throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            Details web = new Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR)).build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        System.out.println("cal authorizationUrl->" + authorizationUrl);
        return authorizationUrl.build();
    }



}
