import * as React from 'react';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

type ConfirmationDialogProps = {
  message: string;
  onConfirm: () => void;
  onDecline: () => void;
  confirmButtonText: string;
  cancelButtonText: string;
};

export function ConfirmationDialog({
                                     message,
                                     onConfirm,
                                     onDecline,
                                     confirmButtonText,
                                     cancelButtonText,
                                   }: ConfirmationDialogProps) {

  return (
    <Box sx={{ textAlign: 'center', p: 2 }}>
      <Typography variant='h6'>{message}</Typography>
      <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center', gap: 2 }}>
        <Button variant='contained' color='error' onClick={onConfirm} sx={{ width: '150px' }}>
          {confirmButtonText}
        </Button>
        <Button variant='outlined' onClick={onDecline} sx={{ width: '150px' }}>
          {cancelButtonText}
        </Button>
      </Box>
    </Box>
  );
}
