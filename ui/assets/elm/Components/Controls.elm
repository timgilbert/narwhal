module Components.Controls exposing (view)

import Html exposing (Html, text, section, h3, p, button, pre, a, i, input, span, br, div)
import Html.Attributes exposing (class, style, value, property, type_)
import Html.Events exposing (onClick, onInput)
import Color exposing (Color)
import Color.Convert exposing (colorToHex, hexToColor)

import Components.Model as CM exposing (Model, toHexJson)

import Components.Messages exposing (Msg(..))


view : Model -> Html Msg
view model =
  let _ = Debug.log "grid" model in
  section [ class "foo" ]
    [ h3 [] [ text "Controls" ]
    , iconButton "Fill" "eraser" FillClicked
    , br [] [] 
    , iconButton "Upload" "cloud-upload" UploadClicked
    , br [] []
    , iconButton "Download" "cloud-download" DownloadClicked
    , br [] []
    , br [] []
    , colorSelect model
    , br [] []
    , colorButtons colorIconList
    , br [] []
    , br [] []
    -- , pre [] [ text <| toHexJson model.grid ]
    ]

colorIconList : List Color
colorIconList = [ Color.black, Color.white, Color.red, Color.orange, Color.yellow, Color.green, Color.blue
                , Color.darkPurple, Color.lightPurple ]

iconButton : String -> String -> Msg -> Html Msg
iconButton label icon msg = 
  a [ class "button is-primary is-outlined", onClick msg ]
    [ span [ class "icon" ]
      [ i [ class ("fa fa-" ++ icon) ] [] ]
      , span [] [ text label ]
    ]

colorButtons : List Color -> Html Msg
colorButtons colors =
  div [] (List.map colorButton colors)

colorButton : Color -> Html Msg
colorButton color = 
  let 
    hex = colorToHex color
    buttonColor = if color == Color.white then colorToHex Color.lightGray else hex
  in
    a [ class "button is-outlined", onClick <| SelectedPaintColor hex
      , style [ ("color", buttonColor) ] ]
    [ span [ class "icon" ]
      [ i [ class ("fa fa-paint-brush") ] [] ]
    ]

colorSelect : Model -> Html Msg
colorSelect model = 
  let hex = colorToHex model.selectedColor in
    div [] 
      [ input [ type_ "color", onInput SelectedPaintColor, value hex ] []
      , span [] [ text <| "Selected: " ++ hex ] ]
