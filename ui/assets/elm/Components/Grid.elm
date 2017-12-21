module Components.Grid exposing (view)

import Html exposing (Html, text, table, thead, tfoot, tbody, tr, td, br)
import Html.Attributes exposing (class, style, property)
import Array exposing (Array, toList, indexedMap)
import Json.Encode exposing (string)
import Color exposing (Color)

import Color.Convert exposing (colorToHex)

import Components.Model exposing (GridModel)

renderCell : Int -> Int -> Color -> Html a
renderCell y x color = 
  td [ class "narwhal-grid-row"
     , style [ ("backgroundColor", (colorToHex color)) ] 
     , property "innerHTML" (string "&nbsp;")]
     []

renderRow : Int -> Array Color -> Html a
renderRow y row = 
  tr [] (toList (indexedMap (renderCell y) row))

renderRows : GridModel -> List (Html a)
renderRows model = 
  toList (indexedMap renderRow model.grid)

view : GridModel -> Html a
view model =
  table [ class "narwhal-grid-table" ] [
    thead [] [],
    tfoot [] [],
    tbody [] (renderRows model)
  ]
