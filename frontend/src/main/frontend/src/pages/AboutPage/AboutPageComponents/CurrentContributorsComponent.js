import * as React from 'react';
import './CurrentContributorsStyle.css';
import { Link } from 'react-router-dom';

const CurrentContributorsComponent = () => {
    return (
        <div className='current-contributors-component-container'>
            <div className='current-contributors-column' style={{ marginTop: '-10px' }}>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-engine-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '10px 0 8px 0' }}>
                        Engine:
                    </p>
                </div>
                <div>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <Link to='https://github.com/daltamur' className='inter-20-bold-blue'>
                            <li>Dominic Altamura</li>
                        </Link>
                        <Link to='https://github.com/jal1999' className='inter-20-bold-blue'>
                            <li>James LaFarr</li>
                        </Link>
                        <Link to='https://github.com/rnelson23 ' className='inter-20-bold-blue'>
                            <li>Ryan Nelson</li>
                        </Link>
                        <Link to='https://github.com/Schmitt-Very-Cool' className='inter-20-bold-blue'>
                            <li>Patrick Schmitt</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/fatemehalsadat-shojaei' className='inter-20-bold-blue'>
                            <li>Fatemehalsadat Shojaei</li>
                        </Link>
                        <Link to='https://github.com/asigdel29' className='inter-20-bold-blue'>
                            <li>Anubhav Sigdel</li>
                        </Link>
                        <Link to='https://github.com/eulia16/' className='inter-20-bold-blue'>
                            <li>Ethan Uliano</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/dennislelic' className='inter-20-bold-blue'>
                            <li>Dennis Lelic</li>
                        </Link>
                    </ul>
                </div>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-qa-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '20px 0 8px 0' }}>
                        Quality Assurance and Testing:
                    </p>
                </div>
                <div>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <Link to='https://uxfol.io/83234f5b' className='inter-20-bold-blue'>
                            <li>Andjela Djapa</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/cameron-francois-47310021a'
                              className='inter-20-bold-blue'>
                            <li>Cameron Francios</li>
                        </Link>
                        <li>Manasa Muthyala</li>
                        <Link to='https://github.com/Kayyali78' className='inter-20-bold-blue'>
                            <li>Scarlett Weeks</li>
                        </Link>
                        <Link to='https://github.com/cwestman63' className='inter-20-bold-blue'>
                            <li>Corey Westman</li>
                        </Link>
                        <Link to='https://github.com/AlexYZhang' className='inter-20-bold-blue'>
                            <li>Alexandra Zhang</li>
                        </Link>
                    </ul>
                </div>
            </div>
            <div className='current-contributors-column'>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-gui-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '20px 0 8px 0' }}>
                        GUI:
                    </p>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <Link to='https://github.com/markabbe' className='inter-20-bold-blue'>
                            <li>Mark Abbe</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/snigbehara/' className='inter-20-bold-blue'>
                            <li>Snigdha Behara</li>
                        </Link>
                        <li>Joel Betancourt</li>
                        <Link to='https://github.com/MatthewDBrown50' className='inter-20-bold-blue'>
                            <li>Matthew D Brown</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/benjamin-melby-9b61a3267/' className='inter-20-bold-blue'>
                            <li>Benjamin Melby</li>
                        </Link>
                        <Link to='https://github.com/rshtikya' className='inter-20-bold-blue'>
                            <li>Raffi Shtikyan</li>
                        </Link>
                    </ul>
                </div>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-usability-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '20px 0 8px 0' }}>
                        Usability:
                    </p>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <Link to='https://www.linkedin.com/in/giovanni-anastasio-399579190/'
                              className='inter-20-bold-blue'>
                            <li>Giovanni Anastasio</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/snigbehara/' className='inter-20-bold-blue'>
                            <li>Snigdha Behara</li>
                        </Link>
                        <li>Joel Betancourt</li>
                        <Link to='https://uxfol.io/83234f5b' className='inter-20-bold-blue'>
                            <li>Andjela Djapa</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/jake-hoffman-03b447154/' className='inter-20-bold-blue'>
                            <li>Jake Hoffman</li>
                        </Link>
                        <Link to='http://jameskuntz.com/' className='inter-20-bold-blue'>
                            <li>James Kuntz</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/dennislelic' className='inter-20-bold-blue'>
                            <li>Dennis Lelic</li>
                        </Link>
                        <li>Manasa Muthyala</li>
                        <li>Bahareh Nejati</li>
                        <Link to='https://www.linkedin.com/in/fatemehalsadat-shojaei' className='inter-20-bold-blue'>
                            <li>Fatemehalsadat Shojaei</li>
                        </Link>
                        <Link to='https://www.raviteja.design/' className='inter-20-bold-blue'>
                            <li>Ravi Teja</li>
                        </Link>
                        <Link to='https://claratribunella.com' className='inter-20-bold-blue'>
                            <li>Clara Tribunella</li>
                        </Link>
                    </ul>
                </div>


            </div>
            <div className='current-contributors-column'>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-db-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '20px 0 8px 0' }}>
                        Database and Networking:
                    </p>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <Link to='https://www.linkedin.com/in/giovanni-anastasio-399579190/'
                              className='inter-20-bold-blue'>
                            <li>Giovanni Anastasio</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/dannielle-k-32591b18a/' className='inter-20-bold-blue'>
                            <li>Dannielle Kline</li>
                        </Link>
                        <li>Bahareh Nejati</li>
                        <Link to='https://www.linkedin.com/in/nwolf2/' className='inter-20-bold-blue'>
                            <li>Nathaniel Wolf</li>
                        </Link>
                    </ul>
                </div>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-req-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '20px 0 8px 0' }}>
                        Requirements:
                    </p>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <Link to='http://cs.oswego.edu/~fcamach2/Portfolio%20Website/' className='inter-20-bold-blue'>
                            <li>Franklin Camacho III</li>
                        </Link>
                        <Link to='https://www.linkedin.com/in/jake-hoffman-03b447154/' className='inter-20-bold-blue'>
                            <li>Jake Hoffman</li>
                        </Link>
                        <Link to='https://claratribunella.com ' className='inter-20-bold-blue'>
                            <li>Clara Tribunella</li>
                        </Link>
                        <li>Sarah Wong</li>
                    </ul>
                </div>
                <div className='current-contributors-icon-container'>
                    <div className='current-contributors-stakeholder-icon' />
                </div>
                <div>
                    <p className='inter-20-bold' style={{ margin: '20px 0 8px 0' }}>
                        Stakeholders:
                    </p>
                    <ul className='inter-20-medium' style={{ listStyleType: 'disc' }}>
                        <li>Paul Austin, IBM</li>
                        <li>Rumana Haque, IBM</li>
                        <li>Bastian Tenbergen</li>
                        <li>Adam Wisniewski, IBM</li>
                        <li>Adam Yoho, IBM</li>
                    </ul>
                </div>

            </div>
        </div>
    );
};

export default CurrentContributorsComponent;