module Components.Controls exposing (view)

import Html exposing (Html, text, section, h3, p, button, pre)
import Html.Attributes exposing (class, style, property)
import Html.Events exposing (onClick)

import Components.Model as CM exposing (GridModel, toHexJson)

import Components.Messages exposing (Msg(..))

view : GridModel -> Html Msg
view model =
  section [ class "foo" ]
   [ h3 [] [ text "Controls" ]
   , button [ onClick FillClicked ] [ text "Fill" ]
   , pre [] [ text <| toHexJson model ]
   ]
