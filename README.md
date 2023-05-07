# Calibrated Peer Review

A highly scalable web application that assists the process of coordinating and evaluating peer reviews of student work.

**Before contributing, see our [Contribution Guidelines](#Contributing).**

**NOTE: The purpose of this ReadMe is to get you started with running the application. It does not provide specific information
about how the deployment process of this application works. For deeper information on deployment of this application, read the
PDF found in the `startUpDocumentation` folder.**

## Running the Project

First things, first, **clone this repository**.

There are multiple conifigurations for running the application in different environments to make the application
machine and OS agnostic. The possible configurations are:
- [Production](#Running-In-a-Production-Environment): For when you want the app out for full use
- [Local](#Running-In-a-Local-Environment): The production app but running only on a localhost network for testing
- [Run Each Backend Microservice Individually](#Running-Backend-Microservices-Individually): Run each backend service independently of eachother for testing on specific microservices
- [Run The Frontend Microservice Individually](#Running-Frontend-Microservice-Individually): Test the frontend service with a subset of the backend microservices



The minimum required software need to run the application is:
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

To run the application on a Windows operating system, you will need:
- [WSL2 on Windows](https://learn.microsoft.com/en-us/windows/wsl/install)
- [Ubuntu on Windows](https://ubuntu.com/tutorials/install-ubuntu-on-wsl2-on-windows-10#3-download-ubuntu)

After downloading these to your computer, run the application and any of the following commands through the Ubuntu app. **Ensure you are using WSL2! Docker and Docker Compose will not run properly using the originial WSL.**

All environments will make use of a .env file for automation of deployment. At the root of the directory, create a `.env` file using `.env.example` as the template. **Do not change the `MONGO_DATABASE`, `MONGO_INITDB_DATABASE`, `MONGO_PORT`, `MONGO2_PORT`, `MONGO3_PORT`, `MONGO4_PORT`, or `MONGO5_PORT` values. Make `MONGO_INITDB_ROOT_USERNAME`, `MONGO_INITDB_ROOT_PASSWORD`, `MONGO_USERNAME`, and `MONGO_PASSWORD` whatever you would like, but keep them consistent across running environments.**

All environments will be using MongoDB deployed with Docker and Docker compose so that database infrastructure is not different between configurations. You will not need to install MongoDB, but it may be a good idea to install [MongoDB Compass](https://www.mongodb.com/products/compass) so you can more easily monitor the databases. [Mongosh](https://www.mongodb.com/docs/mongodb-shell/install/) is another alternative program to monitor the database if you're more comfortable on the command line

All environments will need Google Cloud Console Credentials.
- Head to [Google Cloud Console Credentials](https://console.cloud.google.com/apis/credentials) to create an `OAuth client ID`.
![image](https://user-images.githubusercontent.com/60359581/216734768-5c4f686b-df5e-4346-aaf1-45604c4a3696.png)
![image](https://user-images.githubusercontent.com/60359581/216734798-85874413-99d3-420c-ae84-7aa1968ca2d0.png)
![image](https://user-images.githubusercontent.com/60359581/216734853-a8c1cad9-eb2d-4783-b39c-c3ba4f5643f6.png)

- Set the `Application type` as `Web application`. `Application name` can be anything.
![image](https://user-images.githubusercontent.com/60359581/216734926-d5aa3d6a-83de-421b-b259-b46e01bc3eba.png)

- Add the full domain URL used in `.env` (with protocol and port if used) to `Authorized JavaScript origins` and `Authorized redirect URIs`. Save the changes. For example, if I am running the app at https://example.com on port 443, I would put https://example.com **and** https://example.com:443 in the two sections. Keep in mind that if you are going to be running the application in a production environment, you will need to deploy the application using an https connnection due to the new Google authentication method requiring any applications that are not running on a local host to use https. 
![image](https://user-images.githubusercontent.com/60359581/216735048-39f43fee-9451-476b-b0a7-3b0a392413d7.png)

- Copy and paste the `Client ID` and `Client secret` that pop up after saving the application to your `.env` file as `CLIENT_ID` and `CLIENT_SECRET` respectively.

![image](https://user-images.githubusercontent.com/60359581/216735126-2a3f38bc-9701-4d02-bd95-3829d0d81f10.png)



### Running In a Production Environment

**Step 1:** Clone the repository.

**Step 2a:** Go to the `docker-compose.yml` file found in root. At the bottom of the file under the **nginx** container, make the docker host port equal to whatever port you have listed in the URL environment variable. So if I was running the app with the URL **https://example.com:443**, the configuration at the bottom of the file would look like this:
![image](https://user-images.githubusercontent.com/60359581/216736629-d31a2768-4b55-41c3-9997-08f983ea7dcc.png)

**Step 2b:** Copy the location of the PEM keys to the **nginx** container found here:
![img.png](img.png)

**Step 3:** In the `.env` file, make the **JWK_ACCESS_URL** equal to whatever your URL variable is followed by **/jwt/ibm/api/cpr_access/jwk**. So if the URL is **https://example.com:443**, **JWK_ACCESS_URL** should be `https://example.com:443/jwt/ibm/api/cpr_access/jwk`

**Step 4:** In the `.env` file, make the **JWK_REFRESH_URL** equal to whatever your URL variable is followed by **/jwt/ibm/api/cpr_refresh/jwk**. So if the URL is **https://example.com:443**, **JWK_ACCESS_URL** should be `https://example.com:443/jwt/ibm/api/cpr_refresh/jwk`

**Step 5:** In `scripts` directory, execute `build-app.sh` to start the application.

**Step 6:** In `scripts` directory, execute `mongo-init.sh` to initialize user database login credentials. This is only necessary on the very first build of the application.

The web application should be running on the specified domain in your `.env` file.

**Step 7:** To add users with elevated privileges (a.k.a professors), an Admin must add their email handle and name in the list
of users in the admin interface. Note that the first person who ever logs into the application
is granted admin privileges and can demote and promote other users as needed.



### Running In a Local Environment
**Step 1:** Clone the repository.

**Step 2:** Go the the `docker-compose-local.yml` file found in root. If running on an Apple Silicon Mac (M1 or M2) go to the docker-compose-local-m1.yml file instead. At the bottom of the file under the **nginx** container, make the docker host port equal to whatever port you have listed in the URL environment variable. So if I was running the app with the URL **http://localhost.com:443**, the configuration at the bottom of the file would look like this:
![image](https://user-images.githubusercontent.com/60359581/216736629-d31a2768-4b55-41c3-9997-08f983ea7dcc.png)

**Step 3:** In the `.env` file, make the **JWK_ACCESS_URL** equal to `http://172.17.0.1:<whatever port you're running on>/jwt/ibm/api/cpr_access/jwk` . This URL is specifically needed so that the docker container running the microservices knows to access the docker host's localhost IP address.

**Step 4:** In the `.env` file, make the **JWK_REFRESH_URL** equal to `http://172.17.0.1:<whatever port you're running on>/jwt/ibm/api/cpr_refresh/jwk` . This URL is specifically needed so that the docker container running the microservices knows to access the docker host's localhost IP address.

**Step 5:** In order to properly run on localhost using Google's new login service, one line needs to be added in the frontend's `index.html` file. Simply uncomment the `referrer` meta link found at `frontend/src/main/frontend/public/index.html`. **MAKE SURE TO COMMENT IT OUT AGAIN BEFORE DOING ANY PULL REQUESTS**
![image](https://user-images.githubusercontent.com/60359581/216741360-346c6c24-f180-4387-a94b-4a3456bcd3f1.png)
![image](https://user-images.githubusercontent.com/60359581/216741371-23390934-6375-405e-9436-2bd9fe7baee8.png)

**Step 5:** In `scripts` directory, execute `build-app-local.sh` to start the application.

**Step 6:** In `scripts` directory, execute `mongo-init.sh` to initialize user database login credentials. This is only necessary on the very first build of the application.

The web application should be running on the specified domain in your `.env` file.

**Step 7:** To add users with elevated privileges (a.k.a professors), an Admin must add their email handle and name in the list
of users in the admin interface. Note that the first person who ever logs into the application
is granted admin privileges and can demote and promote other users as needed.

### Running Backend Microservices Individually 
**Before running anything individually, you will need:**
- [Maven](https://maven.apache.org/install.html) >= 3.8.4
- [JDK](https://openjdk.java.net/projects/jdk/17/) >= 17

**Step 1:** If working on the backend microservices and do not need the frontend, only the database needs to run on Docker. This decision was made so that one does not need MongoDB downloaded on their computer and makes the different configurations less different from one another. Before doing so, set the following values as environment variables on your machine:

| Variable          | Value                                                |
|------------------ |------------------------------------------------------|
| `LOCALHOST`       |  `true`                                              |
|  `MONGO_PORT`     |  `27037`                                             |
|  `MONGO2_PORT`    |  `27038`                                             |
|  `MONGO3_PORT`    |  `27039`                                             |
|  `MONGO4_PORT`    |  `27040`                                             |
|  `MONGO5_PORT`    |  `27041`                                             |
|  `MONGO5_DATABASE`|  `cpr`                                               |
|  `MONGO_USERNAME` |  <whatever you set it to in the .env file>           |
|  `MONGO_PASSWORD` |  <whatever you set it to in the .env file>           |
|`JWK_ACCESS_URL`   |  `http://localhost:13126/jwt/ibm/api/cpr_access/jwk` |
|`JWK_REFRESH_URL`  |  `http://localhost:13126/jwt/ibm/api/cpr_refresh/jwk`|

**Step 2:** Go to the `scripts` folder in the root directory and run the `independently-run-db.sh` shell script. If running on an Apple Silicon Mac (M1 or M2) run the `independently-run-db-m1.sh` shell script. If you haven't already, also run the `mongo-init.sh` shell script afterwards. 

**Step 3:** You can now run the backend microservices separately. Simply go to the root of each microservice where the `pom.xml` file is located and run `mvn liberty:dev` to start the microservice. The web app should be running on http://localhost:xxxxx - the port depends on which microservice you are running as following:

| Microservice                     | Port    |
|----------------------------------|---------|
| `login`                          | `13126` |
| `course-manager`                 | `13127` |
| `course-viewer`                  | `13128` |
| `peer-review-teams`              | `13129` |
| `professor-assignment`           | `13130` |
| `student-assignment`             | `13131` |
| `student-peer-review-assignment` | `13132` |

### Running Frontend Microservice Individually

To run the frontend outside of the docker-compose network, we will only run the databases and nginx webserver with docker.

**Before running anything individually, you will need:**
- [Maven](https://maven.apache.org/install.html) >= 3.8.4
- [JDK](https://openjdk.java.net/projects/jdk/17/) >= 17

**Step 1:** If working on the backend microservices and do not need the frontend, only the database needs to run on Docker. This decision was made so that one does not need MongoDB downloaded on their computer and makes the different configurations less different from one another. Before doing so, set the following values as environment variables on your machine:

| Variable          | Value                                                |
|-------------------|------------------------------------------------------|
| `LOCALHOST`       | `false`                                              |
|  `MONGO_PORT`     | `27037`                                              |
|  `MONGO2_PORT`    | `27038`                                              |
|  `MONGO3_PORT`    | `27039`                                              |
|  `MONGO4_PORT`    | `27040`                                              |
|  `MONGO5_PORT`    | `27041`                                              |
|  `MONGO5_DATABASE`| `cpr`                                                |
|  `MONGO_USERNAME` | <whatever you set it to in the .env file>            |
|  `MONGO_PASSWORD` | <whatever you set it to in the .env file>            |
|`JWK_ACCESS_URL`   | `http://172.17.0.1:3000/jwt/ibm/api/cpr_refresh/jwk`  |
|`JWK_REFRESH_URL`  | `http://172.17.0.1:3000/jwt/ibm/api/cpr_refresh/jwk` |

**Step 2:** Go inside the folder `CSC480-22S/frontend/src/main/frontend` and create a .env file. Add the following lines to the file:
`export REACT_APP_URL=http://localhost:3000/`
`export REACT_APP_CLIENT_ID=<whatever your google client ID is>`

**Step 3:** In order to properly run on localhost using Google's new login service, one line needs to be added in the frontend's `index.html` file. Simply uncomment the `referrer` meta link found at `frontend/src/main/frontend/public/index.html`. **MAKE SURE TO COMMENT IT OUT AGAIN BEFORE DOING ANY PULL REQUESTS**
![image](https://user-images.githubusercontent.com/60359581/216741360-346c6c24-f180-4387-a94b-4a3456bcd3f1.png)
![image](https://user-images.githubusercontent.com/60359581/216741371-23390934-6375-405e-9436-2bd9fe7baee8.png)

**Step 4:** Go to the `scripts` folder in the root directory and run the `run-frontend-proxy.sh` shell script. If running on an Apple Silicon Mac, run the `run-frontend-proxy-m1.sh` script instead. If you haven't already, also run the `mongo-init.sh` shell script afterwards. 

**Step 5:** You can now run microservices independently. Simply go to the root of each backend microservice where the `pom.xml` file is located and run `mvn liberty:dev` to start the microservice. To run the frontend service, go to the folder located at `CSC480-22S/frontend/src/main/frontend` and run `PORT=<whatever port you want> npm run start` to start the frontend service. Ensure the port you pick does not conflict with any other applications. You will be able to reach the front end at `http://localhost:<The port you specified>`.

The backend microservices should be running on http://localhost:xxxxx - the port depends on which microservice you are running:

| Microservice                     | Port    |
|----------------------------------|---------|
| `login`                          | `13126` |
| `course-manager`                 | `13127` |
| `course-viewer`                  | `13128` |
| `peer-review-teams`              | `13129` |
| `professor-assignment`           | `13130` |
| `student-assignment`             | `13131` |
| `student-peer-review-assignment` | `13132` |

## Contributing

Contributors are more than welcome to improve the project by creating a new issue to report bugs, suggest new features, or make changes to the source code by making a pull request. To have your work merged in, please make sure the following is done:

1. If you are not a contributor, fork the repository and create your branch from master. Otherwise, create a new development branch with a desired name.
2. Push your commits to the aforementioned fork/branch. 
3. Make sure to keep the changes within the scope of the pull request you are about to make, as it is highly disencouraged to commit irrelevant changes that does not have anything to do with the pull request.
4. Create a pull request.
5. If you’ve fixed a bug or added something new, add a comprehensive list of changes. Ensure that your code is tested, functional, and is linted.
6. When merging into master, it is highly recommended that you choose "squash and merge" instead of the default option "create a merge commit" to prevent flooding the master branch's commit history.
