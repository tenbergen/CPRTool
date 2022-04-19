import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
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
