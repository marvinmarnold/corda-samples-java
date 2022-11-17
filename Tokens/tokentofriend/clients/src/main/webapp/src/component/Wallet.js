import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import axios from 'axios';

import Container from 'react-bootstrap/Container'
import { Row } from 'react-bootstrap';
import { Col } from 'react-bootstrap';
import { BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";

class Wallet extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      username: window.location.search.split("=")[1],
      amount: 0,
      reAmount:0,
      reUsername:'',
      endPoint:'10050',
      callback:''};

      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
      this.handleRefresh = this.handleRefresh.bind(this);
    }

    componentDidMount() {
      this.queryToken();
    }

    handleChange(event) {
      this.setState({ [event.target.name]: event.target.value });
    }

    handleSubmit(event) {
      const queryInfo = JSON.stringify({ 
        "senderUsername": this.state.username,
        "receiverUsername": this.state.reUsername,
        "amount": this.state.reAmount  
       });
      // console.log('A token was submitted: \nSender: '+ this.state.seEmail+'\nReceiver: '+this.state.reEmail+'\nMessage'+this.state.message);
      console.log(`http://localhost:10050/transfer`)    
      axios.post(`http://localhost:10050/transfer`, queryInfo, {"headers": {"content-type": "application/json",}})
      .then(
        response => {
          console.log(response.data)
          this.setState({callback:response.data})
         }
        );
      event.preventDefault();
    }

    queryToken = () => {
      const queryInfo = JSON.stringify({ 
        "username": this.state.username        
       });
       console.log(queryInfo);
       console.log(`http://localhost:10050/retrieve`)    
       axios.post(`http://localhost:10050/retrieve`, queryInfo, {"headers": {"content-type": "application/json",}})
       .then(
         response => {
           console.log(response.data)
           this.setState({callback: response.data})
           this.setState({amount: response.data.split(' ')[0]})
          }
         );
    }

    handleRefresh(event) {
      this.queryToken();
      event.preventDefault();
    }

    render(){
      return (
        <div className="retrieveToken">
          <Container>
            <img src="opentransact_logo.png" alt="Open Transact" width="200" className="App-logo"/>
            <Row>
              <Col md="auto"><h1>Open Transact Wallet</h1></Col>
              <Col md={{offset:2}}>
                <button className="btn btn-primary disabled" >Wallet</button> 
                {this.state.username==="admin" ? 
                <Link to={"/mint?u=" + this.state.username} ><button className="btn btn-secondary" >Mint</button></Link> :
                ""}
              </Col>
            </Row>
            <Row>
              <Col>
              <h3>Logged in as {this.state.username} with ${this.state.amount}</h3>
              <button className="btn-info" onClick={this.handleRefresh}>Refresh</button>
              </Col>
            </Row>
          </Container>
          <Container className="mt-3">
            <h4>Transfer USD: </h4>
            <form onSubmit={this.handleSubmit}>
              <div className="form-group">
                <label>USD Amount</label>
                <input
                  type="number"  className="form-control" name="reAmount" onChange={this.handleChange} />
              </div>
              <div className="form-group">
                <label>
                  Recipient's Username
                </label>
                <input
                  type="text"  className="form-control" name="reUsername" onChange={this.handleChange} />
              </div>
              <br />
              <div className="row">
                <div className="col-9" />
                <div className="col-3">
                  <button type="submit" className="btn btn-secondary">Execute transfer</button>
                </div>
              </div>
            </form>
          </Container>
          <Container>
            {this.state.callback != '' &&
              <div>
                <h4>Here is your query result: </h4>
                <p>{this.state.callback}</p>
              </div>
                  }
          </Container>


        </div>
      );
    }
  }

  export default Wallet;
