import React, { useState, useEffect, useRef } from 'react';
import './Popup.css';

const Popup = ({ content, trigger }) => {
    const [isOpen, setIsOpen] = useState(false);
    const popupRef = useRef(null);

    const handleOutsideClick = e => {
        if (isOpen && popupRef.current && !popupRef.current.contains(e.target)) {
            setIsOpen(false);
        }
    };

    useEffect(() => {
        window.addEventListener('click', handleOutsideClick);
        return () => window.removeEventListener('click', handleOutsideClick);
    }, [isOpen]);

    return (
        <div className="popup-container">
            <div className="popup-trigger" onClick={() => setIsOpen(!isOpen)}>
                {trigger}
            </div>
            {isOpen && (
                <div className="popup" ref={popupRef}>
                    <div className="popup-content">{content}</div>
                </div>
            )}
        </div>
    );
};

export default Popup;
