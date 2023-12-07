import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { useCurrentUser } from "./authentication/Authn";
import PageContent from "./shared/PageContent";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import MenuButton from "./shared/MenuButton";
import { LoginRounded, PlayArrowRounded } from "@mui/icons-material";

export function Home() {
  const currentUser = useCurrentUser()
  const navigate = useNavigate()
  return (
    <div id="homePage">
      <PageContent title={" "}>
        <Typography variant="h5" component="h2" gutterBottom>
          Welcome{currentUser ? ", " + currentUser : ""}!
        </Typography>

        <Typography variant="h6" gutterBottom>
          This is a simple game of gomoku where you can play against other players online.
        </Typography>

        <img src="/images/logo.png" alt="logo" width="300" height="300"/>

        <Box sx={{mt: 1}}>
          <Typography variant="h6" gutterBottom>
            {
              currentUser
                ? "Simply click the button below to start playing!"
                : "You need to be logged in to play. Please log in or sign up to play."
            }
          </Typography>

          <MenuButton
            title={currentUser ? "Play" : "Log in"}
            icon={currentUser ? <PlayArrowRounded/> : <LoginRounded/>}
            onClick={() => navigate(currentUser ? '/gameplay-menu' : '/login')}
          />
        </Box>
      </PageContent>
    </div>
  )
}
