module Main exposing (..)

-- https://medium.com/@diamondgfx/writing-a-full-site-in-phoenix-and-elm-a100804c9499

import Html exposing (Html, text, div)
import Html.Attributes exposing (class)


-- import Color
-- import Color.Convert exposing (colorToHex, hexToColor)

import Components.Grid as Grid
import Components.Messages exposing (Msg(..))
import Components.Model as CM exposing (Model, dumbColor, withStatus, clearStatus)
import Components.Controls as Controls


-- UPDATE


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    -- let _ = Debug.log "msg" msg in
    -- let _ = Debug.log "mod" model in
    case msg of
        GridClicked x y ->
            ( clearStatus { model | grid = CM.paint model.grid x y model.selectedColor }, Cmd.none )

        FillClicked ->
            ( clearStatus { model | grid = CM.fill model.grid model.selectedColor }, Cmd.none )

        SelectedPaintColor color ->
            ( clearStatus { model | selectedColor = dumbColor color }, Cmd.none )

        SaveClicked slotName ->
            ( withStatus ("Saving to slot " ++ slotName ++ "...") model, Cmd.none )

        RestoreClicked slotName ->
            ( withStatus ("Restoring from slot " ++ slotName ++ "...") model, Cmd.none )

        UploadClicked ->
            ( withStatus "Uploading..." model, Cmd.none )

        DownloadClicked ->
            ( withStatus "Downloading..." model, Cmd.none )



-- VIEW


view : Model -> Html Msg
view model =
    div [ class "columns" ]
        [ div [ class "column is-three-fourths" ]
            [ Grid.view model.grid ]
        , div [ class "column is-one-fourth" ]
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
