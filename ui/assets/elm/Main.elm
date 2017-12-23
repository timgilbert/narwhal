module Main exposing (..)

-- https://medium.com/@diamondgfx/writing-a-full-site-in-phoenix-and-elm-a100804c9499

import Html exposing (Html, text, div)
import Html.Attributes exposing (class)
import Color

import Components.Grid as Grid
import Components.Messages exposing (Msg(..))
import Components.Model as CM 
import Components.Controls as Controls 

-- MODEL
type alias Model = 
  { grid : CM.GridModel }

init : (Model, Cmd Msg)
init =
  (Model (CM.init Nothing), Cmd.none)

-- UPDATE

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  -- let _ = Debug.log "msg" msg in
  -- let _ = Debug.log "mod" model in
  case msg of
    GridClicked x y ->
      ({ model | grid = CM.paint model.grid x y Color.darkYellow}, Cmd.none)
    FillClicked ->
      ({ model | grid = CM.fill model.grid Color.blue}, Cmd.none)


-- VIEW

view : Model -> Html Msg
view {grid} =
  div [ class "elm-grid" ] [ 
    Grid.view grid,
    Controls.view grid
  ]

-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions model = 
  Sub.none

main : Program Never Model Msg
main =
  Html.program
    { init = init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }
