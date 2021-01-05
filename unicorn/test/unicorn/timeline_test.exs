defmodule UnicornTimelineTest do
  use ExUnit.Case
  alias Unicorn.Step
  alias Unicorn.Timeline
  alias Unicorn.Fx.Random
  doctest Unicorn.Timeline

  setup _context do
    {:ok,
     %{
       s1: Step.new(Random.new(), 1000, 1),
       s2: Step.new(Random.new(), 2000, 2),
       s3: Step.new(Random.new(), 3000, 3)
     }}
  end

  test "append / total work", context do
    t =
      Timeline.new()
      |> Timeline.append(context[:s1])
      |> Timeline.append(context[:s2])
      |> Timeline.append(context[:s3])

    assert t.total == 6
    assert elem(Timeline.nth(t, 10), 0) == :err
  end

  test "nth works correctly for 0, no repeat", context do
    t =
      Timeline.new()
      |> Timeline.append(context[:s1])
      |> Timeline.append(context[:s2])
      |> Timeline.append(context[:s3])

    assert Timeline.nth(t, 0) == {:ok, context[:s1]}
  end

  test "nth works correctly, no repeat", context do
    t =
      Timeline.new()
      |> Timeline.append(context[:s1])
      |> Timeline.append(context[:s2])
      |> Timeline.append(context[:s3])

    assert Timeline.nth(t, 0) == {:ok, context[:s1]}
    assert Timeline.nth(t, 1) == {:ok, context[:s2]}
    assert Timeline.nth(t, 2) == {:ok, context[:s2]}
    assert Timeline.nth(t, 3) == {:ok, context[:s3]}
    assert Timeline.nth(t, 4) == {:ok, context[:s3]}
    assert Timeline.nth(t, 5) == {:ok, context[:s3]}
    assert elem(Timeline.nth(t, 6), 0) == :err
  end

  test "repeat over single step", context do
    t =
      Timeline.new(true)
      |> Timeline.append(context[:s1])

    assert Timeline.nth(t, 0) == {:ok, context[:s1]}
    assert Timeline.nth(t, 1) == {:ok, context[:s1]}
    assert Timeline.nth(t, 200) == {:ok, context[:s1]}
  end

  test "repeat over multiple steps", context do
    t =
      Timeline.new(true)
      |> Timeline.append(context[:s1])
      |> Timeline.append(context[:s2])

    assert Timeline.nth(t, 0) == {:ok, context[:s1]}
    assert Timeline.nth(t, 1) == {:ok, context[:s2]}
    assert Timeline.nth(t, 2) == {:ok, context[:s2]}
    assert Timeline.nth(t, 3) == {:ok, context[:s1]}
    assert Timeline.nth(t, 4) == {:ok, context[:s2]}
    assert Timeline.nth(t, 5) == {:ok, context[:s2]}
  end
end
