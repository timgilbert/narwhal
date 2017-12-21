module Components.Model exposing (GridModel, Square, init)

import Array exposing (Array)
import Color exposing (Color)

type alias Square a = Array (Array a)

type alias GridModel = 
  { bounds : Int
  , grid : Square Color
  }

defaultBounds : Int
defaultBounds = 16

makeGrid : Int -> a -> Array (Array(a))
makeGrid bounds item =
   Array.repeat bounds (Array.repeat bounds item)

init : Maybe Int -> GridModel
init sizeParam = 
  let selectedBound = 
    case sizeParam of
      Nothing -> defaultBounds
      Just n -> n
  in
    { bounds = selectedBound,
      grid = makeGrid selectedBound Color.red }

fill : GridModel -> Color -> GridModel
fill model color =
  { model | grid = makeGrid model.bounds color }
