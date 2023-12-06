import * as React from 'react';
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserService } from "../service/user/UserService";
import { Success } from "../utils/Either";
import { GameService } from "../service/game/GameService";
import { RuleOutputModel } from "../service/game/models/RuleOutput";
import { GetUserRuleStatsOutputModel } from "../service/user/models/GetUserRuleStatsOutput";
import { EmbeddedSubEntity } from "../service/media/siren/SubEntity";

export function Ranking() {
  const [ruleId, setRuleId] = useState(1);
  const [ranking, setRanking] = useState<EmbeddedSubEntity<GetUserRuleStatsOutputModel>[]>([]); //TODO: Make this a type
  const [rules, setRules]  = useState<EmbeddedSubEntity<RuleOutputModel>[]>([]);
  const navigate = useNavigate();

  const handleUserClick = (userId: number | undefined) => {
    if(userId !== undefined){
      navigate(`/users/${userId}`);
    } else {
      console.error("User ID is undefined");
    }
  };

  // Using useEffect to handle the side effect of fetching ranking
  useEffect(() => {
    // Define an asynchronous function to fetch the ranking
    const fetchRanking = async () => {
      try {
        const res = await UserService.getRanking(ruleId);
        if (res instanceof Success) {
          setRanking(res.value.getEmbeddedSubEntities());
        }
      } catch (error) {
        console.error('Error fetching ranking:', error);
      }
    };

    const fetchRules = async () => {
        try {
          const res = await GameService.getGameRules();
          if (res instanceof Success) {
            setRules(res.value.getEmbeddedSubEntities());
          }
        } catch (error) {
          console.error('Error fetching rules:', error);
        }
    };

    // Call the fetchRanking function when the component mounts or when ruleId changes
    fetchRules();
    fetchRanking();

    // Cleanup function to avoid memory leaks in case the component unmounts
    return () => {
      // You can perform any cleanup tasks here if necessary
    };
  }, [ruleId]); // Add ruleId as a dependency to refetch whenever it changes

  return (
  <div id="ranking">
    <h1>Ranking</h1>

    <select id="rankingRules" value={ruleId} onChange={e => setRuleId(parseInt(e.target.value, 10))}>
      {rules.map((rule, index) => (
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
      {ranking.map((user, index) => (
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
