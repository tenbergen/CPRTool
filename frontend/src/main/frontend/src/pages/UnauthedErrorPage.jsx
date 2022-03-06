const logoutURL = `${process.env.REACT_APP_URL}/logout`
const UnauthedErrorPage = () => {
  return (
    <div>
      <div>401 Not Authenticated</div>
      <div>Please log in using your @oswego.edu account!</div>

      <a href={logoutURL}>Go Back</a>
    </div>
  );
};

export default UnauthedErrorPage;
