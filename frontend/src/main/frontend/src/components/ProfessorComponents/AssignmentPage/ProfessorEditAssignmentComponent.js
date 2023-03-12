import { Field, Form } from 'react-final-form';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  getAssignmentDetailsAsync,
  getCourseAssignmentsAsync,
} from '../../../redux/features/assignmentSlice';
import { useParams } from 'react-router-dom';
import '../../styles/EditAssignmentStyle.css';
import axios from 'axios';

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const ProfessorEditAssignmentComponent = () => {
  const dispatch = useDispatch();
  const { courseId, assignmentId } = useParams();
  const { currentAssignment, currentAssignmentLoaded } = useSelector(
    (state) => state.assignments
  );

  const assignmentFileFormData = new FormData();
  let assignmentFileName = ""
  const rubricFileFormData = new FormData();
  let rubricFileName = ""
  const templateFileFormData = new FormData();
  let templateFileName = ""

  const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`;

  useEffect(() => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
  }, [courseId, assignmentId, dispatch]);

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

  const handleSubmit = async (formObj) => {
    const editUrl = `${getAssUrl}/${assignmentId}/edit`;

    if (JSON.stringify(() => initialValue()) === JSON.stringify(formObj)) {
      alert('Nothing to save!');
      return;
    }

    await axios.put(editUrl, { ...formObj, course_id: courseId }).catch((e) => {
      console.error(e.response);
    });

    await submitNewFiles();
    dispatch(getCourseAssignmentsAsync(courseId));
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
    alert('Successfully updated assignment!');
  };

  const submitNewFiles = async () => {
    const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`;
    const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`;
    const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`;

    if (assignmentFileFormData.get(assignmentFileName)) {
      console.log(assignmentFileFormData.get('file'))
      await axios.post(assignmentFileUrl, assignmentFileFormData).catch((e) => {
        console.error(e);
        alert('Error uploading assignment file.');
      });
    }

    if (rubricFileFormData.get(rubricFileName)) {
      await axios.post(rubricUrl, rubricFileFormData).catch((e) => {
        console.error(e);
        alert('Error uploading peer review rubric.');
      });
    }

    if (templateFileFormData.get(templateFileName)) {
      await axios.post(templateUrl, templateFileFormData).catch((e) => {
        console.error(e);
        alert('Error uploading peer review template.');
      });
    }
  };

  const deleteFile = async (fileName, isPeerReviewRubric, isPeerReviewTemplate) => {
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}`;
    let deleteUrl = url
    if(isPeerReviewRubric){
      deleteUrl = `${url}/peer-review/rubric/remove-file`
    }else if(isPeerReviewTemplate){
      deleteUrl = `${url}/peer-review/template/remove-file`
    }else{
      deleteUrl = `${url}/remove-file`
    }

    await axios.delete(deleteUrl).catch((e) => {
      console.error(e);
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

  const onFileClick = async (fileName, isPeerReviewTemplate, isPeerReviewRubric) => {
    if(isPeerReviewTemplate){
      if(fileName.endsWith(".pdf")){
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/pdf'}), fileName)
      }else if(fileName.endsWith(".docx")){
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
      }else{
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/zip'}), fileName)
      }
    }else if(isPeerReviewRubric){
      if(fileName.endsWith(".pdf")){
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/pdf'}), fileName)
      }else if(fileName.endsWith(".docx")){
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
      }else{
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/zip'}), fileName)
      }
    }else{
      if(fileName.endsWith(".pdf")){
        downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], {type: 'application/pdf'}), fileName)
      }else if(fileName.endsWith(".docx")){
        downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
      }else{
        downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], {type: 'application/zip'}), fileName)
      }
    }
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
            <div className='inter-16-medium-black eac-input-field'>
              <label> Name of assignment: </label>
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

            <div className='inter-16-medium-black eac-instructions'>
              <label> Instructions: </label>
              <Field name='instructions'>
                {({ input }) => (
                  <textarea name='instructions' {...input} required />
                )}
              </Field>
            </div>

            <div className='inter-16-medium-black eac-assignment-files'>
              Current files:
              <span
                className='eac-file-name'
                onClick={() =>
                  onFileClick(currentAssignment.assignment_instructions_name, false,false)
                }
              >
                {currentAssignmentLoaded
                  ? currentAssignment.assignment_instructions_name
                  : null}
              </span>
              <span
                onClick={() =>
                  deleteFile(currentAssignment.assignment_instructions, false, false)
                }
                className={
                  currentAssignmentLoaded &&
                  currentAssignment.assignment_instructions !== ''
                    ? 'eac-crossmark'
                    : 'eac-crossmark-gone'
                }
              >
                &#10060;
              </span>
            </div>

            <div className='inter-16-medium-black eac-assignment-files'>
              <label> New files: </label>
              <input
                type='file'
                name='assignment_files'
                accept='.pdf,.zip,.docx'
                onChange={(e) => fileChangeHandler(e, 'assignment')}
              />
            </div>

            <div className='inter-16-medium-black eac-assignment-info'>
              <label> Due Date: </label>
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

              <label> Points: </label>
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

            <div className='inter-16-medium-black eac-instructions'>
              <label> Peer Review Instructions: </label>
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

            <div className='inter-16-medium-black eac-assignment-files-multiple'>
              <div>
                <div
                  className='eac-assignment-files'
                  style={{ marginBottom: '8%' }}
                >
                  Current files:
                  <span
                    className='eac-file-name'
                    onClick={() =>
                      onFileClick(currentAssignment.rubric_name, false,true)
                    }
                  >
                    {currentAssignmentLoaded
                      ? currentAssignment.rubric_name
                      : null}
                  </span>
                  <span
                    onClick={() =>
                      deleteFile(currentAssignment.peer_review_rubric, true, false)
                    }
                    className={
                      currentAssignmentLoaded &&
                      currentAssignment.peer_review_rubric !== ''
                        ? 'eac-crossmark'
                        : 'eac-crossmark-gone'
                    }
                  >
                    &#10060;
                  </span>
                </div>

                <div
                  className='eac-assignment-files'
                  style={{ marginBottom: '0' }}
                >
                  <label> New rubric: </label>
                  <input
                    type='file'
                    name='peer_review_rubric'
                    accept='.pdf,.zip,.docx'
                    onChange={(e) => fileChangeHandler(e, 'rubric')}
                  />
                </div>
              </div>

              <div>
                <div
                  className='eac-assignment-files'
                  style={{ marginBottom: '8%' }}
                >
                  Current files:
                  <span
                    onClick={() =>
                      onFileClick(currentAssignment.peer_review_template_name, true,false)
                    }
                  >
                    {currentAssignmentLoaded
                      ? currentAssignment.peer_review_template_name
                      : null}
                  </span>
                  <span
                    onClick={() =>
                      deleteFile(currentAssignment.peer_review_template, false, true)
                    }
                    className={
                      currentAssignmentLoaded &&
                      currentAssignment.peer_review_template !== ''
                        ? 'eac-crossmark'
                        : 'eac-crossmark-gone'
                    }
                  >
                    &#10060;
                  </span>
                </div>

                <div
                  className='eac-assignment-files'
                  style={{ marginBottom: '0' }}
                >
                  <label> New template: </label>
                  <input
                    type='file'
                    name='peer_review_template'
                    accept='.pdf,.zip..docx'
                    onChange={(e) => fileChangeHandler(e, 'template')}
                  />
                </div>
              </div>
            </div>

            <div className='inter-16-medium-black eac-assignment-info'>
              <label> Due Date: </label>
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

              <label> Points: </label>
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
            <div className='cap-button'>
              <button className='green-button-medium' type='submit'> Save</button>
            </div>
          </form>
        )}
      </Form>
    </div>
  );
};

export default ProfessorEditAssignmentComponent;
