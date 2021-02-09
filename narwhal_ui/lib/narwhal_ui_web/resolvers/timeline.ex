defmodule NarwhalUiWeb.Resolvers.Timeline do
  @moduledoc false
  alias Unicorn.Timeline
  require Logger
  alias NarwhalUi.Repo

  def empty_timeline(_parent, _args, _resolution) do
    Logger.debug(inspect Timeline.new())
    {:ok, Timeline.new()}
  end

  def is_repeat(parent, _args, _resolution) do
    Logger.debug(inspect parent)
    {:ok, parent.repeat?}
  end

  def all_timelines(_parent, args, _resolution) do
    Repo.all_timelines(args)
  end

  def timeline_by_id(_parent, %{id: timeline_id}, _resolution) do
    Repo.get_timeline_meta(timeline_id)
  end

  def create_timeline(_parent, %{input: input}, _resolution) do
    Logger.warn("create_timeline", inspect input)
    {:ok, metadata} = Repo.insert_new_timeline(input)
    {:ok, all_timelines} = Repo.all_timelines(nil)
    {:ok, %{timeline: metadata, all_timelines: all_timelines}}
  end

  def update_timeline(_parent, %{input: %{id: id} = input}, _resolution) do
    if Repo.timeline_exists?(id) do
      Logger.debug(inspect input)
      {:ok, metadata} = Repo.update_timeline(input)
      {:ok, all_timelines} = Repo.all_timelines(nil)
      {:ok, %{timeline: metadata, all_timelines: all_timelines}}
    else
      {:error, %{message: "Timeline with id '#{id}' does not exist!"}}
    end
  end

  def delete_timeline(_parent, %{input: %{id: id}}, _resolution) do
    if Repo.timeline_exists?(id) do
      {:ok, _metadata} = Repo.delete_timeline(id)
      {:ok, all_timelines} = Repo.all_timelines()
      {:ok, %{timeline_id: id, all_timelines: all_timelines}}
    else
      {:error, %{message: "Timeline with id '#{id}' does not exist!"}}
    end
  end
end
