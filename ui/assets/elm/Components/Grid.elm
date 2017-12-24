module Components.Grid exposing (view)

import Html exposing (Html, text, table, thead, tfoot, tbody, tr, td, br)
import Html.Attributes exposing (class, style, property)
import Html.Events exposing (onClick)

import Array exposing (Array, toList, indexedMap)
import Json.Encode exposing (string)
import Color exposing (Color)
import Color.Convert exposing (colorToHex)

import Components.Model exposing (GridModel)
import Components.Messages exposing (Msg(..))

renderCell : Int -> Int -> Color -> Html Msg
renderCell y x color =
  td [ class "narwhal-grid-row"
     , style [ ("backgroundColor", (colorToHex color)) ]
     , property "innerHTML" (string "&nbsp;")
     , onClick (GridClicked x y)
     ]
     []

renderRow : Int -> Array Color -> Html Msg
renderRow y row =
  tr [] (toList (indexedMap (renderCell y) row))

renderRows : GridModel -> List (Html Msg)
renderRows model =
  toList (indexedMap renderRow model.grid)

view : GridModel -> Html Msg
view model =
  table [ class "narwhal-grid-table" ] [
    thead [] [],
    tfoot [] [],
    tbody [] (renderRows model)
  ]
