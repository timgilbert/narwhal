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

  def all_frames(_parent, args, _resolution) do
    Repo.all_frames(args)
  end

  def frame_by_id(_parent, %{id: frame_id}, _resolution) do
    Repo.get_frame_meta(frame_id)
  end

  def create_frame(_parent, %{input: input}, _resolution) do
    Logger.debug(inspect input)
    {:ok, metadata} = Repo.insert_new_frame(input)
    {:ok, all_frames} = Repo.all_frames(nil)
    {:ok, %{frame: metadata, all_frames: all_frames}}
  end

  def update_frame(_parent, %{input: %{id: id} = input}, _resolution) do
    if Repo.frame_exists?(id) do
      Logger.debug(inspect input)
      {:ok, metadata} = Repo.update_frame(input)
      {:ok, all_frames} = Repo.all_frames(nil)
      {:ok, %{frame: metadata, all_frames: all_frames}}
    else
      {:error, %{message: "Frame with id '#{id}' does not exist!"}}
    end
  end

  def delete_frame(_parent, %{input: %{id: id}}, _resolution) do
    if Repo.frame_exists?(id) do
      {:ok, _metadata} = Repo.delete_frame(id)
      {:ok, all_frames} = Repo.all_frames()
      {:ok, %{frame_id: id, all_frames: all_frames}}
    else
      {:error, %{message: "Frame with id '#{id}' does not exist!"}}
    end
  end

end
