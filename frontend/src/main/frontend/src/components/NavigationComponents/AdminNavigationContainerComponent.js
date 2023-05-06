import uuid from 'react-uuid'
import './_ProfessorNavigationComponents.css'
import useCollapse from 'react-collapsed'
import HomeTileComponent from './HomeTileComponent'
import CoursesTileComponent from './CoursesTileComponent'
import { useRef, useState } from 'react'
import LogoutButton from '../GlobalComponents/LogoutButton'
import BulkDownloadTileComponent from './BulkDownloadTileComponent'

const AdminNavigationContainerComponent = () => {
  return (
    <div className="parent-container">
      <div className="navigation-container">
        <h1 style={{ textAlign: 'center' }}>Admin</h1>
      </div>
      <LogoutButton/>
    </div>
  )
}

export default AdminNavigationContainerComponent