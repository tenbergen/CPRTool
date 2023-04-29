import * as React from 'react';
import './DescriptionStyle.css';
import { Link } from 'react-router-dom';

const DescriptionComponent = () => {
    return (
        <div className='description-component-container'>
            <div className='description-block-1-container'>
                <div className='description-cpr-icon-container'>
                    <div className='description-cpr-icon' />
                </div>
                <div className='description-block-1-paragraphs-container'>
                    <p className='inter-20-medium' style={{ textAlign: 'justify' }}>
                        The <strong>CPR Tool</strong> is the product of a software design project that functions to
                        facilitate the peer review process in an academic setting. A calibrated peer review refers to
                        the process in which an instructor creates an assignment, distributes the assignment to be
                        completed by students, reviews their solutions, disperses the solution to other students for
                        peer review, and then quality checks each review before assigning a final grade.
                    </p>
                    <p className='inter-20-medium' style={{ textAlign: 'justify' }}>
                        The software design team utilized an agile development methodology to create an anonymous peer
                        review platform, which reduced biases and increased the reliability of reviews. The CPR tool
                        includes features for:
                    </p>
                </div>
            </div>
            <div className='description-block-2-container'>
                <ul style={{ listStyleType: 'disc', textAlign: 'justify' }} className='inter-20-medium'>
                    <li>
                        <strong>Automated Peer Review Distribution -</strong> With the click of a button, instructors
                        can distribute
                        completed assignments to pre-created student groups for their review.
                    </li>
                    <li>
                        <strong>Validity Check -</strong> Allows professors to quickly identify unexpected grade
                        allocations and adjust grades.
                    </li>
                    <li>
                        <strong>Archiving Course Work -</strong> Allows instructors to download pertinent documents in a
                        streamlined fashion, organized by student, student team, assignment, class, or semester.
                    </li>
                    <li>
                        <strong>Contextual Grade Overview -</strong> View and analyze student grades in an interactive
                        centralized hub.
                    </li>
                    <li>
                        <strong>Personnel Data Integration -</strong> Add students to courses individually or as a group
                        via a CSV file roster upload.
                    </li>
                    <li>
                        <strong>Institutional Support for Role Assignment -</strong> Quickly switch student/instructor
                        user role types, manage courses, and initialize profanity check settings.
                    </li>
                </ul>
            </div>
            <div className='description-block-3-container'>
                <p className='inter-20-medium' style={{ textAlign: 'justify' }}>
                    The application was designed to be customizable, allowing instructors to adjust the rubrics and
                    criteria for evaluation. The end goal for the CPR Tool was to be user-friendly and accessible to all
                    students, while also providing valuable feedback and efficient automation for instructors. Overall,
                    the app is positioned for future development into a full-fledged learning management system (LMS).
                </p>
            </div>
            <hr width={1002}></hr>
            <div className='description-block-4-container'>
                <div className='description-block-4-paragraphs-container'>
                    <p className='inter-20-medium' style={{ textAlign: 'justify' }}>
                        The development of the CPR Tool was carried out by a SUNY Oswego software design team with
                        oversight
                        from IBM, who acted as stakeholders throughout the project. The Oswego team worked closely with
                        IBM
                        to incorporate their feedback and suggestions into the design of the application, ensuring that
                        it
                        met the needs of both students and instructors alike. In addition, the development team utilized
                        IBM
                        technology throughout the project to create a powerful and scalable platform.
                    </p>
                    <p className='inter-20-medium' style={{ textAlign: 'justify' }}>
                        The association between SUNY Oswego and IBM allowed for a more robust and comprehensive solution
                        to be developed. IBM's expertise in technical tools and development technology helped to enhance
                        the capabilities of the CPR Tool, making it a more powerful and effective tool for academic
                        settings.
                    </p>
                </div>
                <div className='description-block-4-end-block'>
                    <div className='description-block-4-icons-container'>
                        <Link to='https://ww1.oswego.edu/'>
                            <img src={require('../../../assets/icons/aboutPage/SUNY_Oswego_seal.svg.png')} alt=''
                                 width={180} height={180} />
                        </Link>
                        <p className='inter-18-bold'>
                            In association with...
                        </p>
                        <Link to='https://www.ibm.com/us-en/'>
                            <img src={require('../../../assets/icons/aboutPage/ibm 1.png')} alt=''
                                 width={150} height={60} />
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DescriptionComponent;