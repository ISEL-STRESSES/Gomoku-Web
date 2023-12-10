import * as React from 'react';
import { useEffect, useState } from 'react';
import { GameService } from '../../service/game/GameService';
import { Success } from '../../utils/Either';
import { GameOutputModel } from '../../service/game/models/GameOutput';
import { Problem } from '../../service/media/Problem';
import { useLocation, useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import BoardView, { pieceSize, tileSize } from './shared/BoardView';
import { UserService } from '../../service/user/UserService';
import { useCurrentUser } from '../authentication/Authn';
import Button from '@mui/material/Button';
import { AlertDialog, AlertDialogWithRedirect } from '../shared/AlertDialog';
import White from '../../assets/white.png';
import Black from '../../assets/black.png';
import Loading from '../shared/Loading';
import { useInterval } from './utils/useInterval';
import { ConfirmationDialog } from '../shared/ConfirmationDialog';
import { RuleStatsModel } from "../../service/user/models/GetUserStatsOutput";

const POLLING_DELAY = 2000;

type GameState =
  | { type: 'loading' }
  | { type: 'success'; game: GameOutputModel; turn: boolean; error?: string }
  | { type: 'finished'; winner: string; stats: RuleStatsModel }
  | { type: 'error'; message: string }
  | { type: 'confirm'; game: GameOutputModel; turn: boolean };

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
                    turn: true,
                  });
                else
                  setState({
                    type: 'success',
                    game: gameRes.value.properties,
                    turn: false,
                  });
              } else {
                setState({ type: 'error', message: 'No game found' });
              }
            }
          } else {
            setState({ type: 'error', message: 'No game found' });
          }
        } else {
          const errorMessage = handleError(gameRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchGetGame(location.state as number);
  }, [currentUser, location.state]);

  async function checkGameUpdates() {
    if (state.type !== 'success')
      return false;

    if (state.turn)
      return false;

    const fetchGetGame = async (gameId: number) => {
      try {
        const gameRes = await GameService.getGameById(gameId);
        if (gameRes instanceof Success) {
          if (gameRes.value.properties) {
            if (gameRes.value.properties.gameOutcome !== null ) {
              await handleFinishGame(gameRes.value.properties);
              return;
            }
            const userTurn = await UserService.getUser(gameRes.value.properties.turn.user);
            if (userTurn instanceof Success) {
              if (userTurn.value.properties) {
                if (userTurn.value.properties.username === currentUser)
                  setState({
                    type: 'success',
                    game: gameRes.value.properties,
                    turn: true,
                  });
              } else {
                setState({ type: 'error', message: 'No game found' });
              }
            }
          } else {
            setState({ type: 'error', message: 'No game found or unauthorized' });
          }
        } else {
          const errorMessage = handleError(gameRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    await fetchGetGame(state.game.id);

    return false;
  }

  useInterval(checkGameUpdates, POLLING_DELAY, [state.type === 'success' && !state.turn]);

  const handleFinishGame = async (game: GameOutputModel) => {
    try {
      if (game.gameOutcome === null) {
        setState({ type: 'error', message: 'Game didnt finished yet' });
        return;
      }
      const myUser = await UserService.getMe();
      if (myUser instanceof Success) {
        const myStatsForRule = myUser.value.getEmbeddedSubEntities().find((userStats) => userStats.properties?.ruleId === game.rule.ruleId);
        if (!myStatsForRule?.properties) {
          setState({ type: 'error', message: 'No stats found' });
          return;
        }
        if (myUser.value.properties) {
          if ((game.gameOutcome === 'BLACK_WON' && game.playerBlack === myUser.value.properties.userId) || (game.gameOutcome === 'WHITE_WON' && game.playerWhite === myUser.value.properties.userId)) {
            setState({ type: 'finished', winner: "You Won!", stats: myStatsForRule.properties })
          } else {
            setState({ type: 'finished', winner: "You Lost!", stats: myStatsForRule.properties })
          }
        }
      } else {
        const errorMessage = handleError(myUser.value);
        setState({ type: 'error', message: errorMessage });
      }
    } catch (error) {
      console.error('Error fetching ranking and rules:', error);
      const errorMessage = handleError(error);
      setState({ type: 'error', message: errorMessage });
    }
  }

  /**
   * Handles tile click.
   *
   * @param col the column
   * @param row the row
   */
  async function handleTileClick(col: number, row: number) {
    if (state.type !== 'success')
      return false;

    const fetchMakePlay = async () => {
      try {
        const res = await GameService.makePlay(state.game.id, { x: col, y: row });
        if (res instanceof Success) {
          if (res.value.properties) {
            setState({
              type: 'success',
              game: res.value.properties,
              turn: false,
            });
          } else {
            setState({ type: 'error', message: 'No game found' });
          }
        } else {
          const errorMessage = handleError(res.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching lobby:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    await fetchMakePlay();

    return false;
  }

  const handleForfeit = async () => {
    if (state.type !== 'confirm')
      return false;
    const fetchForfeit = async () => {
      try {
        const res = await GameService.forfeitGame(state.game.id);
        if (res instanceof Success) {
          if (res.value.properties) {
            if (res.value.properties.gameOutcome !== null) {
              await handleFinishGame(res.value.properties);
              return;
            }
          } else {
            setState({ type: 'error', message: 'Forfeit failed' });
          }
        } else {
          const errorMessage = handleError(res.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching game:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };
    await fetchForfeit();
    return false;
  };

  const handleDeclineForfeit = async () => {
    if (state.type !== 'confirm')
      return false;
    setState({ type: 'success', game: state.game, turn: state.turn });
    return false;
  };

  function GameDisplay({ game, enable, error }: { game: GameOutputModel, enable: boolean, error?: string }) {
    return (
      <Container maxWidth='lg'>
        <Box sx={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          <Box sx={{
            display: 'flex',
            flexDirection: 'row',
            marginTop: '15px',
            justifyContent: 'space-around',
            flexWrap: 'wrap',
          }}>
            <Box sx={{
              display: 'flex',
              alignSelf: 'flex-start',
              flexDirection: 'column',
            }}>
              <Typography variant='h5' sx={{ textAlign: 'center', mb: '5px' }}>My Board</Typography>
              <Typography variant='h6' sx={{ textAlign: 'center', mb: '5px' }}>
                {enable ? 'Your turn' : 'Opponents turn'}
              </Typography>
              <BoardView board={game.moves} enabled={enable} onTileClicked={handleTileClick}>
                {
                  game.moves.orderOfMoves.map((piece, index) => {
                    return (
                      <Box
                        key={piece.color + index}
                        sx={{
                          position: 'absolute',
                          left: piece.position.x * tileSize,
                          top: piece.position.y * tileSize,
                          display: 'flex',
                          justifyContent: 'center',
                          alignItems: 'center',
                          width: tileSize,
                          height: tileSize,
                        }}>
                        <Box
                          sx={{
                            width: pieceSize,
                            height: pieceSize,
                            backgroundImage: piece.color === 'WHITE' ? `url(${White})` : `url(${Black})`,
                            backgroundSize: 'contain',
                            backgroundRepeat: 'no-repeat',
                          }}
                        />
                      </Box>
                    );
                  })
                }
              </BoardView>
              <Box sx={{ display: 'flex', gap: 2, marginTop: 2, justifyContent: 'space-around' }}>
                <Button id='forfeit-button' variant='contained' color='inherit' onClick={() => setState({ type: 'confirm', game: game, turn: enable })} disabled={!enable}>
                  Forfeit
                </Button>
                {error ? <AlertDialog alert={error} /> : null}
              </Box>
            </Box>
          </Box>
        </Box>
      </Container>
    )
      ;
  }

  function GameFinishedDisplay({ winner, stats }: { winner: string, stats: RuleStatsModel }) {
    return (
      <Container maxWidth='lg'>
        <Box sx={{
          display: 'flex',
          flexDirection: 'column',
        }}>
          <Box sx={{
            display: 'flex',
            flexDirection: 'row',
            marginTop: '15px',
            justifyContent: 'space-around',
            flexWrap: 'wrap',
          }}>
            <Box sx={{
              display: 'flex',
              alignSelf: 'flex-start',
              flexDirection: 'column',
            }}>
              <Typography variant='h5' sx={{ textAlign: 'center', mb: '5px' }}>Game Finished</Typography>
              <Typography variant='h6' sx={{ textAlign: 'center', mb: '5px' }}>
                {winner}
              </Typography>
              <Typography variant='h6' sx={{ textAlign: 'center', mb: '5px' }}>
                Rank: {stats.rank},  Elo: {stats.elo},  GamesPlayed: {stats.gamesPlayed}
              </Typography>
              <Button variant='contained' color='inherit' onClick={() => navigate('/')}>
                Return to lobby
              </Button>
            </Box>
          </Box>
        </Box>
      </Container>
    );
  }

  const handleCloseAlert = () => {
    navigate('/gameplay-menu');
  };

  switch (state.type) {
    case 'loading':
      return <Loading />;

    case 'error':
      return (
        <AlertDialogWithRedirect alert={state.message} redirect={handleCloseAlert} />
      );

    case 'success':
      return (
        <GameDisplay game={state.game} enable={state.turn} error={state.error} />
      );

    case 'finished':
      return (
        <GameFinishedDisplay winner={state.winner} stats={state.stats} />
      );
    case 'confirm':
      return (<ConfirmationDialog
          message='Are you sure you want to forfeit the game?'
          onConfirm={handleForfeit}
          onDecline={handleDeclineForfeit}
          confirmButtonText='Forfeit'
          cancelButtonText='Cancel'
        />
      );
  }
}