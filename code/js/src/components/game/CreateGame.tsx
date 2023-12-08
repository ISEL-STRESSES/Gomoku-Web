import * as React from "react";
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import { EmbeddedSubEntity } from "../../service/media/siren/SubEntity";
import { RuleOutputModel } from "../../service/game/models/RuleOutput";
import { useEffect, useState } from "react";
import { GameService } from "../../service/game/GameService";
import { Success } from "../../utils/Either";
import { Problem } from "../../service/media/Problem";
import { CircularProgress } from "@mui/material";
import PageContent from "../shared/PageContent";
import Typography from "@mui/material/Typography";
import { useNavigate, useLocation } from "react-router-dom";
import { SirenEntity } from "../../service/media/siren/SirenEntity";
import { LobbyOutputModel, PostLobbyIdInputModel, PostRuleIdInputModel } from "../../service/lobby/models/LobbyOutput";
import { LobbyService } from "../../service/lobby/LobbyService";
import { useInterval } from "./utils/useInterval";
import { AlertDialogWithRedirect } from "../shared/AlertDialog";

const POLLING_DELAY = 4000;

type CreateGameState =
  | { type: 'loading' }
  | { type: 'success-rule'; rules: EmbeddedSubEntity<RuleOutputModel>[] }
  | { type: 'success-lobby'; lobby: SirenEntity<LobbyOutputModel> }
  | { type: 'leaving' }
  | { type: 'error'; message: string };

export function CreateGame() {

  const [state, setState] = useState<CreateGameState>({ type: 'loading' });
  const navigate = useNavigate();
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
    const fetchRules = async () => {
      try {
        setState({ type: 'loading' })
        const rulesRes = await GameService.getGameRules();

        if (rulesRes instanceof Success) {
          setState({
            type: 'success-rule',
            rules: rulesRes.value.getEmbeddedSubEntities()
          });
        } else {
          let errorMessage = 'Error fetching data';
          errorMessage = handleError(rulesRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchRules();

    return () => {
      // Cleanup if necessary
    };
  }, []);

  useInterval(checkIfOpponentJoined, POLLING_DELAY, [state.type === 'success-lobby'])

  /**
   * Checks if the opponent has joined the game.
   *
   * @returns true if the opponent has joined, false otherwise
   */
  async function checkIfOpponentJoined() {
    if (state.type !== 'success-lobby')
      return false

    const fetchGetLobby = async () => {
      try {
        if (state.lobby.properties === undefined) {
          setState({ type: 'error', message: 'Lobby is undefined' });
          return false;
        }
        const res = await LobbyService.getLobbyById(state.lobby.properties.id);

        if (res instanceof Success) {
          if (res.value.properties?.state) {
            navigate("/game", { state: res.value.properties?.gameId})
            return true;
          }else {
            console.log("Waiting for opponent to join...")
            return false
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

    fetchGetLobby();

    return false
  }

  const handleRuleClickToCreate = (ruleId: number | undefined) => {
    if (ruleId === undefined) {
      setState({ type: 'error', message: 'Rule ID is undefined' });
      return;
    }
    const fetchLobbyCreate = async () => {
      try {
        setState({ type: 'loading' })
        const ruleInput: PostRuleIdInputModel = {
          ruleId: ruleId
        };
        const lobbyRes = await LobbyService.createLobby(ruleInput);

        if (lobbyRes instanceof Success) {
          setState({
            type: 'success-lobby',
            lobby: lobbyRes.value
          });
        } else {
          let errorMessage = 'Error fetching data';
          errorMessage = handleError(lobbyRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchLobbyCreate();
  }

  const handleLeaveLobbyClick = (lobby: SirenEntity<LobbyOutputModel>) => {
    const fetchLobbyCreate = async () => {
      try {
        if (lobby.properties === undefined) {
          setState({ type: 'error', message: 'Lobby is undefined' });
          return;
        }
        setState({ type: 'leaving' })
        const lobbyInput: PostLobbyIdInputModel = {
          lobbyId: lobby.properties?.id
        };
        const lobbyRes = await LobbyService.leaveLobby(lobbyInput)

        if (lobbyRes instanceof Success) {
          navigate("/gameplay-menu");
        } else {
          let errorMessage = 'Error fetching data';
          errorMessage = handleError(lobbyRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchLobbyCreate();
  }

  const handleRuleClickToJoin = (ruleId: number | undefined) => {
    if (ruleId === undefined) {
      setState({ type: 'error', message: 'Rule ID is undefined' });
      return;
    }
    const fetchRuleJoin = async () => {
      try {
        setState({ type: 'loading' })
        const ruleInput: PostRuleIdInputModel = {
          ruleId: ruleId
        };
        const lobbyRes = await LobbyService.joinByMatchMake(ruleInput);

        if (lobbyRes instanceof Success) {
          console.log(lobbyRes.value.properties)
          if (lobbyRes.value.properties?.isGame) {
            navigate("/game", { state: lobbyRes.value.properties?.id})
          }else {
            setState({
              type: 'success-lobby',
              lobby: lobbyRes.value
            });
          }
        } else {
          let errorMessage = 'Error fetching data';
          errorMessage = handleError(lobbyRes.value);
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchRuleJoin();
  }

  function RulesDisplay({ rules }: { rules: EmbeddedSubEntity<RuleOutputModel>[] }) {
    return (
      <PageContent title={" "}>
        <Typography variant="h5" component="h2" gutterBottom>
          Choose which rule you want to play.
        </Typography>
        <Box sx={{ display: 'flex', gap: 10 }}>
          {rules.map((rule) => (
            <Box key={rule.properties?.ruleId} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <div>X{rule.properties?.boardSize}</div>
              <div>{rule.properties?.openingRule}</div>
              <div>{rule.properties?.variant}</div>
              <Button variant="contained" color="inherit" onClick={() => {location.state? handleRuleClickToJoin(rule.properties?.ruleId) : handleRuleClickToCreate(rule.properties?.ruleId)}}>
                Play
              </Button>
            </Box>
          ))}
        </Box>
        <Box sx={{ display: 'flex', gap: 2, marginTop: 2 }}>
          <Button variant="contained" color="inherit" onClick={() => navigate("/gameplay-menu")}>
            Back
          </Button>
        </Box>
      </PageContent>
    );
  }

  function LobbyDisplay({ lobby }: { lobby: SirenEntity<LobbyOutputModel> }) {
    return (
      <PageContent title={' '}>
        <Typography variant="h5" component="h2" gutterBottom>
          Waiting for opponent to join...
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, marginTop: 2 }}>
          <Button variant="contained" color="inherit" onClick={() => handleLeaveLobbyClick(lobby)}>
            Leave Lobby
          </Button>
        </Box>
      </PageContent>
    );
  }

  const handleCloseAlert = () => {
    navigate('/gameplay-menu');
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
        <AlertDialogWithRedirect alert ={state.message} redirect={handleCloseAlert}/>
      );

    case 'leaving':
      return (
        <div className="loading">
          <div className="loading-message">
            Leaving lobby...
          </div>
        </div>
      );

    case 'success-rule':
      return <RulesDisplay rules={state.rules} />;

    case 'success-lobby':
      return <LobbyDisplay lobby={state.lobby} />;
  }
}