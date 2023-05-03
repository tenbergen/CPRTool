import React, { useState, useEffect } from "react";
import { useParams } from 'react-router-dom'
import styles from "./styles/AssignmentDropdown.module.css";

const AssignmentDropdown = (props) => {
    for (const assignmentObject of props.assignmentObjects) {
        if (assignmentObject.assignment_name.length > 25) {
            assignmentObject.name = assignmentObject.assignment_name.substring(0, 35) + "...";
        }
    }
    
    // const [selectedAssignment, setSelectedAssignment] = useState(props.assignmentObjects[0].name);
    const [selectedAssignment, setSelectedAssignment] =useState(props.assignmentObjects[0].assignment_name);

       // const [assignmentObjects, setAssignmentObjects] = useState(props.assignmentObjects);

    const onAssignmentClick = (assignmentName) => {
        const assignmentObject = props.assignmentObjects.find(assignment => assignment.assignment_name === assignmentName);
        props.setMatrixState(assignmentObject.assignment_id);
        setSelectedAssignment(assignmentObject.assignment_name);
    }

    const mapAssignmentsToDropdown = () => {
        return props.assignmentObjects.map(assignmentObject => {
            return (
                <option key={assignmentObject.assignment_id} className={styles.option} onChange={() => onAssignmentClick(assignmentObject.assignment_id)}>
                { assignmentObject.assignment_name }
                </option>
            )
        });
    };
    
    return (
        <div className={styles.container}>
        <p>Assignment</p>
            <select className={styles.dropdown} onChange={(e) => onAssignmentClick(e.target.value)}>
            { mapAssignmentsToDropdown() }
            </select>
        </div>
    )
};

export default AssignmentDropdown;