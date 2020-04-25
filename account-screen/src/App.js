import React from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import LoginPage from './components/LoginPage';
import './stylesheets/BootstrapCustom.scss';
// import {generateCodeChallenge, generateState} from './components/codeGen'

// console.log(generateCodeChallenge());
// console.log(generateState());

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/authorize">
          <LoginPage></LoginPage>
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
