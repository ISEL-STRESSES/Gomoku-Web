import * as React from "react"
import {useEffect, useState} from "react"
import PlayArrowIcon from '@mui/icons-material/PlayArrow'
import {
  Button,
  Card,
  CardContent,
  CardHeader, CircularProgress,
  Divider,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import {useNavigate} from "react-router-dom"
import { OngoingOutputModel } from "../../service/game/models/GameOutput";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { Problem } from "../../service/media/Problem";
import { AlertDialogWithRedirect } from "../shared/AlertDialog";
import { UserService } from "../../service/user/UserService";

type OngoingGamesState =
  | { type: 'loading' }
  | { type: 'success'; ongoingGames: OngoingOutputModel[]}
  | { type: 'error'; message: string };

/**
 * Ongoing games component.
 *
 */
export default function OngoingGames() {

  const [state, setState] = useState<OngoingGamesState>({ type: 'loading' });
  const navigate = useNavigate()

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
    const fetchOngoingGames = async () => {
      try {
        const gamesRes = await GameService.getOngoingGames();
        const me = await UserService.getMe();

        if (gamesRes instanceof Success && me instanceof Success) {
          const ongoingGames = gamesRes.value.getEmbeddedSubEntities();
          if (ongoingGames) {
            const opponentsPromises = ongoingGames.map(async (game) => {
              const opponentId =
                game.properties?.playerBlack === me.value.properties?.userId
                  ? game.properties?.playerWhite
                  : game.properties?.playerBlack;

              if (
                opponentId === undefined ||
                game.properties?.id === undefined ||
                game.properties?.rule === undefined ||
                game.properties?.moves === undefined
              ) {
                return {
                  gameId: 0,
                  oppId: 0,
                  oppUsername: "Unknown",
                  rule: game.properties?.rule,
                  moves: 0,
                };
              }

              const oppUsername = await getUserUsername(opponentId);

              return {
                gameId: game.properties.id,
                oppId: opponentId,
                oppUsername: oppUsername,
                rule: game.properties.rule,
                moves: game.properties.moves.orderOfMoves.length,
              };
            });

            const ongoingGamesOutput = await Promise.all(opponentsPromises);
            setState({
              type: "success",
              ongoingGames: ongoingGamesOutput,
            });
          } else {
            setState({ type: "success", ongoingGames: [] });
          }
        } else {
          const errorMessage = handleError(gamesRes.value);
          setState({ type: "error", message: errorMessage });
        }
      } catch (error) {
        console.error("Error fetching ongoing games:", error);
        const errorMessage = handleError(error);
        setState({ type: "error", message: errorMessage });
      }
    };

    const getUserUsername = async (userId: number) => {
      const userRes = await UserService.getUser(userId);
      return userRes instanceof Success
        ? userRes.value.properties?.username
        : undefined;
    };

    fetchOngoingGames();

    return () => {
      // Clear up
    };
  }, []);

  const handleCloseAlert = () => {
    navigate('/')
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
      return(
        <Card>
          <CardHeader
            title="Ongoing Games History"
          />
          <Divider/>
          <CardContent>
            <TableContainer component={Paper} sx={{width: '100%'}}>
              <Table stickyHeader aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell>Game</TableCell>
                    <TableCell>Number of moves</TableCell>
                    <TableCell>Opponent</TableCell>
                    <TableCell>Play</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {
                    state.ongoingGames?.map(game => (
                      <TableRow key={game.gameId} sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                        <TableCell component="th" scope="row">
                          X{game.rule?.boardSize} {game.rule?.variant} {game.rule?.openingRule}
                        </TableCell>
                        <TableCell>{game.moves}</TableCell>
                        <TableCell>
                          <Button onClick={
                            () => {
                              navigate('/users/' + game.oppId)
                            }
                          } sx={{maxWidth: 'fit-content'}}>
                            {game.oppUsername}
                          </Button>
                        </TableCell>
                        <TableCell>
                          <Button onClick={
                            () => {
                              navigate("/game", {state: game.gameId})
                            }
                          }>
                            <PlayArrowIcon/>
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))
                  }
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>
      );
  }
}
