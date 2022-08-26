import jwtDecode from 'jwt-decode';

export const reduxInterceptor = async () => {
  const access_token = localStorage.getItem('jwt_token');

  const currentTime = new Date();
  const utcMilliseconds = currentTime.getTime();
  const utcSeconds = Math.round(utcMilliseconds / 1000);

  if (jwtDecode(access_token).exp <= utcSeconds) {
    alert('Session expired!');
    localStorage.clear();
  }
};
