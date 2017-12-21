module Components.Controls exposing (view)

import Html exposing (Html, text, section, h3, p, button)
import Html.Attributes exposing (class, style, property)
import Html.Events exposing (onClick)

import Components.Messages exposing (Msg(..))

view : Html Msg
view =
  section [ class "foo" ] [
    h3 [] [ text "Controls" ],
    button [ onClick FillClicked ] [ text "Fill" ]
  ]
