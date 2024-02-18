import CircularProgress from "@mui/material/CircularProgress";
import * as React from 'react';

export default function Loading() {
  return (
    <div className="loading">
      <CircularProgress />
    </div>
  );
}