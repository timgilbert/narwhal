defmodule NarwhalUiWeb.Schema.Effect do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "The type of an effect"
  enum :effect_type do
    value :random, description: "Replace the frame with a randomized set"
  end

  @desc "A single effect on a timeline"
  interface :effect do
    field :duration_ms, :integer, description: "Total duration of this effect"
    resolve_type fn
      %Unicorn.Fx.Random.Struct{}, _ -> :random_effect
      %Unicorn.Fx.Color.Struct{}, _ -> :color_effect
      _, _ -> nil
    end
  end

  @desc "Replace frame with a random grid"
  object :random_effect do
    interfaces [:effect]
    field :duration_ms, :integer, description: "Total duration of this effect"
  end

  @desc "Replace frame with a uniform color"
  object :color_effect do
    interfaces [:effect]
    field :duration_ms, :integer, description: "Total duration of this effect"
    field :color, :string, description: "Color to replace with"
  end
end
