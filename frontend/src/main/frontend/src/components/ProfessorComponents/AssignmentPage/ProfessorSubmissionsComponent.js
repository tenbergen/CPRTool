import {useEffect, useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import '../../styles/TeamSubmission.css';
import {Link, useParams} from 'react-router-dom';
import axios from "axios";
import {getAssignmentDetailsAsync} from '../../../redux/features/assignmentSlice';

const ToDoComponent = () => {
    const {courseId} = useParams();
    const {assignmentId} = useParams();
    const [teams, TeamAssignments] = useState(Array());
    const dispatch = useDispatch();
    const {currentAssignment, currentAssignmentLoaded} = useSelector(
        (state) => state.assignments
    );

    useEffect(() => {
        dispatch(getAssignmentDetailsAsync({courseId, assignmentId}));
        axios.get(`${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/allTeams`)
            .then((r) => {
                console.log("teams" + r)
                for (let i = 0; i < r.data.length; i++) {
                    TeamAssignments((arr) => [...arr, r.data[i]]);
                }
            });
    }, []);

    return (
        <h3>
            <div id='assList'>
                {teams.map((team) => (
                        <Link to={`/details/professor/${courseId}/${assignmentId}/grade/${team}`}>
                            <li id='assListItem'>
                                {team + ": " + currentAssignment.assignment_name + '\n\n' + 'Grade : ' + 'Pending'}
                            </li>
                        </Link>
                    ))
                }
            </div>
        </h3>
    );
};

export default ToDoComponent;
