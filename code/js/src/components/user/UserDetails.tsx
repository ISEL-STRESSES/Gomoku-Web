import * as React from 'react';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from "react-router-dom";
import { UserService } from '../../service/user/UserService';
import { GameService } from '../../service/game/GameService';
import { Failure, Success } from '../../utils/Either';
import { GetUserStatsOutputModel, RuleStatsModel } from '../../service/user/models/GetUserStatsOutput';
import { RuleOutputModel } from '../../service/game/models/RuleOutput';
import { EmbeddedSubEntity } from '../../service/media/siren/SubEntity';
import { CircularProgress } from '@mui/material';
import { Problem } from '../../service/media/Problem';
import { AlertDialogWithRedirect } from "../shared/AlertDialog";

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
    navigate('/ranking')
  }

  switch (state.type) {
    case 'loading':
      return <CircularProgress />;

    case 'error':
      return (
          <AlertDialogWithRedirect alert={state.message} redirect={handleCloseAlert}/>
      )

    case 'success':
      return (
        <div>
          <div className='user-details-header'>
            <h1>User Details</h1>
            <h2 className='username'>{state.userDetails.username}</h2>
          </div>
          <div className='list-container'>
            <h2>Stats</h2>
            <table className='stats-table'>
              <thead>
              <tr>
                <th>Rule Name</th>
                <th>Rank</th>
                <th>Games Played</th>
                <th>Elo</th>
              </tr>
              </thead>
              <tbody>
              {state.userStatsDetails.map((rule, index) => {
                const ruleDetails = rule.properties?.ruleId !== undefined ? state.rules.get(rule.properties.ruleId) : null;

                return (
                  <tr key={index}>
                    <td>{ruleDetails ? `X${ruleDetails.boardSize} ${ruleDetails.variant} ${ruleDetails.openingRule}` : 'N/A'}</td>
                    <td>{rule.properties?.rank}</td>
                    <td>{rule.properties?.gamesPlayed}</td>
                    <td>{rule.properties?.elo}</td>
                  </tr>
                );
              })}
              </tbody>
            </table>
          </div>
        </div>
      );
  }
};