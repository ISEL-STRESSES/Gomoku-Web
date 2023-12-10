import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

type ConfirmationDialogProps = {
  message: string;
  onConfirm: () => void;
  confirmButtonText: string;
  cancelButtonText: string;
};

export function ConfirmationDialog({
                                     message,
                                     onConfirm,
                                     confirmButtonText,
                                     cancelButtonText,
                                   }: ConfirmationDialogProps) {
  const navigate = useNavigate();

  return (
    <Box sx={{ textAlign: 'center', p: 2 }}>
      <Typography variant='h6'>{message}</Typography>
      <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center', gap: 2 }}>
        <Button variant='contained' color='error' onClick={onConfirm} sx={{ width: '150px' }}>
          {confirmButtonText}
        </Button>
        <Button variant='outlined' onClick={() => navigate(-1)} sx={{ width: '150px' }}>
          {cancelButtonText}
        </Button>
      </Box>
    </Box>
  );
}
