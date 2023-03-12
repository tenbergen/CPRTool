import './styles/UnauthedStyle.css';
import { useNavigate } from 'react-router-dom';

const UnauthedErrorPage = () => {
  let navigate = useNavigate();
  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
    window.location.reload(false);
  };
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
              <button className='go-back-link' onClick={() => handleLogout()}>
                Go Back
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UnauthedErrorPage;
