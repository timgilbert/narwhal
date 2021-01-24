defmodule NarwhalUiWeb.Resolvers.Frame do
  @moduledoc false
  require Logger
  alias Unicorn.Frame
  alias NarwhalUi.Repo

  def rand(_parent, _args, _resolution) do
    {:ok, Frame.rand()}
  end

  def solid(_parent, %{color: color}, _resolution) do
    {:ok, Frame.new(color: color)}
  end
  def solid(_parent, _args, _resolution) do
    {:ok, Frame.new()}
  end

  def pixels(parent, _args, _resolution) do
    # TODO: Frame.hex_grid should just return stuff like this
    {:ok, parent |> Frame.pixels() |> List.flatten}
  end

  def all_saved_frames(_parent, _args, _resolution) do
    {:ok, []}
  end

  def create_frame(_parent, %{input: input}, _resolution) do
    Logger.debug(inspect input)
    {:ok, metadata} = Repo.insert_new_frame(input)
    {:ok, %{frame: metadata, all_frames: [metadata]}}
  end

end
