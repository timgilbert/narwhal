defmodule NarwhalUiWeb.Schema.Step do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A single step in the timeline. A step contains one or more effects, with an optional pause at the end, and may repeat one or more times."
  object :step do
    field :effects, non_null(list_of(non_null(:effect))) do
      description "The steps in this timeline"
    end
    field :repetitions, non_null(:integer),
          description: "The total number of times to repeat this step when it's run"
    field :pause_ms, non_null(:integer),
          description: "Pause for this many milliseconds between each repetition"
  end

end
