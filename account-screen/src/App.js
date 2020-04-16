import React from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import LoginPage from './components/LoginPage';
// import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
// import './App.css';
import './stylesheets/BootstrapCustom.scss';

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
