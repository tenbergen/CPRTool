
import { useEffect, useState } from 'react';
import './styles/StudentCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import StudentTeamComponent from '../../components/StudentComponents/CoursePage/StudentTeamComponent';
import StudentToDoComponent from '../../components/StudentComponents/CoursePage/StudentToDoComponent';
import StudentSubmittedComponent from '../../components/StudentComponents/CoursePage/StudentSubmittedComponent';
import { useLocation, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import { getCurrentCourseTeamAsync } from '../../redux/features/teamSlice';
import MyTeamComponent from '../../components/StudentComponents/CoursePage/MyTeamComponent';
import uuid from 'react-uuid';
import AssBarComponent from "../../components/AssBarComponent";
import StudentHeaderBar from "../../components/HeaderBar/HeaderBar";
import NavigationContainerComponent
    from "../../components/NavigationComponents/NavigationContainerComponent";
import PeerReviewComponent from "../../components/StudentComponents/CoursePage/PeerReviewComponent";
import Breadcrumbs from "../../components/Breadcrumbs";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import CourseAccordionComponent from "../../components/NavigationComponents/CourseAccordionComponent";

/*
export const getCombinedAssignmentPeerReviews = createAsyncThunk(
  'assignments/getCombinedAssignmentPeerReviews',
  async (value, thunkAPI) => {
    const { courseId, currentTeamId, lakerId } = value;
    thunkAPI.dispatch(refreshTokenAsync());
    const courseAssignments = await getToDos(courseId, lakerId);
    const peerReviews = await getPeerReviews(courseId, currentTeamId);
    const combined = [...courseAssignments, ...peerReviews];
    sortByDueDate(combined);
    return { combined };
  }
);*/



const CourseComponent = ({ active, component, onClick }) => {
    return (
        <p
            onClick={onClick}
            className={
                active
                    ? 'kumba-30 scp-component-link-clicked'
                    : 'kumba-30 scp-component-link'
            }
        >
            {component}
        </p>
    );
};

function PeerReviewPage() {
    const dispatch = useDispatch();
    const location = useLocation();
    const { courseId } = useParams();
    const { lakerId } = useSelector((state) => state.auth);
    const { currentTeamId, teamLoaded } = useSelector((state) => state.teams);


    // dispatch(getCombinedAssignmentPeerReviews(courseId, currentTeamId, lakerId));
    //  let { courseAssignmentsAndPeerReviews } = useSelector((state) => state.assignments);


    const initialState =
        location.state !== null ? location.state.initialComponent : 'To Do';
    const [chosen, setChosen] = useState(initialState);

    const components = ['Peer Reviews'];

    //just commented out
    // useEffect(() => {
    //     dispatch(getCourseDetailsAsync(courseId));
    //     dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
    // }, [courseId, lakerId, dispatch]);

    return (
        <div className="page-container">
            <HeaderBar/>
            <div className='scp-parent'>

                <div className='scp-container'>
                    <NavigationContainerComponent/>

                    <div className='scp-component'>
                        <Breadcrumbs />

                        {teamLoaded && currentTeamId === null && <StudentTeamComponent />}

                        {teamLoaded && currentTeamId !== null && (
                            <div>
                                <div className='scp-component-links'>
                                    {components}
                                </div>
                                <div>

                                    <PeerReviewComponent />

                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PeerReviewPage;
