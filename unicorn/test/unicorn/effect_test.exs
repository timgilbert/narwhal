defmodule UnicornEffectTest do
  use ExUnit.Case
  alias Unicorn.Effect
  doctest Unicorn.Effect

  test "append / total work", context do
    t =
      Timeline.new(false)
      |> Timeline.append(context[:s1])
      |> Timeline.append(context[:s2])
      |> Timeline.append(context[:s3])

    assert t.total == 6
    assert elem(Timeline.nth(t, 10), 0) == :err
  end

  test "nth works correctly for 0, no repeat", context do
    t =
      Timeline.new(false)
      |> Timeline.append(context[:s1])
      |> Timeline.append(context[:s2])
      |> Timeline.append(context[:s3])

    assert Timeline.nth(t, 0) == {:ok, context[:s1]}
  end

  test "nth works correctly, no repeat", context do
    t =
      Timeline.new(false)
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

  test "serialization", _context do
    t = Timeline.new(%{is_repeat: true, steps: []})
    assert t == %Timeline{repeat?: true, steps: [], total: 0}
  end

  test "serialization with single step", _context do
    steps = [%{pause_ms: 1000, repeat: 1, effects: []}]
    t = Timeline.new(%{is_repeat: true, steps: steps})

    assert t == %Timeline{
             repeat?: true,
             steps: [%Unicorn.Step{effects: [], pause_ms: 1000, repeat: 1}],
             total: 1
           }
  end

  test "serialization with several steps", _context do
    steps = [
      %{pause_ms: 1000, repeat: 1, effects: []},
      %{pause_ms: 2000, repeat: 2, effects: []},
      %{pause_ms: 3000, repeat: 3, effects: []}
    ]
    t = Timeline.new(%{is_repeat: true, steps: steps})

    assert t == %Timeline{
             repeat?: true,
             steps: [
               %Unicorn.Step{effects: [], pause_ms: 1000, repeat: 1},
               %Unicorn.Step{effects: [], pause_ms: 2000, repeat: 2},
               %Unicorn.Step{effects: [], pause_ms: 3000, repeat: 3},
             ],
             total: 6
           }
  end

  test "serialization with one effect", _context do
    effects = [%{type: :random_frame, pause_ms: 1000}]
    steps = [%{pause_ms: 1000, repeat: 1, effects: effects}]
    t = Timeline.new(%{is_repeat: true, steps: steps})

    assert t == %Timeline{
             repeat?: true,
             steps: [%Unicorn.Step{effects: [], pause_ms: 1000, repeat: 1}],
             total: 1
           }
  end
end
