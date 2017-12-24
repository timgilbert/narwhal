module Main exposing (..)

-- https://medium.com/@diamondgfx/writing-a-full-site-in-phoenix-and-elm-a100804c9499

import Html exposing (Html, text, div)
import Html.Attributes exposing (class)
-- import Color
-- import Color.Convert exposing (colorToHex, hexToColor)

import Components.Grid as Grid
import Components.Messages exposing (Msg(..))
import Components.Model as CM exposing (Model, dumbColor)
import Components.Controls as Controls 

-- UPDATE

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  -- let _ = Debug.log "msg" msg in
  -- let _ = Debug.log "mod" model in
  case msg of
    GridClicked x y ->
      ({ model | grid = CM.paint model.grid x y model.selectedColor}, Cmd.none)
    FillClicked ->
      ({ model | grid = CM.fill model.grid model.selectedColor}, Cmd.none)
    SelectedPaintColor color ->
      ({ model | selectedColor = dumbColor color }, Cmd.none)
    UploadClicked ->
      (model, Cmd.none)
    DownloadClicked ->
      (model, Cmd.none)

-- VIEW

view : Model -> Html Msg
view model =
  div [ class "columns" ] 
    [ div [ class "column is-two-thirds" ] 
      [ Grid.view model.grid ] 
    , div [ class "column" ] 
      [ Controls.view model ] 
    ]

-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions model = 
  Sub.none

main : Program Never Model Msg
main =
  Html.program
    { init = CM.init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }
