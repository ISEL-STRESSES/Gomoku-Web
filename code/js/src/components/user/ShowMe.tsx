import * as React from 'react';
import { getUserName } from '../../utils/cookieUtils';
import Box from "@mui/material/Box"
import {useNavigate} from "react-router-dom"
import Typography from "@mui/material/Typography"
import {PlayArrowRounded} from "@mui/icons-material"
import { useState } from "react";
import PageContent from "../shared/PageContent"
import MenuButton from "../shared/MenuButton";

export function ShowMe() {
    const currentUser = getUserName()
    const navigate = useNavigate()

    const [error] = useState<string | null>(null)

    return (
        <div>
            <PageContent title={""} error={error}>
                <Typography variant="h5" component="h2" gutterBottom>
                    Welcome to the Gomoku Game{", " + currentUser}!
                </Typography>

                <Typography variant="h6" gutterBottom>
                    This is a simple game of gomoku, where you can play against other players online.
                </Typography>

                <Box sx={{mt: 1}}>
                    <Typography variant="h6" gutterBottom>
                        {"Simply click the button below to start playing!"}
                    </Typography>

                    <MenuButton
                      title={"Play"}
                      icon={<PlayArrowRounded/>}
                      onClick={() => navigate('/gameplay-menu')}
                    />
                </Box>
            </PageContent>
        </div>
    );
}
