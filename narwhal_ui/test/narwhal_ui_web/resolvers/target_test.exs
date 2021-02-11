defmodule NarwhalUiWeb.ResolversTargetTest do
  use ExUnit.Case
  import NarwhalUiWeb.Resolvers.Hydrate, only: [hydrate_frame_target: 1]
  alias Unicorn.Target
  alias Unicorn.Color

  doctest Unicorn.Effect

  test "frame target hydration error cases" do
    assert {:error, _} = hydrate_frame_target(%{})
    assert {:error, _} = hydrate_frame_target(%{type: :saved})
    assert {:error, _} = hydrate_frame_target(%{type: :solid})
  end

  test "frame target hydration, random" do
    assert {:ok, %Target.Random{}} =
             hydrate_frame_target(%{type: :random})
  end

  test "frame target hydration, solid" do
    black = Color.black()
    assert {:ok, %Target.Solid{color: ^black}} =
             hydrate_frame_target(
               %{
                 type: :solid,
                 color: black
               }
             )
  end

  # TODO: this should hook into the repo, return :error if id not found
  test "frame target hydration, saved" do
    frame_id = "id"

    assert {:ok, %Target.Saved{frame_id: ^frame_id}} =
             hydrate_frame_target(
               %{
                 type: :saved,
                 frame_id: frame_id
               }
             )
  end

end
