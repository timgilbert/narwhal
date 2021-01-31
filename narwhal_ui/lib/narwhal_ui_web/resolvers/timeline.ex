defmodule NarwhalUiWeb.Resolvers.Timeline do
  @moduledoc false
  alias Unicorn.Timeline
  require Logger

  def all_saved_timelines(_parent, _args, _resolution) do
    {:ok, []}
  end

  def empty_timeline(_parent, _args, _resolution) do
    Logger.debug(inspect %{timeline: Timeline.new()})
    {:ok, %{timeline: Timeline.new()}}
  end

  def effects(parent, _args, _resolution) do
    Logger.debug(inspect parent)
    {:ok, parent.items}
  end

end
