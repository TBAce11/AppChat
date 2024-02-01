# AppChat

AppChat is single page messaging application that was built mainly as a gateway to the world of fullstack development in order to bridge the gap between my backend and frontend skills. The main tech stack used for this project is Angular and Java Spring Boot backed up by Typescript and Java respectfully.

## Usage

The application is currently being hosted on a production environment in the following [link](https://inf5190-chat-prod-1bd3f.web.app).

***Disclaimers***

- As a first time user, any combination of username and password will be accepted. Granted, no text box is left empty. As soon as you access the /chat domain, your credentials will be saved automatically and can be used again to login.

- To use the supported image sharing feature, click on the picture icon, select the file you want to upload, write down some text that goes with it and send the whole package. It should be displayed together as a bundle.

## Run Locally

Clone the project

```bash
  git clone https://github.com/TBAce11/AppChat
```

Install Angular's CLI

```bash
  npm install -g @angular/cli
```

Install dependencies

```bash
  npm install
```

Run the frontend

```bash
  ng serve
```

Start the server

```bash
  ./mvnw clean spring-boot:run
```

***Note:*** for the full experience, click on the website's link as the connection with Firebase might be compromised without the proper authorization. An account will be needed for it to store messages and user information properly.

For the unit tests, start with the frontend part

```bash
  ng test
```

Then the backend part 

```bash
  firebase emulators:exec "./mvnw clean verify"
```