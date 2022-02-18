# Frontend Microservice 

## Getting Started

This frontend microservice is created using React and uses OpenLiberty 
to help package and deploy the app. 

## Running the Project

**Step 1:** Clone the repository.

### Follow either one of the following: 

**Step 2a:** Navigate to `src/main/frontend` and run `npm start`. The React app should run on
http://localhost:3000.

**Step 2b:** Run `mvn process-resources liberty:run`. The React app should run on 
http://127.0.0.1:9080. 

(Note: Step 2b is *required* to deploy the frontend microservice into the server.
However, since 2b takes time, 2a is recommended for viewing/developing purposes.)
