import './styles/UnauthedStyle.css'

const logoutURL = `${window.location.protocol}//${window.location.host}/logout`
const UnauthedErrorPage = () => {
  return (
    <div id="unauth">
        <div id="unauthDiv">
          <div className="unauthContent">401 Not Authenticated
              <span id="emoji">&#128542;</span>
          </div>
      <div className="unauthContent">Please log in using your @oswego.edu account!
        <br/>
        <a href={logoutURL}>Go Back</a>
      </div>

    </div>
    </div>
  );
};

export default UnauthedErrorPage;
