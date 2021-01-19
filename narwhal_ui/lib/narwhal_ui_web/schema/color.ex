defmodule NarwhalUiWeb.Schema.Color do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A color, serialized as an RGB string"
  scalar :rgb_color do
    serialize &Tint.RGB.to_hex/1
    parse &Tint.RGB.from_hex/1
  end

end
