import React from 'react';
import './App.css';
import { useMediaQuery, useTheme } from '@mui/material';
import NewsLetterSignUp from './Components/SignUp/NewsLetterSignUp';

function App() {
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));
  const webAPI = 'http://localhost:8080/api/newsletter/';
  
  return (
    <div style={{ padding: isSmallScreen ? '10px' : '20px' }}>
      <NewsLetterSignUp webAPI={webAPI} />
    </div>
  );
}

export default App;
