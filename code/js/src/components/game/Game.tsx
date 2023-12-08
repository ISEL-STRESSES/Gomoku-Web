import * as React from 'react';
import { useEffect, useState } from "react";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { GameOutputModel } from "../../service/game/models/GameOutput";
import { Problem } from "../../service/media/Problem";
import { useLocation } from "react-router-dom";
import { CircularProgress } from "@mui/material";
import Box from "@mui/material/Box";
import { tileSize } from "./shared/Tile";
import { useInterval } from "./utils/useInterval";
import { PlayPositionInputModel } from "../../service/game/models/PlayPositionInputModel";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import BoardView from "./shared/BoardView";

const POLLING_DELAY = 1000;

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

    async function checkGameUpdates() {
      if (state.type !== 'success')
        return false

      const fetchGetGame = async () => {
        try {
          const res = await GameService.getGameById(state.game.id);

          if (res instanceof Success) {
            if (res.value.properties) {
              setState({
                type: 'success',
                game: res.value.properties
              });
            }else{
              setState({ type: 'error', message: 'No game found' });
            }
          } else {
            let errorMessage = 'Error fetching data';
            errorMessage = handleError(res.value);
            setState({ type: 'error', message: errorMessage });
          }
        } catch (error) {
          console.error('Error fetching lobby:', error);
          const errorMessage = handleError(error);
          setState({ type: 'error', message: errorMessage });
        }
      };

      fetchGetGame();

      return false
    }

    useInterval(checkGameUpdates, POLLING_DELAY, [state.type === 'success'])

    /**
     * Handles tile click.
     *
     * @param col the column
     * @param row the row
     */
    async function handleTileClick(col: number, row: number) {
      if (state.type !== 'success')
        return false

      const fetchMakePlay = async () => {
        try {
          const playPositionInput: PlayPositionInputModel = {
              x: col,
              y: row
          };
          const res = await GameService.makePlay(state.game.id, playPositionInput);

          if (res instanceof Success) {
            if (res.value.properties) {
              setState({
                type: 'success',
                game: res.value.properties
              });
            }else{
              setState({ type: 'error', message: 'No game found' });
            }
          } else {
            let errorMessage = 'Error fetching data';
            errorMessage = handleError(res.value);
            setState({ type: 'error', message: errorMessage });
          }
        } catch (error) {
          console.error('Error fetching lobby:', error);
          const errorMessage = handleError(error);
          setState({ type: 'error', message: errorMessage });
        }
      };

      fetchMakePlay();

      return false
    }

    function GameDisplay({ game }: { game: GameOutputModel }) {
      return (
        <Container maxWidth="lg">
          <Box sx={{
            display: "flex",
            flexDirection: "column",
          }}>
            <Box sx={{
              display: 'flex',
              flexDirection: 'row',
              marginTop: '15px',
              justifyContent: "space-around",
              flexWrap: "wrap"
            }}>
              <Box sx={{
                display: 'flex',
                alignSelf: 'flex-start',
                flexDirection: 'column'
              }}>
                <Typography variant="h5" sx={{textAlign: "center", mb: "5px"}}>My Board</Typography>
                <BoardView board={game.moves} enabled={true} onTileClicked={handleTileClick}>
                  {
                    game.moves.orderOfMoves.map((piece, index) => {
                      return (
                        <Box
                          key={piece.color + index}
                          sx={{
                            position: 'absolute',
                            top: (piece.position.y) * tileSize,
                            left: (piece.position.x) * tileSize,
                          }}>
                          <Box
                            sx={{
                              width: tileSize,
                              height: tileSize,
                              backgroundColor: piece.color,
                              //backgroundImage: `../../../public/images/${piece.color.toLowerCase()}.png`,
                              //backgroundSize: "contain",
                              //backgroundRepeat: "no-repeat",
                              //backgroundPosition: "center",
                            }}
                          />
                        </Box>
                      )
                    })
                  }
                </BoardView>
              </Box>
            </Box>
          </Box>
        </Container>
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