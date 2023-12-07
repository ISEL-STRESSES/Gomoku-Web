import * as React from 'react';
import { useEffect, useState } from "react";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { GameOutputModel } from "../../service/game/models/GameOutput";
import { Problem } from "../../service/media/Problem";
import { useLocation } from "react-router-dom";
import { CircularProgress } from "@mui/material";
import PageContent from "../shared/PageContent";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

type GameState =
  | { type: 'loading' }
  | { type: 'success'; game: GameOutputModel}
  | { type: 'error'; message: string };

export function Game() {

    const [state, setState] = useState<GameState>({ type: 'loading' });
    const location = useLocation();

    const handleError = (error: any) => {
      if (error instanceof Error) {
        return error.message;
      } else if (error instanceof Problem) {
        return error.title || 'A problem occurred';
      } else {
        return 'An unexpected error occurred';
      }
    };

    useEffect(() => {
      const fetchGetGame = async () => {
        try {
          setState({ type: 'loading' })
          const gameRes = await GameService.getGameById(location.state);
          if (gameRes instanceof Success) {
            if (gameRes.value.properties) {
              setState({
                type: 'success',
                game: gameRes.value.properties
              });
            }else{
              setState({ type: 'error', message: 'No game found' });
            }

          } else {
            let errorMessage = 'Error fetching data';
            errorMessage = handleError(gameRes.value);
            setState({ type: 'error', message: errorMessage });
          }
        } catch (error) {
          console.error('Error fetching ranking and rules:', error);
          const errorMessage = handleError(error);
          setState({ type: 'error', message: errorMessage });
        }
      };

      fetchGetGame();

      return () => {
        // Cleanup if necessary
      };
    }, []);

    function GameDisplay({ game }: { game: GameOutputModel }) {
      console.log(game)
      return (
        <PageContent title={" "}>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
            <Typography variant="h2" component="div" gutterBottom>
              Game
            </Typography>
            <Typography variant="h4" component="div" gutterBottom>
              {game.id}
            </Typography>
            <Typography variant="h5" component="div" gutterBottom>
              {game.type}
            </Typography>
            <Typography variant="h6" component="div" gutterBottom>
              {game.playerBlack}
            </Typography>
            <Typography variant="h6" component="div" gutterBottom>
              {game.playerWhite}
            </Typography>
          </Box>
        </PageContent>
      );
    }

    switch (state.type) {
      case 'loading':
        return (
          <div id="loading">
            <CircularProgress />
          </div>
        );

      case 'error':
        return (
          <div id="error">
            {state.message}
          </div>
        );

      case 'success':
        return (
          <GameDisplay game={state.game}/>
        );
    }
}