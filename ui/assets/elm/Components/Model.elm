module Components.Model exposing (GridModel, Square, init, fill, paint)

import Array exposing (Array)
import Color exposing (Color)

type alias Square a = Array (Array a)

type alias GridModel = 
  { bounds : Int
  , grid : Square Color
  }

defaultBounds : Int
defaultBounds = 16

makeGrid : Int -> a -> Square a
makeGrid bounds item =
   Array.repeat bounds (Array.repeat bounds item)

setGrid : Square a -> Int -> Int -> a -> Square a
setGrid square x y item =
  let row = Array.get y square in
    case row of
      Nothing -> square
      Just r ->
        Array.set y (Array.set x item r) square

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

paint : GridModel -> Int -> Int -> Color -> GridModel
paint model x y color =
  { model | grid = setGrid model.grid x y color }
