defmodule NarwhalUiWeb.Schema.Timeline do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A timeline"
  object :timeline do
    field :effects, non_null(list_of(non_null(:effect))) do
      description "The effects in this timeline"
    end
  end

  @desc "A saved timeline with a name and ID"
  object :named_timeline do
    field :name, non_null(:string), description: "The name of this timeline"
    field :id, non_null(:string), description: "Unique ID for a saved timeline"
    field :timeline, non_null(:timeline), description: "The timeline data"
  end

  @desc "Response from a create timeline mutation"
  object :create_timeline_response do
    field :named_timeline, non_null(:named_timeline) do
      description "The frame that was just created"
    end
  end

  @desc "Input object for a new timeline"
  input_object :new_timeline do
    field :name, :string, description: "Name of the timeline (default \"timeline\")"
  end

end
