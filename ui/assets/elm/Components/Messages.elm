module Components.Messages exposing (Msg(..))

import Json.Decode


type Msg
    = GridClicked Int Int
    | SelectedPaintColor String
    | FillClicked
    | SaveClicked String
    | RestoreClicked String
    | DownloadClicked
    | UploadClicked
    | ReceiveFromLocalStorage ( String, Json.Decode.Value )
    | SaveToLocalStorage ( String, Json.Decode.Value )
