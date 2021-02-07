defmodule NarwhalUiWeb.Schema.Timeline do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A timeline"
  object :timeline do
    field :effects, non_null(list_of(non_null(:effect))) do
      description "The effects in this timeline"
      resolve &NarwhalUiWeb.Resolvers.Timeline.effects/3
    end
    field :total, non_null(:integer), description: "The total number of steps in this timeline"
    field :is_repeat, non_null(:boolean)
  end

  @desc "A saved timeline with a name and ID"
  object :timeline_metadata do
    field :name, non_null(:string), description: "The name of this timeline"
    field :id, non_null(:string), description: "Unique ID for a saved timeline"
    field :timeline, non_null(:timeline), description: "The timeline data"
  end

  @desc "Response from a create timeline mutation"
  object :create_timeline_response do
    field :timeline, non_null(:timeline_metadata) do
      description "The frame that was just created"
    end
  end

  @desc "Response from an empty timeline query"
  object :empty_timeline_response do
    field :timeline, non_null(:timeline) do
      description "The empty timeline"
    end
  end

  @desc "Input object for a new timeline"
  input_object :new_timeline do
    field :name, :string, description: "Name of the timeline (default \"timeline\")"
  end

end
