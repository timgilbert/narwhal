defmodule NarwhalUi.Repo do
  @moduledoc false
  require Logger
  alias Unicorn.Frame
  alias NarwhalUi.FrameMetadata
  # Following https://github.com/vorce/playlist_log/blob/master/lib/playlist_log/repo.ex

  @cubdb :cubdb
  @frames :frames

  @spec slugify(string) :: string
  def slugify(name) do
    name
    |> String.trim
    |> String.replace(~r/[^A-Za-z0-9_-]/, "-")
  end

  def generate_id(%{name: name, frame: _}) do
    # TODO: check for duplicate
    "frame." <> slugify(name)
  end

  def key(%FrameMetadata{id: id}) do
    {@frames, id}
  end

  def insert_new_frame(%{name: name, frame: frame} = input) do
    id = generate_id(input)
    metadata = FrameMetadata.new(id, input)
    Logger.debug(inspect metadata)
    :ok = CubDB.put(@cubdb, key(metadata), metadata)
    {:ok, metadata}
  end
end
