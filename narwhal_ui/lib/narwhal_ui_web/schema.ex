defmodule NarwhalUiWeb.Schema do
  @moduledoc false

  use Absinthe.Schema
  alias Unicorn.Frame

  import_types NarwhalUiWeb.Schema.Types

  query do
    @desc "Get a random frame"
    field :random_frame, :frame do
      resolve &NarwhalUiWeb.Resolvers.Frame.rand/3
    end
  end
end
