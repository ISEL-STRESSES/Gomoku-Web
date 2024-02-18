import * as React from 'react';
import IconButton from '@mui/material/IconButton'
import EmailIcon from '@mui/icons-material/Email'
import GitHubIcon from '@mui/icons-material/GitHub'
import { Box } from '@mui/material';

interface Author {
  studentID: number,
  name: string,
  email: string,
  github: string
}

const authors: Author[] = [
  {
    studentID: 48335,
    name: "Rodrigo Correia",
    email: "A48335@alunos.isel.pt",
    github: "https://github.com/rodrigohcorreia"
  },
  {
    studentID: 48331,
    name: "Andre Matos",
    email: "A48331@alunos.isel.pt",
    github: "https://github.com/Matos16"
  },
  {
    studentID: 48253,
    name: "Carlos Pereira",
    email: "A48253@alunos.isel.pt",
    github: "https://github.com/Sideghost"
  }
]

const githubRepository: string = "https://github.com/isel-leic-daw/2023-daw-leic51d-01"
const versionNr: string = "1.0.0"

function AuthorList() {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', flexWrap: 'wrap' }}>
      {authors.map((author, index) => showAuthor(author, index))}
    </Box>
  );
}

const showAuthor = (author: Author, index: number | null | undefined) => {
  return (
    <Box key={index} sx={{ textAlign: 'center', m: 2, width: 200 }}>
      <img
        src={author.github + '.png'}
        alt={author.name}
        style={{ width: 150, height: 150, borderRadius: 75 }}
      />
      <h2 style={{ fontSize: '1.5rem' }}>{author.name}</h2>
      <IconButton aria-label="email" href={`mailto:${author.email}`}>
        <EmailIcon />
      </IconButton>
      <IconButton aria-label="github" href={author.github} target="_blank" rel="noopener noreferrer">
        <GitHubIcon />
      </IconButton>
    </Box>
  );
};


export  function About() {
  return (
    <div className="about">
      <h1>Meet the team</h1>
      <AuthorList />
      <p>Version: {versionNr}</p>
      <p>Repository: <a href={githubRepository} target="_blank" rel="noopener noreferrer">{githubRepository}</a></p>
    </div>
  );
}
