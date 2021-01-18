defmodule NarwhalUiWeb.Resolvers.Timeline do
  @moduledoc false
  alias Unicorn.Frame

  def all_saved_timelines(_parent, _args, _resolution) do
    {:ok, []}
  end

end
