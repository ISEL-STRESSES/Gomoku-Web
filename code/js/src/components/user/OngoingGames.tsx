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
import { EmbeddedSubEntity } from "../../service/media/siren/SubEntity";
import { GameOutputModel } from "../../service/game/models/GameOutput";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { Problem } from "../../service/media/Problem";
import { AlertDialogWithRedirect } from "../shared/AlertDialog";

type OngoingGamesState =
  | { type: 'loading' }
  | { type: 'success'; ongoingGames: EmbeddedSubEntity<GameOutputModel>[]}
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
    async function fetchOngoingGames() {
      const ongoingGamesRes = await GameService.getOngoingGames();
      if (ongoingGamesRes instanceof Success) {
        const ongoingGames = ongoingGamesRes.value.getEmbeddedSubEntities()
        if (ongoingGames) {
          setState({ type: 'success', ongoingGames: ongoingGames });
        } else {
          setState({ type: 'success', ongoingGames: [] });
        }
      } else {
        const errorMessage = handleError(ongoingGamesRes.value);
        setState({ type: 'error', message: errorMessage });
      }
    }
    fetchOngoingGames()
  }, [])

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
                    <TableCell>Quantity of moves</TableCell>
                    <TableCell>Play</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {
                    state.ongoingGames?.map(game => (
                      <TableRow key={game.properties?.id} sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                        <TableCell component="th" scope="row">
                          X{game.properties?.rule.boardSize} {game.properties?.rule.variant} {game.properties?.rule.openingRule}
                        </TableCell>
                        <TableCell>{game.properties?.moves.orderOfMoves.length}</TableCell>
                        <TableCell>
                          <Button onClick={
                            () => {
                              navigate("/game", {state: game.properties?.id})
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
