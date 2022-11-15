import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import axios from 'axios';

import '../css/App.scss';
import retrieveToken from './retrieveToken';
import Container from 'react-bootstrap/Container'
import { Row } from 'react-bootstrap';
import { Col } from 'react-bootstrap';
import { BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      amount:'',
      callback:''};

      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
      this.setState({ [event.target.name]: event.target.value });
    }

    handleSubmit(event) {
      console.log('A mint was submitted: ' + this.state.amount);
      const tokenInfo = JSON.stringify({
        "amount": parseFloat(this.state.amount)
      });
      console.log(tokenInfo);

      axios.post('http://localhost:10050/createToken', tokenInfo, {"headers": {"content-type": "application/json",}})
      .then(
        response => {
          console.log(response.data)
          this.setState({
            callback: response.data
          });
          //  alert('You have just created a token for your friend! He will be able to collect the secret message using the Token Id and Storage Node information.'+response.data);
        }
      );

      event.preventDefault();
    }

    render(){
      return (
        <div className="App bg-primary">
          <Container>
            <img src="opentransact_logo.png" alt="Open Transact" width="200" className="App-logo"/>
            <Row>
              <Col md="auto"><h1>Open Transact Wallet</h1></Col>
              <Col md={{offset:2}}>
                <Link to="/retrieveToken" ><button className="btn btn-primary" >USD Wallet</button></Link>
                <button className="btn btn-secondary disabled" >Mint USD</button> 
              </Col>
            </Row>
          </Container>
          <Container>
            <br />
            <h4>Mint USD </h4>
            <form onSubmit={this.handleSubmit}>
              <div>
                <p>USD amount</p>
                <input
                  type="text" className="form-control" name="amount" onChange={this.handleChange} />
              </div>
              <br />
              <div className="row">
                <div className="col-10" />
                <div className="col">
                  <button type="submit" className="btn btn-primary">Execute mint</button>
                </div>
              </div>
            </form>
          </Container>
          <Container>
            {this.state.callback != '' &&
              <div>
                <h4>Here is your confirmation number: </h4>
                <p>{this.state.callback}</p>
              </div>
            }
          </Container>





        </div>
      );
    }
  }

  export default App;
