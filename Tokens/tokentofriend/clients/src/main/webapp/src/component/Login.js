import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import axios from 'axios';
import { withRouter } from 'react-router-dom';

import '../css/App.scss';
import retrieveToken from './Wallet';
import Container from 'react-bootstrap/Container'
import { Row } from 'react-bootstrap';
import { Col } from 'react-bootstrap';
import { BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      username:'',
      password:''};

      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
      this.setState({ [event.target.name]: event.target.value });
    }

    handleSubmit(event) {
      console.log('Login attempted: ' + this.state.username);
      let queryString = "u=" + this.state.username;
      this.props.history.push(`/wallet?${queryString}`);

      event.preventDefault();
    }

    render(){
      return (
        <div className="App bg-primary">
          <Container>
            <img src="opentransact_logo.png" alt="Open Transact" width="200" className="App-logo"/>
            <Row>
              <Col md="auto"><h1>Open Transact Wallet</h1></Col>
            </Row>
          </Container>
          <Container>
            <br />
            <h4>Login</h4>
            <form onSubmit={this.handleSubmit}>
              <div>
                <p>Username</p>
                <input
                  type="text" className="form-control" name="username" onChange={this.handleChange} />
              </div>
              <div>
                <p>Password</p>
                <input
                  type="password" className="form-control" name="password" onChange={this.handleChange} />
              </div>
              <br />
              <div className="row">
                <div className="col-10" />
                <div className="col">
                  <button type="submit" className="btn btn-secondary">Login</button>
                </div>
              </div>
            </form>
          </Container>
        </div>
      );
    }
  }

export default withRouter(Login);
