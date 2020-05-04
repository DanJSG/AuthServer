import React from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import LoginPage from './components/LoginPage';
import CallbackPage from './components/CallbackPage';
import LogoutPage from './components/LogoutPage';
import SignUpPage from './components/SignUpPage'
import './stylesheets/BootstrapCustom.scss';

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/oauth2/authorize">
          <LoginPage></LoginPage>
        </Route>
        <Route exact path="/oauth2/auth_callback">
          <CallbackPage></CallbackPage>
        </Route>
        <Route exact path="/">
          <LogoutPage></LogoutPage>
        </Route>
        <Route exact path="/oauth2/register">
          <SignUpPage></SignUpPage>
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
