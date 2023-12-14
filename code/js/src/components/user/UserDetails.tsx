import * as React from 'react';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { UserService } from '../../service/user/UserService';
import { GameService } from '../../service/game/GameService';
import { Failure, Success } from '../../utils/Either';
import { GetUserStatsOutputModel, RuleStatsModel } from '../../service/user/models/GetUserStatsOutput';
import { RuleOutputModel } from '../../service/game/models/RuleOutput';
import { EmbeddedSubEntity } from '../../service/media/siren/SubEntity';
import { Problem } from '../../service/media/Problem';
import { AlertDialogWithRedirect } from '../shared/AlertDialog';
import Loading from '../shared/Loading';
import Button from '@mui/material/Button';
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  ThemeProvider,
  Typography,
} from '@mui/material';
import { darkTheme } from '../../index';

type UserDetailsState =
  | { type: 'loading' }
  | { type: 'error'; message: string }
  | {
  type: 'success';
  userDetails: GetUserStatsOutputModel;
  userStatsDetails: EmbeddedSubEntity<RuleStatsModel>[];
  rules: Map<number, RuleOutputModel>;
};

export const UserDetails: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const [state, setState] = useState<UserDetailsState>({ type: 'loading' });
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
    const fetchUserDetails = async () => {
      if (!userId) {
        setState({ type: 'error', message: 'Invalid user ID' });
        return;
      }

      const id = parseInt(userId, 10);
      if (isNaN(id)) {
        setState({ type: 'error', message: 'Invalid user ID' });
        return;
      }

      try {
        const res = await UserService.getUser(id);
        if (res instanceof Success && res.value.properties) {
          const newRules = new Map<number, RuleOutputModel>();
          const ruleStats = res.value.getEmbeddedSubEntities();
          for (const ruleStat of ruleStats) {
            const ruleId = ruleStat.properties?.ruleId;
            if (ruleId !== undefined) {
              const ruleRes = await GameService.getGameRuleById(ruleId);
              if (ruleRes instanceof Success && ruleRes.value.properties) {
                newRules.set(ruleId, ruleRes.value.properties);
              } else if (ruleRes instanceof Failure) {
                setState({ type: 'error', message: handleError(ruleRes.value) });
                return;
              }
            }
          }
          setState({
            type: 'success',
            userDetails: res.value.properties,
            userStatsDetails: ruleStats,
            rules: newRules,
          });
        } else if (res instanceof Failure) {
          setState({ type: 'error', message: handleError(res.value) });
        }
      } catch (error) {
        setState({ type: 'error', message: handleError(error) });
      }
    };

    fetchUserDetails();
  }, [userId]);

  const handleCloseAlert = () => {
    navigate('/ranking');
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
        <ThemeProvider theme={darkTheme}>
          <Typography variant='h4' sx={{ mb: 2 }}>
            User Details: {state.userDetails.username}
          </Typography>
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }} aria-label='user-stats-table'>
              <TableHead>
                <TableRow>
                  <TableCell>Rule Name</TableCell>
                  <TableCell>Rank</TableCell>
                  <TableCell>Games Played</TableCell>
                  <TableCell>Elo</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {state.userStatsDetails.map((rule, index) => {
                  const ruleDetails = rule.properties?.ruleId !== undefined ? state.rules.get(rule.properties.ruleId) : null;
                  return (
                    <TableRow key={index}>
                      <TableCell>{ruleDetails ? `X${ruleDetails.boardSize} ${ruleDetails.variant} ${ruleDetails.openingRule}` : 'N/A'}</TableCell>
                      <TableCell>{rule.properties?.rank}</TableCell>
                      <TableCell>{rule.properties?.gamesPlayed}</TableCell>
                      <TableCell>{rule.properties?.elo}</TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
          <Box sx={{ mt: 2 }}>
            <Button variant='contained' color='primary' onClick={() => navigate(-1)}>
              Back
            </Button>
          </Box>
        </ThemeProvider>
      );
  }
};