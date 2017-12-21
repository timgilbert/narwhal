module Main exposing (..)

-- https://medium.com/@diamondgfx/writing-a-full-site-in-phoenix-and-elm-a100804c9499

import Html exposing (Html, text, div)
import Html.Attributes exposing (class)

import Components.Grid as Grid
import Components.Model as CM

-- MODEL

type alias Model = 
  { grid : CM.GridModel }

init : (Model, Cmd Msg)
init =
  (Model (CM.init Nothing), Cmd.none)

-- UPDATE

type Msg = Submit

update : Msg -> Model -> (Model, Cmd Msg)
update msg model =
  (model, Cmd.none)


-- VIEW

view : Model -> Html Msg
view {grid} =
  div [ class "elm-grid" ] [ 
    Grid.view grid
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
