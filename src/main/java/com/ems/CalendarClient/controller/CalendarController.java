package com.ems.CalendarClient.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RestController
public class CalendarController {
    private final static Log logger = LogFactory.getLog(CalendarController.class);
    private static final String APPLICATION_NAME = "serviceCal";
    private static HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static com.google.api.services.calendar.Calendar client;

    URL url = getClass().getResource("credentials.json");
    File keyFile = new File(url.getPath());
    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;

    HttpTransport TRANSPORT ;
    private String SERVICE_ACCOUNT="calendarservice@beaming-edition-337001.iam.gserviceaccount.com";

    private Set<Event> events = new HashSet<>();

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ResponseEntity<String> oAuth2Callback(HttpServletRequest request) throws IOException, GeneralSecurityException {
        com.google.api.services.calendar.model.Events eventList;
        String message;

        Preconditions.checkArgument(!Strings.isNullOrEmpty(APPLICATION_NAME),
        "applicationName cannot be null or empty!");
        try {
            TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(SERVICE_ACCOUNT)
                    .setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
                    .setServiceAccountPrivateKeyFromP12File(keyFile)
                    .build();
            credential.refreshToken();
            client = new com.google.api.services.calendar.Calendar.Builder(TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
            System.out.println(client);
            Events events = client.events();
            eventList = events.list("primary").setTimeMin(date1).setTimeMax(date2).execute();
            message = eventList.getItems().toString();

            Event event = new Event()
                    .setSummary("Google I/O 2015")
                    .setLocation("800 Howard St., San Francisco, CA 94103")
                    .setDescription("A chance to hear more about Google's developer products.");

            DateTime startDateTime = new DateTime("2021-07-31T09:00:00-07:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("America/Los_Angeles");
            event.setStart(start);

            DateTime endDateTime = new DateTime("")
        }
    }



}
