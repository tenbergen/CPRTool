import './styles/UnauthedStyle.css';

const logoutURL = `${window.location.protocol}//${window.location.host}/logout`;
const UnauthedErrorPage = () => {
  return (
    <div id='unauth'>
      <div className='unauthContent'>
        <div className='unauth-wrapper'>
          <div className='unauth-stm'>
            Not Authenticated!
            <span id='emoji'> &#128542;</span>
          </div>
          <div className='unauth-instruction'>
            Please log in using your{' '}
            <span class='auth-oswego'> @oswego.edu </span> account!
            <br />
            <div className='go-back-wrapper'>
              <a className='go-back-link' href={logoutURL}>
                Go Back
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UnauthedErrorPage;
