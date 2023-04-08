import { useState } from 'react';
import '../../styles/EditCourse.css';
import '../../styles/DeleteModal.css';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { Field, Form } from 'react-final-form';
import {
  getCourseDetailsAsync,
  getCoursesAsync,
} from '../../../redux/features/courseSlice';
import '../../../global_styles/RequiredField.css';
import {base64StringToBlob} from "blob-util";

const deleteCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses`;
const updateUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/update`;
const uploadCsvUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/student/mass-add`;
const assignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const ProfessorEditCourseComponent = () => {
  let navigate = useNavigate();
  let dispatch = useDispatch();
  const { currentCourse } = useSelector((state) => state.courses);
  const { courseAssignments } = useSelector((state) => state.assignments);
  let { courseId } = useParams();
  const [showModal, setShow] = useState(false);
  const csvFormData = new FormData();

  const fileChangeHandler = (event) => {
    let file = event.target.files[0];
    const renamedFile = new File([file], currentCourse.course_id + '.csv', {
      type: file.type,
    });
    csvFormData.set('csv_file', renamedFile);
  };

  const updateCourse = async (data) => {
    const finalData = { ...data, course_id: currentCourse.course_id };

    await axios
      .put(updateUrl, finalData)
      .then((res) => {
        courseId = res.data;
        window.alert('Course successfully updated!');
        if (csvFormData.get('csv_file') != null) {
          uploadCsv();
        } else {
          dispatch(getCourseDetailsAsync(res.data));
          navigate('/professor/' + res.data);
        }
      })
      .catch((e) => {
        console.error(e);
        window.alert('Error updating course. Please try again.');
      });
  };

  const uploadCsv = async () => {
    await axios
      .post(uploadCsvUrl, csvFormData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => {
        window.alert('CSV successfully uploaded!');
      })
      .catch((e) => {
        console.error(e.response.data);
        window.alert('Error uploading CSV. Please try again.');
      });
    dispatch(getCourseDetailsAsync(courseId));
    navigate('/professor/' + courseId);
  };

  const deleteCourse = async () => {
    const url = `${deleteCourseUrl}/${courseId}/delete`;
    await axios.delete(url).catch((e) => console.error(e.response.data));
    if (courseAssignments.length > 0) await deleteAssignments();
    navigate('/');
  };

  const deleteAssignments = async () => {
    const url = `${assignmentUrl}/${courseId}/remove`;
    await axios.delete(url).catch((e) => e.response.data);
  };

  const Modal = () => {
    return (
      <div id='deleteModal'>
        <div id='modalContent'>
          <span id='deleteSpan'>
            Are you sure you want to delete this course?
          </span>

          <div id='deleteButtons'>
            <button id='ecc-delete-button-delete' class='inter-16-medium-red' onClick={deleteCourse}>Delete</button>
            <button id='ecc-delete-button-cancel' class='inter-16-medium-white' onClick={() => setShow(false)}>Cancel</button>
          </div>
        </div>
      </div>
    );
  };

  const initialData = {
    course_name: currentCourse.course_name,
    course_section: currentCourse.course_section,
    semester: currentCourse.semester,
    abbreviation: currentCourse.abbreviation,
    year: currentCourse.year,
    crn: currentCourse.crn,
    team_size: currentCourse.team_size,
  };

  const handleSubmit = async (formObj) => {
    if (JSON.stringify(initialData) === JSON.stringify(formObj)) {
      if (csvFormData.get('csv_file') != null) {
        await uploadCsv();
      } else {
        alert('Nothing to save!');
      }
    } else {
      await updateCourse(formObj);
    }
    dispatch(getCoursesAsync());
  };

  const onCourseClick = async () => {
    const url = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/course-assignment-files`;

    await axios
        .get(url, { responseType: 'blob' })
        .then((res) => prepareCourseFile(res["headers"]["content-disposition"], res.data.text()));
  }

  const prepareCourseFile = (teamDataName, teamData) => {
    var filename = ""
    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    var matches = filenameRegex.exec(teamDataName);
    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '');
    }
    teamData.then((res) => {
      downloadFile(base64StringToBlob(res, 'application/zip'), filename)
    })
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  return (
    <div className='ecc-form'>
      <Form
        onSubmit={async (formObj) => {
          await handleSubmit(formObj);
        }}
        initialValues={initialData}
      >
        {({ handleSubmit }) => (
          <form onSubmit={handleSubmit}>
            <div className='ecc-input-field'>
              <label className='required'>
                <span className='inter-20-bold'>Name of course: </span>
              </label>
              <Field name='course_name'>
                {({ input }) => (
                  <input type='text' name='course_name' {...input} required />
                )}
              </Field>
            </div>

            <div className='ecc-row-multiple'>
              <div className='ecc-input-field'>
                <label className='required'>
                  {' '}
                  <span className='inter-20-bold'> Course abbreviation: </span>{' '}
                </label>
                <Field name='abbreviation'>
                  {({ input }) => (
                    <input
                      type='text'
                      name='abbreviation'
                      {...input}
                      required
                    />
                  )}
                </Field>
              </div>

              <div className='ecc-input-field'>
                <label className='required'>
                  {' '}
                  <span className='inter-20-bold'> Course section : </span>{' '}
                </label>
                <Field name='course_section'>
                  {({ input }) => (
                    <input
                      type='text'
                      name='course_section'
                      {...input}
                      required
                    />
                  )}
                </Field>
              </div>
            </div>

            <div className='ecc-row-multiple'>
              <div className='ecc-input-field'>
                <label className='required'>
                  <span className='inter-20-bold'>Semester: </span>
                </label>
                <Field name='semester'>
                  {({ input }) => (
                    <input type='text' name='semester' {...input} required />
                  )}
                </Field>
              </div>
              <div className='ecc-input-field'>
                <label className='required'>
                  <span className='inter-20-bold'>Year: </span>{' '}
                </label>
                <Field name='year'>
                  {({ input }) => (
                    <input type='text' name='year' {...input} required />
                  )}
                </Field>
              </div>
            </div>

            <div className='ecc-row-multiple'>
              <div className='ecc-input-field'>
                <label className='required'>
                  <span className='inter-20-bold'>CRN: </span>{' '}
                </label>
                <Field name='crn'>
                  {({ input }) => <input type='text' name='crn' {...input} />}
                </Field>
              </div>

              <div className='ecc-input-field'>
                <label className='required'>
                  <span className='inter-20-bold'>Team size: </span>{' '}
                </label>
                <Field name='team_size'>
                  {({ input }) => (
                    <input type='number' min='1' name='team_size' {...input} />
                  )}
                </Field>
              </div>
            </div>

            <div className='ecc-row-multiple'>
              <div className='ecc-file-upload'>
                <label>
                  {' '}
                  <span className='inter-20-bold'> Course CSV: </span>{' '}
                </label>
                <input
                  onChange={fileChangeHandler}
                  type='file'
                  name='course_csv'
                  accept='.csv'
                />
              </div>
              <div>
                <label>
                  {' '}
                  <span className='inter-20-bold'> Bulk Download for Course </span>{' '}
                </label>
                <span className='inter-16-bold-blue p2' >
                  <button className='blue-button-small' onClick={onCourseClick}>
                  {' '}
                    Download{' '}
                  </button>
                </span>
              </div>
            </div>

            <div>
              <label className ='inter-20-medium'>
                      <span className='required-alt'>
                        Indicates Required Field
                      </span>
              </label>
            </div>

            <div id='ecc-button-container'>
              <button class = 'ecc-button' type='submit'>Save</button>
              <div className='ecc-delete'>
                <div className='ecc-anchor' onClick={() => setShow(true)}>
                  Delete course
                </div>
                <div>{showModal ? Modal() : null}</div>
              </div>
            </div>


          </form>
        )}
      </Form>
      {/*<div className='ecc-delete'>*/}
      {/*  <div className='ecc-anchor' onClick={() => setShow(true)}>*/}
      {/*    Delete course*/}
      {/*  </div>*/}
      {/*  <div>{showModal ? Modal() : null}</div>*/}
      {/*</div>*/}
    </div>
  );
};

export default ProfessorEditCourseComponent;
