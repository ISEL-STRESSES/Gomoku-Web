import * as React from 'react';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import { AlertDialog } from './AlertDialog';

/**
 * Properties of the PageContent component.
 *
 * @property title the title of the page
 * @property error the error message
 * @property children the children to render
 * @property alignment the alignment of the children
 */
interface PageContentProps {
  title?: string;
  error?: string;
  alignment?: string;
  children: React.ReactNode;
}

/**
 * Component that wraps the content of a page.
 */
export default function PageContent({ title, error, alignment, children }: PageContentProps) {
  return (
    <Container>
      <h1 id='PageContentTitle'>{title}</h1>
      <Box sx={{
        marginTop: 20,
        display: 'flex',
        flexDirection: 'column',
        alignItems: alignment ?? 'center',
      }}>
        {
          error ? <AlertDialog alert={error} /> : null
        }
        {children}
      </Box>
    </Container>
  );
}
