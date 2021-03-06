defmodule UnicornFrameTest do
  use ExUnit.Case
  alias Unicorn.Color
  alias Unicorn.Frame
  alias Tint.RGB
  doctest Unicorn.Frame

  test "new() creates frames" do
    f = Frame.new()
    assert Frame.get(f, 0, 0) == Frame.default_color()
    assert Frame.get(f, 15, 15) == Frame.default_color()
  end

  test "new() with color, get, set, map" do
    c = RGB.new(100, 100, 100)
    c2 = RGB.new(150, 150, 150)

    f = Frame.new(color: c)
    assert Frame.get(f, 0, 0) == c
    assert Frame.get(f, 15, 15) == c

    f2 = Frame.set(f, 0, 0, c2)
    assert Frame.get(f2, 0, 0) == c2

    assert Frame.map(f2, fn _, _, x -> x end) == f2

    f3 = Frame.map(f2, &Color.darken/3)
    assert Frame.get(f3, 0, 0) == Color.darken(0, 0, c2)
    assert Frame.get(f3, 1, 1) == Color.darken(0, 0, c)
  end
end
