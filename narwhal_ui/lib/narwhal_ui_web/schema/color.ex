defmodule NarwhalUiWeb.Schema.Color do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A color, serialized as an RGB string"
  scalar :rgb_color do
    serialize &Tint.RGB.to_hex/1
    parse &parse_rgb_color/1
  end

  defp parse_rgb_color(%Absinthe.Blueprint.Input.String{value: value}) do
    Tint.RGB.from_hex(value)
  end
  defp parse_rgb_color(%Absinthe.Blueprint.Input.Null{}) do
    {:ok, nil}
  end
  defp parse_rgb_color(_) do
    :error
  end

end
