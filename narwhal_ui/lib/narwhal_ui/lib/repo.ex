defmodule NarwhalUi.Repo do
  @moduledoc false
  require Logger
  alias Unicorn.Frame
  alias NarwhalUi.FrameMetadata
  # Following https://github.com/vorce/playlist_log/blob/master/lib/playlist_log/repo.ex

  @cubdb :cubdb

  @spec slugify(String.t()) :: String.t()
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
    {Frame, id}
  end

  def key(%{frame_id: id}) do
    {Frame, id}
  end

  def insert_new_frame(%{name: _, frame: _} = input) do
    id = generate_id(input)
    metadata = FrameMetadata.new(id, input)
    Logger.debug(inspect metadata)
    :ok = CubDB.put(@cubdb, key(metadata), metadata)
    {:ok, metadata}
  end

  # eg, Repo.select(Frame, %{}})
  # TODO: maybe this is too much of a leaky abstraction
  def select_all(type, _options) do
    CubDB.select(@cubdb,
      pipe: [
        filter: fn {{key_type, _id}, _r} ->
          key_type == type
        end,
        map: fn {_k, v} -> v end
      ]
    )
  end

  def get_frame(frame_id) do
    CubDB.get(@cubdb, key(%{frame_id: frame_id}))
  end

  def frame_exists?(frame_id) do
    get_frame(frame_id) != nil
  end

  def all_frames(args \\ %{}) do
    select_all(Frame, args)
  end

  def delete_frame(frame_id) do
    :ok = CubDB.delete(@cubdb, key(%{frame_id: frame_id}))
    {:ok, frame_id}
  end

  def update_frame(%{id: id, frame: _} = input) do
    metadata = FrameMetadata.new(id, input)
    Logger.debug(inspect metadata)
    :ok = CubDB.put(@cubdb, key(metadata), metadata)
    {:ok, metadata}
  end

  def get_frame_meta(frame_id) do
    frame_data = CubDB.get(@cubdb, key(%{frame_id: frame_id}))
    Logger.debug(inspect frame_data)
    {:ok, frame_data}
  end


end
