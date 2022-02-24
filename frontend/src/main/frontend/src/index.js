import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import RouterHandler from "./routing/RouterHandler";
import {Provider} from "react-redux";
import store from "./redux/store";

ReactDOM.render(
    <React.StrictMode>
        <Provider store={store}>
            <RouterHandler/>
        </Provider>
    </React.StrictMode>,
  document.getElementById('root')
);
