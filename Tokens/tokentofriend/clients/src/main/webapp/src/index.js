import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Wallet from './component/Wallet';
import Mint from './component/Mint';
import Login from './component/Login';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";
// Importing the Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css';

ReactDOM.render(
    <Router>
      <Switch>
        <Route exact path="/" component={Login} />
        <Route path="/mint" component={Mint} />
        <Route path="/wallet" component={Wallet} />
      </Switch>
    </Router>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
