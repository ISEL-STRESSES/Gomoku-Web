import { useEffect, useState } from 'react';
import * as React from 'react';
import { useParams } from 'react-router-dom';
import { UserService } from '../../service/user/UserService';
import { GameService } from '../../service/game/GameService';
import { Success } from '../../utils/Either';
import { GetUserStatsOutputModel, RuleStatsModel } from "../../service/user/models/GetUserStatsOutput";
import { RuleOutputModel } from '../../service/game/models/RuleOutput';
import { EmbeddedSubEntity } from "../../service/media/siren/SubEntity";

export function UserDetails() {
  const { userId } = useParams<{ userId: string }>();
  const [userDetails, setUserDetails] = useState<GetUserStatsOutputModel>();
  const [userStatsDetails, setUserStatsDetails] = useState<EmbeddedSubEntity<RuleStatsModel>[]>([]);
  const [rules, setRules] = useState<Map<number, RuleOutputModel>>(new Map());

  useEffect(() => {
    const fetchUserDetails = async () => {
      if (userId) {
        const id = parseInt(userId, 10);
        if (!isNaN(id)) {
          try {
            const res = await UserService.getUser(id);
            if (res instanceof Success) {
              setUserDetails(res.value.properties);
              setUserStatsDetails(res.value.getEmbeddedSubEntities());
              console.log(userDetails);
              console.log(userStatsDetails);
              // Fetch rule details for each rule ID in the user's stats
              for (const ruleStat of res.value.getEmbeddedSubEntities()) {
                const ruleId = ruleStat.properties?.ruleID;
                if(ruleId){
                  const ruleRes = await GameService.getGameRuleById(ruleId);
                  if (ruleRes instanceof Success) {
                    const props = ruleRes.value.properties
                    if (props){
                      setRules(prev => new Map(prev).set(ruleId, props));
                    }
                  }
                }
              }
            }
          } catch (error) {
            console.error('Error fetching user details:', error);
          }
        } else {
          console.error('Invalid user ID:', userId);
        }
      }
    };

    fetchUserDetails();
  }, [userId]);

  if (!userDetails) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>User Details</h1>
      <p>Username: {userDetails.username}</p>
      <div>
        <h2>Stats for each rule</h2>
        {userStatsDetails.length > 0 ? (
          <ul>
            {userStatsDetails.map((rule, index) => {
              const ruleId = rule.properties?.ruleID;
              if (ruleId !== undefined){
                const ruleDetail = rules.get(ruleId)
                if (ruleDetail !== undefined){
                  const name = `X${ruleDetail.boardSize} ${ruleDetail.variant} ${ruleDetail.openingRule}`
                  return (
                    <li key={index}>
                      <p>Rule Name: {name}</p>
                      <p>Rank: {rule.properties?.rank}</p>
                      <p>Games Played: {rule.properties?.gamesPlayed}</p>
                      <p>Elo: {rule.properties?.elo}</p>
                    </li>
                  );
                }
              }
            })}
          </ul>
        ) : (
          <p>No rule stats available.</p>
        )}
      </div>
    </div>
  );
}
