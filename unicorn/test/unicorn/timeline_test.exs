defmodule UnicornTimelineTest do
  use ExUnit.Case
  alias Unicorn.Step
  alias Unicorn.Timeline
  alias Unicorn.Fx.{Color, Random}
  doctest Unicorn.Timeline

  test "nth works correctly, no repeat" do
    s1 = Step.new(Random.new(), 1000, 1)
    s2 = Step.new(Color.new(), 1000, 2)
    t = Timeline.new()
    |> Timeline.append(s1)
    |> Timeline.append(s2)

    assert Timeline.nth(t, 0) == {:ok, s1}
    assert Timeline.nth(t, 1) == {:ok, s2}
    assert Timeline.nth(t, 2) == {:ok, s2}
  end
end
