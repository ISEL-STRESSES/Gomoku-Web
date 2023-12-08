import * as React from 'react';
import { useEffect, useState } from "react";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { GameOutputModel } from "../../service/game/models/GameOutput";
import { Problem } from "../../service/media/Problem";
import { useLocation, useNavigate } from "react-router-dom";
import { CircularProgress } from "@mui/material";
import Box from "@mui/material/Box";
import { tileSize } from "./shared/Tile";
import { useInterval } from "./utils/useInterval";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import BoardView from "./shared/BoardView";
import { UserService } from "../../service/user/UserService";
import { useCurrentUser } from "../authentication/Authn";
import Button from "@mui/material/Button";
import { AlertDialog, AlertDialogWithRedirect } from "../shared/AlertDialog";

const POLLING_DELAY = 2000;

type GameState =
  | { type: 'loading' }
  | { type: 'success'; game: GameOutputModel; turn: boolean; error?: string}
  | { type: 'error'; message: string };

export function Game() {

    const [state, setState] = useState<GameState>({ type: 'loading' });
    const navigate = useNavigate();
    const location = useLocation();
    const currentUser = useCurrentUser();

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
      const fetchGetGame = async (gameId: number) => {
        try {
          const gameRes = await GameService.getGameById(gameId);
          const turn = await GameService.getTurn(gameId);
          if (gameRes instanceof Success && turn instanceof Success) {
            if (gameRes.value.properties && turn.value.properties) {
              const userTurn = await UserService.getUser(turn.value.properties.turn);
              if (userTurn instanceof Success) {
                if (userTurn.value.properties) {
                  if (userTurn.value.properties.username === currentUser)
                    setState({
                      type: 'success',
                      game: gameRes.value.properties,
                      turn: true
                    });
                  else
                    setState({
                      type: 'success',
                      game: gameRes.value.properties,
                      turn: false
                    });
                }else{
                  setState({ type: 'error', message: 'No game found', });
                }
              }
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

      fetchGetGame(location.state as number)

      return () => {
        // Cleanup if necessary
      };
    }, []);

    async function checkGameUpdates() {
      if (state.type !== 'success')
        return false

      if (state.turn)
        return false

      const fetchGetGame = async (gameId: number) => {
        try {
          const gameRes = await GameService.getGameById(gameId);
          if (gameRes instanceof Success) {
            if (gameRes.value.properties) {
              const userTurn = await UserService.getUser(gameRes.value.properties.turn.user);
              if (userTurn instanceof Success) {
                if (userTurn.value.properties) {
                  if (userTurn.value.properties.username === currentUser)
                    setState({
                      type: 'success',
                      game: gameRes.value.properties,
                      turn: true
                    });
                }else{
                  setState({ type: 'error', message: 'No game found', });
                }
              }
            }else{
              setState({ type: 'error', message: 'No game found or unauthorized' });
            }
          }else {
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

      await fetchGetGame(state.game.id);

      return false
    }

    useInterval(checkGameUpdates, POLLING_DELAY, [state.type === 'success' && !state.turn])

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
          const res = await GameService.makePlay(state.game.id, { x: col, y: row });
          if (res instanceof Success) {
            if (res.value.properties) {
              setState({
                type: 'success',
                game: res.value.properties,
                turn: false
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

      await fetchMakePlay();

      return false
    }

    function GameDisplay({ game, enable, error }: { game: GameOutputModel, enable: boolean, error?: string }) {
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
                <Typography variant="h6" sx={{textAlign: "center", mb: "5px"}}>
                  {enable ? "Your turn" : "Opponents turn"}
                </Typography>
                <BoardView board={game.moves} enabled={enable} onTileClicked={handleTileClick}>
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
                              //backgroundImage: `url(../../../public/images/${piece.color.toLowerCase()}.png)`,
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
                <Button variant="contained" color="inherit" onClick={() => console.log("TODO")}>
                  Forfeit
                </Button>
                {error ? <AlertDialog alert={error}/> : null}
              </Box>
            </Box>
          </Box>
        </Container>
      );
    }

    const handleCloseAlert = () => {
      navigate('/gameplay-menu')
    }

    switch (state.type) {
      case 'loading':
        return (
          <div className="loading">
            <CircularProgress />
          </div>
        );

      case 'error':
        return (
          <AlertDialogWithRedirect alert={state.message} redirect={handleCloseAlert}/>
        );

      case 'success':
        return (
          <GameDisplay game={state.game} enable={state.turn} error={state.error} />
        );
    }
}