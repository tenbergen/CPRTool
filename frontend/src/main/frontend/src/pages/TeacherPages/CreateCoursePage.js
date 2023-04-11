import { useEffect, useState } from 'react';
import axios from 'axios';
import './styles/CreateCourseStyle.css';
import { useNavigate } from 'react-router-dom';
import Loader from '../../components/LoaderComponenets/Loader';
import CourseBarComponent from '../../components/CourseBarComponent';
import { useSelector } from 'react-redux';
import Breadcrumbs from "../../components/Breadcrumbs";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";

const CreateCoursePage = () => {
  const submitCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/create`;

  const { user_given_name } = useSelector((state) => state.auth);
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
    year: 0,
    crn: 0,
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
      year === "" ||
      crn === "" ||
      parseInt(crn) <= 0 ||
      parseInt(year) < parseInt(new Date().getFullYear().toString()) ||
      isNaN(parseInt(crn)) ||
      isNaN(parseInt(year))
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
      await axios
        .post(submitCourseUrl, data)
        .then((_) => {
          navigate('/');
        })
        .catch((e) => {
          alert(`Error: ${e.response.data}`);
        });
    }
  };

  return (
    <div>
      {isLoading ? (
        <Loader />
      ) : (
          <div className="course-page-container">
            <HeaderBar/>
            <div className='ccp-container'>
              <NavigationContainerComponent/>
              <div className='pcp-components'>
                <Breadcrumbs />
                <h2 className='inter-28-bold cpp-title'> Add new course </h2>
                <form className='ccp-form'>
                  <div className='input-field ccp-input-field'>
                    <label className='inter-20-medium'>
                      <span className='required'>
                        Course Name:
                      </span>
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
                    <div className='input-field ccp-input-field'>
                      <label className='inter-20-medium'>
                        <span className='required'>
                          Course Abbreviation:
                        </span>
                      </label>
                      <input
                          type='text'
                          name='abbreviation'
                          value={abbreviation}
                          required
                          onChange={(e) => OnChange(e)}
                      />
                    </div>

                    <div className='input-field ccp-input-field'>
                      <label className='inter-20-medium'>
                        <span className='required'>
                          Course Section:
                        </span>
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
                    <div className='input-field ccp-input-field'>
                      <label className='inter-20-medium'>
                        <span className='required'>
                          Semester:
                        </span>
                      </label>
                      <input
                          type='text'
                          name='semester'
                          value={semester}
                          required
                          onChange={(e) => OnChange(e)}
                      />
                    </div>

                    <div className='input-field ccp-input-field'>
                      <label className='inter-20-medium'>
                        <span className='required'>
                          Year:
                        </span>
                      </label>
                      <input
                          type='number'
                          min={new Date().getFullYear().toString()}
                          step='1'
                          name='year'
                          value={year}
                          required
                          onChange={(e) => OnChange(e)}
                          onWheel={(e) => e.target.blur()}
                      />
                    </div>
                  </div>

                  <div className='cpp-row-multiple'>
                    <div className='input-field ccp-input-field'>
                      <label className='inter-20-medium'>
                        <span className='required'>
                          CRN:
                        </span>
                      </label>
                      <input
                          type='number'
                          name='crn'
                          value={crn}
                          required
                          onChange={(e) => OnChange(e)}
                          onWheel={(e) => e.target.blur()}
                      />
                    </div>
                    <div className='input-field ccp-input-field' 
                    />
                  </div>

                  <div>
                    <label className ='inter-20-medium'>
                      <span className='required-alt'>
                        Indicates Required Field
                      </span>
                    </label>
                  </div>

                  <div className='ccp-button'>
                    <button className='green-button-medium' onClick={handleSubmit}>
                      {' '}
                      Create
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
      )}
    </div>
  );
};

export default CreateCoursePage;
