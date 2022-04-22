import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import "./global_styles/fonts.css";
import "./global_styles/buttons.css";
import RouterHandler from './routing/RouterHandler';
import {Provider} from 'react-redux';
import store from './redux/store';
import {BrowserRouter, Route, Routes} from 'react-router-dom';

ReactDOM.render(
    <React.StrictMode>
        <BrowserRouter>
            <Provider store={store}>
                <Routes>
                    <Route path='/*' element={<RouterHandler/>}/>
                </Routes>
            </Provider>
        </BrowserRouter>
    </React.StrictMode>,
    document.getElementById('root')
);
