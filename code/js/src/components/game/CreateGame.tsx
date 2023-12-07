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
import { LobbyOutputModel, PostRuleIdInputModel } from "../../service/lobby/models/LobbyOutput";
import { LobbyService } from "../../service/lobby/LobbyService";

type CreateGameState =
  | { type: 'loading' }
  | { type: 'success-rule'; rules: EmbeddedSubEntity<RuleOutputModel>[] }
  | { type: 'success-lobby'; lobby: SirenEntity<LobbyOutputModel> }
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

  const handleRuleClick = (ruleId: number | undefined) => {
    if (ruleId === undefined) {
      setState({ type: 'error', message: 'Rule ID is undefined' });
      return;
    }
    const fetchLobby = async () => {
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

    fetchLobby();
  }

  function RulesDisplay({ rules }: { rules: EmbeddedSubEntity<RuleOutputModel>[] }) {
    return (
      <PageContent title={" "}>
        <Typography variant="h5" component="h2" gutterBottom>
          Choose which rule you want to play.
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          {rules.map((rule) => (
            <Box key={rule.properties?.ruleId} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <div>X{rule.properties?.boardSize}</div>
              <div>{rule.properties?.openingRule}</div>
              <div>{rule.properties?.variant}</div>
              <Button variant="contained" color="inherit" onClick={() => {location.state? navigate("/match-make", { state: rule.properties?.ruleId }) : handleRuleClick(rule.properties?.ruleId)}}>
                Button
              </Button>
            </Box>
          ))}
        </Box>
      </PageContent>
    );
  }

  function LobbyDisplay({lobby}: { lobby: SirenEntity<LobbyOutputModel>}) {
    return (
      <PageContent title={" "}>
        <Typography variant="h5" component="h2" gutterBottom>
          Waiting for opponent to join...
        </Typography>
        <Typography variant="h5" component="h2" gutterBottom>
          Game ID: {lobby.properties?.id}
        </Typography>
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

    case 'success-rule':
      return (
        <RulesDisplay rules={state.rules} />
      );

    case 'success-lobby':
      return (
        <LobbyDisplay lobby={state.lobby}/>
      );
  }
}