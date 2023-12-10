import { ForwardedRef } from "react";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import { MovesOutputModel } from "../../../service/game/models/GameOutput";
import boardCenter from '../../../assets/board-center.png';
import boardCorner from '../../../assets/board-corner.png';
import boardLateral from '../../../assets/board-latteral.png';
import { tileSize } from "./Tile";
import * as React from 'react';

interface BoardViewProps {
  board: MovesOutputModel;
  enabled: boolean;
  onTileClicked?: (x: number, y: number) => void;
  children?: React.ReactNode;
}

function BoardView({ board, enabled, onTileClicked, children }: BoardViewProps, ref: ForwardedRef<HTMLDivElement>) {
  const getTileStyle = (col: number, row: number, boardSize: number) => {
    let backgroundImage: string;
    let transform = '';

    const isCorner = (col === 0 || col === boardSize - 1) && (row === 0 || row === boardSize - 1);
    const isLateral = col === 0 || col === boardSize - 1 || row === 0 || row === boardSize - 1;

    if (isCorner) {
      backgroundImage = `url(${boardCorner})`;
      if (col === boardSize - 1 && row === 0) transform = 'rotate(90deg)';
      if (col === boardSize - 1 && row === boardSize - 1) transform = 'rotate(180deg)';
      if (col === 0 && row === boardSize - 1) transform = 'rotate(270deg)';
    } else if (isLateral) {
      backgroundImage = `url(${boardLateral})`;
      if (row === 0) transform = 'rotate(90deg)';
      if (col === boardSize - 1) transform = 'rotate(180deg)';
      if (row === boardSize - 1) transform = 'rotate(270deg)';
    } else {
      backgroundImage = `url(${boardCenter})`;
    }

    return {
      width: tileSize,
      height: tileSize,
      backgroundImage: backgroundImage,
      transform: transform,
      backgroundSize: 'contain',
      backgroundRepeat: 'no-repeat',
    };
  };

  return (
    <Box
      sx={{
        width: board.boardSize * tileSize,
        height: board.boardSize * tileSize,
        margin: 'auto',
        position: "relative",
        opacity: enabled ? 1 : 0.5
      }}
      ref={ref}
    >
      <Grid container columns={board.boardSize}>
        {Array.from(Array(board.boardSize * board.boardSize).keys()).map((tileIndex) => {
          const col = tileIndex % board.boardSize;
          const row = Math.floor(tileIndex / board.boardSize);
          const tileStyle = getTileStyle(col, row, board.boardSize);

          return (
            <Grid item key={tileIndex} xs={1} sm={1} md={1}>
              <Box
                sx={tileStyle}
                onClick={() => onTileClicked && enabled && onTileClicked(col, row)}
              />
            </Grid>
          );
        })}
      </Grid>
      {children}
    </Box>
  );
}

export default React.forwardRef(BoardView);
