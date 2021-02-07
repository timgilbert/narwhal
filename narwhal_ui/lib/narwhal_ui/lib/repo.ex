defmodule NarwhalUi.Repo do
  @moduledoc false
  require Logger
  alias Unicorn.Frame
  alias Unicorn.Timeline
  alias NarwhalUi.FrameMetadata
  alias NarwhalUi.TimelineMetadata
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
    "f." <> slugify(name)
  end

  def generate_id(%{name: name, timeline: _}) do
    # TODO: check for duplicate
    "t." <> slugify(name)
  end

  def key(%FrameMetadata{id: id}) do
    {Frame, id}
  end

  def key(%TimelineMetadata{id: id}) do
    {Timeline, id}
  end

  def key(%{frame_id: id}) do
    {Frame, id}
  end

  def key(%{timeline_id: id}) do
    {Timeline, id}
  end

  def insert_new_frame(%{name: _, frame: _} = input) do
    id = generate_id(input)
    metadata = FrameMetadata.new(id, input)
    Logger.debug(inspect metadata)
    :ok = CubDB.put(@cubdb, key(metadata), metadata)
    {:ok, metadata}
  end

  def insert_new_timeline(%{name: _, timeline: _} = input) do
    id = generate_id(input)
    metadata = TimelineMetadata.new(id, input)
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

  def get_timeline(timeline_id) do
    CubDB.get(@cubdb, key(%{timeline_id: timeline_id}))
  end

  def frame_exists?(frame_id) do
    get_frame(frame_id) != nil
  end

  def timeline_exists?(timeline_id) do
    get_timeline(timeline_id) != nil
  end

  def all_frames(args \\ %{}) do
    select_all(Frame, args)
  end

  def all_timelines(args \\ %{}) do
    select_all(Timeline, args)
  end

  def delete_frame(frame_id) do
    :ok = CubDB.delete(@cubdb, key(%{frame_id: frame_id}))
    {:ok, frame_id}
  end

  def delete_timeline(timeline_id) do
    :ok = CubDB.delete(@cubdb, key(%{timeline_id: timeline_id}))
    {:ok, timeline_id}
  end

  def update_frame(%{id: id, frame: _} = input) do
    metadata = FrameMetadata.new(id, input)
    Logger.debug(inspect metadata)
    :ok = CubDB.put(@cubdb, key(metadata), metadata)
    {:ok, metadata}
  end

  def update_timeline(%{id: id, timeline: _} = input) do
    metadata = TimelineMetadata.new(id, input)
    Logger.debug(inspect metadata)
    :ok = CubDB.put(@cubdb, key(metadata), metadata)
    {:ok, metadata}
  end

  def get_frame_meta(frame_id) do
    frame_data = CubDB.get(@cubdb, key(%{frame_id: frame_id}))
    Logger.debug(inspect frame_data)
    {:ok, frame_data}
  end

  def get_timeline_meta(timeline_id) do
    timeline_data = CubDB.get(@cubdb, key(%{timeline_id: timeline_id}))
    Logger.debug(inspect timeline_data)
    {:ok, timeline_data}
  end

end
