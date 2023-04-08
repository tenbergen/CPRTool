import { useState, useEffect } from 'react';
import './styles/CreateAssignmentStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { Field, Form } from 'react-final-form';
import CourseBarComponent from '../../components/CourseBarComponent';
import Loader from '../../components/LoaderComponenets/Loader';
import ProfessorHeaderBar from "../../components/ProfessorComponents/ProfessorHeaderBar";

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const CreateAssignmentPage = () => {
  let navigate = useNavigate();
  let { courseId } = useParams();

  const submitCourseUrl = `${profAssignmentUrl}/create-assignment`;
  const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`;
  const [loading, setLoading] = useState(false);

  const assignmentFileFormData = new FormData();
  let assignmentFileName = ""
  const rubricFileFormData = new FormData();
  let rubricFileName = ""
  const templateFileFormData = new FormData();
  let templateFileName = ""

  const fileChangeHandler = (event, fileType) => {
    let file = event.target.files[0];
    var reader = new FileReader()
    reader.onloadend = () => {
      // Use a regex to remove data url part
      const base64String = reader.result
          .replace('data:', '')
          .replace(/^.+,/, '');
      if (fileType === 'assignment') {
        assignmentFileName = file.name
        assignmentFileFormData.set(file.name, base64String);
      } else if (fileType === 'rubric') {
        rubricFileName = file.name
        rubricFileFormData.set(file.name, base64String);
      } else {
        templateFileName = file.name
        templateFileFormData.set(file.name, base64String);
      }
    };
    reader.readAsDataURL(file);
  };

  const uploadFiles = async (assignmentId) => {
    const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`;
    const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`;
    const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`;

    await axios
      .post(assignmentFileUrl, assignmentFileFormData)
      .then((res) => {})
      .catch((e) => {
        console.error(e);
        alert('Error uploading assignment file.');
      });

    await axios
      .post(rubricUrl, rubricFileFormData)
      .then((res) => {})
      .catch((e) => {
        console.error(e);
        alert('Error uploading peer review rubric.');
      });

    await axios
      .post(templateUrl, templateFileFormData)
      .then((res) => {})
      .catch((e) => {
        console.error(e);
        alert('Error uploading peer review template.');
      });
  };

  const handleSubmit = async (data) => {
    let due_date = new Date(data['due_date']).getTime()
    let peer_review_due_date = new Date(data['peer_review_due_date']).getTime()
    if (due_date >= peer_review_due_date) {
      alert('Peer Review Due Date CANNOT be due before the due date of the Assignment!')
      return
    }
    else {
      let {points} = data;
      points = parseInt(points);
      const course_id = courseId;

      setLoading(true);

      const sentData = {...data, points, course_id};
      ;

      await axios
          .post(submitCourseUrl, sentData)
          .then((res) => {
          })
          .catch((e) => {
            console.error(e.response.data);
          });
      const assignment_id = await axios
          .get(getAssUrl)
          .then((res) => {
            return res.data.pop().assignment_id;
          })
          .catch((e) => {
            console.error(e.response.data);
          });
      await uploadFiles(assignment_id);

      setLoading(false);
      navigate('/professor/' + courseId);
    }
  };

  return (
    <div>
      {loading ? (
        <Loader />
      ) : (
        <Form
          onSubmit={async (formObj) => {
            await handleSubmit(formObj);
          }}
        >
          {({ handleSubmit }) => (
            <div className="page-container">
              <ProfessorHeaderBar/>
              <div className='pcp-parent'>
                <div className='ccp-container'>
                  <CourseBarComponent title={'Courses'} />
                  <div className='pcp-components'>
                    <h2 className='inter-28-bold'> New Assignment </h2>
                    <div className='cap-form'>
                      <form onSubmit={handleSubmit}>
                        {/*assignment field*/}
                        <div className='field-container'>
                          <div className='field-title'>
                            {' '}
                            <span className='inter-20-medium-white'> Assignment </span>{' '}
                          </div>
                          <div className='field-content'>
                            <div className='input-field cap-input-field'>
                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Name of Assignment:
                                </span>
                              </label>
                              <Field name='assignment_name'>
                                {({ input }) => (
                                    <input
                                        type='text'
                                        name='assignment_name'
                                        {...input}
                                        required
                                    />
                                )}
                              </Field>
                            </div>

                            <div className='input-field cap-instructions'>
                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Instructions:
                                </span>
                              </label>
                              <Field name='instructions'>
                                {({ input }) => (
                                    <textarea
                                        name='instructions'
                                        {...input}
                                        required
                                    />
                                )}
                              </Field>
                            </div>

                            <div className='cap-assignment-files'>
                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Files:
                                </span>
                              </label>
                              <input
                                  type='file'
                                  name='assignment_files'
                                  accept='.pdf,.zip,.docx'
                                  required
                                  onChange={(e) =>
                                      fileChangeHandler(e, 'assignment')
                                  }
                              />
                            </div>

                            <div className='input-field cap-assignment-info'>
                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Due Date:
                                </span>
                              </label>
                              <Field name='due_date'>
                                {({ input }) => (
                                    <input
                                        type='date'
                                        name='due_date'
                                        {...input}
                                        required
                                        min={new Date().toISOString().split('T')[0]}
                                    />
                                )}
                              </Field>

                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Points:
                                </span>
                              </label>
                              <Field name='points'>
                                {({ input }) => (
                                    <input
                                        type='number'
                                        name='points'
                                        {...input}
                                        required
                                        onWheel={(e) => e.target.blur()}
                                    />
                                )}
                              </Field>
                            </div>
                          </div>
                        </div>

                        {/*peer review fields*/}
                        <div className='field-container'>
                          <div className='field-title'>
                            {' '}
                            <span className='inter-20-medium-white'> Peer Review </span>{' '}
                          </div>
                          <div className='field-content'>
                            <div className='input-field cap-instructions'>
                              <label className='inter-20-medium'>
                                {' '}
                                <span className='required'>
                                Peer Review Instructions:
                                </span>{' '}
                              </label>
                              <Field name='peer_review_instructions'>
                                {({ input }) => (
                                    <textarea
                                        name='peer_review_instructions'
                                        {...input}
                                        required
                                    />
                                )}
                              </Field>
                            </div>

                            <div className='cap-assignment-files'>
                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Rubric:
                                </span>
                              </label>
                              <input
                                  type='file'
                                  name='peer_review_rubric'
                                  accept='.pdf,.zip,.docx'
                                  required
                                  onChange={(e) => fileChangeHandler(e, 'rubric')}
                              />

                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Template:
                                </span>
                              </label>
                              <input
                                  type='file'
                                  name='peer_review_template'
                                  accept='.pdf,.zip,.docx'
                                  required
                                  onChange={(e) => fileChangeHandler(e, 'template')}
                              />
                            </div>

                            <div className='input-field cap-assignment-info'>
                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Due Date:
                                </span>
                              </label>
                              <Field name='peer_review_due_date'>
                                {({ input }) => (
                                    <input
                                        type='date'
                                        name='peer_review_due_date'
                                        {...input}
                                        required
                                        min={new Date().toISOString().split('T')[0]}
                                    />
                                )}
                              </Field>

                              <label className='inter-20-medium'>
                                <span className='required'>
                                  Points:
                                </span>
                              </label>
                              <Field name='peer_review_points'>
                                {({ input }) => (
                                    <input
                                        type='number'
                                        min='0'
                                        name='peer_review_points'
                                        {...input}
                                        required
                                        onWheel={(e) => e.target.blur()}
                                    />
                                )}
                              </Field>
                            </div>
                          </div>
                        </div>

                        <div>
                          <label className ='inter-20-medium'>
                      <span className='required-alt'>
                        Indicates Required Field
                      </span>
                          </label>
                        </div>

                        <div className='cap-button'>
                          <button className='green-button-large' type='submit'>
                            {' '}
                            Create{' '}
                          </button>
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </Form>
      )}
    </div>

      // For Dom
      // <div className='inter-20-medium-white ass-tile-title'> {' '}
      //   <span> {'Assignment'} </span>
      // </div>
      // <div className='ass-tile-content' > // be sure to import the assignmentTile.css file to use this and others
      //  <div>
      //      Put code here for assignment
      //  </div>
      // <div className='inter-20-medium-white ass-tile-alt-title'> {' '}
      //   <span> {'Peer Review'} </span>
      //  <div className='ass-tile-alt-content' >
      //    <div>
      //      Put code for peer review here
      //    </div>
      //  </div>
      // </div>
  );
};

export default CreateAssignmentPage;
