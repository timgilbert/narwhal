module Components.Messages exposing (Msg(..))

type Msg = 
  GridClicked Int Int |
  SelectedPaintColor String |
  FillClicked | 
  DownloadClicked |
  UploadClicked
