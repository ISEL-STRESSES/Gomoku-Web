import * as React from 'react';
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserService } from "../service/user/UserService";
import { Success, Failure } from "../utils/Either";
import { GameService } from "../service/game/GameService";
import { RuleOutputModel } from "../service/game/models/RuleOutput";
import { GetUserRuleStatsOutputModel } from "../service/user/models/GetUserRuleStatsOutput";
import { EmbeddedSubEntity } from "../service/media/siren/SubEntity";
import { CircularProgress, Alert } from '@mui/material';
import { Problem } from '../service/media/Problem';

type RankingState =
  | { type: 'loading' }
  | { type: 'success'; ranking: EmbeddedSubEntity<GetUserRuleStatsOutputModel>[]; rules: EmbeddedSubEntity<RuleOutputModel>[] }
  | { type: 'error'; message: string };

export function Ranking() {
  const [state, setState] = useState<RankingState>({ type: 'loading' });
  const [ruleId, setRuleId] = useState(1);
  const navigate = useNavigate();

  const handleUserClick = (userId: number | undefined) => {
    if (userId !== undefined) {
      navigate(`/users/${userId}`);
    } else {
      console.error("User ID is undefined");
    }
  };

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
    const fetchRankingAndRules = async () => {
      try {
        const rankingRes = await UserService.getRanking(ruleId);
        const rulesRes = await GameService.getGameRules();

        if (rankingRes instanceof Success && rulesRes instanceof Success) {
          setState({
            type: 'success',
            ranking: rankingRes.value.getEmbeddedSubEntities(),
            rules: rulesRes.value.getEmbeddedSubEntities()
          });
        } else {
          let errorMessage = 'Error fetching data';
          if (rankingRes instanceof Failure) {
            errorMessage = handleError(rankingRes.value);
          } else if (rulesRes instanceof Failure) {
            errorMessage = handleError(rulesRes.value);
          }
          setState({ type: 'error', message: errorMessage });
        }
      } catch (error) {
        console.error('Error fetching ranking and rules:', error);
        const errorMessage = handleError(error);
        setState({ type: 'error', message: errorMessage });
      }
    };

    fetchRankingAndRules();

    return () => {
      // Cleanup if necessary
    };
  }, [ruleId]);

  switch (state.type) {
    case 'loading':
      return <CircularProgress />;

    case 'error':
      return <Alert severity="error">{state.message}</Alert>;

    case 'success':
      return (
        <div className="list-container">
          <h1>Ranking</h1>
          <select id="rankingRules" value={ruleId} onChange={e => setRuleId(parseInt(e.target.value, 10))}>
            {state.rules.map((rule, index) => (
              <option key={index} value={rule.properties?.ruleId}>
                X{rule.properties?.boardSize}  {rule.properties?.variant}  {rule.properties?.openingRule}
              </option>
            ))}
          </select>
          <table>
            <thead>
            <tr>
              <th>Rank</th>
              <th>Username</th>
              <th>Elo</th>
              <th>Games</th>
            </tr>
            </thead>
            <tbody id="rankingBody">
            {state.ranking.map((user, index) => (
              <tr key={index} onClick={() => handleUserClick(user.properties?.id)}>
                <td>{user.properties?.rank}</td>
                <td>{user.properties?.username}</td>
                <td>{user.properties?.elo}</td>
                <td>{user.properties?.gamesPlayed}</td>
              </tr>
            ))}
            </tbody>
          </table>
        </div>
      );
  }
}
