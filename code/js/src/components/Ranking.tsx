import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserService } from '../service/user/UserService';
import { Failure, Success } from '../utils/Either';
import { GameService } from '../service/game/GameService';
import { RuleOutputModel } from '../service/game/models/RuleOutput';
import { GetUserRuleStatsOutputModel } from '../service/user/models/GetUserRuleStatsOutput';
import { EmbeddedSubEntity } from '../service/media/siren/SubEntity';
import { Problem } from '../service/media/Problem';
import { Link } from '../service/media/siren/Link';
import { AlertDialogWithRedirect } from './shared/AlertDialog';
import Loading from './shared/Loading';
import {
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  SelectChangeEvent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
} from '@mui/material';
import Button from '@mui/material/Button';
import { ThemeProvider } from '@mui/material/styles';
import { darkTheme } from '..';

type RankingState =
  | { type: 'loading' }
  | {
  type: 'success';
  ranking: EmbeddedSubEntity<GetUserRuleStatsOutputModel>[];
  rules: EmbeddedSubEntity<RuleOutputModel>[];
  rankingLinks?: Link[]
}
  | { type: 'error'; message: string };

export function Ranking() {
  const [state, setState] = useState<RankingState>({ type: 'loading' });
  const [rules, setRules] = useState<EmbeddedSubEntity<RuleOutputModel>[]>([]);
  const [ruleId, setRuleId] = useState(1);
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const searchTermRef = useRef(searchTerm);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleSearchSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault(); // Prevent default form submission
    await performSearch();
  };

  const performSearch = async () => {
    setState({ type: 'loading' });
    try {
      const usernameQuery = searchTerm.trim() ? `username=${encodeURIComponent(searchTerm.trim())}` : '';
      const rankingRes = await UserService.getRanking(ruleId, usernameQuery);
      if (rankingRes instanceof Success) {
        setState({
          type: 'success',
          ranking: rankingRes.value.getEmbeddedSubEntities(),
          rules: rules,
          rankingLinks: rankingRes.value.links,
        });
      } else {
        let errorMessage = 'Error fetching data';
        if (rankingRes instanceof Failure) {
          errorMessage = handleError(rankingRes.value);
        }
        setState({ type: 'error', message: errorMessage });
      }
    } catch (error) {
      const errorMessage = handleError(error);
      setState({ type: 'error', message: errorMessage });
    }
  };


  const handleUserClick = (userId: number | undefined) => {
    if (userId !== undefined) {
      navigate(`/users/${userId}`);
    } else {
      console.error('User ID is undefined');
    }
  };

  const handleButtonClick = async (rel: string) => {
    if (state.type === 'success') {
      const link = state.rankingLinks?.find((link) => link.rel.includes(rel));
      if (link) {
        let queryString = link.href.split('?')[1] || '';
        const queryParams = queryString ? queryString.split('&').filter(param => !param.startsWith('username=')) : [];
        if (searchTerm.trim()) {
          queryParams.push(`username=${encodeURIComponent(searchTerm.trim())}`);
        }
        queryString = queryParams.join('&');

        const rankingRes = await UserService.getRanking(ruleId, queryString);
        if (rankingRes instanceof Success) {
          setState({
            type: 'success',
            ranking: rankingRes.value.getEmbeddedSubEntities(),
            rules: state.rules,
            rankingLinks: rankingRes.value.links,
          });
        } else {
          let errorMessage = 'Error fetching data';
          if (rankingRes instanceof Failure) {
            errorMessage = handleError(rankingRes.value);
          }
          setState({ type: 'error', message: errorMessage });
        }
      }
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
    searchTermRef.current = searchTerm;
  }, [searchTerm]);

  useEffect(() => {
    const fetchRankingAndRules = async () => {
      try {
        setState({ type: 'loading' });
        const query = searchTermRef.current.trim() ? `username=${encodeURIComponent(searchTermRef.current.trim())}` : '';
        const rankingRes = await UserService.getRanking(ruleId, query);
        const rulesRes = await GameService.getGameRules();

        if (rankingRes instanceof Success && rulesRes instanceof Success) {
          setState({
            type: 'success',
            ranking: rankingRes.value.getEmbeddedSubEntities(),
            rules: rulesRes.value.getEmbeddedSubEntities(),
            rankingLinks: rankingRes.value.links,
          });
          setRules(rulesRes.value.getEmbeddedSubEntities());
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
      setState({ type: 'loading' });
    };
  }, [ruleId]);

  const handleCloseAlert = () => {
    navigate('/');
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
          <Box sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 2 }}>
              <h1>Ranking</h1>
              <FormControl size='small' sx={{ minWidth: 120 }}>
                <InputLabel id='ranking-rules-label'>Rules</InputLabel>
                <Select
                  labelId='ranking-rules-label'
                  id='rankingRules'
                  value={ruleId.toString()}
                  label='Rules'
                  onChange={(e: SelectChangeEvent) => setRuleId(parseInt(e.target.value, 10))}
                >
                  {rules.map((rule, index) => (
                    <MenuItem key={index} value={rule.properties?.ruleId}>
                      X{rule.properties?.boardSize} {rule.properties?.variant} {rule.properties?.openingRule}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              <Box>
                <form onSubmit={handleSearchSubmit} style={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <TextField
                    size='small'
                    type='text'
                    value={searchTerm}
                    onChange={handleSearchChange}
                    placeholder='Search username'
                    variant='outlined'
                    sx={{ backgroundColor: 'background.paper', flexGrow: 1, minWidth: 'fit-content' }}
                  />

                  <Button type='submit' variant='contained' size='small' color='primary' sx={{maxWidth: 'fit-content'}}>
                    Search
                  </Button>
                </form>
              </Box>
            </Box>
            <TableContainer component={Paper} sx={{ mt: 2 }}>
              <Table sx={{ minWidth: 650 }} aria-label='ranking table'>
                <TableHead>
                  <TableRow>
                    <TableCell>Rank</TableCell>
                    <TableCell>Username</TableCell>
                    <TableCell>Elo</TableCell>
                    <TableCell>Games</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {state.ranking.map((user, index) => (
                    <TableRow
                      key={index}
                      sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                      onClick={() => handleUserClick(user.properties?.id)}
                      style={{ cursor: 'pointer' }}
                    >
                      <TableCell component='th' scope='user'>
                        {user.properties?.rank}
                      </TableCell>
                      <TableCell>{user.properties?.username}</TableCell>
                      <TableCell>{user.properties?.elo}</TableCell>
                      <TableCell>{user.properties?.gamesPlayed}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <div className='buttons-container'>
              {state.rankingLinks?.find((link) => link.rel.at(0) === 'first') ?
                <button onClick={() => handleButtonClick('first')}>First</button> : null}
              {state.rankingLinks?.find((link) => link.rel.at(0) === 'prev') ?
                <button onClick={() => handleButtonClick('prev')}>Previous</button> : null}
              {state.rankingLinks?.find((link) => link.rel.at(0) === 'next') ?
                <button onClick={() => handleButtonClick('next')}>Next</button> : null}
              {state.rankingLinks?.find((link) => link.rel.at(0) === 'last') ?
                <button onClick={() => handleButtonClick('last')}>Last</button> : null}
            </div>
          </Box>
        </ThemeProvider>
      );
  }
}
