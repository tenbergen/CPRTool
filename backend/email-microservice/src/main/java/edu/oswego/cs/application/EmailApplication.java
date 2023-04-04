package edu.oswego.cs.application;

import edu.oswego.cs.util.DeadlineTracker;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("email")
public class EmailApplication extends Application {
    /**
     * For some reason @Startup and @ApplicationScoped won't automatically create a DeadlineTracker,
     * so I put it here.
     */
    public EmailApplication(){
        super();
        new DeadlineTracker();
    }
}