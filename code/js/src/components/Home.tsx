import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { useCurrentUser } from './authentication/Authn';
import PageContent from './shared/PageContent';
import Typography from '@mui/material/Typography';
import MenuButton from './shared/MenuButton';
import { LoginRounded, PlayArrowRounded } from '@mui/icons-material';
import Background from '../assets/background.png';
import { Box, Paper } from '@mui/material';

export function Home() {
  const currentUser = useCurrentUser();
  const navigate = useNavigate();
  return (
    <Paper style={{
      backgroundImage: `url(${Background})`,
      backgroundSize: 'cover',
      backgroundRepeat: 'no-repeat',
      backgroundPosition: 'center',
      height: '100vh',
      width: '100vw',
      position: 'absolute',
    }}>
      <PageContent title={' '} alignment={'normal'}>
        {/* Repositioned with margin and made the text wider and more visible */}
        <Typography
          variant='h3'
          gutterBottom
          color={'white'}
          sx={{
            maxWidth: '90%', // Maximum width of the text
            fontWeight: 'bold', // Makes the text bolder
            textShadow: '2px 2px 4px rgba(0, 0, 0, 0.5)', // Text shadow for better readability
          }}
        >
          Welcome{currentUser ? ', ' + currentUser : ''}!
        </Typography>
        <Box mt={5} sx={{ height: '2vh' }}>
          <Typography variant='h5' gutterBottom color={'white'}>
            This is a simple game of gomoku where you can play against other players online.
          </Typography>

          <Typography variant='h5' gutterBottom color={'white'}>
            {
              currentUser
                ? 'Simply click the button below to start playing!'
                : 'You need to be logged in to play. Please log in or sign up to play.'
            }
          </Typography>

          <MenuButton
            title={currentUser ? 'Play' : 'Log in'}
            icon={currentUser ? <PlayArrowRounded /> : <LoginRounded />}
            onClick={() => navigate(currentUser ? '/gameplay-menu' : '/login')}

          />
        </Box>


      </PageContent>
    </Paper>
  );
}
