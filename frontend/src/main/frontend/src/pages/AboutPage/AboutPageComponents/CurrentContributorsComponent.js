import * as React from 'react';
import './CurrentContributorsStyle.css';

const CurrentContributorsComponent = () => {
    return (
        <div className='current-contributors-component-container'>
            <div className='current-contributors-column-1'>
                <div className='current-contributors-engine-icon' />
            <div className='current-contributors-column-1-names'>
                <p className='inter-20-bold'>
                    Engine
                </p>
            </div>
            <div className='current-contributors-column-1-names'>
                <p className='inter-20-medium'>
                    Dominic Altamura
                    James LaFarr
                    Ryan Nelson
                    Patrick Schmitt
                    Fatemehalsadat Shojaei
                    Anubhav Sigdel
                    Ethan Uliano
                    Dennis Lelic
                </p>
            </div>

            <div className='current-contributors-gui-icon' />
                <div className='current-contributors-column-1-names'>
                    <p className='inter-20-bold' style={{ textAlign: 'left'}}>
                        GUI
                    </p>
                    <p className='inter-20-medium' style={{ textAlign: 'left'}}>
                        Mark Abbe
                        Snigdha Behara
                        Joel Betancourt
                        Matt Brown
                        Benjamin Melby
                        Raffi Shtikyan
                    </p>
                </div>

            <div className='current-contributors-db-icon' />
                <div className='current-contributors-column-1-names'>
                     <p className='inter-20-bold' style={{ textAlign: 'left'}}>
                        Database and Networking
                     </p>
                     <p className='inter-20-medium' style={{ textAlign: 'left'}}>
                        Giovanni Anastasio
                        Dannielle Kline
                        Bahareh Nejati
                        Nathaniel Wolf
                     </p>
                </div>
        </div>
        <div className='current-contributors-column-2'>
            <div className='current-contributors-qa-icon' />
                <div className='current-contributors-column-1-names'>
                    <p className='inter-20-bold'>
                        Quality Assurance and Testing
                    </p>
                </div>
                <div className='current-contributors-column-1-names'>
                    <p className='inter-20-medium'>
                        Andjela Djapa
                        Cameron Francios
                        Manasa Muthyala
                        Scarlett Weeks
                        Corey Westman
                        Alexandra Zhang
                    </p>
                </div>

                <div className='current-contributors-req-icon' />
                    <div className='current-contributors-column-1-names'>
                        <p className='inter-20-bold' style={{ textAlign: 'left'}}>
                            Requirements
                        </p>
                        <p className='inter-20-medium' style={{ textAlign: 'left'}}>
                            Franklin Camacho
                            Jake Hoffman
                            Clara Tribunella
                            Sarah Wong
                        </p>
                    </div>

                    <div className='current-contributors-usability-icon' />
                        <div className='current-contributors-column-1-names'>
                            <p className='inter-20-bold' style={{ textAlign: 'left'}}>
                                Usability
                            </p>
                            <p className='inter-20-medium' style={{ textAlign: 'left'}}>
                                Giovanni Anastasio
                                Snigdha Behara
                                Joel Betancourt
                                Andjeal Djapa
                                Jake Hoffman
                                James Kunts
                                Dennis Lelic
                                Manasa Muthyala
                                Bahareh Nejati
                                Fatemehalsadat Shojaei
                                Ravi Teja
                                Clara Tribunella
                            </p>
                        </div>
            </div>
            <div className='current-contributors-column-3'>

            </div>
        </div>
    );
};

export default CurrentContributorsComponent;