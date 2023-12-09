import * as React from "react"
import {useEffect, useState} from "react"
import {
  Alert,
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
import { GameOutputModel } from "../../service/game/models/GameOutput";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { Problem } from "../../service/media/Problem";
import { useNavigate } from "react-router-dom";
import { EmbeddedSubEntity } from "../../service/media/siren/SubEntity";

type FinishedGamesState =
  | { type: 'loading' }
  | { type: 'success'; finishedGames: EmbeddedSubEntity<GameOutputModel>[]}
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
        if (gamesRes instanceof Success) {
          const finishedGames = gamesRes.value.getEmbeddedSubEntities()
          if (finishedGames) {
            setState({ type: 'success', finishedGames: finishedGames });
          } else {
            setState({ type: 'success', finishedGames: [] });
          }
        } else {
          let errorMessage = 'Error fetching data';
          errorMessage = handleError(gamesRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
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
      return (
        <div className="loading">
          <CircularProgress />
        </div>
      );

    case 'error':
      return (
        <Alert severity="error" onClose={handleCloseAlert}>
          {state.message}
        </Alert>
      );

    case 'success':
      return (
        <Card>
          <CardHeader subheader="Last 10 games" title="Game History" />
          <Divider />
          <CardContent>
            <TableContainer component={Paper} sx={{ width: '100%' }}>
              <Table stickyHeader aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell>Game</TableCell>
                    <TableCell>Winner</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {state.finishedGames.map(game => (
                    <TableRow key={game.properties?.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                      <TableCell component="th" scope="row">
                        X{game.properties?.rule.boardSize} {game.properties?.rule.variant} {game.properties?.rule.openingRule}
                      </TableCell>
                      <TableCell>{game.properties?.gameOutcome}</TableCell>
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
