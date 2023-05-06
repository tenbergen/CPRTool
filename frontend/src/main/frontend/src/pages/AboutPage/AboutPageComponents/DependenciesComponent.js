import * as React from 'react';
import './DependenciesStyle.css';
import { Link } from 'react-router-dom';

const DependenciesComponent = () => {
    return (
        <div className='dependencies-component-container'>
            <p className='inter-20-medium dependencies-head-paragraph' style={{ textAlign: 'justify' }}>
                The following lists represent dependencies utilized by the CPR tool which are necessary for proper
                functionality and performance of the software. These dependencies are the external software components,
                packages, and libraries that ensures the CPR tool functions correctly.
            </p>
            <div className='dependencies-icon-rows-container'>
                <div className='dependencies-icon-row'>
                    <Link to='https://openliberty.io/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/open-liberty.png')} alt=''
                                 width={148} height={130}
                            />
                        </div>
                    </Link>
                    <Link to='https://maven.apache.org/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/maven.png')} alt=''
                                 width={229} height={113}
                            />
                        </div>
                    </Link>
                    <Link to='https://www.docker.com/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/docker.png')} alt=''
                                 width={134} height={115}
                            />
                        </div>
                    </Link>
                </div>
                <div className='dependencies-icon-row'>
                    <Link to='https://react.dev/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/react.png')} alt=''
                                 width={194} height={128}
                            />
                        </div>
                    </Link>
                    <Link to='https://www.selenium.dev/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/selenium.png')} alt=''
                                 width={207} height={51}
                            />
                        </div>
                    </Link>
                    <Link to='https://microprofile.io/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/microprofile.png')} alt=''
                                 width={216} height={91}
                            />
                        </div>
                    </Link>
                </div>
                <div className='dependencies-icon-row'>
                    <Link to='https://www.mongodb.com/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/mongo-db.png')} alt=''
                                 width={224} height={60}
                            />
                        </div>
                    </Link>
                    <Link to='https://www.java.com/en/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/java.png')} alt=''
                                 width={246} height={151}
                            />
                        </div>
                    </Link>
                    <Link to='https://www.w3.org/'>
                        <div className='dependencies-icon-box'>
                            <img src={require('../../../assets/icons/aboutPage/html-css.png')} alt=''
                                 width={193} height={124}
                            />
                        </div>
                    </Link>
                </div>
            </div>
            <div className='dependencies-lists-container'>
                <div className='dependencies-individual-list-container'>
                    <div className='inter-20-bold'>
                        Backend Dependencies:
                    </div>
                    <div className='dependencies-list-columns-container inter-20-medium'>
                        <ul style={{ listStyleType: 'disc', marginRight: 60 }}>
                            <li>Apache Commons</li>
                            <li>Apache Maven</li>
                            <li>Aspose PDF</li>
                            <li>Docker</li>
                            <li>Eclipse Yasson</li>
                            <li>Google GSON</li>
                            <li>IBM Websphere Security</li>
                            <li>Jakarta</li>
                            <li>Java</li>
                            <li>Javax Persistence API</li>
                        </ul>
                        <ul style={{ listStyleType: 'disc' }}>
                            <li>Javax Security</li>
                            <li>JUnit Jupiter</li>
                            <li>Lombok</li>
                            <li>MongoDB</li>
                            <li>Open Liberty</li>
                            <li>OpenAPI Microprofiles</li>
                            <li>Postman API Platform</li>
                            <li>Snyk</li>
                        </ul>
                    </div>
                </div>
                <div className='dependencies-vertical-divider' />
                <div className='dependencies-individual-list-container'>
                    <div className='inter-20-bold'>
                        Frontend Dependencies:
                    </div>
                    <div className='dependencies-list-columns-container inter-20-medium'>
                        <ul style={{ listStyleType: 'disc', marginRight: 60 }}>
                            <li>Adobe Illustrator</li>
                            <li>Axios</li>
                            <li>Chart.js</li>
                            <li>CSS</li>
                            <li>Figma Community Library</li>
                            <li>Google OAuth 2</li>
                            <li>HTML</li>
                            <li>Iconify</li>
                            <li>JavaScript</li>
                            <li>JWT</li>
                            <li>Node.js</li>
                        </ul>
                        <ul style={{ listStyleType: 'disc' }}>
                            <li>NPM.js</li>
                            <li>React JS</li>
                            <li>Selenium IDE</li>
                            <li>Selenium WebDriver</li>
                            <li>YarnPkg</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DependenciesComponent;