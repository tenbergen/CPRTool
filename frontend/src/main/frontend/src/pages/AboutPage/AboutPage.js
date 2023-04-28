import * as React from 'react'
import './AboutPageStyle.css'
import HeaderBar from '../../components/HeaderBar/HeaderBar'
import NavigationContainerComponent from '../../components/NavigationComponents/NavigationContainerComponent'


const AboutPage = () => {
    return (
        <div className='about-page-container'>
            <HeaderBar/>
            <div className="about-page-nav-and-content-container">
                <NavigationContainerComponent/>
                <div>
                    <h1>About Page</h1>
                </div>
            </div>
        </div>
    )
}

export default AboutPage