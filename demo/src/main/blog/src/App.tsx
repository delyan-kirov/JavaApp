import React from 'react';
import logo from './logo.svg';
import './App.css';
import Summary from './components/Summary';
import Feed from './components/Feed';

const App: React.FC = () => {
  return (
    <div className="App">
      <Summary />
      <Feed />
    </div>
  );
};


export default App;
