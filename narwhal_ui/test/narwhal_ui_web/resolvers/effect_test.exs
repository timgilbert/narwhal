defmodule NarwhalUiWeb.ResolversEffectTest do
  use ExUnit.Case
  import NarwhalUiWeb.Resolvers.Hydrate, only:
    [hydrate_effect: 1, hydrate_step: 1]
  alias Unicorn.Target
  alias Unicorn.Effect
  alias Unicorn.Step
  alias Unicorn.Color

  doctest Unicorn.Effect

  test "effect hydration error cases" do
    # Not enough args
    assert {:error, _val} = hydrate_effect(%{})
    assert {:error, _val} = hydrate_effect(%{type: :replace})
    assert {:error, _val} = hydrate_effect(%{type: :tween})
  end

  test "effect hydration, replace" do
    black = Color.black()
    assert {
             :ok,
             %Effect.Replace{
               pause_ms: 1000,
               target: %Target.Random{}
             }
           } = hydrate_effect(
             %{
               type: :replace,
               pause_ms: 1000,
               target: %{
                 type: :random
               }
             }
           )

    assert {
             :ok,
             %Effect.Replace{
               pause_ms: 1000,
               target: %Target.Solid{
                 color: ^black
               }
             }
           } = hydrate_effect(
             %{
               type: :replace,
               pause_ms: 1000,
               target: %{
                 type: :solid,
                 color: black
               }
             }
           )
  end

  test "effect hydration, tween" do
    frame_id = "id"
    assert {
             :ok,
             %Effect.Tween{
               pause_ms: 1000,
               granularity: 1,
               duration_ms: 2000,
               target: %Target.Saved{
                 frame_id: ^frame_id
               }
             }
           } = hydrate_effect(
             %{
               type: :tween,
               pause_ms: 1000,
               granularity: 1,
               duration_ms: 2000,
               target: %{
                 type: :saved,
                 frame_id: frame_id
               }
             }
           )
  end

  test "step hydration error cases" do
    # Not enough args
    assert {:error, _val} = hydrate_effect(%{})
    assert {:error, _val} = hydrate_effect(%{type: :replace})
    assert {:error, _val} = hydrate_effect(%{type: :tween})
  end

  test "step hydration, no steps" do
    _frame_id = "id"
    assert {
             :ok,
             %Step{effects: [], pause_ms: 500, repetitions: 1}
           } = hydrate_step(
             %{
               effects: [],
               pause_ms: 500,
               repetitions: 1
             }
           )
  end

  test "step hydration, one step" do
    assert {
             :ok,
             %Step{
               effects: [
                 %Effect.Replace{
                   pause_ms: 300,
                   target: %Target.Random{}
                 }
               ],
               pause_ms: 500,
               repetitions: 1
             }
           } = hydrate_step(
             %{
               effects: [
                 %{
                   type: :replace,
                   pause_ms: 300,
                   target: %{
                     type: :random
                   }
                 }
               ],
               pause_ms: 500,
               repetitions: 1
             }
           )
  end

  test "step hydration, two steps" do
    black = Color.black()
    frame_id = "frame-id"
    assert {
             :ok,
             %Step{
               effects: [
                 %Effect.Replace{
                   pause_ms: 100,
                   target: %Target.Solid{
                     color: ^black
                   }
                 },
                 %Effect.Tween{
                   pause_ms: 200,
                   duration_ms: 300,
                   granularity: 4,
                   target: %Target.Saved{
                     frame_id: ^frame_id
                   }
                 }
               ],
               pause_ms: 300,
               repetitions: 10
             }
           } = hydrate_step(
             %{
               effects: [
                 %{
                   type: :replace,
                   pause_ms: 100,
                   target: %{
                     type: :solid,
                     color: black
                   }
                 },
                 %{
                   type: :tween,
                   pause_ms: 200,
                   duration_ms: 300,
                   granularity: 4,
                   target: %{
                     type: :saved,
                     frame_id: frame_id
                   }
                 }
               ],
               pause_ms: 300,
               repetitions: 10
             }
           )
  end
end
