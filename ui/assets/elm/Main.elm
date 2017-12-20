module Main exposing (..)

-- https://medium.com/@diamondgfx/writing-a-full-site-in-phoenix-and-elm-a100804c9499

import Html exposing (Html, text, div)
import Html.Attributes exposing (class)

import Components.Grid as Grid

main : Html a
main =
  div [ class "elm-grid" ] [ Grid.view ]
