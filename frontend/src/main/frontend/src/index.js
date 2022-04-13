import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import RouterHandler from "./routing/RouterHandler";
import {Provider} from "react-redux";
import store from "./redux/store";
import {BrowserRouter, Routes, Route} from "react-router-dom";
import './interceptors/axiosInterceptor'

ReactDOM.render(
    <React.StrictMode>
        <BrowserRouter>
            <Provider store={store}>
                <Routes>
                    <Route path="/*" element={<RouterHandler/>}>
                    </Route>
                </Routes>
            </Provider>
        </BrowserRouter>
    </React.StrictMode>,
  document.getElementById('root')
);
