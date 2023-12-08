import Grid from "@mui/material/Grid"
import * as React from "react"
import {ForwardedRef} from "react"
import Box from "@mui/material/Box"
import Tile, {tileSize} from "./Tile"
import { MovesOutputModel } from "../../../service/game/models/GameOutput";

/**
 * Properties for the BoardView component.
 *
 * @property board the board to display
 * @property enabled whether the board is enabled or not
 * @property onTileClicked the callback to be called when a tile is clicked
 * @property children the children to be displayed on top of the board
 */
interface BoardViewProps {
    board: MovesOutputModel
    enabled: boolean
    onTileClicked?: (x:number, y:number) => void
    children?: React.ReactNode
}

/**
 * BoardView component.
 */
function BoardView({board, enabled, onTileClicked, children}: BoardViewProps, ref: ForwardedRef<HTMLDivElement>) {
    return (
        <Box
            sx={{
                width: board.boardSize * tileSize,
                height: board.boardSize * tileSize,
                margin: 'auto',
                position: "relative",
                opacity: enabled ? 1 : 0.5
            }}>
            <Grid ref={ref} container columns={board.boardSize}>
                {
                    Array.from(Array(board.boardSize * board.boardSize).keys()).map((tileIndex) => {
                            const col = tileIndex % board.boardSize
                            const row = Math.floor(tileIndex / board.boardSize)

                            return <Grid item key={tileIndex} xs={1} sm={1} md={1}>
                                {
                                    tileIndex == 0
                                        ? <Box sx={{width: tileSize, height: tileSize}}/>
                                        : (
                                            tileIndex <= board.boardSize
                                                ? <Box sx={{width: tileSize, height: tileSize}}>
                                                    {String.fromCharCode(64 + tileIndex)}
                                                </Box>
                                                : (
                                                    col === 0
                                                        ? <Box sx={{width: tileSize, height: tileSize}}>
                                                            {row}
                                                        </Box>
                                                        : <Tile onClick={() => {
                                                            if (onTileClicked)
                                                                onTileClicked(col, row)
                                                        }}/>
                                                )
                                        )
                                }
                            </Grid>
                        }
                    )
                }
            </Grid>
            {children}
        </Box>
    )
}

export default React.forwardRef(BoardView)
