import '../../styles/Gradebook.css';

const ProfessorGradebookComponent = () => {
    return (
        <div className='GradebookPage'>
            <div id='gradebook'>
                <table className='gradebookTable'>
                    <tr>
                        <th className='gradebookHeader'>Name</th>
                        <th className='gradebookHeader'>Team</th>
                        <th className='gradebookHeader'>Grade</th>
                    </tr>
                    {/*{studentArray.map(d =>*/}
                    {/*    <tr>*/}
                    {/*        <th>{d}</th>*/}
                    {/*        <th>{d.Email}</th>*/}
                    {/*        <th>{d.Team}</th>*/}
                    {/*        <span onClick={() => deleteStudent(d)} className="crossMark">&#10060;</span>*/}
                    {/*    </tr>*/}
                    {/*)}*/}
                </table>
            </div>
            {/*{show ? addsStudent(): <button className="button_plus" onClick={setTrue}>*/}
            {/*    <img className="button_plus" src={require("./styles/plus-purple.png")}/></button>}*/}
        </div>
    );
};

export default ProfessorGradebookComponent;
