defmodule UnicornTweenTest do
  use ExUnit.Case
  alias Unicorn.Fx.Color
  import Unicorn.Color, only: [black: 0, white: 0]
  alias Unicorn.Step
  alias Unicorn.Frame
  alias Unicorn.Timeline
  doctest Unicorn.Timeline

  test "tween" do
    target = Frame.new(color: white())
    t =
      Timeline.new()
      |> Timeline.append(Step.new(Color.new(color: black()), 1000, 1))
      |> Timeline.tween_to(target, 10, 1000)
      |> IO.inspect()

    assert t.total == 11
  end

end
