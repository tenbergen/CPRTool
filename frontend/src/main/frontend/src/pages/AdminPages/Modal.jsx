import React from 'react'
import plusIcon from './plus.png';

const Modal = ({ open, onClose, courseArr }) => {
    if (!open) return null
    return (
        <div className='overlay'>
            <div className='modalContainer'>
                <div className='modal-header'>
                    <h2 className='courses-title'>Courses</h2>
                </div>
                <div className='modalRight'>
                    <b onClick={onClose} className="closeBtn">X</b>
                    <div className="content">
                        {
                            courseArr.map(course => (
                                <div className='courses-excluded-list'>
                                    <div className='course-item' key={course}>{course}</div>
                                    <div className='delete-course-container'>
                                        <button className='delete-course-button'><b>X</b></button>
                                    </div>
                                </div>
                            ))
                        }
                    </div>
                    <div className="add-course">
                            <div className='profanity-icon-div'>
                                <input
                                    type='text' 
                                    placeholder='Add Course...'/>
                                <button className='plus-profanity-button'><img className='plus-icon' src={plusIcon} /></button>
                            </div>
                        </div>
                </div>
            </div>
        </div>
    )
}

export default Modal