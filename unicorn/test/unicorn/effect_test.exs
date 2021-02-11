defmodule UnicornEffectTest do
  use ExUnit.Case
  alias Unicorn.Effect
  alias Unicorn.Target
  alias Unicorn.Color

  doctest Unicorn.Effect

  # This is kind of dumb, is it useful?
  test "frame target creation" do
    e = Target.Random.new()
    assert e == %Target.Random{}

    black = Color.black()
    e = Target.Solid.new(black)
    assert e == %Target.Solid{color: black}

    f = "frame-id"
    e = Target.Saved.new(f)
    assert e == %Target.Saved{frame_id: f}
  end

  test "effect creation, replace" do
    target = Target.Random.new()

    e = Effect.Replace.new(target, 1000)
    assert Effect.Effect.pause_ms(e) == 1000
    assert Effect.Effect.granularity(e) == 1
    assert Effect.Effect.duration_ms(e) == 0
    assert Effect.Effect.immediate?(e) == true
    assert Effect.Effect.target(e) == target
  end

  test "effect creation, tween" do
    target = Target.Solid.new(Color.black())

    e = Effect.Tween.new(target, 2000, 3000, 10)
    assert Effect.Effect.pause_ms(e) == 2000
    assert Effect.Effect.duration_ms(e) == 3000
    assert Effect.Effect.granularity(e) == 10
    assert Effect.Effect.immediate?(e) == false
    assert Effect.Effect.target(e) == target
  end
end
