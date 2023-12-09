import * as React from 'react';
import Box from '@mui/material/Box';

export const tileSize = 40;
export const pieceSize = tileSize * 0.75;

/**
 * Properties for the Tile component.
 *
 * @property onClick the callback to be called when the tile is clicked
 */
interface TileProps {
  onClick?: () => void;
}

/**
 * Tile component.
 */
export default function Tile({ onClick }: TileProps) {
  return (
    <Box
      sx={{
        width: tileSize,
        height: tileSize,
        backgroundColor: '#c19a6b',
      }}
      onClick={onClick}
    />
  );
}
