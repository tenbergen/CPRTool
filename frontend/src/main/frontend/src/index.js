import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import './global_styles/fonts.css';
import './global_styles/buttons.css';
import './global_styles/forms.css';
import RouterHandler from './routing/RouterHandler';
import { Provider } from 'react-redux';
import store from './redux/store';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import {GoogleOAuthProvider} from "@react-oauth/google";

const REACT_APP_CLIENT_ID = `${process.env.REACT_APP_CLIENT_ID}`;

ReactDOM.render(
    <React.StrictMode>
        <GoogleOAuthProvider clientId={REACT_APP_CLIENT_ID}>
            <BrowserRouter>
                <Provider store={store}>
                    <Routes>
                        <Route path='/*' element={<RouterHandler />} />
                    </Routes>
                </Provider>
            </BrowserRouter>
        </GoogleOAuthProvider>
    </React.StrictMode>,
    document.getElementById('root')
);
