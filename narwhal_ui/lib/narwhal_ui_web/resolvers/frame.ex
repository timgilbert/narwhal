defmodule NarwhalUiWeb.Resolvers.Frame do
  @moduledoc false
  alias Unicorn.Frame

  def rand(parent, _args, _resolution) do
    {:ok, Frame.rand()}
  end

  def pixels(parent, _args, _resolution) do
    # TODO: Frame.hex_grid should just return stuff like this
    {:ok, parent |> Frame.hex_grid() |> List.flatten}
  end
end
