import './_ProfessorNavigationComponents.css'
import React, {useEffect, useState} from 'react';

let yearText = "Year \u2304"

const handleClick = () => {
    let popup = document.getElementById("myPopup");
    popup.classList.toggle("show");
}

const YearDropdownComponent = () => {
    const [year, setYear] = useState("Year \u2304")

    return (
        <div id="year-popup" className="inter-16-medium-black" onClick={() => handleClick()}>{year}
            <div className="popup-text" id="myPopup">
                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() + 1).toString() + " \u2304")}>
                    {/*<input type="checkbox"/>*/}
                    <span className="inter-14-medium-black">{new Date().getFullYear() + 1}</span>
                </div>
                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear()).toString() + " \u2304")}>
                    {/*<input type="checkbox"/>*/}
                    <span className="inter-14-medium-black">{new Date().getFullYear()}</span>
                </div>
                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 1).toString() + " \u2304")}>
                    {/*<input type="checkbox"/>*/}
                    <span className="inter-14-medium-black">{new Date().getFullYear() - 1}</span>
                </div>
                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 2).toString() + " \u2304")}>
                    {/*<input type="checkbox"/>*/}
                    <span className="inter-14-medium-black">{new Date().getFullYear() - 2}</span>
                </div>
                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 3).toString() + " \u2304")}>
                    {/*<input type="checkbox"/>*/}
                    <span className="inter-14-medium-black">{new Date().getFullYear() - 3}</span>
                </div>
                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 4).toString() + " \u2304")}>
                    {/*<input type="checkbox"/>*/}
                    <span className="inter-14-medium-black">{new Date().getFullYear() - 4}</span>
                </div>
            </div>
        </div>
    )
}

export default YearDropdownComponent;