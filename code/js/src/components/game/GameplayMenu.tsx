import PageContent from "../shared/PageContent";
import MenuButton from "../shared/MenuButton";
import { AddRounded, PlayArrowRounded, SearchRounded } from "@mui/icons-material";
import * as React from "react";
import { useNavigate } from "react-router-dom";

export default function GameplayMenu() {
  const navigate = useNavigate()

  return (
    <PageContent title={""}>
      <MenuButton
        title={"Quick Play"}
        icon={<PlayArrowRounded/>}
        onClick={() => navigate("/create-game", { state: "match-make" })}
      />
      <MenuButton
        title={"New Game"}
        icon={<AddRounded/>}
        onClick={() => navigate("/create-game")}
      />
      <MenuButton
        title={"Search Game"}
        icon={<SearchRounded/>}
        onClick={() => navigate("/lobby")}
      />
    </PageContent>
  )
}