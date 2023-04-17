import './_ProfessorNavigationComponents.css'
import React, {forwardRef, useEffect, useImperativeHandle} from 'react';

const BulkDownloadTileComponent = ({trigger, deactivateTiles}) => {

    useEffect(() => {
        if(trigger){
            document.getElementById("bulk-download-tile").classList.remove("inter-24-bold-blue")
            document.getElementById("bulk-download-tile").classList.add("inter-24-medium")
            document.getElementById("bulk-download-tile").children[0].classList.remove("bulk-download-icon-active")
            document.getElementById("bulk-download-tile").children[0].classList.add("bulk-download-icon-default")
        }
    })

    const onClick = () => {
        deactivateTiles()
        setTimeout(() => {
            document.getElementById("bulk-download-tile").classList.remove("inter-24-medium")
            document.getElementById("bulk-download-tile").classList.add("inter-24-bold-blue")
            document.getElementById("bulk-download-tile").children[0].classList.remove("bulk-download-icon-default")
            document.getElementById("bulk-download-tile").children[0].classList.add("bulk-download-icon-active")
        }, 30)
    }

    return (
        <div id="bulk-download-tile" className="inter-24-medium" onClick={() => onClick()}>
            <div className="bulk-download-icon-default"></div>
            <p>Bulk Download</p>
        </div>
    );
};

export default BulkDownloadTileComponent;