import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './styles/CreateCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import { useNavigate } from 'react-router-dom';
import Loader from '../../components/LoaderComponenets/Loader';

const CreateCoursePage = () => {
  const submitCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/create`;
  let navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setIsLoading(true);
    setTimeout(() => setIsLoading(false), 200);
  }, []);

  const [formData, setFormData] = useState({
    course_name: '',
    course_section: '',
    semester: '',
    abbreviation: '',
    year: undefined,
    crn: undefined,
  });

  const { course_name, course_section, semester, abbreviation, year, crn } =
    formData;

  const OnChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    if (
      abbreviation === '' ||
      course_name === '' ||
      course_section === '' ||
      semester === '' ||
      year === undefined ||
      crn === undefined
    ) {
      alert("Fields can't be empty!");
    } else {
      if (year < new Date().getFullYear()) {
        alert('Not a valid year!');
        return;
      }
      e.preventDefault();
      const data = {
        course_name: course_name.trim(),
        course_section: course_section.trim(),
        semester: semester.trim(),
        abbreviation: abbreviation.trim(),
        year: year.toString(),
        crn: crn.toString(),
      };
      console.log(data);
      await axios
        .post(submitCourseUrl, data)
        .then((res) => {
          if (res.data === 'Course already existed.') {
            alert(res.data);
          } else {
            navigate('/');
          }
        })
        .catch((e) => {
          console.log(e);
        });
    }
  };

  return (
    <div>
      {isLoading ? (
        <Loader />
      ) : (
        <div className='cpp-parent'>
          <SidebarComponent />
          <div className='cpp-container'>
            <h2 className='cpp-title'> Add new course </h2>
            <form className='ccp-form'>
              <div className='ccp-input-field'>
                <label>
                  {' '}
                  <b> Course name: </b>{' '}
                </label>
                <input
                  type='text'
                  name='course_name'
                  value={course_name}
                  required
                  onChange={(e) => OnChange(e)}
                />
              </div>

              <div className='cpp-row-multiple'>
                <div className='ccp-input-field'>
                  <label>
                    {' '}
                    <b> Course abbreviation: </b>{' '}
                  </label>
                  <input
                    type='text'
                    name='abbreviation'
                    value={abbreviation}
                    required
                    onChange={(e) => OnChange(e)}
                  />
                </div>

                <div className='ccp-input-field'>
                  <label>
                    {' '}
                    <b> Course section: </b>{' '}
                  </label>
                  <input
                    type='text'
                    name='course_section'
                    value={course_section}
                    required
                    onChange={(e) => OnChange(e)}
                  />
                </div>
              </div>

              <div className='cpp-row-multiple'>
                <div className='ccp-input-field'>
                  <label>
                    {' '}
                    <b> Semester: </b>{' '}
                  </label>
                  <input
                    type='text'
                    name='semester'
                    value={semester}
                    required
                    onChange={(e) => OnChange(e)}
                  />
                </div>

                <div className='ccp-input-field'>
                  <label>
                    {' '}
                    <b> Year: </b>{' '}
                  </label>
                  <input
                    type='number'
                    min={new Date().getFullYear().toString()}
                    step='1'
                    name='year'
                    value={year}
                    required
                    onChange={(e) => OnChange(e)}
                  />
                </div>
              </div>

              <div className='ccp-input-field'>
                <label>
                  {' '}
                  <b> CRN: </b>{' '}
                </label>
                <input
                  type='number'
                  name='crn'
                  value={crn}
                  required
                  onChange={(e) => OnChange(e)}
                />
              </div>

              <div className='ccp-button'>
                <button onClick={handleSubmit}> Create </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreateCoursePage;
