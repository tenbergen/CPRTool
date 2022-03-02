const UnauthedErrorPage = () => {
  return (
    <div>
      <div>401 Not Authenticated</div>
      <div>Please log in using your @oswego.edu account!</div>

      <a href='http://moxie.cs.oswego.edu:13126/logout'>Go Back</a>
    </div>
  );
};

export default UnauthedErrorPage;
