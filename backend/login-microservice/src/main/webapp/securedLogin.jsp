<%@ page contentType="text/html;charset=UTF-8"%>
<html>
  <head>
    <title>Calibrated Peer Review</title>
  </head>
  <body>
    <h1>Login Page</h1>
    <p>Welcome to Calibrated Peer Review!</p>
    <p>You are currently authenticated!</p>
    <p>Hello, ${username}</p>
    <form method="post" action="logout">
      <button type="submit">Log out</button>
    </form>
  </body>
</html>
