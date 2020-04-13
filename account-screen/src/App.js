import React from 'react';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom';
import LoginPage from './components/LoginPage';
import './App.css';

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/sign-in">
          <LoginPage></LoginPage>
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
