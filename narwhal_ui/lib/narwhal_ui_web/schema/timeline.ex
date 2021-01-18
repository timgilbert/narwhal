defmodule NarwhalUiWeb.Schema.Timeline do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A timeline"
  object :timeline do
    field :name, :string, description: "The name of this timeline"
    field :id, :integer, description: "Unique ID for a saved timeline"
    field :effects, list_of(:effect), description: "The effects in this timeline"
  end

end
