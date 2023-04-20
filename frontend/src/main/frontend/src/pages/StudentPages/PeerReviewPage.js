import { useEffect, useState } from 'react';
import './styles/StudentCourseStyle.css';
import StudentTeamComponent from '../../components/StudentComponents/CoursePage/StudentTeamComponent';
import { useLocation, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import NavigationContainerComponent
    from "../../components/NavigationComponents/NavigationContainerComponent";
import PeerReviewComponent from "../../components/StudentComponents/CoursePage/PeerReviewComponent";
import Breadcrumbs from "../../components/Breadcrumbs";
import HeaderBar from "../../components/HeaderBar/HeaderBar";

function PeerReviewPage() {
    const dispatch = useDispatch();
    const location = useLocation();
    const { courseId } = useParams();
    const { lakerId } = useSelector((state) => state.auth);
    const { currentTeamId, teamLoaded } = useSelector((state) => state.teams);


    // dispatch(getCombinedAssignmentPeerReviews(courseId, currentTeamId, lakerId));
    //  let { courseAssignmentsAndPeerReviews } = useSelector((state) => state.assignments);


    // const initialState =
    //     location.state !== null ? location.state.initialComponent : 'To Do';
    // const [chosen, setChosen] = useState(initialState);

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
