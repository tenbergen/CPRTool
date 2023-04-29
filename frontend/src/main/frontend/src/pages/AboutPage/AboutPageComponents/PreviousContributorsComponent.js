import * as React from 'react';
import './PreviousContributorsStyle.css';

const PreviousContributorsComponent = () => {
    return (
        <div className='previous-contributors-component-container inter-20-medium'>
            <div className='previous-contributors-column'>
                <ul style={{ listStyleType: 'disc' }}>
                    <li>Louis Calbet</li>
                    <li>Mikayla Conner-Spagnola</li>
                    <li>Eric Cuevas</li>
                    <li>Matt Danielsson</li>
                    <li>Nicholas Davis</li>
                    <li>Emir Erturk</li>
                    <li>Ashley Ferguson</li>
                    <li>Brittany Fialkowski</li>
                    <li>Pierce Hurd</li>
                    <li>Ricard Kanin</li>
                    <li>Aung Khant Kyaw</li>
                    <li>Alicia Lee</li>
                    <li>Minh Luu</li>
                    <li>Marc Maestri</li>
                </ul>
            </div>
            <div className='previous-contributors-column'>
                <ul style={{ listStyleType: 'disc' }}>
                    <li>Gregory Maldonado</li>
                    <li>Liam McMahan</li>
                    <li>Matthew Michaelis</li>
                    <li>Noah Newton</li>
                    <li>Logan Nguyen</li>
                    <li>Charles Noto</li>
                    <li>Sal Oestreicher</li>
                    <li>Jane Okada</li>
                    <li>Taeyoung Park</li>
                    <li>Koby Perez</li>
                    <li>Jacob Rosado</li>
                    <li>Sean Schukraft</li>
                    <li>Joshua Smith</li>
                    <li>Ethan Sonneville</li>
                </ul>
            </div>
        </div>
    );
};

export default PreviousContributorsComponent;