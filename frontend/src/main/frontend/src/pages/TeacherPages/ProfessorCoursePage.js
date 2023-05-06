import { useEffect, useState } from 'react'
import './styles/ProfessorCourseStyle.css'
import ProfessorRosterComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorRosterComponent'
import { useParams } from 'react-router-dom'
import ProfessorEditCourseComponent
  from '../../components/ProfessorComponents/CoursesPage/ProfessorEditCourseComponent'
import ProfessorAssignmentComponent
  from '../../components/ProfessorComponents/CoursesPage/ProfessorAssignmentComponent'
import ProfessorProfanitySettingsComponent
  from '../../components/ProfessorComponents/CoursesPage/ProfessorProfanitySettingsComponent'
import { useDispatch } from 'react-redux'
import {getCourseDetailsAsync, getCoursesAsync} from '../../redux/features/courseSlice'
import ProfessorTeamComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorTeamComponent'
import Loader from '../../components/LoaderComponenets/Loader'
import NavigationContainerComponent from '../../components/NavigationComponents/NavigationContainerComponent'
import HeaderBar from '../../components/HeaderBar/HeaderBar'
import Breadcrumbs from '../../components/Breadcrumbs'

const CourseComponent = ({ active, component, onClick }) => {
  return (
    <p
      onClick={onClick}
      className={
        active
          ? 'inter-28-bold pcp-component-link-clicked'
          : 'inter-28-light pcp-component-link'
      }
    >
      {component}
    </p>
  )
}

function ProfessorCoursePage ({ chosen }) {
  const [isLoading, setIsLoading] = useState(false)
  let dispatch = useDispatch()
  let { courseId } = useParams()

  const components = ['Assignments', 'Roster', 'Teams', 'Manage']
  const [chosenComponent, setChosenComponent] = useState(chosen)

  useEffect(() => {
    setIsLoading(true)
    dispatch(getCoursesAsync());
    dispatch(getCourseDetailsAsync(courseId))
    setTimeout(() => setIsLoading(false), 200)
  }, [dispatch, courseId])

  useEffect(() => {
    setChosenComponent(chosen)
  }, [chosen])

  return (
    <div>
      {isLoading ? (
        <Loader/>
      ) : (
        <div className="page-container">
          <HeaderBar/>
          <div className="pdp-container">
            <NavigationContainerComponent/>
            <div className="pcp-components">
              <Breadcrumbs/>
              <div style={{paddingTop: '2%'}}>
                {chosenComponent === 'Assignments' && <ProfessorAssignmentComponent/>}
                {chosenComponent === 'Roster' && <ProfessorRosterComponent/>}
                {chosenComponent === 'Teams' && <ProfessorTeamComponent/>}
                {chosenComponent === 'Manage' && <ProfessorEditCourseComponent/>}
                {chosenComponent === 'Profanity' && <ProfessorProfanitySettingsComponent/>}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ProfessorCoursePage
