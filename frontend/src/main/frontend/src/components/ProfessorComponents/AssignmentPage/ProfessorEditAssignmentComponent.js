import { Field, Form } from 'react-final-form';
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice';
import { useParams } from 'react-router-dom';
import '../../styles/ProfEditStyle.css';
import axios from 'axios';

const ProfessorEditAssignmentComponent = () => {
  const dispatch = useDispatch();
  const { courseId, assignmentId } = useParams();
  const { currentAssignment, currentAssignmentLoaded } = useSelector(
    (state) => state.assignments
  );

  useEffect(() => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
  }, []);

  const handleSubmit = async (formObj) => {
    const editUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/edit`;

    if (JSON.stringify(() => initialValue()) === JSON.stringify(formObj)) {
      alert('Nothing to save!');
      return;
    }

    const data = {
      ...formObj,
      course_id: courseId,
    };

    await axios
      .put(editUrl, data)
      .then((res) => {
        console.log(res.data);
      })
      .catch((e) => {
        console.log(e);
      });
  };

  const deleteFile = async (fileName, isPeerReview) => {
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}`;

    const deleteUrl = isPeerReview
      ? `${url}/peer-review/remove-file/${fileName}`
      : `${url}/remove-file/${fileName}`;

    await axios
      .delete(deleteUrl)
      .then((res) => {
        console.log(res);
      })
      .catch((e) => {
        console.log(e);
      });

    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  const onFileClick = async (fileName, isPeerReview) => {
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}`;
    const downloadUrl = isPeerReview
      ? `${url}/peer-review/download/${fileName}`
      : `${url}/download/${fileName}`;

    await axios
      .get(downloadUrl, { responseType: 'blob' })
      .then((res) => downloadFile(res.data, fileName));
  };

  const initialValue = () => {
    if (currentAssignmentLoaded) {
      return {
        assignment_name: currentAssignment.assignment_name,
        instructions: currentAssignment.instructions,
        due_date: currentAssignment.due_date,
        points: currentAssignment.points,
        peer_review_instructions: currentAssignment.peer_review_instructions,
        peer_review_due_date: currentAssignment.peer_review_due_date,
        peer_review_points: currentAssignment.peer_review_points,
      };
    }
  };

  return (
    <div className='eac-form'>
      <Form
        onSubmit={async (formObj) => {
          await handleSubmit(formObj);
        }}
        initialValues={() => initialValue()}
      >
        {({ handleSubmit }) => (
          <form onSubmit={handleSubmit}>
            <div className='cap-input-field'>
              <label>
                {' '}
                <b> Name of assignment: </b>{' '}
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

            <div className='cap-instructions'>
              <label>
                {' '}
                <b> Instructions:</b>{' '}
              </label>
              <Field name='instructions'>
                {({ input }) => (
                  <input type='text' name='instructions' {...input} required />
                )}
              </Field>
            </div>

            <div className='eac-assignment-files'>
              <label>
                {' '}
                <b> Files: </b>{' '}
              </label>
              <input
                type='file'
                name='assignment_files'
                accept='.pdf,.zip'
                // onChange={(e) => assignmentFileHandler(e)}
              />
              <p>
                {' '}
                Current files:
                <span
                  onClick={() =>
                    onFileClick(
                      currentAssignment.assignment_instructions,
                      false
                    )
                  }
                >
                  {currentAssignmentLoaded
                    ? currentAssignment.assignment_instructions
                    : null}
                </span>
                <span
                  onClick={() =>
                    deleteFile(currentAssignment.assignment_instructions, false)
                  }
                  className='crossMark'
                >
                  &#10060;
                </span>
              </p>
            </div>

            <div className='cap-assignment-info'>
              <label>
                {' '}
                <b> Due Date: </b>{' '}
              </label>
              <Field name='due_date'>
                {({ input }) => (
                  <input type='date' name='due_date' {...input} required />
                )}
              </Field>

              <label>
                {' '}
                <b> Points: </b>{' '}
              </label>
              <Field name='points'>
                {({ input }) => (
                  <input type='number' name='points' {...input} required />
                )}
              </Field>
            </div>

            <div className='cap-instructions'>
              <label>
                {' '}
                <b> Peer Review Instructions:</b>{' '}
              </label>
              <Field name='peer_review_instructions'>
                {({ input }) => (
                  <input
                    type='text'
                    name='peer_review_instructions'
                    {...input}
                    required
                  />
                )}
              </Field>
            </div>

            <div className='eac-assignment-files'>
              <label>
                {' '}
                <b> Rubric: </b>{' '}
              </label>
              <input
                type='file'
                name='peer_review_rubric'
                accept='.pdf,.zip'
                required
                //onChange={(e) => peerReviewRubricHandler(e)}
              />
              <p>
                {' '}
                Current files:
                <span
                  onClick={() =>
                    onFileClick(currentAssignment.peer_review_rubric, true)
                  }
                >
                  {currentAssignmentLoaded
                    ? currentAssignment.peer_review_rubric
                    : null}
                </span>
                <span
                  onClick={() =>
                    deleteFile(currentAssignment.peer_review_rubric, true)
                  }
                  className='crossMark'
                >
                  &#10060;
                </span>
              </p>
            </div>

            <div className='eac-assignment-files'>
              <label>
                {' '}
                <b> Template: </b>{' '}
              </label>
              <input
                type='file'
                name='peer_review_template'
                accept='.pdf,.zip'
                required
                //onChange={(e) => peerReviewTemplateHandler(e)}
              />
              <p>
                Current files:
                <span
                  onClick={() =>
                    onFileClick(currentAssignment.peer_review_template, true)
                  }
                >
                  {currentAssignmentLoaded
                    ? currentAssignment.peer_review_template
                    : null}
                </span>
                <span
                  onClick={() =>
                    deleteFile(currentAssignment.peer_review_template, true)
                  }
                  className='crossMark'
                >
                  &#10060;
                </span>
              </p>
            </div>

            <div className='cap-assignment-info'>
              <label>
                {' '}
                <b> Due Date: </b>{' '}
              </label>
              <Field name='peer_review_due_date'>
                {({ input }) => (
                  <input
                    type='date'
                    name='peer_review_due_date'
                    {...input}
                    required
                  />
                )}
              </Field>

              <label>
                {' '}
                <b>Points: </b>{' '}
              </label>
              <Field name='peer_review_points'>
                {({ input }) => (
                  <input
                    type='number'
                    min='0'
                    name='peer_review_points'
                    {...input}
                    required
                  />
                )}
              </Field>
            </div>
            <div className='cap-button'>
              <button type='submit'> Save</button>
            </div>
          </form>
        )}
      </Form>
    </div>
  );
};

export default ProfessorEditAssignmentComponent;
