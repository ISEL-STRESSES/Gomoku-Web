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
    name: "André Matos",
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

function AuthorList() {
  return (
    <Box>
      {authors.map((author, index) => showAuthor(author, index))}
    </Box>
  );
}

const showAuthor = (author: Author, index: number | null | undefined) => {
  return (
    <Box key={index} sx={{ textAlign: 'center', my: 2 }}>
      <img
        src={author.github + '.png'}
        alt={author.name}
        style={{ width: 100, height: 100, borderRadius: 50 }}
      />
      <h2>{author.name}</h2>
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
  //TODO: Make titles have better styling, also add a link to the github repo and version number
  return (
    <div className="about">
      <h1>Meet the team</h1>
      <AuthorList />
    </div>
  );
}
