defmodule NarwhalUiWeb.Schema.Types do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A single frame"
  object :frame do
    field :height, :integer, description: "Height of the frame in pixels"
    field :width, :integer, description: "Width of the frame in pixels"
    # TODO: custom scalar
    field :pixels, list_of(:string), description: "The pixels, as RGB hex strings" do
      resolve &NarwhalUiWeb.Resolvers.Frame.pixels/3
    end
  end

end
