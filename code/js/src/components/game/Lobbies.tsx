import * as React from "react";
import { useEffect, useState } from "react";
import { Success } from "../../utils/Either";
import { EmbeddedSubEntity } from "../../service/media/siren/SubEntity";
import { GetLobbyModel, PostLobbyIdInputModel } from "../../service/lobby/models/LobbyOutput";
import { LobbyService } from "../../service/lobby/LobbyService";
import { Problem } from "../../service/media/Problem";
import PageContent from "../shared/PageContent";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import { useNavigate } from "react-router-dom";
import { AlertDialogWithRedirect } from "../shared/AlertDialog";
import Loading from "../shared/Loading";

type CreateGameState =
  | { type: 'loading' }
  | { type: 'success'; lobbies: EmbeddedSubEntity<GetLobbyModel>[] }
  | { type: 'error'; message: string };

export function Lobbies() {

  const [state, setState] = useState<CreateGameState>({ type: 'loading' });
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
    const fetchLobbies = async () => {
      try {
        setState({ type: 'loading' })
        const lobbiesRes = await LobbyService.getLobbies();

        if (lobbiesRes instanceof Success) {
          setState({
            type: 'success',
            lobbies: lobbiesRes.value.getEmbeddedSubEntities()
          });
        } else {
          const errorMessage = handleError(lobbiesRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchLobbies();

    return () => {
      // Cleanup if necessary
    };
  }, []);

  const handleLobbyClick = (lobbyId: number | undefined) => {
    if (lobbyId === undefined) {
      setState({ type: 'error', message: 'Lobby ID is undefined' });
      return;
    }
    const fetchLobby = async () => {
      try {
        setState({ type: 'loading' })
        const lobbyInput: PostLobbyIdInputModel = {
          lobbyId: lobbyId
        };
        const res = await LobbyService.joinLobby(lobbyInput);

        if (res instanceof Success) {
          navigate("/game", { state: res.value.properties?.id });
        } else {
          const errorMessage = handleError(res.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchLobby();
  }

  function LobbiesDisplay({ lobbies }: { lobbies: EmbeddedSubEntity<GetLobbyModel>[] }) {
    return (
      <PageContent title={" "}>
        <Typography variant="h5" component="h2" gutterBottom>
          Choose which lobby you want to join.
        </Typography>
        <Box sx={{ display: 'flex', gap: 10 }}>
          {lobbies.map((lobby) => (
            <Box key={lobby.properties?.id} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <div>{lobby.properties?.rule.boardSize}</div>
              <div>{lobby.properties?.rule.type}</div>
              <Button variant="contained" color="inherit" onClick={() => handleLobbyClick(lobby.properties?.id)}>
                Play
              </Button>
            </Box>
          ))}
        </Box>
        <Box sx={{ display: 'flex', gap: 2, marginTop: 2 }}>
          <Button variant="contained" color="inherit" onClick={() => navigate(-1)}>
            Back
          </Button>
        </Box>
      </PageContent>
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
        <AlertDialogWithRedirect alert={state.message} redirect={handleCloseAlert}/>
      );

    case 'success':
      return (
        <LobbiesDisplay lobbies={state.lobbies}/>
      );
  }
}