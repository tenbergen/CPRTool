import * as React from 'react';
import './AboutPageStyle.css';
import HeaderBar from '../../components/HeaderBar/HeaderBar';
import NavigationContainerComponent from '../../components/NavigationComponents/NavigationContainerComponent';
import { useState } from 'react';
import CurrentContributorsComponent from './AboutPageComponents/CurrentContributorsComponent';
import PreviousContributorsComponent from './AboutPageComponents/PreviousContributorsComponent';
import DescriptionComponent from './AboutPageComponents/DescriptionComponent';
import DependenciesComponent from './AboutPageComponents/DependenciesComponent';

const AboutPage = () => {
    const [pageDescription, setPageDescription] = useState('CPR Tool');
    const [aboutIsActive, setAboutIsActive] = useState(true);
    const [currentIsActive, setCurrentIsActive] = useState(false);
    const [previousIsActive, setPreviousIsActive] = useState(false);
    const [descriptionIsActive, setDescriptionIsActive] = useState(true);
    const [dependenciesIsActive, setDependenciesIsActive] = useState(false);

    const activateAbout = () => {
        if (!aboutIsActive) {
            setAboutIsActive(true);
            setCurrentIsActive(false);
            setPreviousIsActive(false);
            setDescriptionIsActive(true);
            setPageDescription('CPR Tool');

            const aboutTab = document.getElementById('aboutTab');
            const currentTab = document.getElementById('currentContributorsTab');
            const previousTab = document.getElementById('previousContributorsTab');

            aboutTab.classList.remove('about-page-inactive-tab');
            aboutTab.classList.add('about-page-active-tab');
            currentTab.classList.add('about-page-inactive-tab');
            currentTab.classList.remove('about-page-active-tab');
            previousTab.classList.add('about-page-inactive-tab');
            previousTab.classList.remove('about-page-active-tab');
        }
    };

    const activateCurrent = () => {
        if (!currentIsActive) {
            setAboutIsActive(false);
            setCurrentIsActive(true);
            setPreviousIsActive(false);
            setDescriptionIsActive(false);
            setDependenciesIsActive(false);
            setPageDescription('Current Contributors');

            const aboutTab = document.getElementById('aboutTab');
            const currentTab = document.getElementById('currentContributorsTab');
            const previousTab = document.getElementById('previousContributorsTab');

            aboutTab.classList.add('about-page-inactive-tab');
            aboutTab.classList.remove('about-page-active-tab');
            currentTab.classList.remove('about-page-inactive-tab');
            currentTab.classList.add('about-page-active-tab');
            previousTab.classList.add('about-page-inactive-tab');
            previousTab.classList.remove('about-page-active-tab');
        }
    };

    const activatePrevious = () => {
        if (!previousIsActive) {
            setAboutIsActive(false);
            setCurrentIsActive(false);
            setPreviousIsActive(true);
            setDescriptionIsActive(false);
            setDependenciesIsActive(false);
            setPageDescription('Previous Contributors');

            const aboutTab = document.getElementById('aboutTab');
            const currentTab = document.getElementById('currentContributorsTab');
            const previousTab = document.getElementById('previousContributorsTab');

            aboutTab.classList.add('about-page-inactive-tab');
            aboutTab.classList.remove('about-page-active-tab');
            currentTab.classList.add('about-page-inactive-tab');
            currentTab.classList.remove('about-page-active-tab');
            previousTab.classList.remove('about-page-inactive-tab');
            previousTab.classList.add('about-page-active-tab');
        }
    };

    const activateDescription = () => {
        if (!descriptionIsActive) {
            setPageDescription('CPR Tool');

            setDependenciesIsActive(false);
            setDescriptionIsActive(true);

            const dependenciesTab = document.getElementById('dependenciesTab');
            const descriptionTab = document.getElementById('descriptionTab');

            dependenciesTab.classList.remove('about-page-active-tab');
            dependenciesTab.classList.add('about-page-inactive-tab');
            descriptionTab.classList.add('about-page-active-tab');
            descriptionTab.classList.remove('about-page-inactive-tab');
        }
    };

    const activateDependencies = () => {
        if (!dependenciesIsActive) {
            setPageDescription('Dependencies');

            setDependenciesIsActive(true);
            setDescriptionIsActive(false);

            const dependenciesTab = document.getElementById('dependenciesTab');
            const descriptionTab = document.getElementById('descriptionTab');

            dependenciesTab.classList.add('about-page-active-tab');
            dependenciesTab.classList.remove('about-page-inactive-tab');
            descriptionTab.classList.remove('about-page-active-tab');
            descriptionTab.classList.add('about-page-inactive-tab');
        }
    };

    return (
        <div className='about-page-container'>
            <HeaderBar />
            <div className='about-page-nav-and-content-container'>
                <NavigationContainerComponent />
                <div className='about-page-content-container'>
                    <p className='inter-28-bold about-page-description'
                       style={{ userSelect: 'none' }}>{pageDescription}</p>
                    <div className='about-page-main-tabs-container'>
                        <div id='aboutTab' className='about-page-about-tab about-page-active-tab'
                             onClick={() => activateAbout()}>
                            About
                        </div>
                        <div id='currentContributorsTab'
                             className='about-page-current-contributors-tab about-page-inactive-tab'
                             onClick={() => activateCurrent()}>
                            Current Contributors
                        </div>
                        <div id='previousContributorsTab'
                             className='about-page-previous-contributors-tab about-page-inactive-tab'
                             onClick={() => activatePrevious()}>
                            Previous Contributors
                        </div>
                    </div>
                    {aboutIsActive ? (
                        <div className='about-page-sub-buttons-container'>
                            <div id='descriptionTab' className='about-page-description-tab about-page-active-tab'
                                 onClick={() => activateDescription()}>
                                Description
                            </div>
                            <div id='dependenciesTab' className='about-page-dependencies-tab about-page-inactive-tab'
                                 onClick={() => activateDependencies()}>
                                Dependencies
                            </div>
                        </div>
                    ) : null}
                    {descriptionIsActive ? (<DescriptionComponent />) : null}
                    {dependenciesIsActive ? (<DependenciesComponent />) : null}
                    {currentIsActive ? (<CurrentContributorsComponent />) : null}
                    {previousIsActive ? (<PreviousContributorsComponent />) : null}
                </div>
            </div>
        </div>
    );
};

export default AboutPage;