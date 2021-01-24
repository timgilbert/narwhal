defmodule NarwhalUiWeb.Schema.General do
  @moduledoc false

  use Absinthe.Schema.Notation

  enum :sort_order do
    value :asc, description: "Sort from highest to lowest"
    value :desc, description: "Sort from lowest to highest"
  end

  @desc "Options for sorting various lists"
  input_object :sort_options do
    field :order, :sort_order, description: "Sort order (ascending or descending)"
  end

end
