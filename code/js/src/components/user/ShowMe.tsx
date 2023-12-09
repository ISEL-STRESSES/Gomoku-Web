import * as React from 'react';
import { useCurrentUser } from "../authentication/Authn";
import { Avatar, Box, Card, CardContent } from "@mui/material";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import GameHistory from "./GameHistory";

export function ShowMe() {
  const currentUser = useCurrentUser()
  return (
    <Box component="main">
      <Container maxWidth="lg">
        <Typography sx={{mb: 3}} variant="h4">Account</Typography>
        <Grid container spacing={3}>
          <Grid item lg={4} md={6} xs={12}>
            <Card>
              <CardContent>
                <Box sx={{
                  alignItems: 'center',
                  display: 'flex',
                  flexDirection: 'column'
                }}>
                  <Avatar sx={{height: 64, mb: 2, width: 64}}/>
                  <Typography color="textPrimary" gutterBottom variant="h5">
                    {currentUser}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid item lg={8} md={6} xs={12}>
            <GameHistory/>
          </Grid>
        </Grid>
      </Container>
    </Box>
  )
}
