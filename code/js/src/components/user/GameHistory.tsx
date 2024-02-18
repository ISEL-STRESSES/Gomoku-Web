import * as React from "react"
import {useEffect, useState} from "react"
import {
  Button,
  Card,
  CardContent,
  CardHeader, Divider,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import { FinishedOutputModel } from "../../service/game/models/GameOutput";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { Problem } from "../../service/media/Problem";
import { useNavigate } from "react-router-dom";
import Loading from "../shared/Loading";
import { AlertDialogWithRedirect } from "../shared/AlertDialog";
import { UserService } from "../../service/user/UserService";

type FinishedGamesState =
  | { type: 'loading' }
  | { type: 'success'; finishedGames: FinishedOutputModel[]}
  | { type: 'error'; message: string };
export default function GameHistory() {
  
  const [state, setState] = useState<FinishedGamesState>({ type: 'loading' });
  const navigate = useNavigate();

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
    const fetchGetFinishedGames = async () => {
      try {
        const gamesRes = await GameService.getHub();
        const me = await UserService.getMe();

        if (gamesRes instanceof Success && me instanceof Success) {
          const finishedGames = gamesRes.value.getEmbeddedSubEntities();
          if (finishedGames) {
            const opponentsPromises = finishedGames.map(async game => {
              const opponentId = game.properties?.playerBlack === me.value.properties?.userId
                ? game.properties?.playerWhite
                : game.properties?.playerBlack;

              const outcome = game.properties?.gameOutcome === 'DRAW' ?
                'Draw!'
                : (game.properties?.gameOutcome === 'BLACK_WON' && opponentId === game.properties?.playerBlack) || (game.properties?.gameOutcome === 'WHITE_WON' && opponentId === game.properties?.playerWhite)
                  ? 'You lost!'
                  : 'You won!';

              if (opponentId === undefined || game.properties?.id === undefined || game.properties?.rule === undefined ) {
                return {
                  gameId: 0,
                  oppId: 0,
                  oppUsername: 'Unknown',
                  rule: game.properties?.rule,
                  outcome: outcome,
                };
              }

              const oppUsername = await getUserUsername(opponentId);

              return {
                gameId: game.properties.id,
                oppId: opponentId,
                oppUsername: oppUsername,
                rule: game.properties.rule,
                outcome: outcome,
              };
            });

            const finishedGamesOutput = await Promise.all(opponentsPromises);
            setState({
              type: 'success',
              finishedGames: finishedGamesOutput
            });
          } else {
            setState({ type: 'success', finishedGames: [] });
          }
        } else {
          const errorMessage = handleError(gamesRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching finished games:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    const getUserUsername = async (userId: number) => {
      const userRes = await UserService.getUser(userId);
      return userRes instanceof Success ? userRes.value.properties?.username : undefined;
    };

    fetchGetFinishedGames();

    return () => {
      // Clear up
    };
  }, []);

  const handleCloseAlert = () => {
    navigate('/')
  }

  switch (state.type) {
    case 'loading':
      return <Loading />;

    case 'error':
      return (
        <AlertDialogWithRedirect alert={state.message} redirect={handleCloseAlert}/>
      );

    case 'success':
      return (
        <Card>
          <CardHeader title="Game History" />
          <Divider />
          <CardContent>
            <TableContainer component={Paper} sx={{ width: '100%' }}>
              <Table stickyHeader aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell>Game</TableCell>
                    <TableCell>Opponent</TableCell>
                    <TableCell>Winner</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {state.finishedGames.map((game) => (
                    <TableRow key={game.gameId} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                      <TableCell component="th" scope="row">
                        X{game.rule?.boardSize} {game.rule?.variant} {game.rule?.openingRule}
                      </TableCell>
                      <TableCell>
                        <Button onClick={
                          () => {
                            navigate('/users/' + game.oppId)
                          }
                        } sx={{maxWidth: 'fit-content'}}>
                          {game.oppUsername}
                        </Button>
                      </TableCell>
                      <TableCell>{game.outcome}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      );
  }
}
