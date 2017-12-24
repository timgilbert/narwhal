module Components.Model exposing (Model, GridModel, Square, init, fill, paint, toHexJson, fromHexJson, dumbColor, withStatus, clearStatus)

import Array exposing (Array)
import Color exposing (Color)
import Json.Encode as JE
import Json.Decode as JD
import Color.Convert exposing (colorToHex, hexToColor)
import Components.Messages exposing (Msg(..))


-- MODEL


type alias Model =
    { grid : GridModel
    , selectedColor : Color
    , statusMsg : Maybe String
    }


init : ( Model, Cmd Msg )
init =
    ( Model (initGrid Nothing) defaultColor Nothing, Cmd.none )


type alias Square a =
    Array (Array a)


type alias GridModel =
    { bounds : Int
    , grid : Square Color
    }


clearStatus : { r | statusMsg : Maybe String } -> { r | statusMsg : Maybe String }
clearStatus r =
    { r | statusMsg = Nothing }


withStatus : String -> { r | statusMsg : Maybe String } -> { r | statusMsg : Maybe String }
withStatus msg r =
    { r | statusMsg = Just msg }


defaultBounds : Int
defaultBounds =
    16


defaultColor : Color
defaultColor =
    Color.black


makeGrid : Int -> a -> Square a
makeGrid bounds item =
    Array.repeat bounds (Array.repeat bounds item)


mapSquare : (a -> b) -> Square a -> Square b
mapSquare fn square =
    Array.map (\row -> Array.map fn row) square


setGrid : Square a -> Int -> Int -> a -> Square a
setGrid square x y item =
    let
        row =
            Array.get y square
    in
        case row of
            Nothing ->
                square

            Just r ->
                Array.set y (Array.set x item r) square


initGrid : Maybe Int -> GridModel
initGrid sizeParam =
    let
        selectedBound =
            Maybe.withDefault defaultBounds sizeParam
    in
        { bounds = selectedBound
        , grid = makeGrid selectedBound defaultColor
        }


fill : GridModel -> Color -> GridModel
fill model color =
    { model | grid = makeGrid model.bounds color }


paint : GridModel -> Int -> Int -> Color -> GridModel
paint model x y color =
    { model | grid = setGrid model.grid x y color }


toHexJson : GridModel -> String
toHexJson model =
    JE.encode 2 <| toHexJsonValue model


toHexJsonValue : GridModel -> JE.Value
toHexJsonValue model =
    JE.array <| Array.map hexRow model.grid


hexRow : Array Color -> JE.Value
hexRow cs =
    JE.array <| Array.map (\c -> c |> colorToHex |> JE.string) cs


fromHexJson : GridModel -> String -> Maybe GridModel
fromHexJson model json =
    let
        res =
            JD.array (JD.array JD.string)
    in
        case JD.decodeString res json of
            Ok matrix ->
                Just { model | grid = mapSquare dumbColor matrix }

            Err msg ->
                Nothing


dumbColor : String -> Color
dumbColor s =
    case hexToColor s of
        Ok color ->
            color

        Err msg ->
            Color.red
