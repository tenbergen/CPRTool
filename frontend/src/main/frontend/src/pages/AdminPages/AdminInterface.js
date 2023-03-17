import { useEffect, useState } from 'react';
import '../TeacherPages/styles/ProfessorCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import GradeAssBarComponent from '../../components/GradeAssBarComponent';
import ProfessorAllSubmissionsComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorAllSubmissionsComponent';
import ProfessorEditAssignmentComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorEditAssignmentComponent';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';
import Loader from '../../components/LoaderComponenets/Loader';
import uuid from 'react-uuid';
import UserList from './UserList';

function AdminInterface() {
  const [userList, setUserList] = useState([
    { id: uuid(), name: 'John Doe', role: 'Admin' },
    { id: uuid(), name: 'Jane Smith', role: 'Teacher' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    { id: uuid(), name: 'Bob Johnson', role: 'Student' },
    // Add more users here
  ]);

  return (
    <div className='admin-container'>
      <div className='sidebar'>
        <h1>Admin</h1>
      </div>
      <div className='user-roles'>
        <h2>User Roles</h2>
        <button className='add-user-button'>Add User +</button>
        <UserList userList={userList} />
      </div>
    </div>
  );
}

export default AdminInterface;
